/**
 * @author Nigel Cook
 *
 * (C) Copyright 2010-2011. All rights reserved.
 */
package n3phele.service.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.model.core.Credential;
import n3phele.service.model.core.Entity;
import n3phele.service.model.core.TypedParameter;

import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Unindex;

@XmlRootElement(name="Cloud")
@XmlType(name="Cloud", propOrder={"description", "location", "factory", "inputParameters", "outputParameters"})
@Unindex
@Cache
@com.googlecode.objectify.annotation.Entity
public class Cloud extends Entity {
	@Id private Long id;
	private Text description;
	private String location;
	private String factory;
	@Embed private Credential factoryCredential;
	@Embed private ArrayList<TypedParameter> inputParameters; 
	@Embed private ArrayList<TypedParameter> outputParameters; 
	private String costDriverName;
	private Map<String, Float> costMap;
	
	public Cloud() {}
	

	public Cloud(String name, String description,
		 URI location, URI factory, Credential factoryCredential, URI owner, boolean isPublic, String costDriverName, Map<String,Float> costMap) {
		super(name, null, owner, isPublic);
		this.id = null;
		setDescription(description);
		this.location = (location==null)? null : location.toString();
		this.factory = (factory==null)? null : factory.toString();
		this.factoryCredential = factoryCredential;
		this.costDriverName = costDriverName;
		setCostMap(costMap);
	}
	
	/**
	 * @param costMap the costs mapping
	 */
	public void setCostMap(Map<String, Float> costMap){
		for (Map.Entry<String, Float> entry : costMap.entrySet())
		{
		    this.costMap.put(entry.getKey().replace(".","_"), entry.getValue());
		}
		
	}
	
	/**
	 * @return the costs mapping
	 */
	public Map<String,Float> getCostMap(){
		return costMap;
	}
	
	/**
	 * @return the server cost driver name
	 */
	public String getCostDriverName(){
		return costDriverName;
	}
	
	/**
	 * @param costDriverName the server cost driver name
	 */
	public void setCostDriverName(String costDriverName){
		this.costDriverName = costDriverName;
	}
	
	/**
	 * @return the id
	 */
	@XmlTransient
	public Long getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return (description==null)?null:description.getValue();
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = (description==null)?null:new Text(description);
	}


	/**
	 * @return the location
	 */
	public URI getLocation() {
		URI result = null;
		if(this.location != null)
			result = URI.create(this.location);
		return result;
	}


	/**
	 * @param location the location to set
	 */
	public void setLocation(URI location) {
		this.location = (location == null)? null : location.toString();
	}


	/**
	 * @return the factory
	 */
	public URI getFactory() {
		URI result = null;
		if(this.factory != null) 
			result = URI.create(this.factory);
		return result;
	}


	/**
	 * @param factory the factory to set
	 */
	public void setFactory(URI factory) {
		this.factory = (factory == null)? null : factory.toString();
	}


	/**
	 * @return the factoryCredential
	 */
	@XmlTransient
	public Credential getFactoryCredential() {
		return factoryCredential;
	}


	/**
	 * @param factoryCredential the factoryCredential to set
	 */
	public void setFactoryCredential(Credential factoryCredential) {
		this.factoryCredential = factoryCredential;
	}


	/**
	 * @return the inputParameters
	 */
	public ArrayList<TypedParameter> getInputParameters() {
		return inputParameters;
	}


	/**
	 * @param inputParameters the inputParameters to set
	 */
	public void setInputParameters(ArrayList<TypedParameter> inputParameters) {
		this.inputParameters = inputParameters;
	}


	/**
	 * @return the outputParameters
	 */
	public ArrayList<TypedParameter> getOutputParameters() {
		return outputParameters;
	}


	/**
	 * @param outputParameters the outputParameters to set
	 */
	public void setOutputParameters(ArrayList<TypedParameter> outputParameters) {
		this.outputParameters = outputParameters;
	}


	public static Cloud summary(Cloud c) {
			if(c == null) return null;
			Cloud result = new Cloud(c.name, null, null, null, null, c.getOwner(), c.isPublic, c.costDriverName, c.costMap);
			result.uri = c.uri;
			return result;
	}


}
