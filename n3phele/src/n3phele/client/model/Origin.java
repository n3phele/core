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
package n3phele.client.model;

import java.util.Date;

import n3phele.client.presenter.helpers.SafeDate;

import com.google.gwt.core.client.JavaScriptObject;

public class Origin extends JavaScriptObject {
	
	protected Origin() {}
	

	/**
	 * @return the activity
	 */
	public native final String getActivity() /*-{
		return this.activity;
	}-*/;

	/**
	 * @return the total
	 */
	public native final int getTotal() /*-{
		return parseInt(this.total);
	}-*/;

	/**
	 * @return the activityName
	 */
	public native final String getActivityName() /*-{
		return this.activityName;
	}-*/;

	/**
	 * @return the activityModification
	 */
	public final Date getActivityModification() {
		return SafeDate.parse(activityModification());
	}

	public native final String activityModification() /*-{
		return this.activityModification;
	}-*/;


	/**
	 * @return the activitySize
	 */
	public final long getActivitySize() {
		return Long.parseLong(activitySize());
	}
	public native final String activitySize() /*-{
		return this.activitySize;
	}-*/;


	/**
	 * @param text
	 * @return
	 */
	public static final native Origin asOrigin(String assumedSafe) /*-{
		return eval("("+assumedSafe+")");
		// return JSON.parse(assumedSafe);
	}-*/;

}
