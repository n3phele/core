package n3phele.service.model.core;

/**
 * (C) Copyright 2010-2013. Nigel Cook. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * Licensed under the terms described in LICENSE file that accompanied this code, (the "License"); you may not use this file
 * except in compliance with the License. 
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on 
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 *  specific language governing permissions and limitations under the License.
 */

import java.security.Principal;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Unindex;

@XmlRootElement(name="User")
@XmlType(name="User", propOrder={"firstName", "lastName", "validationSeed", "badAttempts", "validated", "validationDate", "admin"})
@Unindex
@Cache
@com.googlecode.objectify.annotation.Entity
public class User extends Entity implements Principal {
	@Id private Long id;
	private String firstName;
	private String lastName;
	private int validationSeed;
	private int badAttempts = 0;
	private boolean validated = false;
	private Date validationDate = null;
	private boolean admin = false;
	@Embed private Credential credential;
	
	public User() {}
	
	public User(String email, String firstName, String lastName) {
		super(email, null, "factory/vnd.com.n3phele.User+json", null, false);
		this.id = null;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public User(String email, String firstName, String lastName, String secret) {
		super(email, null, "factory/vnd.com.n3phele.User+json", null, false);
		this.id = null;
		this.firstName = firstName;
		this.lastName = lastName;
		this.credential = new Credential(email, secret).encrypt();
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
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}


	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}


	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	/**
	 * @return the validationSeed
	 */
	public int getValidationSeed() {
		return validationSeed;
	}


	/**
	 * @param validationSeed the validationSeed to set
	 */
	public void setValidationSeed(int validationSeed) {
		this.validationSeed = validationSeed;
	}


	/**
	 * @return the badAttempts
	 */
	public int getBadAttempts() {
		return badAttempts;
	}


	/**
	 * @param badAttempts the badAttempts to set
	 */
	public void setBadAttempts(int badAttempts) {
		this.badAttempts = badAttempts;
	}


	/**
	 * @return the validated
	 */
	public boolean isValidated() {
		return validated;
	}


	/**
	 * @param validated the validated to set
	 */
	public void setValidated(boolean validated) {
		this.validated = validated;
	}


	/**
	 * @return the validationDate
	 */
	public Date getValidationDate() {
		return validationDate;
	}


	/**
	 * @return the admin
	 */
	public boolean isAdmin() {
		return admin;
	}
	
	/**
	 * @return the admin
	 */
	public boolean getAdmin() {
		return admin;
	}


	/**
	 * @param admin the admin to set
	 */
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}


	/**
	 * @param validationDate the validationDate to set
	 */
	public void setValidationDate(Date validationDate) {
		this.validationDate = validationDate;
	}


	/**
	 * @return the credential
	 */

	@XmlTransient
	public Credential getCredential() {
		return this.credential;
	}


	/**
	 * @param credential the credential to set
	 */
	public void setCredential(Credential credential) {
		this.credential = credential;
	}

	@Override
	public String toString() {
		return this.firstName+" "+this.lastName;
	}
}
