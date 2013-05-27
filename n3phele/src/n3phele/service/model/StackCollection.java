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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.model.core.Collection;
import n3phele.service.model.core.Entity;

@XmlRootElement(name="ServiceCollection")
@XmlType(name="Collection", propOrder={"total", "elements"})
public class StackCollection  extends Entity  {
	private long total;
	private List<Stack> elements;
	/**
	 * 
	 */
	public StackCollection() {
		super();
	}

	
	/**
	 * @param pc
	 * @param start
	 * @param end
	 */
	public StackCollection(Collection<Stack> pc, int start, int end) {
		super(pc.getName(), pc.getUri(), pc.getOwner(), pc.isPublic());
		this.elements = pc.getElements();
		Collections.sort(this.elements, new Comparator<Stack>() {
			// Sort by ascending name, ascending owner, descending version
			public int compare(Stack o1, Stack o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			} 
		});
		this.total = elements.size();
		if(end < 0 || end > this.elements.size()) end = this.elements.size();
		if(start != 0 || end != this.elements.size())
			this.elements = this.elements.subList(start, end);
	}
	
	/**
	 * @param pc
	 */
	public StackCollection(Collection<Stack> pc) {
		super(pc.getName(), pc.getUri(), pc.getOwner(), pc.isPublic());
		this.elements = pc.getElements();
		this.total = pc.getTotal();
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
	public List<Stack> getElements() {
		return elements;
	}

	/**
	 * @param elements the elements to set
	 */
	public void setElements(List<Stack> elements) {
		this.elements = elements;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("StackCollection [total=%s, elements=%s]",
				total, elements);
	}

}

