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
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
import n3phele.service.rest.impl.CloudProcessResource;
import n3phele.service.rest.impl.UserResource;

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
@XmlRootElement(name = "JobAction")
@XmlType(name = "JobAction", propOrder = { "notify", "actionName", "childProcess", "childComplete", "childEndState" })
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
			switch(this.childEndState) {
			case CANCELLED:
				notifyOwner(true, "Processing was cancelled");
				break;
			case FAILED:
				notifyOwner(true, "Processing encountered a failure");
				break;
			default:
				notifyOwner(false, "");
				break;
			}
			
			return true;
		}
		
			
	}

	@Override
	public void cancel() {
		log.warning("Cancel");
		ProcessLifecycle.mgr().dump(URI.create(this.childProcess));
		this.childEndState = ActionState.CANCELLED;
		notifyOwner(true, "Processing was requested to be cancelled");
		
	}

	@Override
	public void dump() {
		log.warning("Dump");
		ProcessLifecycle.mgr().dump(URI.create(this.childProcess));
		this.childEndState = ActionState.CANCELLED;
		notifyOwner(true, "Processing was requested to be cancelled, and a diagnostic dump taken");
		
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
	
	private void notifyOwner(boolean failure, String reason) {
		if(notify) {
			sendNotificationEmail(failure, reason);
		}
	}
	
	public void sendNotificationEmail(boolean failure, String reason) {
		User user = null;
		try {
			URI owner = this.getOwner();
			if (owner == null || owner.equals(UserResource.Root.getUri()))
				return;
			user = UserResource.dao.load(owner);
			StringBuilder subject = new StringBuilder();
			StringBuilder body = new StringBuilder();
			if (failure) {
				subject.append("FAILURE: ");
				subject.append(this.getName());
				body.append(user.getFirstName());
				body.append(",\nAn error has occured processing your activity named ");
				body.append(this.getName());
				body.append(". The problem description is \n\n");
				body.append(reason);
				body.append("\n\nMore details are available at https://n3phele.appspot.com\n\nn3phele");
			} else {
				subject.append(this.getName());
				subject.append(" has completed.");
				body.append(user.getFirstName());
				body.append(",\n\nYour activity named ");
				body.append(this.getName());
				body.append(" has completed sucessfully.\n\nTotal elapsed time was ");
				CloudProcess process = CloudProcessResource.dao.load(this.getChildProcess());
				Date start = process.getStart();
				Date finished = process.getComplete();
				Long duration = finished.getTime() - start.getTime();
				if (duration > 1000 * 60 * 60 * 24) {
					double days =  Math.round(10 * duration / (1000 * 60 * 60 * 24)) / 10.0;
					body.append(Double.toString(days));
					body.append(days > 1 ? " days" : " day");
				} else if (duration > 1000 * 60 * 60) {
					double hours = Math.round(10 * duration
							/ (1000 * 60 * 60.0)) / 10.0;
					body.append(Double.toString(hours));
					body.append(hours > 1.0 ? " hours" : " hour");
				} else if (duration >= 1000 * 60) {
					double minutes = Math.round(10 * duration
							/ (1000 * 60.0)) / 10.0;
					body.append(Double.toString(minutes));
					body.append(minutes > 1.0 ? " minutes" : " minute");

				} else if (duration >= 1000) {
					double seconds = Math.round(10 * duration / (1000.0)) / 10.0;
					body.append(Double.toString(seconds));
					body.append(seconds > 1.0 ? " seconds" : " second");
				} else {
					body.append(Long.toString(duration));
					body.append(duration > 1 ? " milliseconds"
							: " millisecond");
				}
				body.append(".\n\nn3phele\n--\nhttps://n3phele.appspot.com\n\n");
			}

			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);

			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress("n3phele@gmail.com", "n3phele"));
			msg.addRecipient(Message.RecipientType.TO,
					new InternetAddress(user.getName(), user.getFirstName()
							+ " " + user.getLastName()));
			msg.setSubject(subject.toString());
			msg.setText(body.toString());
			Transport.send(msg);
		} catch (AddressException e) {
			log.log(Level.SEVERE,
					"Email to " + user.getName() + " " + user.getUri(), e);
		} catch (MessagingException e) {
			log.log(Level.SEVERE,
					"Email to " + user.getName() + " " + user.getUri(), e);
		} catch (UnsupportedEncodingException e) {
			log.log(Level.SEVERE,
					"Email to " + user.getName() + " " + user.getUri(), e);
		} catch (Exception e) {
			log.log(Level.SEVERE,
					"Email for activity " + this.getUri(), e);
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