/**
 * @author Nigel Cook
 *
 * (C) Copyright 2010-2011. All rights reserved.
 */
package n3phele.service.model;

import java.net.URI;

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

@XmlRootElement(name="Account")
@XmlType(name="Account", propOrder={"description", "cloud", "cloudName"})
@Unindex
@Cache
@com.googlecode.objectify.annotation.Entity
public class Account extends Entity {
	@com.googlecode.objectify.annotation.Id private Long id;
	private Text description;
	private String cloud;
	@Index private String cloudName;
	@Embed private Credential credential;
	
	public Account() {}
	

	public Account(String name, String description,
		 URI cloud, String cloudName, Credential credential, URI owner, boolean isPublic) {
		super(name, null, owner, isPublic);
		this.id = null;
		this.cloudName = cloudName;
		setDescription(description);
		this.cloud = (cloud == null) ? null : cloud.toString();
		this.credential = credential;
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
	 * @return the history
	 */
	public URI getCloud() {
		URI result = null;
		if(this.cloud != null)
			result = URI.create(this.cloud);
		return result;
	}


	/**
	 * @param history the history to set
	 */
	public void setCloud(URI cloud) {
		this.cloud = (cloud != null)? cloud.toString():null;
	}


	/**
	 * @return the credential
	 */

	@XmlTransient
	public Credential getCredential() {
		return this.credential;
	}

	

	/**
	 * @return the cloudName
	 */
	public String getCloudName() {
		return this.cloudName;
	}


	/**
	 * @param cloudName the cloudName to set
	 */
	public void setCloudName(String cloudName) {
		this.cloudName = cloudName;
	}


	/**
	 * @param credential the credential to set
	 */
	public void setCredential(Credential credential) {
		this.credential = credential;
	}


	public static Account summary(Account c) {
		if(c == null) return null;
		Account result = new Account(c.name, null, null, null, null, c.getOwner(), c.isPublic);
		result.uri = c.uri;
		return result;
	}



}
