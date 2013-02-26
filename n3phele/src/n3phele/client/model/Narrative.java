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

public class Narrative extends JavaScriptObject {
	
	protected Narrative() {
	}


	/**
	 * @return the tag
	 */
	public native final String getTag() /*-{
		return this.tag;
	}-*/;
	
	/**
	 * @return the processUri
	 */
	public native final String getProcessUri() /*-{
		return this.processUri;
	}-*/;


	/**
	 * @return the stamp
	 */
	public final Date getStamp() {
		return SafeDate.parse(stamp());
	}

	public native final String stamp() /*-{
		return this.stamp;
	}-*/;


	/**
	 * @return the state
	 */
	public native final String getState() /*-{
		return this.state;
	}-*/;


	/**
	 * @return the text
	 */
	public native final String getText() /*-{
		return this.text;
	}-*/;
	

}
