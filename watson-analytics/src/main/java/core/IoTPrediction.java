/*****************************************************************************
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v2.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v20.html
*****************************************************************************/

package core;

import java.io.Serializable;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

@SuppressWarnings("serial")
public abstract class IoTPrediction implements Serializable {
	private String accessURL;
	
	public void setAccessURL(String accessURL) {
		this.accessURL = accessURL;
	}

	public String getAccessURL() {
		return accessURL;
	}
	
    public  IoTPrediction() {
    	
    }
    
    public static java.lang.String post(java.lang.String pURL, java.lang.String payload) throws ClientProtocolException, IOException {

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(pURL);
        StringEntity input = new StringEntity(payload);
        input.setContentType("application/json");
        post.setEntity(input);

        HttpResponse response = client.execute(post);

        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder out = new StringBuilder();
        java.lang.String line;
        while ((line = rd.readLine()) != null) {
            out.append(line);
        }
        
        System.out.println(out.toString()); 
        rd.close();
        return out.toString();
    }
    
    public abstract void appendDataSet(JSONObject obj) throws JSONException;
    public abstract String predict(String s) throws Exception;
    public abstract void load(int number) throws FileNotFoundException, JSONException, Exception;
    public abstract void load(int number, int size) throws FileNotFoundException, JSONException, Exception;

}

