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

public class UploadSignature extends JavaScriptObject {
	
	protected UploadSignature() {};

	/**
	 * @return the filename
	 */
	public native final String getFilename() /*-{
		return this.filename;
	}-*/;
	/**
	 * @return the acl
	 */
	public native final String getAcl() /*-{
		return this.acl;
	}-*/;
	/**
	 * @return the url
	 */
	public native final String getUrl() /*-{
		return this.url;
	}-*/;
	/**
	 * @return the base64Policy
	 */
	public native final String getBase64Policy() /*-{
		return this.base64Policy;
	}-*/;
	/**
	 * @return the signature
	 */
	public native final String getSignature() /*-{
		return this.signature;
	}-*/;
	/**
	 * @return the awsId
	 */
	public native final String getAwsId() /*-{
		return this.awsId;
	}-*/;
	/**
	 * @return the contentType
	 */
	public native final String getContentType() /*-{
		return this.contentType;
	}-*/;
	
	public static final native UploadSignature asUploadSignature(String assumedSafe) /*-{
	return eval("("+assumedSafe+")");
	// return JSON.parse(assumedSafe);
	}-*/;

}
