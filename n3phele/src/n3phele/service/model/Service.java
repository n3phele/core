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

@XmlRootElement(name="Service")
@XmlType(name="Service", propOrder={"id","description","stacks"})
@Unindex
@Cache
@com.googlecode.objectify.annotation.Entity
public class Service extends Entity{
	@com.googlecode.objectify.annotation.Id private Long id;
	private String description;
	//Not sure if stacks are going to be organized on a list...
	private List<Stack> stacks;
	
	public Service(){}
	
	public Service(String description,String name, URI owner, boolean isPublic){
		super(name, null, owner, isPublic);
		this.description = description;
		stacks = new ArrayList<Stack>();
	}


	/*
	 * Automatic Generated Methods
	 */
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
	public List<Stack> getStacks() {
		return this.stacks;
	}
	public void setStacks(List<Stack> stacks) {
		this.stacks = stacks;
	}

}
