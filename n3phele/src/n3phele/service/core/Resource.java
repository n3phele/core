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

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * FIXME Warning .. prototype version
 */
public class Resource {
	private static Logger log = Logger.getLogger(Resource.class.getName());
	private static Resource resource = null;
	protected ResourceBundle bundle;
	protected Map<String, String> globalSettings = null;
	private Resource() {
		try {
			bundle =
				ResourceBundle.getBundle("n3phele.resource.service",
				Locale.getDefault(), this.getClass().getClassLoader());	
		} catch (Exception e) {
		}
		try {
				globalSettings = getGlobalSettings();	
		} catch (Exception e) {
			log.log(Level.WARNING, "Issue getting global settings", e);
		}
	}
	public static String get(String key, String defaultValue) {
		String result = defaultValue;
		log.info("Resource query for "+key+" with default "+defaultValue);
		if(resource == null) {
			resource = new Resource();
		}
		try {
			if(resource.globalSettings != null && resource.globalSettings.containsKey(key))
				result = resource.globalSettings.get(key);
			else
				result = resource.bundle.getString(key);
		} catch (Exception e) {
			result = defaultValue;
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
			String field;
			if(resource.globalSettings != null && resource.globalSettings.containsKey(key))
				field = resource.globalSettings.get(key);
			else
				field = resource.bundle.getString(key);
			result = Boolean.valueOf(field);
		} catch (Exception e) {
			result = defaultValue;
		}
		return result;
	}
	
	private final Map<String, String> getGlobalSettings() {
		Map<String, String> result = null;
		return result;
	}
	
}
