package com.pa.fileupload.app.utils;

import java.util.HashMap;
import java.util.Map;

public class FileUploadAppProperties {
	
	private Map<String, String> properties = new HashMap<String, String>();
	
	private FileUploadAppProperties(){}
	
	private static FileUploadAppProperties instance = new FileUploadAppProperties();
	
	public static FileUploadAppProperties getInstance() {
		return instance;
	}
	
	public void addProperty(String propertyName, String propertyValue){
		properties.put(propertyName, propertyValue);
	}
	
	public String getProperty(String propertyName){
		return properties.get(propertyName);
	}
	
}
