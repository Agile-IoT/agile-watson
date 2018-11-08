# Agile platform
## Raspberry credential
* username: pi
* password: marou033
* ip: 192.168.0.5/24

## Prod location
```console
cd ~/documents/prod
```

## Docker and docker-compose based installation

### Requirements
* docker version 1.10.0 (or later) and docker-compose version 1.10.0 (or later)
* git

### Clone the project from sensinov gitlab
```console
git clone http://52.5.56.30/iot/agile.git
```


### Start agile
```console
cd agile/agile/agile-cli
./agile start
```

# Check web interface
Open the following address in your browser
* http://192.168.137.5:3000/login

## How to change the device type to update streams

### change this file 
path : agile/agile/agile-core/org.eclipse.agail DeviceFactory/src/main/java/org/eclipse/agail/device/instance/DummyDevice.java

### apply change by build the project using docker-compose
```console
cd agile/agile/agile-stack
docker-compose build
```

## How to change the protocol, for example change the read data method that send data of a device

### change this file 
path : /agile-sensi/org.eclipse.agail.protocol.DummyProtocol/src/main/java/org/eclipse/agail/protocol/dummy/DummyProtocol.java

### apply change by build the project using docker-compose
```console
cd agile/agile/agile-stack
docker-compose build
```
