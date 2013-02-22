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

import java.net.URI;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Unindex;

@XmlRootElement(name="Entity")
@XmlType(name="Entity", propOrder={"name", "uri", "owner", "public"})
public class Entity {
	
	@Index protected String name;
	@Unindex protected String uri;
	@Index protected String owner;
	@Index protected boolean isPublic;

	public Entity() {
		super();
	}
	

	/**
	 * @param name
	 * @param uri
	 * @param owner
	 * @param isPublic
	 */
	public Entity(String name, URI uri, URI owner, boolean isPublic) {
		this.name = name;
		this.uri = (uri == null)? null : uri.toString();
		this.owner = (owner == null)?null:owner.toString();
		this.isPublic = isPublic;
	}

	/*
	 * Getters and Setters..
	 * ---------------------
	 */
	

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
	 * @return the uri
	 */
	public URI getUri() {
		URI result = null;
		if(this.uri != null)
			result = URI.create(uri);
		return result;
	}


	/**
	 * @param uri the uri to set
	 */
	public void setUri(URI uri) {
		if(uri != null)
			this.uri = uri.toString();
		else
			uri = null;
	}
	
	/**
	 * @return the owner uri
	 */
	public URI getOwner() {
		return (this.owner==null)?null:URI.create(this.owner);
	}


	/**
	 * @param owner the owner uri to set
	 */
	public void setOwner(URI owner) {
		this.owner = (owner == null)?null: owner.toString();
	}

	/**
	 * @return the isPublic visibility
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
	 * @param visibility the isPublic visibility to set
	 */
	public void setPublic(boolean visibility) {
		this.isPublic = visibility;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isPublic ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entity other = (Entity) obj;
		if (isPublic != other.isPublic)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

	
}
