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

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Unindex;


import n3phele.service.model.core.Entity;
import n3phele.service.model.core.User;

import static n3phele.service.model.core.Helpers.stringToURI;
import static n3phele.service.model.core.Helpers.URItoString;

@XmlRootElement(name="CloudProcess")
@XmlType(name="Action", propOrder={"id","state", "running", "waitTimeout", "pendingInit", "pendingCall", "pendingCancel", "pendingDump", "pendingAssertion", 
		"dependentOn", "dependencyFor", "start", "complete", "finalized", "task", "parent"})
@Unindex
@com.googlecode.objectify.annotation.Entity
public class CloudProcess extends Entity {
	//final private static java.util.logging.Logger log = java.util.logging.Logger.getLogger(CloudProcess.class.getName()); 
	@Id private Long id;
	private ActionState state = ActionState.NEWBORN;
	private Date running = null;
	private Date waitTimeout = null;
	private boolean pendingInit = false;
	private boolean pendingCall = false;
	private boolean pendingCancel = false;
	private boolean pendingDump = false;
	private ArrayList<String> pendingAssertion = new ArrayList<String>();
	private ArrayList<String> dependentOn = new ArrayList<String>();
	private ArrayList<String> dependencyFor = new ArrayList<String>();
	private Date start = null;
	private Date complete = null;
	@Index private boolean finalized = false;
	private String task = null;
	@Index private String parent = null;
	
	
	public CloudProcess() {}
	/** Describes a cloud process. 
	 * @param owner process owner
	 * @param parent Parent to be notified on process state changes
	 * @param taskId Reference to the task managed by the process
	 */
	public CloudProcess(User owner, String name, URI parent, Action task)  {
		super(name, null, "", owner.getUri(), false);
		this.task = task.getUri().toString();
		this.parent = URItoString(parent);
	}
	
	/*
	 * Getters and Settings
	 */
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * @return the state
	 */
	public ActionState getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(ActionState state) {
		this.state = state;
	}
	
	/**
	 * @return the date process with queue to run
	 */
	public Date getRunning() {
		return running;
	}
	/**
	 * @param running the date process with queue to run
	 */
	public void setRunning(Date running) {
		this.running = running;
	}
	
	/**
	 * @return the waitTimeout
	 */
	public Date getWaitTimeout() {
		return waitTimeout;
	}
	/**
	 * @param waitTimeout the waitTimeout to set
	 */
	public void setWaitTimeout(Date waitTimeout) {
		this.waitTimeout = waitTimeout;
	}
	/**
	 * @return the pendingInit
	 */
	public boolean isPendingInit() {
		return pendingInit;
	}
	/**
	 * @return the pendingInit
	 */
	public boolean getPendingInit() {
		return pendingInit;
	}
	/**
	 * @param pendingInit the pendingInit to set
	 */
	public void setPendingInit(boolean pendingInit) {
		this.pendingInit = pendingInit;
	}
	/**
	 * @return the pendingCall
	 */
	public boolean isPendingCall() {
		return pendingCall;
	}
	/**
	 * @return the pendingCall
	 */
	public boolean getPendingCall() {
		return pendingCall;
	}
	/**
	 * @param pendingCall the pendingCall to set
	 */
	public void setPendingCall(boolean pendingCall) {
		this.pendingCall = pendingCall;
	}
	/**
	 * @return the pendingCancel
	 */
	public boolean isPendingCancel() {
		return pendingCancel;
	}
	/**
	 * @return the pendingCancel
	 */
	public boolean getPendingCancel() {
		return pendingCancel;
	}
	/**
	 * @param pendingCancel the pendingCancel to set
	 */
	public void setPendingCancel(boolean pendingCancel) {
		this.pendingCancel = pendingCancel;
	}
	/**
	 * @return the pendingDump
	 */
	public boolean isPendingDump() {
		return pendingDump;
	}
	/**
	 * @return the pendingDump
	 */
	public boolean getPendingDump() {
		return pendingDump;
	}
	/**
	 * @param pendingDump the pendingDump to set
	 */
	public void setPendingDump(boolean pendingDump) {
		this.pendingDump = pendingDump;
	}
	
	/**
	 * @return the pendingAssertion
	 */
	public List<String> getPendingAssertion() {
		return pendingAssertion;
	}
	/**
	 * @param pendingAssertion the pendingAssertion to set
	 */
	public void setPendingAssertion(List<String> pendingAssertion) {
		this.pendingAssertion.clear();
		this.pendingAssertion.addAll(pendingAssertion);
	}
	/**
	 * @return TRUE if process has pending assertions
	 */
	public boolean hasPendingAssertions() {
		return this.pendingAssertion.size() != 0;
	}
	
	/**
	 * @return TRUE if has currently pending actions
	 */
	public boolean hasPending() {
		return pendingInit || pendingCall || pendingCancel || pendingDump || pendingAssertion.size() != 0;
	}
	/**
	 * @return the dependentOn
	 */
	public ArrayList<String> getDependentOn() {
		return dependentOn;
	}
	/**
	 * @param dependentOn the dependentOn to set
	 */
	public void setDependentOn(ArrayList<String> dependentOn) {
		this.dependentOn = dependentOn;
	}
	/**
	 * @return the dependencyFor
	 */
	public ArrayList<String> getDependencyFor() {
		return dependencyFor;
	}
	/**
	 * @param dependencyFor the dependencyFor to set
	 */
	public void setDependencyFor(ArrayList<String> dependencyFor) {
		this.dependencyFor = dependencyFor;
	}
	/**
	 * @return the start
	 */
	public Date getStart() {
		return start;
	}
	/**
	 * @param start the start to set
	 */
	public void setStart(Date start) {
		this.start = start;
	}
	/**
	 * @return the complete
	 */
	public Date getComplete() {
		return complete;
	}
	/**
	 * @param complete the complete to set
	 */
	public void setComplete(Date complete) {
		this.complete = complete;
	}
	/**
	 * @return the finalized
	 */
	public boolean isFinalized() {
		return finalized;
	}
	/**
	 * @param finalized the finalized to set
	 */
	public void setFinalized(boolean finalized) {
		this.finalized = finalized;
	}
	/**
	 * @return the task
	 */
	public URI getTask() {
		return stringToURI(task);
	}
	/**
	 * @param task the task to set
	 */
	public void setTask(URI task) {
		this.task = URItoString(task);
	}
	/**
	 * @return the parent
	 */
	public URI getParent() {
		return stringToURI(parent);
	}
	/**
	 * @param parent the parent to set
	 */
	public void setParent(URI parent) {
		this.parent = URItoString(parent);
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("CloudProcess [name=%s, uri=%s, mime=%s, owner=%s, isPublic=%s, state=%s, running=%s, waitTimeout=%s, pendingInit=%s, pendingCall=%s, pendingCancel=%s, pendingDump=%s, pendingAssertion=%s, dependentOn=%s, dependencyFor=%s, start=%s, complete=%s, finalized=%s, task=%s, parent=%s]",
						name, uri, mime,
						owner, isPublic, state, running, waitTimeout,
						pendingInit, pendingCall, pendingCancel, pendingDump,
						pendingAssertion, dependentOn, dependencyFor, start,
						complete, finalized, task, parent );
	}

}
