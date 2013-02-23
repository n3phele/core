/**
 * @author Nigel Cook
 *
 * (C) Copyright 2010-2012. Nigel Cook. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * Licensed under the terms described in LICENSE file that accompanied this code, (the "License"); you may not use this file
 * except in compliance with the License. 
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on 
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 *  specific language governing permissions and limitations under the License.
 */
package n3phele.client.model;

import java.util.List;
import java.util.Map;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;



public class ExecuteCommandRequest {
	private String name;
	private boolean notify;
	private String user;
	private String command;
	private String implementationId;
	private String account;
	private Map<String,String> parameters;
	private List<FileSpecification> inputFiles;
	private List<FileSpecification> outputFiles;

	/**
	 * @return the name
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the notify
	 */
	public final boolean isNotify() {
		return this.notify;
	}

	/**
	 * @param notify the notify to set
	 */
	public final void setNotify(boolean notify) {
		this.notify = notify;
	}

	/**
	 * @return the user
	 */
	public final String getUser() {
		return this.user;
	}

	/**
	 * @param user the user to set
	 */
	public final void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the command
	 */
	public final String getCommand() {
		return this.command;
	}

	/**
	 * @param command the command to set
	 */
	public final void setCommand(String command) {
		this.command = command;
	}

	/**
	 * @return the profileId
	 */
	public final String getImplementationId() {
		return this.implementationId;
	}

	/**
	 * @param profileId the profileId to set
	 */
	public final void setImplementationId(String implementationId) {
		this.implementationId = implementationId;
	}

	/**
	 * @return the account
	 */
	public final String getAccount() {
		return this.account;
	}

	/**
	 * @param account the account to set
	 */
	public final void setAccount(String account) {
		this.account = account;
	}

	/**
	 * @return the parameters
	 */
	public final Map<String, String> getParameters() {
		return this.parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public final void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	/**
	 * @return the inputFiles
	 */
	public final List<FileSpecification> getInputFiles() {
		return this.inputFiles;
	}

	/**
	 * @param inputFiles the inputFiles to set
	 */
	public final void setInputFiles(List<FileSpecification> inputFiles) {
		this.inputFiles = inputFiles;
	}

	/**
	 * @return the outputFiles
	 */
	public final List<FileSpecification> getOutputFiles() {
		return this.outputFiles;
	}

	/**
	 * @param outputFiles the outputFiles to set
	 */
	public final void setOutputFiles(
			List<FileSpecification> outputFiles) {
		this.outputFiles = outputFiles;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		JSONObject builder = new JSONObject();
		builder.put("name", new JSONString(name));
		builder.put("notify", JSONBoolean.getInstance(notify));
		builder.put("user", new JSONString(user));
		builder.put("command", new JSONString(command));
		builder.put("implementationId", new JSONString(implementationId));
		builder.put("account", new JSONString(account));
		builder.put("parameters", nameValueArray(parameters));
		builder.put("inputFiles", fileSpecificationArray(inputFiles));
		builder.put("outputFiles", fileSpecificationArray(outputFiles));
		return builder.toString();
	}
	
	private JSONArray nameValueArray(Map<String, String> nameValue) {
		JSONArray result = new JSONArray();
		int i = 0;
		for(Map.Entry<String,String> e : nameValue.entrySet()) {
			NameValue x = NameValue.createObject().<NameValue> cast();
			x.setKey(e.getKey());
			x.setValue(e.getValue());
			result.set(i++, new JSONObject(x));
		}
		return result;
	}
	
	private JSONArray fileSpecificationArray(List<FileSpecification>arg) {
		JSONArray result = new JSONArray();
		int i = 0;
		for(FileSpecification x : arg) {
			result.set(i++, new JSONObject(x));
		}
		return result;
	}
	
}
