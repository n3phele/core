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

public class StackServiceAction extends Entity {
	protected StackServiceAction() {
	}

	/**
	 * @return the description
	 */
	public native final String getServiceDescription() /*-{
		return this.serviceDescription;
	}-*/;

	/**
	 * @return the cloud
	 */
	public native final Long stackNumber() /*-{
		return this.stackNumber;
	}-*/;



	public final List<Stack> getStackList() {
		JavaScriptObject jsa = stacks();
		return JsList.asList(jsa);
	};


	public native final JavaScriptObject stacks() /*-{
		var array = [];
		if(this.stacks) {
			if(this.stacks.length) {
				array = this.stacks;
			} else {
				array[0] = this.stacks;
			}
		}
		return array;
	}-*/;
	
	public native final String adopted() /*-{
		var array = [];
		if (this.adopted != undefined && this.adopted != null) {
			if (this.adopted.length == undefined) {
				array[0] = this.adopted;
			} else {
				array = this.adopted;
			}
		} else
			return "";
		return array;
	}-*/;

	public final List<String> getAdopted() {
		String jsa = adopted();
		List<String> list = new ArrayList<String>();
		String[] costs = jsa.toString().split(",");
		for (int i = 0; i < costs.length; i++) {
			list.add(costs[i]);
		}
		return list;
	}

	public static final native Collection<StackServiceAction> asCollection(
			String assumedSafe) /*-{
		return eval("(" + assumedSafe + ")");
		// return JSON.parse(assumedSafe);
	}-*/;

	public static final native StackServiceAction asAction (String assumedSafe) /*-{
		return eval("(" + assumedSafe + ")");
		// return JSON.parse(assumedSafe);
	}-*/;

}
