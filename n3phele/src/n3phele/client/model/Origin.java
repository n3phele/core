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
	 * @return the canonicalName
	 */
	public native final String getCanonicalName() /*-{
		return this.canonicalName;
	}-*/;

	/**
	 * @return the process
	 */
	public native final String getProcess() /*-{
		return this.process;
	}-*/;



	/**
	 * @return the length
	 */
	public native final int getLength() /*-{
		return parseInt(this.length);
	}-*/;



	/**
	 * @return the modified
	 */
	public final Date getModified() {
		return SafeDate.parse(modified());
	}
	
	/**
	 * @return the modified
	 */
	private native final String modified() /*-{
		return this.modified;
	}-*/;
	
	/**
	 * @return the processName
	 */
	public native final String getProcessName() /*-{
		return this.processName;
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
