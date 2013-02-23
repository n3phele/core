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


public class Repository extends Entity {
	
	protected Repository() {}
	/**
	 * @return the description
	 */
	public native final String getDescription() /*-{
		return this.description;
	}-*/;
	/**
	 * @return the target
	 */
	public native final String getTarget() /*-{
		return this.target;
	}-*/;
	/**
	 * @return the root
	 */
	public native final String getRoot() /*-{
		return this.root;
	}-*/;
	/**
	 * @return the kind
	 */
	public native final String getKind() /*-{
		return this.kind;
	}-*/;
	
	public static final native Collection<Repository> asCollection(String assumedSafe) /*-{
	return eval("("+assumedSafe+")");
	// return JSON.parse(assumedSafe);
	}-*/;
	public static final native Repository asRepository(String assumedSafe) /*-{
	return eval("("+assumedSafe+")");
	// return JSON.parse(assumedSafe);
	}-*/;
	
	
	
	
	
}
