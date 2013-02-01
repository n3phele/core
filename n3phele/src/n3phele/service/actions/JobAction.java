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

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.Unindex;

import n3phele.service.core.NotFoundException;
import n3phele.service.lifecycle.ProcessLifecycle;
import n3phele.service.lifecycle.ProcessLifecycle.WaitForSignalRequest;
import n3phele.service.model.Action;
import n3phele.service.model.ActionState;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.Context;
import n3phele.service.model.SignalKind;
import n3phele.service.model.core.Helpers;
import n3phele.service.model.core.User;
import n3phele.service.rest.impl.UserResource;


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
@XmlRootElement(name = "JobAction")
@XmlType(name = "JobAction", propOrder = { "notify", "actionName", "childProcess", "childComplete", "childStatus" })
@Unindex
@Cache
public class JobAction extends Action {
	final private static java.util.logging.Logger log = java.util.logging.Logger.getLogger(JobAction.class.getName()); 
	
	private boolean notify = false;									/* notify owner of job issues or completion if TRUE */
	private String actionName;										/* task class name */
	private String childProcess;									/* job process URI */
	private boolean childComplete = false;							/* child completed */
	private ActionState childEndState = null;
	
	
	public JobAction() {}
	
	protected JobAction(User owner, String name, Context context) {
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
		
		if(Helpers.isBlankOrNull(this.actionName) && argv.length > 0) {
			this.actionName = argv[0];
			childEnv.putValue("action", this.actionName);
		}
		
		String childName = childEnv.getValue("name");
		if(Helpers.isBlankOrNull(childName)) {
			childName = this.getName()+"."+this.actionName;
			childEnv.putValue("name", childName);
		}

		StringBuilder newArg = new StringBuilder();
		for(int i = 1; i < argv.length; i++) {
			if(i != 1)
				newArg.append(" ");
			newArg.append(argv[i]);
		}
		childEnv.putValue("arg", newArg.toString());
		CloudProcess child = ProcessLifecycle.mgr().spawn(this.getOwner(), childName, childEnv, null, this.getProcess(), this.actionName);
		ProcessLifecycle.mgr().init(child);
		log.info("Created child "+child.getName()+" "+child.getUri()+" Action "+child.getAction());
		this.childProcess = child.getUri().toString();
	}
	

	@Override
	public boolean call() throws WaitForSignalRequest {
		log.warning("Call");
		if(!childComplete) {
			ProcessLifecycle.mgr().waitForSignal();
			return false; // never executed
		} else {
			notifyOwner();
			return true;
		}
		
			
	}

	@Override
	public void cancel() {
		log.warning("Cancel");
		ProcessLifecycle.mgr().dump(URI.create(this.childProcess));
		this.childEndState = ActionState.CANCELLED;
		notifyOwner();
		
	}

	@Override
	public void dump() {
		log.warning("Dump");
		ProcessLifecycle.mgr().dump(URI.create(this.childProcess));
		this.childEndState = ActionState.CANCELLED;
		notifyOwner();
		
	}



	@Override
	public void signal(SignalKind kind, String assertion) throws NotFoundException {
		log.info("Signal "+kind+":"+assertion);
		boolean isChild = this.childProcess.equals(assertion);
		switch(kind) {
		case Adoption:
			log.info((isChild?"Child ":"Unknown ")+assertion+" adoption");
			URI processURI = URI.create(assertion);
			ProcessLifecycle.mgr().dump(processURI);
			return;
		case Cancel:;
			log.info((isChild?"Child ":"Unknown ")+assertion+" cancelled");
			if(isChild) {
				this.childEndState = ActionState.CANCELLED;
			}
			break;
		case Event:
			log.warning("Ignoring event "+assertion);
			return;
		case Failed:
			log.info((isChild?"Child ":"Unknown ")+assertion+" failed");
			if(isChild) {
				this.childEndState = ActionState.FAILED;
			}
			break;
		case Ok:
			log.info((isChild?"Child ":"Unknown ")+assertion+" ok");
			if(isChild) {
				this.childEndState = ActionState.COMPLETE;
			}
			break;
		default:
			return;		
		}
		childComplete = isChild;
	}
	
	private void notifyOwner() {
		if(notify) {
			User owner = UserResource.dao.load(this.getOwner());
			String email = owner.getName();
			log.severe("*********** Unimplemented email notificaiton to "+owner.getFirstName()+" email "+email+" end state "+childEndState);
		}
	}

	/**
	 * @return the notify
	 */
	public boolean isNotify() {
		return notify;
	}
	
	/**
	 * @return the notify
	 */
	public boolean getNotify() {
		return notify;
	}

	/**
	 * @param notify the notify to set
	 */
	public void setNotify(boolean notify) {
		this.notify = notify;
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

	/**
	 * @return the childComplete
	 */
	public boolean isChildComplete() {
		return childComplete;
	}
	
	/**
	 * @return the childComplete
	 */
	public boolean getChildComplete() {
		return childComplete;
	}

	/**
	 * @param childComplete the childComplete to set
	 */
	public void setChildComplete(boolean childComplete) {
		this.childComplete = childComplete;
	}

	/**
	 * @return the childEndState
	 */
	public ActionState getChildEndState() {
		return childEndState;
	}

	/**
	 * @param childEndState the childEndState to set
	 */
	public void setChildEndState(ActionState childEndState) {
		this.childEndState = childEndState;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("JobAction [%s, notify=%s, actionName=%s, childProcess=%s, childComplete=%s, childEndState=%s]",
						super.toString(), notify, actionName, childProcess, childComplete,
						childEndState );
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
		result = prime * result + (childComplete ? 1231 : 1237);
		result = prime * result
				+ ((childEndState == null) ? 0 : childEndState.hashCode());
		result = prime * result
				+ ((childProcess == null) ? 0 : childProcess.hashCode());
		result = prime * result + (notify ? 1231 : 1237);
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
		JobAction other = (JobAction) obj;
		if (actionName == null) {
			if (other.actionName != null)
				return false;
		} else if (!actionName.equals(other.actionName))
			return false;
		if (childComplete != other.childComplete)
			return false;
		if (childEndState != other.childEndState)
			return false;
		if (childProcess == null) {
			if (other.childProcess != null)
				return false;
		} else if (!childProcess.equals(other.childProcess))
			return false;
		if (notify != other.notify)
			return false;
		return true;
	}


	
}