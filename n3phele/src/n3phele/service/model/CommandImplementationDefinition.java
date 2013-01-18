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
public class CommandImplementationDefinition {
	
	private String name;
	private String annotation;
	private String body;
	private int lineNo;

	public CommandImplementationDefinition(String name, String annotation,
			String body, int lineNo) {
		this.name = name;
		this.annotation = annotation;
		this.body = body;
		this.lineNo = lineNo;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the annotation
	 */
	public String getAnnotation() {
		return annotation;
	}

	/**
	 * @param annotation the annotation to set
	 */
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @return the lineNo
	 */
	public int getLineNo() {
		return lineNo;
	}

	/**
	 * @param lineNo the lineNo to set
	 */
	public void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("CommandImplementationDefinition [name=%s, annotation=%s, body=%s, lineNo=%s]",
						name, annotation, body, lineNo);
	}

}
