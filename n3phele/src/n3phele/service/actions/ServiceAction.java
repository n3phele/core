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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.core.NotFoundException;
import n3phele.service.lifecycle.ProcessLifecycle;
import n3phele.service.lifecycle.ProcessLifecycle.WaitForSignalRequest;
import n3phele.service.model.Action;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.Context;
import n3phele.service.model.SignalKind;
import n3phele.service.model.core.Helpers;
import n3phele.service.model.core.User;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.Unindex;


/** Manages execution of a job or finite task.
 * 
 * Manages the lifecycle of a single child and exits when child completes. 
 * System Ensures that any VM resources created by a child passed to parent on child exit or terminated if no parent.
 * JobAction will dump children handed to it
 * 
 * Supplies context to the child
 * Optionally notifies owner on termination
 * 
 * @author Nigel Cook
 *
 */
@EntitySubclass
@XmlRootElement(name = "ServiceAction")
@XmlType(name = "ServiceAction", propOrder = { "actionName", "childProcess" })
@Unindex
@Cache
public class ServiceAction extends Action {
	final private static java.util.logging.Logger log = java.util.logging.Logger.getLogger(ServiceAction.class.getName()); 
	
	private String actionName;	
	private String childProcess;
	
	public ServiceAction() {}
	
	protected ServiceAction(User owner, String name, Context context) {
		super(owner.getUri(), name, context);
	}
	
	@Override
	public void init() throws Exception {
		this.actionName = this.getContext().getValue("action");

		String arg = this.getContext().getValue("arg");
		String[] argv;
		if(Helpers.isBlankOrNull(arg)) {
			argv = new String[0];
		} else {
			argv =	arg.split("[\\s]+");	// FIXME - find a better regex for shell split
		}
		
		Context childEnv = new Context();
		childEnv.putAll(this.getContext());
		childEnv.remove("name");
		
		if(Helpers.isBlankOrNull(this.actionName) && argv.length > 0) {
			this.actionName = argv[0];
			childEnv.putValue("action", this.actionName);
		}
		
		String childName = this.getName()+"."+this.actionName;

		StringBuilder newArg = new StringBuilder();
		for(int i = 1; i < argv.length; i++) {
			if(i != 1)
				newArg.append(" ");
			newArg.append(argv[i]);
		}
		childEnv.putValue("arg", newArg.toString());
		CloudProcess child = processLifecycle().spawn(this.getOwner(), childName, childEnv, null, this.getProcess(), this.actionName);
		processLifecycle().init(child);
		log.info("Created child "+child.getName()+" "+child.getUri()+" Action "+child.getAction());
		this.childProcess = child.getUri().toString();
	}
	

	@Override
	public boolean call() throws WaitForSignalRequest {
		log.warning("Call");
		throw new WaitForSignalRequest();	
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
	public void signal(SignalKind kind, String assertion) throws NotFoundException {
		log.info("Signal "+kind+":"+assertion);
		switch(kind) {
		case Adoption:
			log.info(assertion+" adoption");
			return;
		case Cancel:
			log.info(assertion+" cancelled");
			break;
		case Event:
			log.warning("Ignoring event "+assertion);
			return;
		case Failed:
			log.info(assertion+" failed");
			break;
		case Ok:
			log.info(assertion+" ok");
			break;
		default:
			return;		
		}
	}
	

	/**
	 * @return the actionName
	 */
	public String getActionName() {
		return actionName;
	}

	/**
	 * @param actionName the actionName to set
	 */
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	/**
	 * @return the childProcess
	 */
	public URI getChildProcess() {
		return Helpers.stringToURI(childProcess);
	}

	/**
	 * @param childProcess the childProcess to set
	 */
	public void setChildProcess(URI childProcess) {
		this.childProcess = childProcess.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("ServiceAction [actionName=%s, childProcess=%s, %s]",
						actionName, childProcess,
						super.toString());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((actionName == null) ? 0 : actionName.hashCode());
		result = prime * result
				+ ((childProcess == null) ? 0 : childProcess.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServiceAction other = (ServiceAction) obj;
		if (actionName == null) {
			if (other.actionName != null)
				return false;
		} else if (!actionName.equals(other.actionName))
			return false;
		if (childProcess == null) {
			if (other.childProcess != null)
				return false;
		} else if (!childProcess.equals(other.childProcess))
			return false;
		return true;
	}

	/*
	 * Unit Testing
	 * ============
	 */
	
	protected ProcessLifecycle processLifecycle() {
		return ProcessLifecycle.mgr();
	}
}
	
