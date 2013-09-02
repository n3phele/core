package n3phele.service.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Serialize;
import com.googlecode.objectify.annotation.Unindex;

@XmlRootElement(name="Stack")
@XmlType(name="Stack", propOrder={"name","id", "description" ,"commandUri","vms","deployProcess"})
@Unindex
@Cache
public class Stack{
	/**
	 * 
	 */
	private long id;
	private String name;
	
	private String description;
	private String commandUri;
	private String deployProcess;
	

	

	@Serialize
	private List<String> vms = new ArrayList<String>();

	public Stack () {
		this.commandUri = "";
		this.id = (long)- 1;
	}
	
	public Stack( String name, String description) {
		this.id = (long)- 1;
		this.name = name;
		this.description = description;
		this.commandUri = "";
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
	
	public List<String> getVms() {
		return this.vms;
	}

	public void setVms(List<String> vms) {
		this.vms = vms;
	}
	
	public boolean addVm(String r){
		return vms.add(r);
	}
	
	public String getCommandUri() {
		return this.commandUri;
	}

	public void setCommandUri(String commandUri) {
		this.commandUri = commandUri;
	}

	@Override
	public String toString() {
		return "Stack [id=" + this.id + ", name=" + this.name + ", description=" + this.description + ", vms=" + this.vms + "]";
	}
	
	public String getDeployProcess() {
		return this.deployProcess;
	}

	public void setDeployProcess(String deployProcess) {
		this.deployProcess = deployProcess;
	}
}
