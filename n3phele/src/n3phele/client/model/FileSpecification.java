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

import com.google.gwt.core.client.JavaScriptObject;

public class FileSpecification extends JavaScriptObject {
	
	protected FileSpecification() {}
	/**
	 * @return the name
	 */
	public native final String getName() /*-{
		return this.name;
	}-*/;
	/**
	 * @param name the name to set
	 */
	public native final void setName(String name) /*-{
		this.name = name;
	}-*/;
	/**
	 * @return the description
	 */
	public native final String getDescription() /*-{
		return this.description;
	}-*/;
	/**
	 * @param description the description to set
	 */
	public native final void setDescription(String description) /*-{
		this.description = description;
	}-*/;
	/**
	 * @return the repository
	 */
	public native final String getRepository() /*-{
		return this.repository;
	}-*/;
	/**
	 * @param repository the repository to set
	 */
	public native final void setRepository(String repository) /*-{
		this.repository = repository;
	}-*/;
	/**
	 * @return the filename
	 */
	public native final String getFilename() /*-{
		return this.filename;
	}-*/;
	/**
	 * @param filename the filename to set
	 */
	public native final void setFilename(String filename) /*-{
		this.filename = filename;
	}-*/;
	
	/**
	 * @return the canonical path
	 */
	public native final String getCanonicalPath() /*-{
		return this.filename;
	}-*/;
	
	/**
	 * @return the last modified date
	 */
	public final Date getModified() {
		return SafeDate.parse(modified());
	}

	public native final String modified() /*-{
		return this.modified;
	}-*/;
	
	/**
	 * @return the size
	 */
	public final long getSize() {
		return Long.parseLong(size());
	}
	public native final String size() /*-{
		return this.size;
	}-*/;
	
	/**
	 * @return the isOptional
	 */
	public native final boolean isOptional() /*-{
		return this['optional']==null?false:this['optional']=="true";
	}-*/;
	
	/**
	 * @return the isOptional
	 */
	public native final void setOptional(boolean isOptional) /*-{
		this.optional = isOptional;
	}-*/;

	
}
