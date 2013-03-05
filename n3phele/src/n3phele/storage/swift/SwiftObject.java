/*
 * (C) Copyright 2010-2013. Nigel Cook. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * Adapted from Luis Gervaso woorea project.
 * 
 * Licensed under the terms described in LICENSE file that accompanied this code, (the "License"); you may not use this file
 * except in compliance with the License. 
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on 
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 *  specific language governing permissions and limitations under the License.
 *
 */
package n3phele.storage.swift;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;


public class SwiftObject  {

	private String subdir;

	private String name;

	private String hash;
	
	private int bytes;
	
	
	private String contentType;
	
	private Date lastModified;

	/**
	 * @return the subdir
	 */
	public String getSubdir() {
		return subdir;
	}

	/**
	 * @param subdir the subdir to set
	 */
	public void setSubdir(String subdir) {
		this.subdir = subdir;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the hash
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * @param hash the hash to set
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}

	/**
	 * @return the bytes
	 */
	public int getBytes() {
		return bytes;
	}

	/**
	 * @param bytes the bytes to set
	 */
	public void setBytes(int bytes) {
		this.bytes = bytes;
	}

	/**
	 * @return the contentType
	 */
	@XmlElement(name="content_type")
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * @return the lastModified
	 */
	@XmlElement(name="last_modified")
	public Date getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("SwiftObject [subdir=%s, name=%s, hash=%s, bytes=%s, contentType=%s, lastModified=%s]",
						this.subdir, this.name, this.hash, this.bytes,
						this.contentType, this.lastModified);
	}
	
}
