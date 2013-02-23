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

import com.google.gwt.core.client.JavaScriptObject;

import n3phele.client.presenter.helpers.SafeDate;

public class FileNode extends JavaScriptObject {
	protected FileNode() {}
	
	/**
	 * @return the name
	 */
	public native final String getName() /*-{
		return this.name;
	}-*/;
	/**
	 * @return the path
	 */
	public native final String getPath() /*-{
		return this.path;
	}-*/;
	/**
	 * @return the mime
	 */
	public native final String getMime() /*-{
		return this.mime;
	}-*/;
	/**
	 * @return the repository
	 */
	public native final String getRepository() /*-{
		return this.repository;
	}-*/;
	
	/**
	 * @return the repositoryName
	 */
	public native final String getRepositoryName() /*-{
		return this.repositoryName;
	}-*/;
	/**
	 * @return the modified
	 */
	public final Date getModified() {
		return SafeDate.parse(modified());
	}

	protected native final String modified() /*-{
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
	
	public static FileNode newInstance(String name, String path, String mime, String repository, String repositoryName) {
		FileNode result = (FileNode) JavaScriptObject.createObject();
		result._init(name, path, mime, repository, repositoryName);
		return result;
	}
	private native final void _init(String name, String path, String mime, String repository, String repositoryName) /*-{
		this.name = name;
		this.path = path;
		this.mime = mime;
		this.repository = repository;
		this.repositoryName = repositoryName;
	}-*/;
	
	public native final void setName(String name) /*-{
		this.name = name;
	}-*/;

	public final String toFormattedString() {
		return "FileNode [ "+"name=" + this.getName()+", path=" + this.getPath()+", mime=" + this.getMime()
				+ ", repository=" + this.getRepository() + ", repositoryName="
				+ this.getRepositoryName() + ", modified=" + this.getModified()+
				"]";
	}
}
