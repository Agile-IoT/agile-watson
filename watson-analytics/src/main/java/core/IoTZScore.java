/*****************************************************************************
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v2.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v20.html
*****************************************************************************/

package core;

import java.io.Serializable;
import java.util.ArrayList;
import java.lang.IndexOutOfBoundsException;
import org.apache.commons.math.MathException;
import org.apache.commons.math.special.Erf;
import org.apache.commons.math3.distribution.*;

@SuppressWarnings("serial")
public class IoTZScore implements Serializable{
	private ArrayList<Double> entries = new ArrayList<Double>();
    private double mu = 0.0; 
    private double temp = 0.0; 
    
    private ArrayList<Double> wentries = new ArrayList<Double>();
    private double wmu = 0.0; 
    private double wtemp = 0.0; 
    private int wsize = 0;
    

    public void setWindowSize(int size) {
    	wsize = size;
    	System.out.println("window size is " + wsize);
    }
    
    public void addEntry(double e) {
    	entries.add(e);
    }
    
    public void updateZScoreMeans(Double a) {
		entries.add(a);
		double delta = a - mu;
		mu += delta / entries.size();
		
		if (entries.size() > 0) {
			temp += delta * (a - mu);
		}
		
	}
    

    public void updateWindowZScoreMeans(Double a) {
    	
    	try {
	    	if (wentries.size() >= wsize ) {
	    		for (int i = 0; i < wsize -1; i++) {
	    			wentries.set(i, wentries.get(i+1));
	    		}
	    		wentries.set(wsize -1,  a);
	    	} else {
	    		wentries.add(a);
	    	}
    	}catch(IndexOutOfBoundsException e) {
    		e.printStackTrace();
    	}
    	
    	wmu = 0;
    	wtemp = 0;

    	for (int j = 0; j < wentries.size(); j++) {
    		double delta = wentries.get(j) - wmu;
    		wmu += delta / wentries.size();
    		if (wentries.size() > 0) {
    			wtemp += delta * (wentries.get(j) - wmu);
    		}
    	}
		
	}
    
	public double zScore(Double x) {
	
		double sigma = Math.sqrt(temp / (entries.size()));
		if (sigma == 0) {
			sigma = 1;
		}

	
		double z = (x - mu)/sigma;
		updateZScoreMeans(x);
		return z;
	}
	
	public double windowZScore(Double x) {
		if (wentries.size() < wsize) {
			updateWindowZScoreMeans(x);
			return 0.0;
		}
		double sigma = Math.sqrt(wtemp / wentries.size());
		if (sigma == 0) {
			sigma = 1;
		}

		double z = (x - wmu)/sigma;
		updateWindowZScoreMeans(x);
		return z;
	}

    public double calculatePvalue(double aZValue) {
        aZValue = aZValue / Math.sqrt(2.0);
        double lPvalue = 0.0;
        try {
            lPvalue = Erf.erf(aZValue);
        } catch (MathException e) {
            e.printStackTrace();
        }
        return lPvalue;
    }

    public double zScoreToPercentile(double zScore)
	{
		double percentile = 0;
		
		NormalDistribution dist = new NormalDistribution();
		percentile = dist.cumulativeProbability(zScore) * 100;
		return percentile;
	}
    
}
