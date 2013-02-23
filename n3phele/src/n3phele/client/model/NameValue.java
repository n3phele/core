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

import com.google.gwt.core.client.JavaScriptObject;

public  class NameValue extends JavaScriptObject {
	protected NameValue() {}
	/**
	 * @return the key
	 */
	final public native String getKey() /*-{
		return this.key;
	}-*/;
	/**
	 * @param key the key to set
	 */
	final public native void setKey(String key) /*-{
		this.key = key;
	}-*/;
	/**
	 * @return the value
	 */
	final public native String getValue() /*-{
		return this.value;
	}-*/;
	/**
	 * @param value the value to set
	 */
	final public native void setValue(String value) /*-{
		this.value = value;
	}-*/;
	
}
