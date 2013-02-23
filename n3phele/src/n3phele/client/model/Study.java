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

public class Study extends Entity {
	
	protected Study() {}
	/**
	 * @return the description
	 */
	public native final String getDescription() /*-{
		return this.description;
	}-*/;
	
	/**
	 * @return the dataSet
	 */
	public final List<DataSetElement> getDataSet() {	
		JavaScriptObject jsa = dataSet();
		return JsList.asList(jsa);
		
	}
	protected final native JavaScriptObject dataSet() /*-{
		var array = [];
		if(this.dataSet != undefined && this.dataSet!=null) {
			if(this.dataSet.length==undefined) {
				array[0] = this.dataSet;
			} else {
				array = this.dataSet;
			}
		}
		return array;

	}-*/;
	
	/**
	 * @return the dataSet
	 */
	public final List<DataSetElement> getResults() {	
		JavaScriptObject jsa = results();
		return JsList.asList(jsa);
		
	}
	protected final native JavaScriptObject results() /*-{
		var array = [];
		if(this.results != undefined && this.results!=null) {
			if(this.results.length==undefined) {
				array[0] = this.results;
			} else {
				array = this.results;
			}
		}
		return array;

	}-*/;
	
	/**
	 * @return the dataSet
	 */
	public final List<Activity> getAnalysis() {	
		JavaScriptObject jsa = analysis();
		return JsList.asList(jsa);
		
	}
	protected final native JavaScriptObject analysis() /*-{
		var array = [];
		if(this.analysis != undefined && this.analysis!=null) {
			if(this.analysis.length==undefined) {
				array[0] = this.analysis;
			} else {
				array = this.analysis;
			}
		}
		return array;

	}-*/;
	
	public static final native Collection<Study> asCollectionOfStudy(String assumedSafe) /*-{
	return eval("("+assumedSafe+")");
	// return JSON.parse(assumedSafe);
	}-*/;
	public static final native Study asStudy(String assumedSafe) /*-{
	return eval("("+assumedSafe+")");
	// return JSON.parse(assumedSafe);
	}-*/;
	
}
	
	
	