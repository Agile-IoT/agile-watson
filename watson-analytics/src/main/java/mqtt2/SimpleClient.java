/*****************************************************************************
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v2.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v20.html
*****************************************************************************/

package mqtt2;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Random;

import javax.net.ssl.SSLContext;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;

public class SimpleClient implements MqttCallback {

	Random random = new Random();
	
	private static SimpleClient instance = new SimpleClient();
	
	public static SimpleClient getSimpleClient() {
		return instance;
	}
	
	private int state = 7;

	private static final int BEGIN = 0;
	private static final int CONNECTED = 1;
	private static final int PUBLISHED = 2;
	private static final int ERROR = 6;

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private MqttAsyncClient mqtt;
    private MqttConnectOptions   conOpt;
	private Throwable ex = null;
	private Object waiter = new Object();
	private boolean	donext = false;

	private SimpleClient() {
	
	}
	
	public void disconnect() {
		try {
			this.mqtt.disconnect().waitForCompletion();
		} catch(Exception e) { 
		}
	}
	
	public void connect(String serverURI, String clientId, String userName, String password) throws MqttException {
		
		if(state <= PUBLISHED) {
			return;
		}
		
		System.out.println("serverURI:" + serverURI);

		clientId = clientId + random.nextInt(100);
		System.out.println("clientId:" + clientId);
                
		try {
			conOpt = new MqttConnectOptions();
			conOpt.setCleanSession(true);
			conOpt.setPassword(password.toCharArray());
			conOpt.setUserName(userName);
			
			SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
			sslContext.init(null, null, null);
			conOpt.setSocketFactory(sslContext.getSocketFactory());
		

			mqtt = new MqttAsyncClient(serverURI, clientId);
			mqtt.setCallback(this);
			IMqttActionListener conListener = new IMqttActionListener() {
				public void onSuccess(IMqttToken asyncActionToken) {
					logger.info("Connected");
					state = CONNECTED;
					System.out.println("MQTT Client is connected to the server");
					carryOn();
				}

				public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
					ex = exception;
					state = ERROR;
					logger.error("connect failed" +exception);
					System.out.println("MQTT Client failed to connected to the server");
		            carryOn();
				}

				public void carryOn() {
					synchronized (waiter) {
						donext=true;
						waiter.notifyAll();
					}
				}
	        };
	        
	    	IMqttToken token = mqtt.connect(conOpt,"Connect sample context", conListener);

	    	token.waitForCompletion();
	    	logger.info("Connected to MQTT and Kafka"); 
	    	System.out.println("Connected to MQTT server and Kafka");
		} catch (MqttException | NoSuchAlgorithmException | KeyManagementException e) {
			e.printStackTrace();
		}

	}

	private void reconnect() throws MqttException {
		IMqttToken token = mqtt.connect();
		token.waitForCompletion();
	}
	
	void subscribe(String[] mqttTopicFilters) throws MqttException {
		int[] qos = new int[mqttTopicFilters.length];
		for (int i = 0; i < qos.length; ++i) {
			qos[i] = 0;
		}
		mqtt.subscribe(mqttTopicFilters, qos);
	}

	@Override
	public void connectionLost(Throwable cause) {
		logger.warn("Lost connection to MQTT server", cause);
		while (true) {
			try {
				logger.info("Attempting to reconnect to MQTT server");
				reconnect();
				logger.info("Reconnected to MQTT server, resuming");
				return;
			} catch (MqttException e) {
				logger.warn("Reconnect failed, retrying in 10 seconds", e);
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {	
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {

                byte[] payload = message.getPayload();

                String messageStr = new String(payload);

                long startTime = System.currentTimeMillis();
                String time = new Timestamp(startTime).toString();
		System.out.println("Time:\t" +time +
                           "  Topic:\t" + topic +
                           "  Message:\t" + new String(message.getPayload()) +
                           "  QoS:\t" + message.getQos());
	}

    public void publish(String topicName, int qos, byte[] payload)  {
        Publisher pub = new Publisher();
        try{
        	pub.doPublish(topicName, qos, payload);
        }catch(Exception e){
        	e.printStackTrace();
        }
    }
    
    private void waitForStateChange(int maxTTW ) throws MqttException {
        synchronized (waiter) {
            if (!donext ) {
                try {
                    waiter.wait(maxTTW);
                } catch (InterruptedException e) {
                    logger.warn("timed out");
                    e.printStackTrace();
                }

                if (ex != null) {
                    throw (MqttException)ex;
                }
            }
            donext = false;
        }
    }

    public class Publisher {
        public void doPublish(String topicName, int qos, byte[] payload) {
            MqttMessage message = new MqttMessage(payload);
            message.setQos(qos);


            String time = new Timestamp(System.currentTimeMillis()).toString();
            logger.info("Publishing at: "+time+ " to topic \""+topicName+"\" qos "+qos);

            IMqttActionListener pubListener = new IMqttActionListener() {
                public void onSuccess(IMqttToken asyncActionToken) {
                    logger.info("Publish Completed");
                    state = PUBLISHED;
                    carryOn();
                }

                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    ex = exception;
                    state = ERROR;
                    logger.info("Publish failed" +exception);
                    carryOn();
                }

                public void carryOn() {
                    synchronized (waiter) {
                        donext=true;
                        waiter.notifyAll();
                    }
                }
            };

            try {
                mqtt.publish(topicName, message, "Pub predictive data", pubListener);
            } catch (MqttException e) {
            	e.printStackTrace();
                state = ERROR;
                donext = true;
                ex = e;
            }
        }
    }

}


