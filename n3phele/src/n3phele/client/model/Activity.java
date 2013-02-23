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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;

public class Activity extends Entity {
	protected Activity () {} 
	
	/**
	 * @return the command
	 */
	public native final String getCommand() /*-{
		return this.command;
	}-*/;
	/**
	 * @return the account
	 */
	public native final String getAccount() /*-{
		return this.account;
	}-*/;
	/**
	 * @return the cloudProfileId
	 */
	public final Long getCloudProfileId() {
		String s = cloudProfileId();
		return s==null?null:Long.valueOf(s);
	}
	private native final String cloudProfileId() /*-{
		return this.cloudProfileId;
	}-*/;
	/**
	 * @return the parameters
	 */
	public final List<NameValue> getParameters() {
		JavaScriptObject jsa = parameters();
		return JsList.asList(jsa);
	}
	
	protected final native JavaScriptObject parameters() /*-{
		var array = [];
		if(this.parameters != undefined && this.parameters!=null) {
			if(this.parameters.length==undefined) {
				array[0] = this.parameters;
			} else {
				array = this.parameters;
			}
		}
		return array;

	}-*/;
	/**
	 * @return the inputs
	 */
	public final List<FileSpecification> getInputs() {
		JavaScriptObject jsa = inputs();
		return JsList.asList(jsa);
	}
	
	protected final native JavaScriptObject inputs() /*-{
		var array = [];
		if(this.inputs != undefined && this.inputs!=null) {
			if(this.inputs.length==undefined) {
				array[0] = this.inputs;
			} else {
				array = this.inputs;
			}
		}
		return array;

	}-*/;
	
	/**
	 * @return the outputs
	 */
	public final List<FileSpecification> getOutputs() {
		JavaScriptObject jsa = outputs();
		return JsList.asList(jsa);
	}
	
	protected final native JavaScriptObject outputs() /*-{
		var array = [];
		if(this.outputs != undefined && this.outputs!=null) {
			if(this.outputs.length==undefined) {
				array[0] = this.outputs;
			} else {
				array = this.outputs;
			}
		}
		return array;

	}-*/;
	
	/**
	 * @return the fileTable
	 */
	public final List<FileSpecification> getFileTable() {
		JavaScriptObject jsa = fileTable();
		return JsList.asList(jsa);
	}
	
	protected final native JavaScriptObject fileTable() /*-{
		var array = [];
		if(this.fileTable != undefined && this.fileTable!=null) {
			if(this.fileTable.length==undefined) {
				array[0] = this.fileTable;
			} else {
				array = this.fileTable;
			}
		}
		return array;

	}-*/;
	
	/**
	 * @return the context
	 */
	public final List<FileSpecification> getContext() {
		JavaScriptObject jsa = context();
		return JsList.asList(jsa);
	}
	
	protected final native JavaScriptObject context() /*-{
		var array = [];
		if(this.context != undefined && this.context!=null) {
			if(this.context.length==undefined) {
				array[0] = this.context;
			} else {
				array = this.context;
			}
		}
		return array;

	}-*/;
	
	/**
	 * @return the progress
	 */
	public native final String getProgress() /*-{
		return this.progress;
	}-*/;
	/**
	 * @return the actions
	 */
	public native final ArrayList<String> getActions() /*-{
		return this.actions;
	}-*/;
	/**
	 * @return the notify
	 */
	public native final boolean isNotify() /*-{
		return this.notify;
	}-*/;
	/**
	 * @return the state
	 */
	public native final String getState() /*-{
		return this.state;
	}-*/;
	
	/**
	 * @return the parameters
	 */
	public final List<NameValue> getOrigin() {
		JavaScriptObject jsa = origin();
		return JsList.asList(jsa);
	}
	
	protected final native JavaScriptObject origin() /*-{
		var array = [];
		if(this.origin != undefined && this.origin!=null) {
			if(this.origin.length==undefined) {
				array[0] = this.origin;
			} else {
				array = this.origin;
			}
		}
		return array;

	}-*/;
	
	public static final native Collection<Activity> asCollection(String assumedSafe) /*-{
	return eval("("+assumedSafe+")");
	// return JSON.parse(assumedSafe);
	}-*/;
	public static final native Activity asActivity(String assumedSafe) /*-{
	return eval("("+assumedSafe+")");
	// return JSON.parse(assumedSafe);
	}-*/;

}
