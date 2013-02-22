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

import n3phele.service.model.core.Collection;
import n3phele.service.model.core.Entity;
import n3phele.service.model.repository.Repository;

@XmlRootElement(name="RepositoryCollection")
@XmlType(name="Collection", propOrder={"total", "elements"})
public class RepositoryCollection extends Entity {
	private long total;
	private List<Repository> elements;
	/**
	 * 
	 */
	public RepositoryCollection() {
		super();
	}

	/**
	 * @param name
	 * @param uri
	 * @param elements
	 */
	public RepositoryCollection(Collection<Repository> pc, int start, int end) {
		super(pc.getName(), pc.getUri(), pc.getOwner(), pc.isPublic());
		this.total =( pc.getTotal());
		this.elements = pc.getElements();
		if(end < 0 || end > this.elements.size()) end = this.elements.size();
		if(start != 0 || end != this.elements.size())
			this.elements = this.elements.subList(start, end);
	}

	/**
	 * @return the total
	 */
	public long getTotal() {
		return total;
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
	public List<Repository> getElements() {
		return elements;
	}

	/**
	 * @param elements the elements to set
	 */
	public void setElements(List<Repository> elements) {
		this.elements = elements;
	}


}
