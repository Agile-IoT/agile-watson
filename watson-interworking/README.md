# Interwork agile-iot with Watson IoT Platform

## Getting Started

### Requirements
* Node.JS 6.4.1 (at least) to run the project

### Clone the project if not done yet
#### Clone the project and go to the folder agile/agileiot-watson

```console
git clone http://52.5.56.30/iot/agile.git
cd agile/agileiot-watson
```

#### Install the node.js required libraries to run this project using the following commands

`request` library to send HTTP requests
```console
npm install request
```

`ws` library for web sockets
```console
npm install ws
```

`ibmiotf` ibm-watson-iot/iot-nodejs to communicate with watson IoT platform
```console
npm install ibmiotf
```
#### Run the project using the following commands
```console
cd src
node app.js
```