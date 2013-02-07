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
package n3phele.service.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.model.repository.FileNode;

@XmlRootElement(name="RepoListResponse")
@XmlType(name="RepoListResponse", propOrder={"crumbs", "files"})
public class RepoListResponse {
	private List<FileNode> crumbs;
	private List<FileNode> files;
	
	public RepoListResponse(){}

	/**
	 * @return the crumbs
	 */
	public List<FileNode> getCrumbs() {
		return this.crumbs;
	}

	/**
	 * @param crumbs the crumbs to set
	 */
	public void setCrumbs(List<FileNode> crumbs) {
		this.crumbs = crumbs;
	}

	/**
	 * @return the files
	 */
	public List<FileNode> getFiles() {
		return this.files;
	}

	/**
	 * @param files the files to set
	 */
	public void setFiles(List<FileNode> files) {
		this.files = files;
	}
	
	
}
