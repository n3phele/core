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



public class Cloud extends Entity {
	
	protected Cloud() {}

	/**
	 * @return the description
	 */
	public native final String getDescription() /*-{
		return this.description;
	}-*/;

	/**
	 * @return the location
	 */
	public native final String getLocation() /*-{
		return this.location;
	}-*/;

	/**
	 * @return the factory
	 */
	public native final String getFactory() /*-{
		return this.factory;
	}-*/;

	/**
	 * @return the inputParameters
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
		return array;
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
		return array;
	}-*/;
	
	public static final native Collection<Cloud> asCollection(String assumedSafe) /*-{
	return eval("("+assumedSafe+")");
	// return JSON.parse(assumedSafe);
	}-*/;
	public static final native Cloud asCloud(String assumedSafe) /*-{
	return eval("("+assumedSafe+")");
	// return JSON.parse(assumedSafe);
	}-*/;
}
