# Agile to Watson interworking

## Requirements
* docker version 1.10.0 at least
* docker-compose version 1.10.0 at least
* Node.JS 6.4.1 at least
* JAVA 1.7 at least
* Apache Maven 3 at least

## clone the project
1. Clone the project from Github
* The project is tructured as follow
 * Agile: updated version of Agile IoT platform including a dummy device measuing pollution and temperature.
 * Watson-interworking: interworking proxy component that discover devices from Agile platform and pushing data to Watson IoT Platform
 * Watson-analytics: analytics module subscribing to data from Watson IoT platform and puishing it to Watson Machine Learning service to measure trends and calculate deviation. Deviation are stored back into Watson IoT Platform for triggering alerts when needed.

## Run Agile platform
1. Open the Agile platform project available on folder watson-interworking
2. Run the agile platform
```shell
cd agile/agile/agile-cli
./agile start
```
2. Check Agile web interface: http://127.0.0.1:3000/login

## Run Watson interworking
1. Open the Watson interworking project available on folder watson-interworking
1. Move to folder src
2. Install the Node required libraries
```shell
npm install request
npm install ws
npm install ibmiotf
```
* Configure the interworking using the file config.js

```js
const config = {
    agile: {
        host: '192.168.137.5',
        port: 8080,
        pollingPeriod: 15000,
        log: "[AGILE]:"
    },
    watson: {
        orga: 'thq3pl',
        domain : 'internetofthings.ibmcloud.com',
        application: {
            key: 'a-thq3pl-qt35bgz8ay',
            token: 'PUH4HwOir?MoB8BkvU',
            id: 'agile-watson-ipe'
        },
        log: "[WATSON]:"
    }
};
```

* Run the project
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

3. Configure the analytics using the file config.ini

```shell
# MQTT parameters
MQTT_TOPICS = iot-2/type/+/id/+/evt/event/fmt/+
MQTT_SERVER_URI = ssl://8riy9e.messaging.internetofthings.ibmcloud.com:8883

#Watson IoT Platform parameters
APP_ID = a:8riy9e:oneM2M123
API_KEY = a-8riy9e-e2ywsxpahe
AUTH_TOKEN =  vx)RY+4MW-gqeUwkC8

#Prediction parameters
PREDICTION_CYCLE = 10
ZSCORE_WINDOW = 10
PREDICTIVE_SERVICE_URL = https://ibm-watson-ml.eu-gb.bluemix.net/pm/v1/score/nocycle20rebuid50?accesskey=EzjIR1yqpeSLI1k8XXXO1x8hwYLRGn9Hb4/5XXgqYG5wWJAm8oHM3dFJPzSvZ0fKc1AbOE1UW5e5NZRAC6JLeJm4UhduKiR4fCfmGQLC1t8=
```

4. Start the analytics
Execute the following command from the folde onem2m-analytics to start the analytics
```shell
$ mvn exec:java 
```
