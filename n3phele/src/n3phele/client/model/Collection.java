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

public class Collection<T extends Entity> extends Entity {
	
	protected Collection() {  }
	
	
	/**
	 * @return the total
	 */
	public final int getTotal() {
		int total = total();
		if(total < 0) {
			if(getElements() != null)
				return getElements().size();
		} else {
			return total;
		}
		return 0;
	}

	/**
	 * @return the elements
	 */

	public final List<T> getElements() {
		JavaScriptObject jsa = elements();
		return JsList.asList(jsa);
	}
	private final native JavaScriptObject elements() /*-{
		var array = [];
		if(this.elements != undefined && this.elements !=null) {
			if(this.elements.length==undefined) {
				array[0] = this.elements;
			} else {
				array = this.elements;
			}
		}
		return array;
	}-*/;
	
	/**
	 * @return the total
	 */
	private final native int total() /*-{
		return this.total==null?-1:parseInt(this.total);
	}-*/;
	
	private final native boolean isUndefined(Object o) /*-{
		return o==undefined
	}-*/;

}
