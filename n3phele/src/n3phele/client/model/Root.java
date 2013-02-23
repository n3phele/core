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

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;

public class Root extends JavaScriptObject {
	protected Root() {}
	/**
	 * @return the stamp
	 */
	public final Long getStamp() {
		String s = stamp();
		return s==null?null:Long.valueOf(s);
	}
	public native final String stamp() /*-{
		return this.stamp;
	}-*/;


	/**
	 * @return the changeGroup
	 */
	public native final ChangeGroup getChangeGroup() /*-{
		return this.changeGroup;
	}-*/;



	/**
	 * @return the changeCount
	 */
	public native final int getChangeCount() /*-{
		return this.changeCount==null?0:parseInt(this.changeCount);
	}-*/;



	/**
	 * @return the cacheAvailable
	 */
	public native final boolean isCacheAvailable() /*-{
		return this.cacheAvailable==null?false:this.cacheAvailable=="true";
	}-*/;

	/**
	 * @return the account
	 */
	public final List<Entity> getAccount() {	
		JavaScriptObject jsa = account();
		return JsList.asList(jsa);
		
	}
	protected final native JavaScriptObject account() /*-{
		var array = [];
		if(this.account != undefined && this.account!=null) {
			if(this.account.length==undefined) {
				array[0] = this.account;
			} else {
				array = this.account;
			}
		}
		return array;

	}-*/;

	/**
	 * @return the activity
	 */
	
	public final List<Entity> getActivity() {	
		JavaScriptObject jsa = account();
		return JsList.asList(jsa);
		
	}
	protected final native JavaScriptObject activity() /*-{
		var array = [];
		if(this.activity != undefined && this.activity!=null) {
			if(this.activity.length==undefined) {
				array[0] = this.activity;
			} else {
				array = this.activity;
			}
		}
		return array;

	}-*/;

	/**
	 * @return the cloud
	 */
	public final List<Entity> getCloud() {	
		JavaScriptObject jsa = cloud();
		return JsList.asList(jsa);
		
	}
	protected final native JavaScriptObject cloud() /*-{
		var array = [];
		if(this.cloud != undefined && this.cloud!=null) {
			if(this.cloud.length==undefined) {
				array[0] = this.cloud;
			} else {
				array = this.cloud;
			}
		}
		return array;

	}-*/;


	/**
	 * @return the command
	 */
	public final List<Entity> getCommand() {	
		JavaScriptObject jsa = command();
		return JsList.asList(jsa);
		
	}
	protected final native JavaScriptObject command() /*-{
		var array = [];
		if(this.command != undefined && this.command!=null) {
			if(this.command.length==undefined) {
				array[0] = this.command;
			} else {
				array = this.command;
			}
		}
		return array;

	}-*/;
	
	/**
	 * @return the fileNode
	 */
	public final List<Entity> getFileNode() {	
		JavaScriptObject jsa = fileNode();
		return JsList.asList(jsa);
		
	}
	protected final native JavaScriptObject fileNode() /*-{
		var array = [];
		if(this.fileNode != undefined && this.fileNode!=null) {
			if(this.fileNode.length==undefined) {
				array[0] = this.fileNode;
			} else {
				array = this.fileNode;
			}
		}
		return array;

	}-*/;

	/**
	 * @return the progress
	 */
	public final List<Entity> getProgress() {	
		JavaScriptObject jsa = progress();
		return JsList.asList(jsa);
		
	}
	protected final native JavaScriptObject progress() /*-{
		var array = [];
		if(this.progress != undefined && this.progress!=null) {
			if(this.progress.length==undefined) {
				array[0] = this.progress;
			} else {
				array = this.progress;
			}
		}
		return array;

	}-*/;


	/**
	 * @return the repository
	 */
	public final List<Entity> getRepository() {	
		JavaScriptObject jsa = repository();
		return JsList.asList(jsa);
		
	}
	protected final native JavaScriptObject repository() /*-{
		var array = [];
		if(this.repository != undefined && this.repository!=null) {
			if(this.repository.length==undefined) {
				array[0] = this.repository;
			} else {
				array = this.repository;
			}
		}
		return array;

	}-*/;
	
	/**
	 * @return the study
	 */
	public final List<Entity> getStudy() {	
		JavaScriptObject jsa = study();
		return JsList.asList(jsa);
		
	}
	protected final native JavaScriptObject study() /*-{
		var array = [];
		if(this.study != undefined && this.study!=null) {
			if(this.study.length==undefined) {
				array[0] = this.study;
			} else {
				array = this.study;
			}
		}
		return array;

	}-*/;


	/**
	 * @return the user
	 */
	public final List<Entity> getUser() {	
		JavaScriptObject jsa = user();
		return JsList.asList(jsa);
		
	}
	protected final native JavaScriptObject user() /*-{
		var array = [];
		if(this.user != undefined && this.user!=null) {
			if(this.user.length==undefined) {
				array[0] = this.user;
			} else {
				array = this.user;
			}
		}
		return array;

	}-*/;



	public static final native Root parse(String assumedSafe) /*-{
		return eval("("+assumedSafe+")")
	}-*/;
}
