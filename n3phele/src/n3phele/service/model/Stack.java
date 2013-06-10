package n3phele.service.model;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.model.core.Credential;
import n3phele.service.model.core.Entity;

import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Serialize;
import com.googlecode.objectify.annotation.Unindex;

@XmlRootElement(name="Stack")
@XmlType(name="Stack", propOrder={"id","description","vms"})
@Unindex
@Cache
public class Stack{
	/**
	 * 
	 */
	private long id;
	private String name;
	
	private String description;
	
	//An account bound to a Stack can be referenced like a private field or with the URI owner of the super class Entity 
	//private String account;
	
	// nodecount?
	
	//Stacks contain vms, a class Virtual Machine don't exist, may have to store CloudProcess... also have to check nodes...
	@Serialize
	private List<URI> vms = new ArrayList<URI>();
//	@Serialize
//	private List<URI> relations = new ArrayList<URI>();
//	
	public Stack () {}
	
	public Stack( String name, String description) {
		this.id = (long)- 1;
		this.name = name;
		this.description = description;

	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getId() {
		return this.id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<URI> getVms() {
		return this.vms;
	}

	public void setVms(List<URI> vms) {
		this.vms = vms;
	}
	
	public boolean addVm(URI r){
		return vms.add(r);
	}
	
	@Override
	public String toString() {
		return "Stack [id=" + this.id + ", name=" + this.name + ", description=" + this.description + ", vms=" + this.vms + "]";
	}
}
