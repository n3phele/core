/**
 * @author Nigel Cook
 * @author Leonardo Amado
 * @author Douglas Tondin
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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;

public class AssimilateVMAction extends Entity {
	protected AssimilateVMAction() {
	}

	/**
	 * @return the description
	 */
	public native final String getTargetIP() /*-{
		return this.targetIP;
	}-*/;

	public static final native Collection<AssimilateVMAction> asCollection(
			String assumedSafe) /*-{
		return eval("(" + assumedSafe + ")");
		// return JSON.parse(assumedSafe);
	}-*/;

	public static final native AssimilateVMAction asAction (String assumedSafe) /*-{
		return eval("(" + assumedSafe + ")");
		// return JSON.parse(assumedSafe);
	}-*/;

}
