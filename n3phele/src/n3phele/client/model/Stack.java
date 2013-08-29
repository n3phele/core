/**
 * @author Nigel Cook
 * @author Leonardo Amado
 * @author Douglas Tondin
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

public class Stack extends JavaScriptObject {
	protected Stack () {} 

	public native final String getId() /*-{
		return this.id;
	}-*/;
	
	public native final String getName() /*-{
		return this.name;
	}-*/;
	
	public native final String getDescription() /*-{
		return this.description;
	}-*/;
	
	public native final String getCommandUri() /*-{
		return this.commandUri;
	}-*/;
	public native final String getDeployProcess() /*-{
		return this.deployProcess;
	}-*/;
	public static final native Stack asActivity(String assumedSafe) /*-{
		return eval("("+assumedSafe+")");
		// return JSON.parse(assumedSafe);
	}-*/;

	public native final String vms() /*-{
		var array = [];
		if (this.vms != undefined && this.vms != null) {
			if (this.vms.length == undefined) {
				array[0] = this.vms;
			} else {
				array = this.vms;
			}
		} else
			return "";
		return array;
	}-*/;

	public final List<String> getVms() {
		String jsa = vms();
		List<String> list = new ArrayList<String>();
		String[] vms = jsa.toString().split(",");
		for (int i = 0; i < vms.length; i++) {
			list.add(vms[i]);
		}
		return list;
	}
}
