package n3phele.service.model.core;
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
 *
 */
import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Unindex;


@Unindex
@Entity
public class GlobalSetting {
	@Id private Long id;
	@Index private String name; // these are indexed so they show in the GAE console datastore viewer create tab
	@Index private String value;
	
	public GlobalSetting() {}

	public GlobalSetting(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return this.id;
	}



	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}



	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("GlobalSetting [name=%s, value=\"%s\"]", this.name,
				this.value);
	};
	
	final private static Map<String, String> globalSettings = new HashMap<String,String>();
	public static Map<String, String> getGlobalSettings() {
		List<GlobalSetting> all = ofy().load().type(GlobalSetting.class).list();
		for(GlobalSetting g : all) {
			globalSettings.put(g.getName(), g.getValue());
		}
		return globalSettings;
	}
	
	public static boolean init(String ... nameValue) {
		int count = ofy().load().type(GlobalSetting.class).count();
		if(count == 0) {
			for(int i=0; i < nameValue.length; i += 2) {
				String name = nameValue[i];
				String value = nameValue[i+1];
				GlobalSetting g = new GlobalSetting(name, value);
				ofy().save().entity(g).now();
			}
			
		}
		return count == 0;
	}
}
