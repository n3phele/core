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


	public static final native Root parse(String assumedSafe) /*-{
		return eval("("+assumedSafe+")")
	}-*/;
}
