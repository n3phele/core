package n3phele.service.model;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.core.NotFoundException;
import n3phele.service.model.core.Entity;
import n3phele.service.model.core.Helpers;
import n3phele.service.model.core.TypedParameter;
import n3phele.service.model.core.User;
import n3phele.service.rest.impl.AccountResource;
import n3phele.service.rest.impl.ActionResource;
import n3phele.service.rest.impl.CloudProcessResource;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Unindex;


@XmlRootElement(name="Command")
@XmlType(name="Command", propOrder={"shell", "description", "tags", "processor", "preferred", "version", "icon", "ownerName", "inputFiles", "outputFiles", "executionParameters", "cloudAccounts", "implementations","serviceList"})
@Unindex
@Cache
@com.googlecode.objectify.annotation.Entity
public class Command extends Entity {
	@Id private Long id;
	private String shell;
	private String description;
	private List<String>tags = new ArrayList<String>();
	private String processor;
	@Index private boolean preferred;
	private String version;
	private String icon;
	private String ownerName;
	private List<FileSpecification> inputFiles;
	private List<TypedParameter> executionParameters = new ArrayList<TypedParameter>();
	private List<FileSpecification> outputFiles;
	private List<CommandImplementationDefinition> implementations = new ArrayList<CommandImplementationDefinition>();
	private List<CommandCloudAccount> cloudAccounts = new ArrayList<CommandCloudAccount>();
	private List<CommandCloudAccount> serviceList = new ArrayList<CommandCloudAccount>();
	
	public Command() {}
	
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
	 * @return the shell
	 */
	public final String getShell() {
		return this.shell;
	}

	/**
	 * @param shell the shell to set
	 */
	public final void setShell(String shell) {
		this.shell = shell;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the preferred
	 */
	public boolean isPreferred() {
		return this.preferred;
	}
	
	/**
	 * @return the preferred
	 */
	public boolean getPreferred() {
		return this.preferred;
	}

	/**
	 * @param preferred the preferred to set
	 */
	public void setPreferred(boolean isPreferred) {
		this.preferred = isPreferred;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}	

	/**
	 * @return the tags
	 */
	public List<String> getTags() {
		return this.tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	/**
	 * @return the processor
	 */
	public String getProcessor() {
		return this.processor;
	}

	/**
	 * @param processor the processor to set
	 */
	public void setProcessor(String processor) {
		this.processor = processor;
	}

	/**
	 * @return the icon
	 */
	public URI getIcon() {
		return Helpers.stringToURI(this.icon);
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(URI icon) {
		this.icon = Helpers.URItoString(icon);
	}

	/**
	 * @return the inputFiles
	 */
	public List<FileSpecification> getInputFiles() {
		return this.inputFiles;
	}

	/**
	 * @param inputFiles the inputFiles to set
	 */
	public void setInputFiles(List<FileSpecification> inputFiles) {
		this.inputFiles = inputFiles;
	}

	/**
	 * @return the executionParameters
	 */
	public List<TypedParameter> getExecutionParameters() {
		return this.executionParameters;
	}

	/**
	 * @param executionParameters the executionParameters to set
	 */
	public void setExecutionParameters(List<TypedParameter> executionParameters) {
		this.executionParameters = executionParameters;
	}

	/**
	 * @return the outputFiles
	 */
	public List<FileSpecification> getOutputFiles() {
		return this.outputFiles;
	}

	/**
	 * @param outputFiles the outputFiles to set
	 */
	public void setOutputFiles(List<FileSpecification> outputFiles) {
		this.outputFiles = outputFiles;
	}

	/**
	 * @return the implementations
	 */
	public List<CommandImplementationDefinition> getImplementations() {
		return implementations;
	}

	/**
	 * @param implementations the implementations to set
	 */
	public void setImplementations(
			List<CommandImplementationDefinition> implementations) {
		this.implementations.clear();
		if(implementations != null)
			this.implementations.addAll(implementations);
	}

	/**
	 * @return the ownerName
	 */
	public String getOwnerName() {
		return ownerName;
	}

	/**
	 * @param ownerName the ownerName to set
	 */
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	
	

	/**
	 * @return the cloudAccounts
	 */
	public List<CommandCloudAccount> getCloudAccounts() {
		return cloudAccounts;
	}

	/**
	 * @param cloudAccounts the cloudAccounts to set
	 */
	public void setCloudAccounts(List<CommandCloudAccount> cloudAccounts) {
		this.cloudAccounts = cloudAccounts;
	}
	

	public List<CommandCloudAccount> getServiceList() {
		return this.serviceList;
	}

	public void setServiceList(List<CommandCloudAccount> serviceList) {
		this.serviceList = serviceList;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("Command [id=%s, shell=%s, description=%s, tags=%s, processor=%s, preferred=%s, version=%s, icon=%s, ownerName=%s, inputFiles=%s, executionParameters=%s, outputFiles=%s, implementations=%s, cloudAccounts=%s]",
						this.id, this.shell, this.description, this.tags,
						this.processor, this.preferred, this.version,
						this.icon, this.ownerName, this.inputFiles,
						this.executionParameters, this.outputFiles,
						this.implementations, this.cloudAccounts);
	}

	public Command initCloudAccounts(User user) {
		if(this.getImplementations() != null) {
			ArrayList<CommandCloudAccount> decoratedProfiles = new ArrayList<CommandCloudAccount>();
			for(CommandImplementationDefinition implementation : this.getImplementations()) {
				for(Account account : AccountResource.dao.getAccountsForCloud(user,implementation.getName())) {
					decoratedProfiles.add(new CommandCloudAccount(implementation.getName(),
																  account.getName(), 
																  account.getUri()));
				}
			}

			this.setCloudAccounts(decoratedProfiles);
		}
		return this;
	}
	

	public Command initServiceList(User user) throws NotFoundException, URISyntaxException {
		if(this.getImplementations() != null) {
			ArrayList<CommandCloudAccount> decoratedProfiles = new ArrayList<CommandCloudAccount>();
			for(CloudProcess process: CloudProcessResource.dao.getServiceStackCollectionNonFinalized(user.getUri().toString()).getElements()){
				Action action = ActionResource.dao.load(new URI(process.action));
				String accountURI = action.getContext().get("account").getValue()[0];
				Account account = AccountResource.dao.load(new URI(accountURI), user);		
				for(CommandImplementationDefinition implementation2 : this.getImplementations()) {
					if(account.getCloudName().equals(implementation2.getName())){
						decoratedProfiles.add(new CommandCloudAccount(implementation2.getName(), process.getName(), process.getUri()));
					}
				}
			}
			this.setServiceList(decoratedProfiles);
		}
		return this;
	}
	
}
