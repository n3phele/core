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



public class ActionLogger {
	private static Logger debug = Logger.getLogger(ActionLogger.class.getName()); 
	private String id;
	private String processUri;

	
	public ActionLogger() {} ;
	public ActionLogger(Action action) {
		URI loggerUri = Helpers.stringToURI(action.getContext().getValue("logger"));
		if(loggerUri == null) {
			action.getContext().putValue("logger", action.getProcess());
			loggerUri = action.getProcess();
		}
		this.processUri= Helpers.URItoString(loggerUri);
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

	public String log(NarrativeLevel state, String message) {
		try {
			debug.log(Level.INFO, String.format("===>Narrative %s %s %s %s", processUri, id, state, message));

			NarrativeResource.dao.addNarrative(Helpers.stringToURI(processUri), id, state, message);
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
}
