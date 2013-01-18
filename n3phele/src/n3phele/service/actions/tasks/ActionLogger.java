package n3phele.service.actions.tasks;

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
import n3phele.service.rest.impl.NarrativeResource;



public class ActionLogger {
	private static Logger debug = Logger.getLogger(ActionLogger.class.getName()); 
	private URI progressUri;
	private String id;
	
	private ActionLogger() {};
	
	public ActionLogger(URI progress, String id) {
		this.progressUri= progress;
		this.id = id;
	}
	
	public ActionLogger(ActionLogger log, String id) {
		this.progressUri = log.progressUri;
		this.id = id;
	}

	public String log(NarrativeLevel state, String message) {
		try {
			NarrativeResource.dao.addNarrative(progressUri, id, state, message);
		} catch (Exception e) {
			debug.log(Level.SEVERE, String.format("Narrative %s %s %s %s failed %s", progressUri, id, state, message, e.getMessage()),e);
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
			debug.log(Level.SEVERE, String.format("Narrative %s %s", progressUri, msgId),e);
		}
		return null;
	}

	
	public String getMessage(Long msgId) {
		return NarrativeResource.dao.getNarrative(msgId);
	}
	
	public static ActionLogger name(Action me) {
		return new ActionLogger(me.getProcess(), me.getName());
	}
	
	public ActionLogger withName(String name) {
		this.id = name;
		return this;
	}
	
	public static ActionLogger withProcess(URI uri) {
		ActionLogger x = new ActionLogger();
		x.progressUri = uri;
		return x;
	}
}
