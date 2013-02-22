/**
 * @author Nigel Cook
 *
 * (C) Copyright 2010-2011. All rights reserved.
 */
package n3phele.service.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.model.core.Collection;
import n3phele.service.model.core.Entity;

@XmlRootElement(name="AccountCollection")
@XmlType(name="Collection", propOrder={"total", "elements"})
public class AccountCollection extends Entity {
	private long total;
	private List<Account> elements;
	/**
	 * 
	 */
	public AccountCollection() {
		super();
	}

	/**
	 * @param name
	 * @param uri
	 * @param elements
	 */
	public AccountCollection(Collection<Account> pc, int start, int end) {
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
	public List<Account> getElements() {
		return elements;
	}

	/**
	 * @param elements the elements to set
	 */
	public void setElements(List<Account> elements) {
		this.elements = elements;
	}


}
