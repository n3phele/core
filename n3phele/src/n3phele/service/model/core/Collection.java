package n3phele.service.model.core;

/**
 * (C) Copyright 2010-2013. Nigel Cook. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * Licensed under the terms described in LICENSE file that accompanied this code, (the "License"); you may not use this file
 * except in compliance with the License. 
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on 
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 *  specific language governing permissions and limitations under the License.
 */

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name="", propOrder={"total", "elements"})
@XmlSeeAlso(Entity.class)

public class Collection<T extends Entity> extends Entity {
	private long total;
	private List<T> elements;
	
	public Collection() {  }
	
	public Collection(String name, URI uri) {
		super(name, uri, "application/vnd.com.n3phele.Collection+json", null, true);
		this.total = 0;
		this.elements = new ArrayList<T>();
	}
	
	public Collection(String name, URI uri, List<T> elements) {
		super(name, uri, "application/vnd.com.n3phele.Collection+json", null, true);
		this.total = 0;
		this.elements = elements;
	}
	
	public Collection(String name, URI uri, String mime, List<T> elements) {
		super(name, uri, mime, null, true);
		this.total = 0;
		this.elements = elements;
	}
	
	public Collection(String name, URI uri, String mime, long total) {
		super(name, uri, mime, null, true);
		this.total = total;
		this.elements = null;
	}

	
	public Collection<BaseEntity> collection(boolean summary) {
		Collection<BaseEntity> condensed = new Collection<BaseEntity>();
		condensed.name = this.name;
		condensed.uri = this.uri;
		condensed.mime = this.mime;
		condensed.total = this.total;
		condensed.isPublic = this.isPublic;
		if(!summary) {
			if(this.elements != null) {
				condensed.elements = new ArrayList<BaseEntity>(this.elements.size());
				for(Entity e : this.elements){
					BaseEntity n = new BaseEntity(e);
					condensed.elements.add(n);
				}
			}
		}
		return condensed;
	}
	
	/**
	 * @return the total
	 */
	public long getTotal() {
			return total == 0? (elements != null ? elements.size() : total) : total;
	}
	/**
	 * @param total the total to set
	 */
	public void setTotal(long total) {
		this.total = total;
	}
	/**
	 * @return the elements
	 */
	public List<T> getElements() {
		return elements;
	}
	/**
	 * @param elements the elements to set
	 */
	public void setElements(List<T> elements) {
		this.elements = elements;
	}
}
