package n3phele.service.actions;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import n3phele.service.model.Action;
import n3phele.service.model.NarrativeLevel;
import n3phele.service.model.core.Helpers;
import n3phele.service.rest.impl.NarrativeResource;

import com.googlecode.objectify.annotation.Embed;


@Embed
public class ActionLogger {
	private static Logger debug = Logger.getLogger(ActionLogger.class.getName()); 
	private String id;
	private String processUri;
	private String group;

	
	public ActionLogger() {} ;
	public ActionLogger(Action action) {
		URI groupUri = Helpers.stringToURI(action.getContext().getValue("logger"));
		if(groupUri == null) {
			this.setGroup(action);
		} else {
			this.group = groupUri.toString();
		}
		
		this.processUri= Helpers.URItoString(action.getProcess());
		this.id = action.getName();
	};
	
	public ActionLogger(URI process, String id) {
		this.processUri= Helpers.URItoString(process);
		this.id = id;
	}
	
	public ActionLogger(ActionLogger log, String id) {
		this.processUri = log.processUri;
		this.id = id;
	}
	
	public void setGroup(Action action) {
		URI groupUri = action.getProcess();
		action.getContext().putValue("logger", groupUri);
		this.group = groupUri.toString();
	}

	public String log(NarrativeLevel state, String message) {
		try {
			debug.log(Level.INFO, String.format("Narrative %s %s %s %s", processUri, id, state, message));

			NarrativeResource.dao.addNarrative(URI.create(processUri), URI.create(group), id, state, message);
		} catch (Exception e) {
			debug.log(Level.SEVERE, String.format("Narrative %s %s %s %s failed %s", processUri, id, state, message, e.getMessage()),e);
		}
		return null;
	}
	
	public String error(String message) {
		return log(NarrativeLevel.error, message);
	}
	
	public String success(String message) {
		return log(NarrativeLevel.success, message);
	}
	
	public String warning(String message) {
		return log(NarrativeLevel.warning, message);
	}

	public String info(String message) {
		return log(NarrativeLevel.info, message);
		
	}
	
	public Long updateMessage(Long msgId, String newText) {
		try {
			return NarrativeResource.dao.updateNarrativeText(msgId, newText);
		} catch (Exception e) {
			debug.log(Level.SEVERE, String.format("Narrative %s %s", processUri, msgId),e);
		}
		return null;
	}

	
	public String getMessage(Long msgId) {
		return NarrativeResource.dao.getNarrative(msgId);
	}
	
	public static ActionLogger forAction(Action me) {
		return new ActionLogger(me.getProcess(), me.getName());
	}
	
	public ActionLogger withName(String name) {
		this.id = name;
		return this;
	}
	
	public ActionLogger withProcess(URI uri) {
		this.processUri = Helpers.URItoString(uri);
		return this;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		result = prime * result
				+ ((this.processUri == null) ? 0 : this.processUri.hashCode());
		result = prime
				* result
				+ ((this.group == null) ? 0 : this.group.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActionLogger other = (ActionLogger) obj;
		if (this.id == null) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		if (this.processUri == null) {
			if (other.processUri != null)
				return false;
		} else if (!this.processUri.equals(other.processUri))
			return false;
		if (this.group == null) {
			if (other.group != null)
				return false;
		} else if (!this.group.equals(other.group))
			return false;
		return true;
	}

}
