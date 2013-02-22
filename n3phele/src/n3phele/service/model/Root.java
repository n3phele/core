/**
 * @author Nigel Cook
 *
 * (C) Copyright 2010-2012. Nigel Cook. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * Licensed under the terms described in LICENSE file that accompanied this code, (the "License"); you may not use this file
 * except in compliance with the License. 
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on 
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 *  specific language governing permissions and limitations under the License.
 */
package n3phele.service.model;


import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.model.core.Entity;


@XmlRootElement
@XmlType(name="Root", propOrder={"stamp", "changeGroup", "changeCount", "cacheAvailable" })
@XmlSeeAlso(Entity.class)
public class Root {
	private long stamp;
	private ChangeGroup changeGroup;
	private int changeCount=0;
	private boolean cacheAvailable = false;

	
	public Root() {}
	
	/**
	 * @return the stamp
	 */
	public long getStamp() {
		return stamp;
	}



	/**
	 * @param stamp the stamp to set
	 */
	public void setStamp(long stamp) {
		this.stamp = stamp;
	}



	/**
	 * @return the changeGroup
	 */
	public ChangeGroup getChangeGroup() {
		return changeGroup;
	}


	/**
	 * @param changeGroup the changeGroup to set
	 */
	public void setChangeGroup(ChangeGroup changeGroup) {
		this.changeGroup = changeGroup;
	}




	/**
	 * @return the changeCount
	 */
	public int getChangeCount() {
		return changeCount;
	}

	/**
	 * @param changeCount the changeCount to set
	 */
	public void setChangeCount(int changeCount) {
		this.changeCount = changeCount;
	}

	/**
	 * @return the cacheAvailable
	 */
	public boolean isCacheAvailable() {
		return cacheAvailable;
	}

	/**
	 * @param cacheAvailable the cacheAvailable to set
	 */
	public void setCacheAvailable(boolean cacheAvailable) {
		this.cacheAvailable = cacheAvailable;
	}

	
	
}
