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
package n3phele.service.model.core;

import java.io.Serializable;
import java.net.URI;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;



@XmlRootElement(name="Change")
@XmlType(name="Change", propOrder={"stamp", "uri", "deleted", "owner", "public"})
public class Change implements Serializable {
	private static final long serialVersionUID = 1L;
	private long stamp;
	private URI uri;
	private boolean deleted;
	private boolean isPublic;
	private URI owner;
	
	public Change() {}
	public Change(Entity e, boolean deleted) {
		this.uri = e.getUri();
		this.deleted = deleted;
		this.owner = e.getOwner();
		this.isPublic = e.isPublic();
	}
	public Change(URI myPath, URI owner, boolean isPublic, boolean deleted) {
		this.uri = myPath;;
		this.deleted = deleted;
		this.owner = owner;
		this.isPublic = isPublic;
	}
	/**
	 * @return the stamp
	 */
	public long getStamp() {
		return stamp;
	}
	/**
	 * @param stamp the stamp to set
	 */
	public void setStamp(long stamp) {
		this.stamp = stamp;
	}
	/**
	 * @return the uri
	 */
	public URI getUri() {
		return uri;
	}
	/**
	 * @param uri the uri to set
	 */
	public void setUri(URI uri) {
		this.uri = uri;
	}
	
	
	/**
	 * @return true if change is object deleted
	 */
	public boolean isDeleted() {
		return deleted;
	}
	/**
	 * @return true if change is object deleted
	 */
	public boolean getDeleted() {
		return deleted;
	}
	/**
	 * @param deleted true is change is object deletion
	 */
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	/**
	 * @return the isPublic
	 */
	public boolean isPublic() {
		return isPublic;
	}
	/**
	 * @return the isPublic visibility
	 */
	public boolean getPublic() {
		return isPublic;
	}
	/**
	 * @param isPublic the isPublic to set
	 */
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	/**
	 * @return the owner
	 */
	public URI getOwner() {
		return owner;
	}
	/**
	 * @param owner the owner to set
	 */
	public void setOwner(URI owner) {
		this.owner = owner;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(
				"Change [stamp=%s, uri=%s, deleted=%s, isPublic=%s, owner=%s]",
				this.stamp, this.uri, this.deleted, this.isPublic, this.owner);
	}
	


}
