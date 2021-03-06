package n3phele.service.model;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.Serialize;

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
@Embed
public class CommandImplementationDefinition {
	
	private String name;
	private String annotation;
	private Text body;
	private int lineNo;
	@Serialize private List<ShellFragment> compiled = new ArrayList<ShellFragment>();
	
	public CommandImplementationDefinition() {}

	public CommandImplementationDefinition(String name, String annotation,
			String body, int lineNo) {
		this.name = name;
		this.annotation = annotation;
		this.body = new Text(body);
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
		return body.getValue();
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = new Text(body);
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

	
	
	/**
	 * @return the compiled
	 */
	public List<ShellFragment> getCompiled() {
		return compiled;
	}

	/**
	 * @param compiled the compiled to set
	 */
	public void setCompiled(List<ShellFragment> compiled) {
		this.compiled.clear();
		this.compiled.addAll(compiled);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("CommandImplementationDefinition [name=%s, annotation=%s, body=%s, lineNo=%s, compiled=%s]",
						name, annotation, body, lineNo, compiled);
	}

}
