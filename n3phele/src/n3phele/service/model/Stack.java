package n3phele.service.model;
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
import com.googlecode.objectify.annotation.Unindex;

@XmlRootElement(name="Stack")
@XmlType(name="Stack", propOrder={"id","description","vms"})
@Unindex
@Cache
@com.googlecode.objectify.annotation.Entity
public class Stack extends Entity{
	@com.googlecode.objectify.annotation.Id private Long id;
	private String description;
	//An account bound to a Stack can be referenced like a private field or with the URI owner of the super class Entity 
	//private String account;
	
	// nodecount?
	
	//Stacks contain vms, a class Virtual Machine don't exist, may have to store CloudProcess... also have to check nodes...
	private List<CloudProcess> vms;

	public Stack () {}
	
	public Stack(String description,String name, URI owner, boolean isPublic) {
		super(name, null, owner, isPublic);
		this.description = description;
		vms = new ArrayList<CloudProcess>();
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
}
