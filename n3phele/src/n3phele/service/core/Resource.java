package n3phele.service.core;
/**
 * (C) Copyright 2010-2013. Nigel Cook. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * Licensed under the terms described in LICENSE file that accompanied this code, (the "License"); you may not use this file
 * except in compliance with the License. 
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on 
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 *  specific language governing permissions and limitations under the License.
 */

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import n3phele.service.model.core.GlobalSetting;

import com.googlecode.objectify.ObjectifyService;

public class Resource {
	private static Logger log = Logger.getLogger(Resource.class.getName());
	private static Resource resource = null;
	protected ResourceBundle bundle;
	protected Map<String, String> globalSettings = null;
	private Resource() {
		try {
			globalSettings = new HashMap<String, String>();
			bundle = ResourceBundle.getBundle("n3phele.resource.service",
				Locale.getDefault(), this.getClass().getClassLoader());
			for(String i : bundle.keySet()) {
				globalSettings.put(i, bundle.getString(i));
			}
		} catch (Exception e) {
		}
		try {
				globalSettings.putAll(getGlobalSettings());	
		} catch (Exception e) {
			log.log(Level.WARNING, "Issue getting global settings", e);
		}
	}
	public static String get(String key, String defaultValue) {
		String result = defaultValue;
		
		if(resource == null) {
			resource = new Resource();
		}
		try {
			if(resource.globalSettings != null && resource.globalSettings.containsKey(key))
				result = resource.globalSettings.get(key);
			else
				result = defaultValue;
		} catch (Exception e) {
			result = defaultValue;
		} finally {
			log.info("Resource query for "+key+" with default "+defaultValue+" returns "+result);
		}
		return result;
	}
	
	public static boolean get(String key, boolean defaultValue) {
		boolean result = defaultValue;
		log.info("Resource query for "+key+" with default "+defaultValue);
		
		if(resource == null) {
			resource = new Resource();
		}
		try {
			if(resource.globalSettings != null && resource.globalSettings.containsKey(key))
				result = Boolean.valueOf(resource.globalSettings.get(key));
			else
				result = defaultValue;
		} catch (Exception e) {
			result = defaultValue;
		} finally {
			log.info("Resource query for "+key+" with default "+defaultValue+" returns "+result);
		}
		return result;
	}
	
	public static Map<String,String> getResourceMap() {
		if(resource == null) {
			resource = new Resource();
		}
		return resource.globalSettings;
		
	}
	
	private final Map<String, String> getGlobalSettings() {
		Map<String, String> result = GlobalSetting.getGlobalSettings();
		if((result == null || result.isEmpty()) ) {
			GlobalSetting.init("created", Calendar.getInstance().getTime().toString(), "seed", Integer.toString((int)(Math.random()*100000)));
			result = GlobalSetting.getGlobalSettings();
		}
		return result;
	}
	
	static {
		log.info("Init GlobalSetting class");
		ObjectifyService.register(GlobalSetting.class);
	}
}
