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

import java.net.URI;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="Origin")
@XmlType(name="Origin", propOrder={"activity", "total", "activityName", 
		"activityModification", "activitySize"})
public class Origin {
	private String activity;
	private int total;
	private String activityName;
	private Date activityModification;
	private long activitySize;
	
	
	public Origin() {
	}
	
	
	
	/**
	 * @param activity
	 * @param total
	 * @param activityName
	 * @param commandName
	 * @param activityModification
	 * @param activitySize
	 * @param activtyCompleted
	 */
	public Origin(URI activity, int total, String activityName,
			Date activityModification, long activitySize) {
		super();
		setActivity(activity);
		this.total = total;
		this.activityName = activityName;
		this.activityModification = activityModification;
		this.activitySize = activitySize;
	}



	/**
	 * @return the activity
	 */
	public URI getActivity() {
		return this.activity==null?null:URI.create(this.activity);
	}
	/**
	 * @param activity the activity to set
	 */
	public void setActivity(URI activity) {
		this.activity = activity==null?null:activity.toString();
	}
	/**
	 * @return the total
	 */
	public int getTotal() {
		return this.total;
	}
	/**
	 * @param total the total to set
	 */
	public void setTotal(int total) {
		this.total = total;
	}
	/**
	 * @return the activityName
	 */
	public String getActivityName() {
		return this.activityName;
	}
	/**
	 * @param activityName the activityName to set
	 */
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	/**
	 * @return the activityModification
	 */
	public Date getActivityModification() {
		return this.activityModification;
	}
	/**
	 * @param activityModification the activityModification to set
	 */
	public void setActivityModification(Date activityModification) {
		this.activityModification = activityModification;
	}
	/**
	 * @return the activitySize
	 */
	public long getActivitySize() {
		return this.activitySize;
	}
	/**
	 * @param activitySize the activitySize to set
	 */
	public void setActivitySize(long activitySize) {
		this.activitySize = activitySize;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("Origin [activity=%s, total=%s, activityName=%s, activityModification=%s, activitySize=%s]",
						this.activity, this.total, this.activityName,
						this.activityModification, this.activitySize);
	}
	
}
