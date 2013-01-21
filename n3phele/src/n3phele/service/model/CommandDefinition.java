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
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name="CommandDefinition")
@XmlType(name="CommandDefinition", propOrder={"uri", "name", "description", "public", "preferred", "version", "icon", "inputFiles", "outputFiles", "executionParameters", "implementations"})
public class CommandDefinition {
	private URI uri;
	private String name;
	private String description;
	private boolean isPublic;
	private boolean isPreferred;
	private String version;
	private URI icon;
	private List<FileSpecification> inputFiles;
	private List<TypedParameter> executionParameters;
	private List<FileSpecification> outputFiles;
	private ArrayList<CommandImplementationDefinition> implementations = new ArrayList<CommandImplementationDefinition>();
	
	public CommandDefinition() {}
	/**
	 * @return the uri
	 */
	public URI getUri() {
		return this.uri;
	}

	/**
	 * @param uri the uri to set
	 */
	public void setUri(URI uri) {
		this.uri = uri;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return the isPublic
	 */
	public boolean isPublic() {
		return this.isPublic;
	}

	/**
	 * @param isPublic the isPublic to set
	 */
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	/**
	 * @return the isPreferred
	 */
	public boolean isPreferred() {
		return this.isPreferred;
	}

	/**
	 * @param isPreferred the isPreferred to set
	 */
	public void setPreferred(boolean isPreferred) {
		this.isPreferred = isPreferred;
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
	 * @return the icon
	 */
	public URI getIcon() {
		return this.icon;
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(URI icon) {
		this.icon = icon;
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
		this.implementations.addAll(implementations);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("CommandDefinition [uri=%s, name=%s, description=%s, isPublic=%s, isPreferred=%s, version=%s, icon=%s, inputFiles=%s, executionParameters=%s, outputFiles=%s, implementations=%s]",
						uri, name, description, isPublic, isPreferred, version,
						icon, inputFiles, executionParameters, outputFiles,
					    implementations);
	}

	
}
