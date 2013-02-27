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

public class Entity extends JavaScriptObject {

	protected Entity() {}

	/**
	 * @return the name
	 */
	public native final String getName() /*-{
		return this.name;
	}-*/;

	/**
	 * @return the uri
	 */
	public native final String getUri() /*-{
		return this.uri;
	}-*/;

	/**
	 * @return the mime
	 */
	public native final String getMime() /*-{
		return this.mime;
	}-*/;

	/**
	 * @return the owner
	 */
	public native final String getOwner() /*-{
		return this.owner;
	}-*/;

	/**
	 * @return the isPublic
	 */
	public native final boolean isPublic() /*-{
		return this['public']==null?false:this['public']=="true";
	}-*/;

	
}
