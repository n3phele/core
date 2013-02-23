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

public class ChangeGroup extends JavaScriptObject {
	protected ChangeGroup() {}
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
	 * @return the change
	 */

	public final List<Change> getChange() {
		JavaScriptObject jsa = change();
		return JsList.asList(jsa);
	}
	private final native JavaScriptObject change() /*-{
		var array = [];
		if(this.change != undefined && this.change !=null) {
			if(this.change.length==undefined) {
				array[0] = this.change;
			} else {
				array = this.change;
			}
		}
		return array;
	}-*/;


}
