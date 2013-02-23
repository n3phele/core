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

public class RepoListResponse extends JavaScriptObject {
	
	protected RepoListResponse(){}

	/**
	 * @return the crumbs
	 */
	public final List<FileNode> getCrumbs() {
		JavaScriptObject jsa = crumbs();
		return JsList.asList(jsa);
	}
	private final native JavaScriptObject crumbs() /*-{
		var array = [];
		if(this.crumbs != undefined && this.crumbs !=null) {
			if(this.crumbs.length==undefined) {
				array[0] = this.crumbs;
			} else {
				array = this.crumbs;
			}
		}
		return array;
	}-*/;


	/**
	 * @return the files
	 */
	public final List<FileNode> getFiles() {
		JavaScriptObject jsa = files();
		return JsList.asList(jsa);
	}
	private final native JavaScriptObject files() /*-{
		var array = [];
		if(this.files != undefined && this.files !=null) {
			if(this.files.length==undefined) {
				array[0] = this.files;
			} else {
				array = this.files;
			}
		}
		return array;
	}-*/;
	
	public static final native RepoListResponse parseJSON(String assumedSafe) /*-{
	return eval("("+assumedSafe+")");
	// return JSON.parse(assumedSafe);
	}-*/;
}
