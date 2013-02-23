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
import n3phele.client.model.JsList;

public class ActionSpecification extends JavaScriptObject {
	protected ActionSpecification() {}
	/**
	 * @return the name
	 */
	public native final String getName() /*-{
		return this.name;
	}-*/;
	/**
	 * @return the description
	 */
	public native final String getDescription() /*-{
		return this.description;
	}-*/;
	/**
	 * @return the workloadKey
	 */
	public native final String getWorkloadKey() /*-{
		return this.workloadKey;
	}-*/;
	/**
	 * @return the action
	 */
	public native final String getAction() /*-{
		return this.action;
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
		if(this.outputFiles != undefined && this.outputFiles !=null) {
			if(this.outputFiles.length==undefined) {
				array[0] = this.outputFiles;
			} else {
				array = this.outputFiles;
			}
		}
		return this.outputFiles;
	}-*/;

	/**
	 * @return the outputParameters
	 */
	public final List<TypedParameter> getOutputParameters() {
		JavaScriptObject jsa = outputParameters();
		return JsList.asList(jsa);
	}
	public native final JavaScriptObject outputParameters() /*-{
		var array = [];
		if(this.outputParameters != undefined && this.outputParameters !=null) {
			if(this.outputParameters.length==undefined) {
				array[0] = this.outputParameters;
			} else {
				array = this.outputParameters;
			}
		}
		return this.outputParameters;
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
		if(this.inputFiles != undefined && this.inputFiles !=null) {
			if(this.inputFiles.length==undefined) {
				array[0] = this.inputFiles;
			} else {
				array = this.inputFiles;
			}
		}
		return this.inputFiles;
	}-*/;
	/**
	 * @return the executionParameters
	 */
	public final List<TypedParameter> getInputParameters() {
		JavaScriptObject jsa = inputParameters();
		return JsList.asList(jsa);
	}
	public native final JavaScriptObject inputParameters() /*-{
		var array = [];
		if(this.inputParameters != undefined && this.inputParameters !=null) {
			if(this.inputParameters.length==undefined) {
				array[0] = this.inputParameters;
			} else {
				array = this.inputParameters;
			}
		}
		return this.inputParameters;
	}-*/;
	
}
