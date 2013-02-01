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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.Unindex;

import n3phele.service.model.Action;
import n3phele.service.model.Context;
import n3phele.service.model.NarrativeLevel;
import n3phele.service.model.SignalKind;
import n3phele.service.model.core.User;

@EntitySubclass
@XmlRootElement(name = "LogAction")
@XmlType(name = "LogAction", propOrder = { "count" })
@Unindex
@Cache
public class LogAction extends Action {
	final private static java.util.logging.Logger log = java.util.logging.Logger.getLogger(LogAction.class.getName()); 
	private ActionLogger logger;
	public LogAction() {}
	
	protected LogAction(User owner, String name,
			Context context) {
		super(owner.getUri(), name, context);
	}
	
	@Override
	public void init() throws Exception {
		logger = new ActionLogger(this);
		log.fine(this.getContext().getValue("arg"));
		
		String arg = this.getContext().getValue("arg");
		NarrativeLevel level = NarrativeLevel.info;
		if(arg.startsWith("--info ")) {
			level = NarrativeLevel.info;
			arg = arg.substring("--info ".length()).trim();
		} else if(arg.startsWith("--success ")) {
			level = NarrativeLevel.success;
			arg = arg.substring("--success ".length()).trim();
		} if(arg.startsWith("--warning ")) {
			level = NarrativeLevel.warning;
			arg = arg.substring("--warning ".length()).trim();
		} if(arg.startsWith("--error ")) {
			level = NarrativeLevel.error;
			arg = arg.substring("--error ".length()).trim();
		} 
		
		logger.log(level, arg);
	}

	@Override
	public boolean call() throws Exception {
		return true;
	}

	@Override
	public void cancel() {
		log.warning("Cancel");
		
	}

	@Override
	public void dump() {
		log.warning("Dump");
		
	}

	@Override
	public void signal(SignalKind kind, String assertion) {
		log.warning("Signal "+assertion);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("LogAction [%s]",
				super.toString());
	}

	
}
