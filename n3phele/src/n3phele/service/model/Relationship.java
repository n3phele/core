package n3phele.service.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Unindex;


@XmlRootElement(name="Relationship")
@XmlType(name="Relationship", propOrder={"type","description","name"})
@Unindex
@Cache
public class Relationship {

	private long idStackMaster;
	private long idStackSubordinate;
	
	private String type;

	
	private String description;
	//may have to remove...
	private String name;

	public Relationship(){}
	
	public Relationship(long idStackMaster,long idStackSubordinate,String type,
		String description){
		this.idStackMaster = idStackMaster;
		this.idStackSubordinate = idStackSubordinate;
		this.type = type;
		this.description = description;	
		this.name = "";
	}

	public long getIdStackMaster() {
		return this.idStackMaster;
	}

	public void setUriStackMaster(long idStackMaster) {
		this.idStackMaster = idStackMaster;
	}

	public long getidStackSubordinate() {
		return this.idStackSubordinate;
	}

	public void setUriStackSubordinate(long idStackSubordinate) {
		this.idStackSubordinate = idStackSubordinate;
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
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
