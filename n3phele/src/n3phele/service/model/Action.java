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
import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.lifecycle.ProcessLifecycle.WaitForSignalRequest;
import n3phele.service.model.core.Entity;
import n3phele.service.model.core.Helpers;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.OnLoad;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Serialize;
import com.googlecode.objectify.annotation.Unindex;

@XmlRootElement(name = "Action")
@XmlType(name = "Action", propOrder = {"contextList", "process" })
@Unindex
@Cache
@com.googlecode.objectify.annotation.Entity
public abstract class Action extends Entity {

	@Id private Long id;
	private String process;
	@Serialize
	private ArrayList<Variable> contextList = new ArrayList<Variable>();
	@Ignore
	protected Context context = new Context();
	@OnLoad void makeMap() {  
		for(Variable v : this.contextList) { 
			if(v != null)
				this.context.put(v.getName(), v); 
		} 
	}
	@OnSave void saveMap() { 
		this.contextList.clear(); if(this.context.size() != 0) this.contextList.addAll(context.values()); 
	}
	
	protected Action() {}
	
	protected Action(URI owner, String name, Context context) {
		super();
		this.owner = owner.toString();
		this.name = name;
		this.context = context;
		this.isPublic = false;
	}
	
	public Action create(URI owner, String name, Context context) {
		this.owner = owner.toString();
		this.name = name;
		this.context = context;
		this.isPublic = false;
		 return this;
	}
	
	/** Initialize a process for execution. The initialization phase can cause initial resource to be
	 * allocated by the process. A task will not be initialized until all dependencies
	 * has successfully completed processing.
	 * @throws Exception
	 */
	public abstract void init() throws Exception;
	
	/** Invokes the process for a running timeslice and returns information regarding on going processing.
	 * When a process completes, all processes that have nominated this process as a dependency are notified.
	 * A process is only initialized and scheduled to run when all nominated dependency processes have completed
	 * without error. 
	 * @return True if the process has completed processing, False if additional time slices are needed
	 * @throws WaitForSignalRequest thrown when CloudProcessResource.waitForSignal() is called
	 * @throws Exception on process error preventing additional processing
	 */

	public abstract boolean call() throws WaitForSignalRequest, Exception;

	/** cancel running an existing process.
	 * Causes the existing process to stop current processing and to close and free any resources that the
	 * process is currently using.
	 * 
	 */
	public abstract void cancel();
	
	/** Dump a running process, which causes the process to be stopped current processing and 
	 * to save diagnostic information that
	 * can be later reviews.
	 * 
	 */
	public abstract void dump();
	
	/** Signals a process with an assertion.
	 * @param assertion
	 */
	public abstract void signal(SignalKind kind, String assertion);
	
	/** Returns a description of the action instance
	 * @return description
	 */
	public abstract String getDescription();
	
	/** Returns a Command instance associated with the action
	 * @return description
	 */
	public abstract Command getPrototype();

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((process == null) ? 0 : process.hashCode());
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
		Action other = (Action) obj;
		if (context == null) {
			if (other.context != null)
				return false;
		} else if (!context.equals(other.context))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (process == null) {
			if (other.process != null)
				return false;
		} else if (!process.equals(other.process))
			return false;
		return true;
	}

	/**
	 * @return the id
	 */
	@XmlTransient
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
	 * @return the action context
	 */
	@XmlTransient
	public Context getContext() {
		return context == null? (context = new Context()) : context;
	}

	/**
	 * @param context the context for the action
	 */
	public void setContext(Context context) {
		this.context = context;
	}
	
	/**
	 * @return the action context
	 */
	@XmlElement(name="context")
	public Collection<Variable> getContextList() {
		return this.context.values();
	}

	/**
	 * @param context the context for the action
	 */
	public void setContextList(Collection<Variable> context) {
		this.context.clear();
		for(Variable v: context) {
			this.context.put(v.getName(), v);
		}
	}
	

	/**
	 * @return the process controlling the action
	 */
	public URI getProcess() {
		return Helpers.stringToURI(process);
	}

	/**
	 * @param process the process controlling the action
	 */
	public void setProcess(URI process) {
		this.process = Helpers.URItoString(process);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("Action [id=%s, uri=%s, name=%s, context=%s, owner=%s, isPublic=%s, process=%s]",
						id, uri, name, context, owner, isPublic, process);
	}
}
