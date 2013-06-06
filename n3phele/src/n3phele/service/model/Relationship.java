package n3phele.service.model;

import java.io.Serializable;
import java.net.URI;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.model.core.Entity;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Unindex;


@XmlRootElement(name="Relationship")
@XmlType(name="Relationship", propOrder={"id","uriStackMaster","uriStackSubordinate","type","description"})
@Unindex
@Cache
@com.googlecode.objectify.annotation.Entity
//must add on ServiceModelDao.java
public class Relationship extends Entity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@com.googlecode.objectify.annotation.Id private Long id;
	

	private String uriStackMaster;
	private String uriStackSubordinate;
	
	private String type;

	//need to reference both stacks of the relationship...
	private String description;
	public Relationship(){}
	
	public Relationship(String uriStackMaster,String uriStackSubordinate,String type,String description,String name, URI owner, boolean isPublic){
		super(name, null, owner, isPublic);
		this.uriStackMaster = uriStackMaster;
		this.uriStackSubordinate = uriStackSubordinate;
		this.type = type;
		this.description = description;	
	}

	public String getUriStackMaster() {
		return this.uriStackMaster;
	}

	public void setUriStackMaster(String uriStackMaster) {
		this.uriStackMaster = uriStackMaster;
	}

	public String getUriStackSubordinate() {
		return this.uriStackSubordinate;
	}

	public void setUriStackSubordinate(String uriStackSubordinate) {
		this.uriStackSubordinate = uriStackSubordinate;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
