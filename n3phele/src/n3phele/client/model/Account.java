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

public class Account extends Entity {
	protected Account() {}

	/**
	 * @return the description
	 */
	public native final String getDescription() /*-{
		return this.description;
	}-*/;



	/**
	 * @return the cloud
	 */
	public native final String getCloud() /*-{
		return this.cloud;
	}-*/;
	
	
	public native final Long getId() /*-{
		return this.id;
	}-*/;
	
	
	/**
	 * @return the cloudName
	 */
	public native final String getCloudName() /*-{
		return this.cloudName;
	}-*/;



	public static final native Collection<Account> asCollection(String assumedSafe) /*-{
	return eval("("+assumedSafe+")");
	// return JSON.parse(assumedSafe);
	}-*/;
	public static final native Account asAccount(String assumedSafe) /*-{
	return eval("("+assumedSafe+")");
	// return JSON.parse(assumedSafe);
	}-*/;

}
