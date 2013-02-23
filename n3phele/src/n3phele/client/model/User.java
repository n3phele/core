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

import java.util.Date;

import n3phele.client.presenter.helpers.SafeDate;

public class User extends Entity {
	protected User() {}
	/**
	 * @return the firstName
	 */
	public native final String getFirstName() /*-{
		return this.firstName;
	}-*/;
	/**
	 * @return the lastName
	 */
	public native final String getLastName() /*-{
		return this.lastName;
	}-*/;
	/**
	 * @return the validationSeed
	 */
	public native final int getValidationSeed() /*-{
		return this.validationSeed==null?0:parseInt(this.validationSeed);
	}-*/;
	/**
	 * @return the badAttempts
	 */
	public native final int getBadAttempts() /*-{
		return this.badAttempts==null?0:parseInt(this.badAttempts);
	}-*/;
	/**
	 * @return the validated
	 */
	public native final boolean isValidated() /*-{
		return this.validated==null?false:this.validated=="true";
	}-*/;
	/**
	 * @return the validationDate
	 */
	public final Date getValidationDate() {
		return SafeDate.parse(validationDate());
	}
	public native final String validationDate() /*-{
		return this.validationDate;
	}-*/;
	/**
	 * @return the admin
	 */
	public native final boolean isAdmin() /*-{
		return this.admin==null?false:this.admin=="true";
	}-*/;
	public static final native User asUser(String assumedSafe) /*-{
	return eval("("+assumedSafe+")");
	// return JSON.parse(assumedSafe);
	}-*/;
	

}
