# Change Log

All notable changes to this project will be documented in this file
automatically by Versionist. DO NOT EDIT THIS FILE MANUALLY!
This project adheres to [Semantic Versioning](http://semver.org/).

## v0.4.2 - 2018-08-16

* agile-core 028afda...bddef66 (14):
  * v0.2.22
  * Merge pull request #86 from Agile-IoT/devicefactory
  * Added support for more device factories.
  * v0.2.21
  * Merge branch 'devicebuild'
  * DeviceFactory: fix link to dependencies
  * Merge pull request #99 from dpap/websocketBug
  * fix jetty websocket close bug
  * Merge pull request #95 from Agile-IoT/Exulansis-patch-1
  * Fix typo
  * Fix typo
  * Merge pull request #91 from gabiSRC/master
  * Add DALI device based on agile-oneM2M protocol
  * v0.2.20
  * fixing build
  * removing old license headers
  * v0.2.19
  * Merge pull request #79 from Agile-IoT/fix-device-write
  * v0.2.18
  * Merge pull request #84 from dpap/protocolManagerFix1
  * fix protocolManager add/remove protocols
  * Merge branch 'master' of https://github.com/Agile-IoT/agile-core
  * fix devicefactory plugin loading
  * Device write fixed.

* agile-data b5ca90d...f45bdb3 (4):
  * v0.2.1
  * Merge pull request #44 from Agile-IoT/fix-fetch-dependency
  * Fix missing isomorphic-fetch dependency
  * v0.2.0
  * Merge pull request #42 from Agile-IoT/feature-dropbox
  * Timeout increase on test to prevent travis failing
  * Add dropbox support to export data to cloud functionality

* agile-kura 0abd8a7...02507ff (1):
  * Merge pull request #9 from MMaiero/enh_rest-projects
  * First drop for the rest bundles.

* agile-recommender 8df05ea...a12e42c (2):
  * v0.3.8
  * Merge branch 'epl-cleanup'
  * removing eclipse project settings
  * removing useless files

* agile-security 4291266...fbd2ac5 (2):
  * v3.8.0
  * Oauth2 Password Grant (#18)

* gui/agile-nodered 62f9691...19ce068 (10):
  * v0.4.3
  * bumping version for PRISMACLOUD secure sharing
  * Merge pull request #23 for PRISMACLOUD secure sharing
  * v0.4.2
  * adding pudated thingspeak node
  * v0.4.1
  * adding AGILE device write node
  * v0.4.0
  * introducing multi-stage build
  * v0.3.3
  * update error handling
  * documentation of secret-sharing node
  * fix typo
  * add secret sharing node

* gui/agile-osjs 3df5a2d...4b3a912 (3):
  * v0.4.1
  * update some submodules to EPL 2.0
  < update some submodules to EPL 2.0

* gui/agile-ui 680977a...c449052 (3):
  * v2.5.2
  * Merge pull request #91 from Agile-IoT/fix-default-discovery
  * Merged branch master into fix-default-discovery
  * Small fix on category expansion logic
  * Merge pull request #90 from Agile-IoT/ui_testing
  * Adjust some style attributes
  * Improve adding locks by moving the view to locks overview and let edit it directly at the specific policy card instead of in an own view
  * Remove dots from ids
  * Fix UI bug, when deleting a group with same name as a group by another owner
  * Add owner and ids to groups and improve group members view
  * Add ids to device view
  * Sorted sensor data summary, unit fix
  * Work on resizeable graphs, encryption
  * Fix to early drawing of groups list in user view
  * Use checkboxes to assign entities to groups
  * Choose entity id or group name, depending on entity type for UI element id
  * Add ids
  * Add ids to entity items in list view
  * Add ids to attributes
  * Consistent rendering of user buttons
  * Add addLock info
  * Add key properties + add max-width for tooltips
  * Add ids entity and policy cards
  * Remove duplicate action.
  * Initial work on resizable graphs
  * Error message in case devices recommaendations, or discovery is not avaiable

* protocol/agile-ble ad0f35d...e1d0fcb (9):
  * v0.1.15
  * Merge branch 'register'
  * v0.1.14
  * Merge branch 'generic-device'
  * updating DBus Java interface  patch Change-type: Signed-off-by: Csaba Kiraly <csaba.kiraly@gmail.com>
  * Merge remote-tracking branch 'origin/generic-device'
  * v0.1.13
  * register self in ProtocolManager
  * switching to install Bluez from Debian stable repo
  * Merge pull request #11 from Agile-IoT/fix-bluez-install
  * Generic Device
  * switching to install Bluez from Debian stable repo
  * v0.1.12
  * removing old license headers

* protocol/agile-dummy 4601a3c...a41de39 (5):
  * v0.3.3
  * adding qdbus to the deplyed image
  * register self in ProtocolManager
  * v0.3.2
  * removing old lincese headers

* protocol/agile-makers-shield-software cd5a95a...3cc165b (5):
  * v0.1.1
  * upgrade pip to fix dependency installation issues
  * Dockerfile: use buildpack-deps image and install python
  * Dockerfile: add install prerequisites
  * fixing baseimage

## v0.4.1 - 2018-05-08

* Newer versions of Agile UI and Agile Data [Eugeniu Rusu]

## v0.4.0 - 2018-03-30

* Updating licenses to EPL 2.0 [Csaba Kiraly]
* Agile-security: adding database entity type [Csaba Kiraly]
* Agile-data: adding query support [Csaba Kiraly]
* Agile-ui: fixes to recommender UI, sersor units, discovery and cloud integraton [Csaba Kiraly]
* Adding SPDX license identifier [Csaba Kiraly]
* Agile-devicemanager: implement device peristance [Csaba Kiraly]
* Updating modules to EPL versions [Csaba Kiraly]

## v0.3.5 - 2018-02-08

* Agile-recommender: fixed connection to backend server [Csaba Kiraly]
* Agile-nodered: changed the Read node's output JSON [Csaba Kiraly]

## v0.3.4 - 2018-02-06

* Upgrading to EPL 2.0 [Csaba Kiraly]
* Agile-data: add encryption support [Csaba Kiraly]
* Agile-osjs: enable re-configure based on AGILE_HOST [Csaba Kiraly]
* Agile-security: update URLs based on AGIEL_HOST [Csaba Kiraly]
* Agile-recommender: use new cloud service endpoint [Csaba Kiraly]

## v0.3.3 - 2017-12-19

* WinOS instructions for device network discovery [dp]

## v0.3.2 - 2017-12-19

* Enable automated versioning by versionist [Csaba Kiraly]
* Agile-ui: add front-end proxy for all agile service [Csaba Kiraly]
* Automating versioning though travis and versionist [Csaba Kiraly]

## v0.3.1 - 2017-11-27

* Agile-ui: fix subscribe on graphs screen and discovery toggle [Csaba Kiraly]

## v0.3.0 - 2017-10-19

* Agile-ui: use SDK woth fixed unsubscribe [Csaba Kiraly]
* Agile-security: fixing group deletion and updating configuration [Csaba Kiraly]
* Add docker-compose override file example [Csaba Kiraly]
* Agile-ui: fix discovery toggle and add subscribe for graphs [Csaba Kiraly]
* Agile-recommender: update recommenderd and nodered integration [Csaba Kiraly]
* Agile-core: fix empty device list [Csaba Kiraly]
* Agile-dummy: use fixed dbus-java [Csaba Kiraly]
* Agile-ble: use fixed dbus-java [Csaba Kiraly]
* Agile-core: fix device deletion [Csaba Kiraly]
* Changing back name of agile-http to agile-core [Csaba Kiraly]
* Agile-recommender: use ZuluJDK [Csaba Kiraly]
* Agile-core: fix protocol status [Csaba Kiraly]
* Add instructions to .env.example [Csaba Kiraly]
* Do not use variable substitution in .env [Csaba Kiraly]
* Revert to resinos instructions + improve readme [craig-mulligan]
* Simplify env file [Csaba Kiraly]
* Update agile-ui to v0.5.0 [Csaba Kiraly]
* Added production env variable for agile-data. [Exulansis]
* Newer versions for OSjs and the Idm, hostnames for agile core and agile data. [Exulansis]
* Updating agile-nodered to v0.1.7 with recommender-node [Csaba Kiraly]
* Add agile-data [craig-mulligan]
* Adding recommender integration to Node-RED [Csaba Kiraly]
* Enabling recommender service [Csaba Kiraly]

## v0.2.1 - 2017-09-13

# Updating agile-core: stability fixes on dbus [Csaba Kiraly]

## v0.2.0 - 2017-07-21

# Adding agile-security [Csaba Kiraly]
# Updating agile-osjs: handle login [Csaba Kiraly]
# Updating agile-nodeded: handle login [Csaba Kiraly]
# Updating agile-ui: better device type handling [Csaba Kiraly]

## v0.1.5 - 2017-07-05

# Updating Java components to use ZuluJDK [Csaba Kiraly]

## v0.1.4 - 2017-07-05

# Updating component versions [Csaba Kiraly]

## v0.1.3 - 2017-05-08

* Init [Csaba Kiraly]