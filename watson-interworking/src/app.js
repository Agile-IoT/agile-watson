/*******************************************************************************
 * Copyright (c) 2017 Sensinov (www.sensinov.com)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *******************************************************************************/

const request = require('request');
const WebSocket = require('ws');
const Watson = require('ibmiotf');
const config = require('./config');

var agileDevicesIdStore = [];
var agileDevicesTypeStore = [];

main();

function main(){
        run(); 
        setInterval(() => {
            run();
        }, config.agile.pollingPeriod);
}

function run(){
    findAllDevices().then( devices => {
        var newDevices = getNotRegistredDevices(devices);
        if (newDevices.length > 0) {
            newDevices.forEach( deviceAgile => {
                log(`${config.agile.log} new device description`);
                log('<<<<<<<<');
                log(deviceAgile);
                log('>>>>>>>>');
                // register device type if new
                if( !isNewDeviceType(deviceAgile.name) ) {
                    agileDevicesTypeStore.push(deviceAgile.name);
                    // register device type in watson
                    createDeviceTypeInWatson(deviceAgile.name).then( () => {
                        registerDevice(deviceAgile);
                    }, error => log(error));
                } else {
                    registerDevice(deviceAgile);
                }
            });
        } else {
            log(`${config.agile.log} no new devices found`);
        }
    }, error => log(error) );
}

function registerDevice(deviceAgile){
    // register device in watson
    registerDeviceInWatson(deviceAgile).then( () => {
        // subscribe to agile device events and publish them in watson
        subscribeAndPublishEvents(deviceAgile);
    },  error => log(error));
}

function createDeviceTypeInWatson(type) {
    log(`${config.watson.log} register new device type ${type}`);
    var appClientConfig = {
        org: config.watson.orga,
        id: config.watson.application.id,
        "auth-key": config.watson.application.key,
        "auth-token": config.watson.application.token
    };
    var appClient = new Watson.IotfApplication(appClientConfig);

    var desc = type;
    var metadata = {}
    var deviceInfo = { "serialNumber": "XXXX", "manufacturer": "none", "model": "none", "descriptiveLocation": "France", "fwVersion": "1.0", "hwVersion": "1.0" }

    return new Promise( (resolve, reject) => {
        appClient.registerDeviceType(type, desc, deviceInfo, metadata).then(function onSuccess(argument) {
            log(`${config.watson.log} device type ${type} created`);
            resolve();
        }, function onError(error) {
            if (error.status === 409) {
                log(`${config.watson.log} device type ${type} already exists`);
                resolve();
            } else {
                log(`${config.watson.log} device type ${type} cannot be registered`);
                reject(error);
            }
        });
    });
}

function findAllDevices() {
    var method = "GET";
    var uri = `http://${config.agile.host}:${config.agile.port}/api/devices`;

    var options = {
        uri: uri,
        method: method,
    };

    return new Promise( (resolve, reject) => {
        request(options,  (error, response, body) =>{
            if (error) {
                reject(error);
            } else {
                log(`${config.agile.log} Get devices response status =  ${response.statusCode}`);
                if (response.statusCode === 200) {
                    var devices = JSON.parse(body);
                    if (devices !== undefined && devices !== null) {
                        log(`${config.agile.log} Number of registred devices is ${devices.length}`);
                        resolve(devices);
                    }
                }
            }
        });
    });
}

function registerDeviceInWatson(deviceAgile) {
    var appClientConfig = {
        org: config.watson.orga,
        id: config.watson.application.id,
        "auth-key": config.watson.application.key,
        "auth-token": config.watson.application.token
    };

    var appClient = new Watson.IotfApplication(appClientConfig);
    var type = deviceAgile.name;
    var authToken = config.watson.application.token;
    var metadata = {};
    var deviceInfo = {};
    var location = {};

    return new Promise( (resolve, reject) => {
        appClient.registerDevice(type, deviceAgile.deviceId, authToken, deviceInfo, location, metadata).then(function onSuccess(response) {
            log(`${config.watson.log} device ${deviceAgile.deviceId} created`);
            resolve();
        }, function onError(error) {
            if (error.status === 409) {
                log(`${config.watson.log} device ${deviceAgile.deviceId} already exists`);
                resolve();
            } else {
                log(`${config.watson.log} device ${deviceAgile.deviceId} cannot be registered`);
                reject(error);
            }
        });
    });
}

// subscribe to device agile events and publish new events in watson
function subscribeAndPublishEvents(deviceAgile) {
    var deviceClientConfig = {
        org: config.watson.orga,
        id: deviceAgile.deviceId,
        domain: config.watson.domain,
        type: deviceAgile.name,
        "auth-method": "token",
        "auth-token": config.watson.application.token
    };
 
    var i = 10; // DELETE_ME
    // connect to watson device
    var deviceClient = new Watson.IotfDevice(deviceClientConfig);
    deviceClient.connect();
    deviceClient.on("connect", () => {
        console.log(`${config.watson.log} connect device ${deviceClientConfig.id}`);
        deviceAgile.streams.forEach(stream => {
            var uri = `ws://${config.agile.host}:${config.agile.port}/ws/device/${deviceAgile.deviceId}/${stream.id}/subscribe`;
            log(uri);
            var ws = new WebSocket(uri);
            // subsribe to events from agile device
            log(`${config.agile.log} subscribe to component ${stream.id} events of device ${deviceAgile.deviceId}`);
            ws.onmessage = (event) => {
                // new event received from agile device
                if (i === 10) {// DELETE_ME
                    i = 0;// DELETE_ME
                    log(event.data);
                    log(`${config.watson.log}  ${deviceAgile.deviceId}/${stream.id} publish event`);
                    deviceClient.publish(stream.id, 'json', event.data, 2);
                } // DELETE_ME
                i++; // DELETE_ME
            };
            ws.onerror = () => {
                console.log('Connection Error');
            };
            ws.onclose = () => {
                console.log('Connection Closed');
            };
        });
    });
}

function getNotRegistredDevices(devices){
    return devices.map( deviceAgile => {
        if (agileDevicesIdStore.includes(deviceAgile.deviceId) === false ){
            log(`${config.agile.log} new device ${deviceAgile.deviceId} discovered`);
            agileDevicesIdStore.push(deviceAgile.deviceId);
            return deviceAgile;
        }
    }).filter(deviceAgile => deviceAgile !== undefined);
}

function isNewDeviceType(type) {
    return agileDevicesTypeStore.includes(type);
}

function log ( message ) {
    console.log(message);
}
