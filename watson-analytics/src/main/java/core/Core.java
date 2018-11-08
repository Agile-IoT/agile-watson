/*****************************************************************************
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html
*****************************************************************************/

package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import scala.Tuple2;
import mqtt2.SimpleClient;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.*;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.mqtt.MQTTUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.google.common.base.Optional;
import com.typesafe.config.ConfigFactory;

import config.Parameters;

@SuppressWarnings("serial")
public class Core implements Serializable {
	  private final static String PROPERTIES_FILE_NAME = "config.ini";
	  static Logger logger = Logger.getLogger(Core.class.getName());
	  
	  private String serverURI;
	  private String clientId;
	  private String apiKey;
	  private String authToken;
	  private String predictiveServiceURL;
	  
	  private int zScoreWindow = 0;
	  
	  private int wZScoreWindow = 0;
	  
	  private static class IoTEvent implements Serializable {
			private String deviceId;
			private String deviceType;
			private String event;
			
			public IoTEvent(String deviceType, String deviceId, String event) {
				super();
				this.deviceId = deviceId;
				this.deviceType = deviceType;
				this.event = event;
			}
	
			public String getDeviceId() {
				return deviceId;
			}
	
			public String getDeviceType() {
				return deviceType;
			}
	
			public String getEvent() {
				return event;
			}

			@Override
			public String toString() {
				return "IoTEvent [deviceId=" + deviceId + ", deviceType="
						+ deviceType + ", event=" + event + "]";
			}
			
	  }

	  private static class State implements Serializable {
		  private IoTPrediction prediction;
		
		  private State(int zScoreWindow, int wZSsoreWindow, String accessKey) {
				prediction = new IoTPredictionNonPeriodic();
		        prediction.setAccessURL(accessKey);
		        try {
					prediction.load(zScoreWindow, wZSsoreWindow);
				} catch (Exception e) {
					e.printStackTrace();
				}  
		  }
	
		  public IoTPrediction getPrediction() {
				return prediction;
		  }
	
		  @Override
		  public String toString() {
				return "State [prediction=" + prediction.toString() + "]";
		  }
		
	}

	private Function2<List<IoTEvent>, Optional<State>, Optional<State>> ENTRY_EXTRACTOR =
			new Function2<List<IoTEvent>, Optional<State>, Optional<State>>() {

		@Override
		public Optional<State> call(List<IoTEvent> readings, Optional<State> predictionState)
				throws Exception {

			if(readings == null || readings.size() == 0) {
				return predictionState;
			}
			
			State state = predictionState.orNull();
			try {
				if(state == null) {
					state = predictionState.or(new State(zScoreWindow, wZScoreWindow, predictiveServiceURL));
				}
				
				for(IoTEvent event : readings) {
					String forecast = state.getPrediction().predict(event.getEvent());
					if (forecast != null && !forecast.equals("{}")) { 
						int qos = 0;
						String publishTopic = "iot-2/type/"+ event.getDeviceType() + "/id/" + event.getDeviceId() + "/evt/result/fmt/json";
						SimpleClient client = SimpleClient.getSimpleClient();
						client.connect(serverURI, clientId, apiKey, authToken);
						client.publish(publishTopic, qos,forecast.getBytes());
				
				    }
				}
			} catch (Throwable e) {
				e.printStackTrace(); 
			}
			
			return Optional.of(state);
		}
	     
	};
	    
	private Core() {

    }
  
  	public void runPrediction(String mqtopic, String brokerUrl, String appID, 
  			String apiKey, String authToken, SparkContext sc, int interval) throws Throwable {

  		this.serverURI = brokerUrl;
  		this.apiKey = apiKey;
  		this.authToken = authToken;
  		this.clientId = appID;
  		
  		SimpleClient client = SimpleClient.getSimpleClient();

  		System.out.println("Testing the connectivity to Watson IoT Platform ... ");
		client.connect(serverURI, clientId, apiKey, authToken);

		System.out.println("Able to connect to Watson IoT Platform successfully !!");
		
		System.out.println("Testing the connectivity to Predictive Analytics service ... ");
		if(IoTPredictionNonPeriodic.testScoring(this.predictiveServiceURL) == false) {
			return;
		}
		  
	    Logger.getLogger("org").setLevel(Level.OFF);
	    Logger.getLogger("akka").setLevel(Level.OFF);
	
	    
	    if(sc == null) {
		    SparkConf sparkConf = new SparkConf().setAppName("Core").setMaster("local[*]");
		    sc = new SparkContext(sparkConf);
	    }
	    
	    JavaSparkContext jsc = JavaSparkContext.fromSparkContext(sc);
	
	    System.out.println("+++ print out the received data now:");
	    JavaStreamingContext jssc = new JavaStreamingContext(jsc, Durations.seconds(interval));
	    
	    jssc.checkpoint(".");
	    
	    JavaReceiverInputDStream<String> messages = MQTTUtils.createStream(jssc, brokerUrl, mqtopic, appID, apiKey, authToken);
	    
	    JavaPairDStream<String, IoTEvent> mappedStream = messages.mapToPair(
	            new PairFunction<String, String, IoTEvent>() {
	
	            public Tuple2<String, IoTEvent> call(String payload) {

	            	String[] parts = payload.split(" " , 2);
	            	String deviceType = parts[0].split("/")[2];	
					String deviceId = parts[0].split("/")[4];
		        	return new Tuple2(deviceId, new IoTEvent(deviceType, deviceId, parts[1]));
	              }
	     });
	    
	    JavaPairDStream<String, State> updatedLines =  mappedStream.updateStateByKey(ENTRY_EXTRACTOR);
	   
	    updatedLines.print();
	
	    jssc.start();
	    jssc.awaitTermination();
	  }
  
   public static void main(String[] args) {
	   Properties props = new Properties();
		try {
			props.load(new FileInputStream(PROPERTIES_FILE_NAME));
		} catch (IOException e1) {
			System.err.println("Not able to read the properties file, exiting..");
			System.exit(1);
		}
		new Parameters(props);

       try {
    	 
           System.out.println("MQTT subscribe topics:" + Parameters.mqttTopics);
           System.out.println("MQTT uri:" + Parameters.watsonUri);
     	   System.out.println("MQTT appid:" + Parameters.watsonAppID);
     	  System.out.println("MQTT apikey:" + Parameters.watsonApiKey);
     	  System.out.println("MQTT authtoken:" + Parameters.watsonToken);
				
           Core sample = new Core();
           
           sample.zScoreWindow = Parameters.predictionCycle;
           sample.wZScoreWindow = Parameters.zscoreWindow;
           sample.predictiveServiceURL = Parameters.predictiveServiceUrl;
           sample.runPrediction(Parameters.mqttTopics, Parameters.watsonUri, Parameters.watsonAppID, Parameters.watsonApiKey, Parameters.watsonToken, null, 4);
          
			
       } catch (FileNotFoundException fe) {
           fe.printStackTrace(System.err);
       } catch (MqttException e) {
           e.printStackTrace(System.err);
       } catch (Exception e) {
           e.printStackTrace(System.err); 
       } catch (Throwable te) {
           te.printStackTrace(System.err);
       }
   }

}
