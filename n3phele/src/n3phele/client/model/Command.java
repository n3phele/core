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

import com.google.gwt.core.client.JavaScriptObject;


public class Command extends Entity {
	protected Command() {}
	
	/**
	 * @return the shell
	 */
	public native final String getShell() /*-{
		return this.shell;
	}-*/;
	/**
	 * @return the description
	 */
	public native final String getDescription() /*-{
		return this.description;
	}-*/;
	/**
	 * @return the version
	 */
	public native final String getVersion() /*-{
		return this.version;
	}-*/;
	
	/**
	 * @return the ownerName
	 */
	public native final String getOwnerName() /*-{
		return this.ownerName;
	}-*/;
	
	/**
	 * @return the preferred
	 */
	public native final boolean isPreferred() /*-{
		return this['preferred']==null?false:this['preferred']=="true";
	}-*/;
	
	/**
	 * @return the icon
	 */
	public native final String getIcon() /*-{
		return this.icon;
	}-*/;
	/**
	 * @return the outputFiles
	 */
	public final List<FileSpecification> getOutputFiles() {
		JavaScriptObject jsa = outputFiles();
		return JsList.asList(jsa);
	}
	public native final JavaScriptObject outputFiles() /*-{
		var array = [];
		if(this.outputFiles) {
			if(this.outputFiles.length) {
				array = this.outputFiles;
			} else {
				array[0] = this.outputFiles;
			}
		}
		return array;
	}-*/;


	/**
	 * @return the inputFiles
	 */
	public final List<FileSpecification> getInputFiles() {
		JavaScriptObject jsa = inputFiles();
		return JsList.asList(jsa);
	}
	public native final JavaScriptObject inputFiles() /*-{
		var array = [];
		if(this.inputFiles) {
			if(this.inputFiles.length) {
				array = this.inputFiles;
			} else {
				array[0] = this.inputFiles;
			}
		}
		return array;
	}-*/;
	/**
	 * @return the executionParameters
	 */
	public final List<TypedParameter> getExecutionParameters() {
		JavaScriptObject jsa = executionParameters();
		return JsList.asList(jsa);
	}
	public native final JavaScriptObject executionParameters() /*-{
		var array = [];
		if(this.executionParameters) {
			if(this.executionParameters.length) {
				array = this.executionParameters;
			} else {
				array[0] = this.executionParameters;
			}
		}
		return array;
	}-*/;
	/**
	 * @return the cloudProfiles
	 */

	public final List<CommandCloudAccount> getCloudAccounts() {
		JavaScriptObject jsa = cloudAccounts();
		return JsList.asList(jsa);
	}
	public native final JavaScriptObject cloudAccounts() /*-{
		var array = [];
		if(this.cloudAccounts) {
			if(this.cloudAccounts.length) {
				array = this.cloudAccounts;
			} else {
				array[0] = this.cloudAccounts;
			}
		}
		return array;
	}-*/;
	
	public static final native Collection<Command> asCollection(String assumedSafe) /*-{
	return eval("("+assumedSafe+")");
	// return JSON.parse(assumedSafe);
	}-*/;
	public static final native Command asCommand(String assumedSafe) /*-{
	return eval("("+assumedSafe+")");
	// return JSON.parse(assumedSafe);
	}-*/;

}
