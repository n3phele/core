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
import java.util.List;

import n3phele.client.presenter.helpers.SafeDate;

import com.google.gwt.core.client.JavaScriptObject;

public class Progress extends Entity {
	
	
	protected Progress() {}
	/**
	 * @return this.the id
	 */
	public final native Long getId() /*-{
		return this.id;
	}-*/;


	/**
	 * @return the command
	 */
	public final native String getCommand() /*-{
		return this.command;
	}-*/;

	/**
	 * @return the description
	 */
	public final native String getDescription() /*-{
		return this.description;
	}-*/;

	/**
	 * @return this.the narratives
	 */
	public final List<Narrative> getNarratives() {	
		JavaScriptObject jsa = narratives();
		return JsList.asList(jsa);
		
	}
	protected final native JavaScriptObject narratives() /*-{
		var array = [];
		if(this.narratives != undefined && this.narratives!=null) {
			if(this.narratives.length==undefined) {
				array[0] = this.narratives;
			} else {
				array = this.narratives;
			}
		}
		return array;

	}-*/;


	/**
	 * @return this.the started
	 */
	public final Date getStarted() {
		return SafeDate.parse(started());
	}
	protected final native String started() /*-{
		return this.started ;
	}-*/;


	/**
	 * @return this.the completed
	 */
	public final Date getComplete() {
		return SafeDate.parse(completed());
	}
	protected final native String completed() /*-{
		return this.completed ;
	}-*/;


	/**
	 * @return this.the duration
	 */
	public final native int getDuration() /*-{
		return this.duration==null?-1:parseInt(this.duration);
	}-*/;


	/**
	 * @return this.the percentx10Complete
	 */
	public final native int getPercentx10Complete() /*-{
		return this.percentx10Complete==null?-1:parseInt(this.percentx10Complete);
	}-*/;
	
	/**
	 * @return this.the lastUpdate
	 */
	public final Date getLastUpdate() {
		return SafeDate.parse(lastUpdate());

	}
	protected final native String lastUpdate() /*-{
		return this.lastUpdate;
	}-*/;


	/**
	 * @return this.the status
	 */
	public final native String getStatus() /*-{
		return this.status;
	}-*/;


	/**
	 * @return this.the activity
	 */
	public final native String getActivity() /*-{
		return this.activity;
	}-*/;
	
	public static final native Collection<Progress> asCollection(String assumedSafe) /*-{
	return eval("("+assumedSafe+")");
	// return JSON.parse(assumedSafe);
	}-*/;
	public static final native Progress asProgress(String assumedSafe) /*-{
	return eval("("+assumedSafe+")");
	// return JSON.parse(assumedSafe);
	}-*/;
	public final native void setPercentagex10Complete(int updateProgress) /*-{
		this.percentx10Complete = updateProgress+'';
	}-*/;

}
