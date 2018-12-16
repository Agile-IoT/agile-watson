/*****************************************************************************
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v2.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v20.html
*****************************************************************************/

package core;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.wink.json4j.JSON;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

@SuppressWarnings("serial")
public class IoTPredictionNonPeriodic extends IoTPrediction {
	
    protected static final DateFormat ISO8601_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private JSONObject data = null;
    private int cycle = 1;
    private int windowsize = 0;
    
    private JSONArray pa = new JSONArray();
	private int count = 0;
	private String pdt;

	private IoTZScore zscoreObj = null;
	
    IoTPredictionNonPeriodic() {
    	System.out.println("Creating new instance of IoTPredictionNonPeriodic");
    	zscoreObj = new IoTZScore();
    }
    
    public static boolean testScoring(String predictiveServiceURL) throws FileNotFoundException, JSONException, Exception {
    	IoTPredictionNonPeriodic p = new IoTPredictionNonPeriodic();
    	p.load(10, 10);
    	p.setAccessURL(predictiveServiceURL);
    	String ret = post(p.getAccessURL(), p.data.toString());
    	
    	if(ret.contains("Failed to score")) {
    		System.out.println("Looks like an issue with the URL, Please check the AccessKey and context id!!");
    		return false;
    	} else {
    		System.out.println("Connection to Predictive Analytics service is proper and able to invoke the service successfully");
    		return true;
    	}
    }

    public void load(int number) throws FileNotFoundException, JSONException, Exception {
        try {
	        InputStream dataStream = this.getClass().getResourceAsStream("/historicaldata.json");
	        data = new JSONObject(dataStream);
		    cycle = number;
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }


    public  void load(int number, int size) throws FileNotFoundException, JSONException, Exception {
        try {
        	InputStream dataStream = this.getClass().getResourceAsStream("/historicaldata.json");
	        data = new JSONObject(dataStream);
		    windowsize = size;
		    zscoreObj.setWindowSize(size);
		    cycle = number;
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }
        
    public void appendDataSet(JSONObject obj) throws JSONException {
    	double d = Double.parseDouble(obj.get("value").toString());

        try {
	        JSONArray ja = (JSONArray) data.get("data");
	        int snum = ja.length();
	        JSONArray na = new JSONArray();
	        na.put(d);
	        na.put(snum+1);
	        ja.put(na);
        } catch(Exception e) {
        }        
        return;
    	
    }

    public String predict(String s) throws Exception {

    	if(data == null) {
    		load(this.cycle, this.windowsize);
    	}
    	
       Double forecast = 0.0;
       JSONObject pdt = new JSONObject();
       
       if (s == null)
           return null;
      
       JSONObject obj = (JSONObject)JSON.parse(s);

       if(obj.has("d")) {
    	   obj = obj.getJSONObject("d");
       }

       if (obj.has("id")&&obj.get("id") != null && 
           obj.has("value") && obj.get("value") != null )  {
           appendDataSet(obj);
           
           if (this.count == 1) {
        	   
	           String ret = post(this.getAccessURL(), data.toString());
	           if (ret.length() > 0 && ret.charAt(0) == '[' && ret.charAt(ret.length()-1) == ']') {
	               ret = ret.substring(1, ret.length()-1);
	           }
	           JSONObject prediction = new JSONObject(ret);	
	           
	           if(prediction.has("data")){
	        	   pa = (JSONArray) prediction.get("data");
	           }else{
	        	   System.out.println("Wrong prediction output");
	           }
	         
	
	           if (pa.length() < cycle) {
	               System.out.println("Wrong prediction output with " + pa.length() + " entries, while prediction cycle is " + cycle);
	               throw new JSONException("Wrong prediction output with " + pa.length() + " entries, while prediction cycle is " + cycle);
	          } 
          }
           
          if (pa.isNull(this.count-1) == true) {
        	  System.out.println("the JSONArray is empty with index " + (this.count-1));
        	  
          } else {
	          JSONArray pj = pa.getJSONArray(this.count-1);
	          String pstr = pj.toString();
	          System.out.println("the string is *" + pstr + "*"); 
	          String[] parts = pstr.split(",");
	          if (parts.length < 7) {
	              forecast = 0.0;
	          } else {
	              forecast = Double.parseDouble(parts[6]);
	          }
	
	          double current = Double.parseDouble(obj.get("value").toString());
              Double zscore = zscoreObj.zScore(current-forecast);
             
              pdt = new JSONObject(obj);
              pdt.put("forecast", forecast);
              if(zscore == null || zscore.isNaN() || zscore == 0) {
            	  pdt.put("zscore", 0.000000000000001);
              } else {
        		  if(!zscore.toString().contains(".")) {
        			  zscore = Double.parseDouble(zscore + ".000000000000001");
        		  }
            	  pdt.put("zscore", zscore.doubleValue());
              }
              
              if (windowsize > 0) {
            	  Double wzscore = zscoreObj.windowZScore(current-forecast);
            	  if(wzscore == null || wzscore.isNaN() || wzscore == 0) {

            		  pdt.put("wzscore", 0.000000000000001);
            	  } else {
            		  if(!wzscore.toString().contains(".")) {
            			  wzscore = Double.parseDouble(wzscore + ".000000000000001");
            		  }
            		  pdt.put("wzscore", wzscore.doubleValue());
            	  }
              } 
              System.out.println("[" + this.count +"] JSON to RTI:" + pdt.toString());
          }
          
        
          this.count++;
          
          if (this.count > cycle) {
        	  this.count = this.count-cycle;
          }
          this.pdt = pdt.toString();
          return pdt.toString();
          
       } 

       return null;
    }

    public String toString() {
    	return this.pdt;
    }  

}

