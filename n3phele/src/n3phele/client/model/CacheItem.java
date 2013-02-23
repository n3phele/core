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


public class CacheItem {
	private Kind kind;
	private Object object;
	private String id;

	
	
	public enum Kind {
		Progress
	}



	/**
	 * @param kind the kind to set
	 */
	public void setKind(Kind kind) {
		this.kind = kind;
	}



	/**
	 * @return the kind
	 */
	public Kind getKind() {
		return kind;
	}



	/**
	 * @return the object
	 */
	public Object getObject() {
		return object;
	}



	/**
	 * @param object the object to set
	 */
	public void setObject(Object object) {
		this.object = object;
	}



	public String getId() {
		return id;
	}



	
	
}
