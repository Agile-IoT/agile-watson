<!--
# Copyright (C) 2017 Create-Net / FBK.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License 2.0
# which accompanies this distribution, and is available at
# https://www.eclipse.org/legal/epl-2.0/
# 
# SPDX-License-Identifier: EPL-2.0
# 
# Contributors:
#     Create-Net / FBK - initial API and implementation
-->

# AGILE-CLI: command line interface

This repository contains the AGILE command line interface, a small tool to simplify deployment, development, and maintenance of the AGILE stack from the command line.

AGILE-CLI is a thin layer on top of agile-stack, simplifying some of the most common operations. For more advanced usage, agile-stack can also be used directly using docker and docker-compose commands.

AGILE-CLI supports two different workflows:
- direct deployment on the AGILE Gateway
- managing the AGILE Gateway remotely

#Prerequisites

A platform for the Agile GW: Currently supported platforms are:

- RaspberryPi 2/3 running ResinOS
- RaspberryPi 2/3 running Raspbian with Docker installed
- Generic x86_64 running Linux of MacOS with Docker installed

If used in remotely managed mode, a platform to run the CLI with Docker, Docker-compose and bash installed.

## Installation

The CLI works on top of agile-stack, therefore both repos are required:
```
git clone https://github.com/Agile-IoT/agile-cli.git
git clone https://github.com/Agile-IoT/agile-stack.git
```

By default AGILE-CLI reads its configuration from `agile.config`, defining the path to agile-stack, the operation mode, and eventually the IP address of the AGILE GW. Configuration examples are available in `agile.config.examples/`.

```
cd agile-cli
cp agile.config.examples/agile.config.resinOS agile.config
```

For simplicity, the tool can self-install the `agile` command in the path with
```
sudo ./agile install
```

## initializing the GW

First of all, if not yet done, the GW should be initialized with `./agile init`

## Usage

Use `./agile start` to start the main components, and `./agile stop` to stop them.

Once components started you can visit http://127.0.0.1:8000 (or http://resin.local:8000 or http://agilegw.local:8000, depending on the use case) to access the AGILE user interface and start building your IoT solution.

## Update

Use `./agile update` to download the newest version of AGILE component. Note that this is different from `git pull`, which updates the startup scripts only.


### Updating a single component

In most cases, you can update a single coponent while other components keep running.
For example, to update agile-osjs without restarting the rest, use the following commands:
```
./agile compose stop agile-osjs
./agile compose pull agile-osjs
./agile compose up agile-osjs
```

## Troubleshooting

You can access the logs with `./agile compose logs` and view all logs.

To view per component log use `./agile compose logs <component>`

To get the list of running components use `./agile compose ps` with the pattern `compose_<component name>_1`
