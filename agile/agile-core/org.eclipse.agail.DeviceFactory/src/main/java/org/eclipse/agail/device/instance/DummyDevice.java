/*******************************************************************************
 * Copyright (C) 2017 Create-Net / FBK.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Create-Net / FBK - initial API and implementation
 ******************************************************************************/
package org.eclipse.agail.device.instance;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.agail.device.base.SensorUuid;
import org.eclipse.agail.Device;
import org.eclipse.agail.Protocol;
import org.eclipse.agail.device.base.DeviceImp;
import org.eclipse.agail.exception.AgileNoResultException;
import org.eclipse.agail.object.DeviceComponent;
import org.eclipse.agail.object.DeviceOverview;
import org.eclipse.agail.object.DeviceStatusType;
import org.eclipse.agail.object.StatusType;
import java.util.List;

public class DummyDevice extends DeviceImp implements Device {
  protected Logger logger = LoggerFactory.getLogger(DummyDevice.class);

  public static final String deviceTypeName = "Dummy";

  public static final Map<String, SensorUuid> sensors = new HashMap<String, SensorUuid>();

  public static final String GATT_SERVICE = "GATT_SERVICE";
  public static final String GATT_CHARACTERSTICS = "GATT_CHARACTERSTICS";
  /**
   * DUMMY Protocol imp DBus interface id
   */
  private static final String DUMMY_PROTOCOL_ID = "org.eclipse.agail.protocol.Dummy";
  /**
   * DUMMY Protocol imp DBus interface path
   */
  private static final String DUMMY_PROTOCOL_PATH = "/org/eclipse/agail/protocol/Dummy";

  private static final String DUMMY_COMPONENT = "DummyDataTest";
  private static final String TEMP_COMPONENT = "Temperature";
  private static final String CO2_COMPONENT = "CO2";

  private DeviceStatusType deviceStatus = DeviceStatusType.DISCONNECTED;

  {
    profile.add(new DeviceComponent(TEMP_COMPONENT, "Degree Celsius (°C)"));
    profile.add(new DeviceComponent(CO2_COMPONENT, "ppm"));
  }

  {
    subscribedComponents.put(TEMP_COMPONENT,0);
    subscribedComponents.put(CO2_COMPONENT,0);
  }

  static {
	sensors.put(TEMP_COMPONENT,
	new SensorUuid("f000aa00-0451-4000-b000-000000000000", "f000aa01-0451-4000-b000-000000000000",
			"f000aa02-0451-4000-b000-000000000000", "f000aa03-0451-4000-b000-000000000000"));
	sensors.put(CO2_COMPONENT,
	new SensorUuid("f000aa20-0451-4000-b000-000000000000", "f000aa21-0451-4000-b000-000000000000",
	"f000aa22-0451-4000-b000-000000000000", "f000aa23-0451-4000-b000-000000000000"));
  }

  public DummyDevice(DeviceOverview deviceOverview) throws DBusException {
    super(deviceOverview);
    this.protocol = DUMMY_PROTOCOL_ID;
    String devicePath = AGILE_DEVICE_BASE_BUS_PATH + "dummy" + deviceOverview.id.replace(":", "");
    dbusConnect(deviceAgileID, devicePath, this);
    deviceProtocol = (Protocol) connection.getRemoteObject(DUMMY_PROTOCOL_ID, DUMMY_PROTOCOL_PATH, Protocol.class);
    logger.debug("Exposed device {} {}", deviceAgileID, devicePath);
  }

  private Map<String, String> getReadValueProfile(String sensorName) {
	Map<String, String> profile = new HashMap<String, String>();
	SensorUuid s = sensors.get(sensorName);
	if (s != null) {
		profile.put(GATT_SERVICE, s.serviceUuid);
		profile.put(GATT_CHARACTERSTICS, s.charValueUuid);
	}
	return profile;
  }

  public static boolean Matches(DeviceOverview d) {
    return d.name.contains(deviceTypeName);
  }

  @Override
  protected String DeviceRead(String componentName) {
    if ((protocol.equals(DUMMY_PROTOCOL_ID)) && (deviceProtocol != null)) {
      if (isConnected()) {
        if (isSensorSupported(componentName.trim())) {
          try {
            byte[] readData = deviceProtocol.Read(address, getReadValueProfile(componentName));
            return formatReading(componentName, readData);
          } catch (DBusException e) {
            e.printStackTrace();
          }
        } else {
          throw new AgileNoResultException("Componet not supported:" + componentName);
        }
      } else {
        throw new AgileNoResultException("Device not connected: " + deviceName);
      }
    } else {
      throw new AgileNoResultException("Protocol not supported: " + protocol);
    }
    throw new AgileNoResultException("Unable to read " + componentName);
  }

  @Override
  public void Subscribe(String componentName) {
    if ((protocol.equals(DUMMY_PROTOCOL_ID)) && (deviceProtocol != null)) {
      if (isConnected()) {
        if (isSensorSupported(componentName.trim())) {
          try {
            if (!hasOtherActiveSubscription()) {
              addNewRecordSignalHandler();
            }
            if (!hasOtherActiveSubscription(componentName)) {
              deviceProtocol.Subscribe(address, getReadValueProfile(componentName));
            }
            subscribedComponents.put(componentName, subscribedComponents.get(componentName) + 1);
          } catch (Exception e) {
            e.printStackTrace();
          }
        } else {
          throw new AgileNoResultException("Component not supported:" + componentName);
        }
      } else {
        throw new AgileNoResultException("Device not connected: " + deviceName);
      }
    } else {
      throw new AgileNoResultException("Protocol not supported: " + protocol);
    }
    }

  @Override
  public synchronized void Unsubscribe(String componentName) throws DBusException {
    if ((protocol.equals(DUMMY_PROTOCOL_ID)) && (deviceProtocol != null)) {
      if (isConnected()) {
        if (isSensorSupported(componentName.trim())) {
          try {
            subscribedComponents.put(componentName, subscribedComponents.get(componentName) - 1);
            if (!hasOtherActiveSubscription(componentName)) {
              deviceProtocol.Unsubscribe(address, getReadValueProfile(componentName));
             }
            if (!hasOtherActiveSubscription()) {
              removeNewRecordSignalHandler();
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        } else {
          throw new AgileNoResultException("Component not supported:" + componentName);
        }
      } else {
        throw new AgileNoResultException("Device not connected: " + deviceName);
      }
    } else {
      throw new AgileNoResultException("Protocol not supported: " + protocol);
    }
   }

  @Override
  public void Connect() throws DBusException {
    deviceStatus = DeviceStatusType.CONNECTED;
    logger.info("Device connected {}", deviceID);
  }

  @Override
  public void Disconnect() throws DBusException {
    deviceStatus = DeviceStatusType.DISCONNECTED;
    logger.info("Device disconnected {}", deviceID);
  }

  @Override
  public StatusType Status() {
    return new StatusType(deviceStatus.toString());
  }
  
//  @Override
//  public void Execute(String command, Map<String, Variant> args) {
//    if(command.equalsIgnoreCase(DeviceStatusType.ON.toString())){
//      deviceStatus = DeviceStatusType.ON;
//    }else if(command.equalsIgnoreCase(DeviceStatusType.OFF.toString())){
//      deviceStatus = DeviceStatusType.OFF;
//    }
//  }
//  
  protected boolean isConnected() {
    if (Status().getStatus().equals(DeviceStatusType.CONNECTED.toString()) || Status().getStatus().equals(DeviceStatusType.ON.toString())) {
      return true;
    }
    return false;
  }
  
  @Override
  protected boolean isSensorSupported(String sensorName) {
   //  boolean exists = DUMMY_COMPONENT.equals(sensorName) || TEMP_COMPONENT.equals(sensorName) || CO2_COMPONENT.equals(sensorName) ;
    return  true ;
  }
  
  @Override
  protected String formatReading(String sensorName, byte[] readData) {
     int result = (readData[0] & 0xFF); 
     return String.valueOf(result);
  }
  
  @Override
  protected String getComponentName(Map<String, String> profile) {
      String serviceUUID = profile.get(GATT_SERVICE);
      String charValueUuid = profile.get(GATT_CHARACTERSTICS);
	for (Entry<String, SensorUuid> su : sensors.entrySet()) {
		if (su.getValue().serviceUuid.equals(serviceUUID) && su.getValue().charValueUuid.equals(charValueUuid)) {
			return su.getKey();
		}
	}
	return null;
  }
  
  @Override
  public void Write(String componentName, String payload) {
	  if ((protocol.equals(DUMMY_PROTOCOL_ID)) && (deviceProtocol != null)) {
		  if (isConnected()) {
				if (isSensorSupported(componentName.trim())) {
					try {
						logger.debug("Device Write: Time to step into the the moon's atmosphere without mask");
						if(payload.equals("0")) {
							deviceProtocol.Unsubscribe(address, getReadValueProfile(componentName));
						} else {
							deviceProtocol.Subscribe(address, getReadValueProfile(componentName));
						}
					} catch (Exception ex) {
						logger.error("Exception occured in Write: " + ex);
					}
				} else {
			        throw new AgileNoResultException("Componet not supported:" + componentName);
			    }
			} else {
				throw new AgileNoResultException("Dummy Device not connected: " + deviceName);
			}
		} else {
			throw new AgileNoResultException("Protocol not supported: " + protocol);
		}
	}

  @Override
  public void Execute(String command) {
    logger.debug("Device. Execute not implemented");
	}

  @Override
  public List<String> Commands(){
    logger.debug("Device. Commands not implemented");
    return null;
      }
}
