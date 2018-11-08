# Agile to Watson interworking

## Requirements
* To run Agile IoT Platform you need
   * docker version 1.10.0 at least
   * docker-compose version 1.10.0 at least
* To run Watson interworking you need
   * Node.JS 6.4.1 at least
* To run Watson Analytics you need
   * JAVA 1.7 at least
   * Apache Maven 3 at least

## Project structure
Clone the project from Github. 
* The project is tructured as follow:
    * Agile: updated version of Agile IoT platform including a dummy device measuing pollution and temperature.
    * Watson-interworking: interworking proxy component that discover devices from Agile platform and pushing data to Watson IoT Platform
    * Watson-analytics: analytics module subscribing to data from Watson IoT platform and puishing it to Watson Machine Learning service to measure trends and calculate deviation. Deviation are stored back into Watson IoT Platform for triggering alerts when needed.

## Run Agile IoT platform
1. Open the Agile platform project available on folder watson-interworking
2. Move to folder agile-cli
3. Run the agile platform
```shell
./agile start
```
4. Check Agile web interface: http://127.0.0.1:3000/login

## Run Watson interworking
1. Open the Watson interworking project available on folder watson-interworking
2. Move to folder src
3. Install the Node required libraries
```shell
npm install request
npm install ws
npm install ibmiotf
```
4 Configure the interworking using the file config.js

```js
const config = {
    agile: {
        host: '127.0.0.1',
        port: 8080,
        pollingPeriod: 15000,
        log: "[AGILE]:"
    },
    watson: {
        orga: 'XXXXXXXXX',
        domain : 'internetofthings.ibmcloud.com',
        application: {
            key: 'XXXXXXXXX',
            token: 'XXXXXXXXXX',
            id: 'agile-watson-ipe'
        },
        log: "[WATSON]:"
    }
};
```

5 Run the project
```shell
node app.js
```

## Run Watson Analytics 
1. Open the Watson interworking project available on folder watson-interworking
2. Go to folder src
3. Install the Node required libraries
```shell
npm install request
npm install ws
npm install ibmiotf
```
4 Run the project
```shell
node app.js
```

## Run Watson Analytics 
1. Open the Watson Analytics project available on folder watson-analytics
2. Build the project using the following command. The binary and config files are generated under the folder "target"
```sh
$ mvn clean install
```

3. Configure the project using the file config.ini

```shell
# MQTT parameters
MQTT_TOPICS = iot-2/type/+/id/+/evt/event/fmt/+
MQTT_SERVER_URI = ssl://XXXXXXXX.messaging.internetofthings.ibmcloud.com:8883

#Watson IoT Platform parameters
APP_ID = XXXXXXXXXXXXXX
API_KEY = XXXXXXXXXXXXXX
AUTH_TOKEN =  XXXXXXXXXXXXXX

#Prediction parameters
PREDICTION_CYCLE = 10
ZSCORE_WINDOW = 10
PREDICTIVE_SERVICE_URL = https://ibm-watson-ml.eu-gb.bluemix.net/pm/v1/score/nocycle20rebuid50?accesskey=XXXXXXXXXXXXXX
```

4. Run the project using the following command
```shell
$ mvn exec:java 
```
