package com.iotbox;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.Math;  

public class Util { 
	public static String getNowTime() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     
		String date = sDateFormat.format(new java.util.Date());
		return date;
	}
	
	public static int getCh(String M) {
    	String str = Integer.toString(Integer.parseInt(M, 16), 2);
    	if(str.length()<8) {
    		return 0;
    	}
    	
        int C = Integer.parseInt(str.substring(1, 4),2);
        int S = Integer.parseInt(str.substring(4, 8),2);
        double ADC = (16.5 * (((Math.pow(2, C)) - 1))) + (S * Math.pow(2 , C));
        return (int)Math.floor(ADC);
    }
    
	public static double convertTolit(String str) {  
		double lux = 0;
        int ch0 = getCh(str.substring(0, 2));
        int ch1 = getCh(str.substring(2, 4));
        if(ch0 != ch1) {
            lux = (ch0 - ch1) * 0.39 * Math.pow(Math.E, (-0.181 * (Math.pow((ch1 / (ch0 - ch1)), 2))));
        }
        
        return lux;
    }
    
	public static double convertTotemp(String str) { 
		String temData;
		double d1 = -39.7, d2 = 0.01;		 
		int SO = Integer.parseInt(str,16);
		String strBin=  Integer.toString(SO,2);		
		if (strBin.length() > 14) {
			temData = strBin.substring(0, 14);
		}
		else { 
			temData = strBin;
		}  
		
		return (d1 + d2 * (Integer.parseInt(temData, 2)));
	}
    
	public static double convertTohumi(String str) {
		double c1 = -2.0468, c2 = 0.0367, t1 = 0.01, t2 = 0.00008;
		double c3 = -1.5955E-6;
		int SO = Integer.parseInt("0"+str.substring (1,4), 16);
		return (c1 + c2 * SO + c3 * (SO ^ 2) + (-39.7 - 25) * (t1 + t2 * SO));
    } 
	
	public static boolean isNumeric(String str) { 
		Pattern pattern = Pattern.compile("[0-9]*");    
		Matcher isNum = pattern.matcher(str);   
		if(!isNum.matches()){ 
			return false;  
		}   
		return true;    
	}   
}
