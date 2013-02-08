package n3phele.service.lifecycle;
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
import static com.googlecode.objectify.ObjectifyService.ofy;

import java.net.URI;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import n3phele.service.core.NotFoundException;
import n3phele.service.model.Action;
import n3phele.service.model.ActionState;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.SignalKind;
import n3phele.service.model.core.User;
import n3phele.service.rest.impl.ActionResource;
import n3phele.service.rest.impl.CloudProcessResource;
import n3phele.service.rest.impl.UserResource;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.Work;

public class ProcessLifecycle {
	final private static java.util.logging.Logger log = java.util.logging.Logger.getLogger(ProcessLifecycle.class.getName()); 

	protected ProcessLifecycle() {}
	
	public CloudProcess createProcess(User user, String name, n3phele.service.model.Context context, List<URI> dependency, CloudProcess parent, Class<? extends Action> clazz) throws IllegalArgumentException {
		Action action;
		try {
			action = clazz.newInstance().create(user.getUri(), name, context);
		} catch (InstantiationException e) {
			log.log(Level.SEVERE, "Class "+clazz, e);
			throw new IllegalArgumentException(e);
		} catch (IllegalAccessException e) {
			log.log(Level.SEVERE, "Class "+clazz, e);
			throw new IllegalArgumentException(e);
		}
		ActionResource.dao.add(action);	
		CloudProcess process = new CloudProcess(user.getUri(), name, parent, action);
		CloudProcessResource.dao.add(process);
		action.setProcess(process.getUri());
		ActionResource.dao.update(action);
		setDependentOn(process.getUri(), dependency);
		return process;
	}
	
	/*
	 * Process execution management
	 */
	
	/** Refresh the status of active actions
	 * @return Map of states of active cloud processes, and count of processes in those states.
	 */
	public Map<String, Long> periodicScheduler() {
		Map<String, Long> counter = new HashMap<String, Long>();
		
		for(CloudProcess process : CloudProcessResource.dao.getNonfinalized()) {
			ActionState processState = process.getState();
			String active = process.getRunning()!=null?"_Running":"";
			String wait = process.getWaitTimeout()!=null?"_Wait":"";
			Long count = 0L;
			if(counter.containsKey(processState.toString()+active+wait))
				count = counter.get(processState.toString()+active+wait);
			count = count + 1;
			counter.put(processState.toString()+active+wait, count);
			log.info("Process "+process.getUri()+" "+processState+" "+process.getRunning());
			if(process.getRunning() == null && processState != ActionState.NEWBORN) {
				if(process.hasPending() || processState == ActionState.RUNABLE)
					schedule(process);
			} 
		} 
		
		return counter;
	}
	
	/** Places a task on to the run queue
	 * @param process
	 * @return TRUE if process queued
	 */
	public boolean schedule(CloudProcess process) {
		final URI processURI = process.getUri();
		final Long processId = process.getId();
		final Key<CloudProcess> processRoot = process.getRoot();
		return CloudProcessResource.dao.transact(new Work<Boolean>() {

			@Override
			public Boolean run() {
				boolean result = false;
				boolean dirty = false;
				CloudProcess process = CloudProcessResource.dao.load(processRoot, processId);
				if(process.getRunning() == null) {
					if(process.getWaitTimeout() != null) {
						Date now = new Date();
						if(process.hasPendingAssertions() || now.after(process.getWaitTimeout())) {
							process.setWaitTimeout(null);
							dirty = true;
						}
					}
					
					if(process.getWaitTimeout() == null && !process.isPendingCall() && !(process.isPendingInit() || process.isPendingCancel() || process.isPendingDump()) ) {
						dirty = true;
						process.setPendingCall(true);
					}
					if(process.hasPending()) {
						process.setRunning(new Date());
						QueueFactory.getDefaultQueue().add(ofy().getTxn(),
								TaskOptions.Builder.withPayload(new Schedule(processURI)));
						result = true;
						dirty = true;
					}
					if(dirty)
						CloudProcessResource.dao.update(process);
				}
				return result;
			}});
		
	}
	private static class Schedule implements DeferredTask {
		private static final long serialVersionUID = 1L;
		final private URI process;
		public Schedule(URI process) {
			this.process = process;
		}

		@Override
		public void run(){
			CloudProcessResource.dao.clear();
			try {
				mgr().dispatch(process);
			} catch (Exception e) {
				log.log(Level.SEVERE, "Dispatch exception", e);
			}
		}
	}
	
	
	
	/** Dispatches execution to a task's entry points
	 * @param processId
	 */
	private void dispatch(final URI processId) {
		
		NextStep dispatchCode = CloudProcessResource.dao.transact(new Work<NextStep>() {
		    public NextStep run() {
		        CloudProcess process = CloudProcessResource.dao.load(processId);
				if(process.isFinalized()) {
					log.warning("Processing called on process "+processId+" finalized="+process.isFinalized()+" state="+process.getState());
					return new NextStep(DoNext.nothing);
				}
				
				if(process.isPendingCancel() || process.isPendingDump()) { 
					process.setState(ActionState.CANCELLED);
			       CloudProcessResource.dao.update(process);
			        if(process.isPendingCancel()) { 
			        	process.setPendingCancel(false);
			        	process.setPendingDump(false);
			        	return new NextStep(DoNext.cancel);
			        } else {
			        	process.setPendingCancel(false);
			        	process.setPendingDump(false);
			        	return new NextStep(DoNext.dump);
			        } 
				} else if(process.isPendingInit()) {
					process.setPendingInit(false);
			        CloudProcessResource.dao.update(process);
			        return new NextStep(DoNext.init);
				} else if(process.hasPendingAssertions()) {
						String assertion = process.getPendingAssertion().remove(0);
						 CloudProcessResource.dao.update(process);
						return new NextStep(DoNext.assertion, assertion);
				} else if(process.isPendingCall()) {
						process.setPendingCall(false);
						CloudProcessResource.dao.update(process);
						return new NextStep(DoNext.call);	
				}
		        return new NextStep(DoNext.nothing);
		} });
		CloudProcess process = CloudProcessResource.dao.load(processId);
		Action task = null;
		try {
			task = ActionResource.dao.load(process.getAction());
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to access task "+process.getAction(), e);
			toFailed(process);
			return;
		}
		log.info("Dispatch "+dispatchCode+": process "+process.getName()+" "+process.getUri()+" task "+task.getUri());
		
		boolean error = false;
		switch(dispatchCode.todo){
		case assertion:
			try {
				
				int index = dispatchCode.assertion.indexOf(":");
				SignalKind kind = SignalKind.valueOf(dispatchCode.assertion.substring(0,index));
				task.signal(kind, dispatchCode.assertion.substring(index+1));
			} catch (Exception e) {
				log.log(Level.WARNING, "Assertion "+ dispatchCode.assertion + " exception for process "+process.getUri()+" task "+task.getUri(), e);
			}
			writeOnChange(task);
			endOfTimeSlice(process);
			break;
		case init:
			try {
				task.init();
			} catch (Exception e) {
				log.log(Level.WARNING, "Init exception for process "+process.getUri()+" task "+task.getUri(), e);
				error = true;
			}
			writeOnChange(task);
			if(error) {
				toFailed(process);
			} else {
				endOfTimeSlice(process);
			}
			break;
		case call:
			boolean complete = false;
			try {
				complete = task.call();
			} catch (WaitForSignalRequest e) {
				toWait(process, e.getTimeout());
			} catch (Exception e) {
				log.log(Level.WARNING, "Call exception for process "+process.getUri()+" task "+task.getUri(), e);
				error = true;
			}
			writeOnChange(task);
			if(error) {
				toFailed(process);
				break;
			} else if(complete) {
				toComplete(process);
			} else {
				endOfTimeSlice(process);
			}
			break;
		case cancel:
			try {
				task.cancel();
			} catch (Exception e) {
				log.log(Level.WARNING, "Cancel exception for process "+process.getUri()+" task "+task.getUri(), e);
			}
			writeOnChange(task);
			toCancelled(process);
			break;
		case dump:
			try {
				task.dump();
			} catch (Exception e) {
				log.log(Level.WARNING, "Dump exception for process "+process.getUri()+" task "+task.getUri(), e);
			}
			writeOnChange(task);
			toCancelled(process);
			break;
		case nothing:
		default:
			log.severe("Nothing to do for "+process.getName()+":"+process.toString());
			toFailed(process);
			break;
		}
		
	}
	
	/* -------------------------------------------------------------------------------------------------
	 * The following routines manage the lifecycle changes associated with the transaction of a process
	 * to a particular state.
	 * ------------------------------------------------------------------------------------------------
	 */
	
	/** Moves a process to the "complete" state, signifying error free completion of processing
	 * @param process
	 */
	private void toComplete(final CloudProcess process) {
		final Long processId = process.getId();
		final Key<CloudProcess> processRoot = process.getRoot();
		log.info("Complete "+process.getName()+":"+process.getUri());
		giveChildrenToParent(process);
		final List<String> dependents = CloudProcessResource.dao.transact(new Work<List<String>>() {
			@Override
			public List<String> run() {
				CloudProcess targetProcess = CloudProcessResource.dao.load(processRoot, processId);
				logExecutionTime(targetProcess);
				targetProcess.setState(ActionState.COMPLETE);
				targetProcess.setComplete(new Date());
				targetProcess.setRunning(null);
				targetProcess.setFinalized(true);
				if(targetProcess.getParent() != null) {
					CloudProcess parent;
					try {
						parent = CloudProcessResource.dao.load(targetProcess.getParent());
						signal(parent, SignalKind.Ok, targetProcess.getUri().toString());
					} catch (NotFoundException e) {
						log.severe("Unknown parent "+targetProcess);
					} catch (Exception e) {
						log.log(Level.SEVERE, "Signal failure "+targetProcess, e);
					}
					
				}
				CloudProcessResource.dao.update(targetProcess);
				return targetProcess.getDependencyFor();
			}
		});
		if(dependents != null && dependents.size() > 0) {
			for(String dependent : dependents) {
				signalDependentProcessIsComplete(process, URI.create(dependent));
			}
		}
	}
	
	/** Signals to a process that its dependency has now successfully completed
	 * @param process that completed successfully
	 * @param depender process awaiting dependency completion
	 */
	private void signalDependentProcessIsComplete(CloudProcess process, final URI depender ) {
		final URI processUri = process.getUri();
		CloudProcessResource.dao.transact(new VoidWork(){
			@Override
			public void vrun() {
			String hasFinalized = processUri.toString();
			CloudProcess dprocess = CloudProcessResource.dao.load(depender);
			if(dprocess.getDependentOn() != null && dprocess.getDependentOn().size() > 0) {
				boolean found = dprocess.getDependentOn().remove(hasFinalized);
				if(found) {
					if(dprocess.getDependentOn().size() == 0 && dprocess.getState() != ActionState.NEWBORN && !dprocess.isFinalized()) {
						if(dprocess.getState() == ActionState.INIT) {
								dprocess.setPendingInit(true);
						} 
						dprocess.setState(ActionState.RUNABLE);
						CloudProcessResource.dao.update(dprocess);
						schedule(dprocess);
					}
				} else {
					if(dprocess.getState() == ActionState.NEWBORN){
						log.warning("**HANDLED RACE CONDITION** Dependency "+hasFinalized+" not found in "+dprocess.getUri());
					} else {
						log.severe("Dependency "+hasFinalized+" not found in "+dprocess.getUri());
					}
					
				}
			}				
		}});
		
	}
	
	/** Blocks process execution block until successful completion of dependent process
	 * @param process the process to block awaiting successful completion
	 * @param dependent the process that when complete unblocks execution of process 
	 * @return true if the dependency causes the process to block
	 * @throws IllegalArgumentException
	 */
	public boolean addDependentOn(final CloudProcess process, CloudProcess dependent) throws IllegalArgumentException {
		return setDependentOn(process.getUri(), Arrays.asList(dependent.getUri()));
	}
	
	/** Adds a set of dependency such that process runs dependent on the successful execution of dependentOnList members
	 * @param process 
	 * @param dependentOnList
	 * @return TRUE if dependency causes process execution to block
	 * @throws IllegalArgumentException if the dependency is non-existent or has terminated abnormally
	 */
	public boolean setDependentOn(URI process, List<URI> dependentOnList) {
		if(dependentOnList == null || dependentOnList.isEmpty()) return false;
		boolean result = false;
		int chunkSize = 4;
		for(int i = 0; i < dependentOnList.size(); i += chunkSize) {
			int lim = (i + chunkSize) < dependentOnList.size()? i+ chunkSize : dependentOnList.size();
			boolean chunkResult = setListOfDependentOn(process, dependentOnList.subList(i, lim));
			result = result || chunkResult;
		}
		return result;
		
	}
	
	/** Adds a set of dependency such that process runs dependent on the successful execution of dependentOnList members
	 * @param process 
	 * @param dependentOnList
	 * @return TRUE if dependency causes process execution to block
	 * @throws IllegalArgumentException if the dependency is non-existent or has terminated abnormally
	 */
	private boolean setListOfDependentOn(final URI process, final List<URI> dependentOnList) {
		
		return CloudProcessResource.dao.transact(new Work<Boolean>(){

			@Override
			public Boolean run() {
				boolean willBlock = false;
				CloudProcess depender = CloudProcessResource.dao.load(process);
				if(depender.isFinalized()) {
					log.warning("Cannot add dependencies to finalized process "+process+" state "+depender.getState());
					throw new IllegalArgumentException("Cannot add dependencies to finalized process "+process+" state "+depender.getState());
				}
				for(URI dependentOn : dependentOnList) {
					CloudProcess dependency;
					try {
						dependency = CloudProcessResource.dao.load(dependentOn);
					} catch (NotFoundException e) {
						throw new IllegalArgumentException("Dependency does exist "+dependentOn,e);
					}
					if(dependency.isFinalized()) {
						if(dependency.getState().equals(ActionState.COMPLETE)) {
							log.warning(dependentOn+" already finalized, removing dependency constraint from "+process);
						} else {
							throw new IllegalArgumentException("Process "+process+" has a dependency on "+dependentOn+" which is "+dependency.getState());
						}
					} else {
						if(!depender.getDependentOn().contains(dependency.getUri().toString())) {
							dependency.getDependencyFor().add(depender.getUri().toString());
							depender.getDependentOn().add(dependency.getUri().toString());
							CloudProcessResource.dao.update(dependency);
							willBlock = true;
						} else {
							log.severe(dependentOn+" already in list for "+process);
						}
						
					}
				}
				CloudProcessResource.dao.update(depender);
				return willBlock;
			}});
	}
		
	/** Set process to having terminated with processing failure. The process parent is notified of
	 * the failure.
	 * @param process
	 */
	private void toFailed(CloudProcess process) {
		final Long processId = process.getId();
		final Key<CloudProcess> processRoot = process.getRoot();
		giveChildrenToParent(process);
		List<String> dependents = CloudProcessResource.dao.transact(new Work<List<String>>() {

			@Override
			public List<String> run() {
				CloudProcess targetProcess = CloudProcessResource.dao.load(processRoot, processId);
				List<String> result = null;
				if(!targetProcess.isFinalized()) {
					logExecutionTime(targetProcess);
					targetProcess.setState(ActionState.FAILED);
					targetProcess.setComplete(new Date());
					targetProcess.setRunning(null);
					targetProcess.setFinalized(true);
					if(targetProcess.getParent() != null) {
						CloudProcess parent;
						try {
							parent = CloudProcessResource.dao.load(targetProcess.getParent());
							signal(parent, SignalKind.Failed, targetProcess.getUri().toString());
						} catch (NotFoundException e) {
							log.severe("Unknown parent "+targetProcess);
						} catch (Exception e) {
							log.log(Level.SEVERE, "Signal failure "+targetProcess, e);
						}
					}
					result = targetProcess.getDependencyFor();
					CloudProcessResource.dao.update(targetProcess);
				} else {
					log.warning("Failed process "+targetProcess.getUri()+" is finalized");
				}
				return result;
			}});
		if(dependents != null && dependents.size() > 0) {
			for(String dependent : dependents) {
				CloudProcess dprocess = CloudProcessResource.dao.load(URI.create(dependent));
				if(!dprocess.isFinalized()) {
					toCancelled(dprocess);
				}
			}
		}
	}
	
	private void giveChildrenToParent(final CloudProcess process) {
		URI uri = process.getParent();
		CloudProcess parent=null;
		while(uri != null) {
			parent = CloudProcessResource.dao.load(uri);
			if(!parent.isFinalized()) break;
			uri = parent.getParent();
		}
		final URI parentURI = uri;
		/*
		 * 
		 */
		boolean moreToDo = true;
		while(moreToDo) {
			moreToDo = CloudProcessResource.dao.transact(new Work<Boolean>(){

			@Override
			public Boolean run() {
				boolean goAgain = false;
				CloudProcess parent = null;
				if(parentURI != null) {
					parent = CloudProcessResource.dao.load(parentURI);
					if(parent.isFinalized()) {
						log.warning("Parent "+parent.getUri()+" is finalized");
						throw new IllegalArgumentException("Parent "+parent.getUri()+" is finalized");
					}
				}
				List<CloudProcess> children = getNonfinalizedChildren(process);
				boolean parentUpdate = false;
				int chunk = 4;
				for(CloudProcess childProcess : children) {
					if(chunk-- < 1) {
						goAgain = true;
						break;
					}
					if(parent != null) {
						childProcess.setParent(parentURI);
						CloudProcessResource.dao.update(childProcess);
						final String typedAssertion = SignalKind.Adoption+":"+childProcess.getUri().toString();
						if(!parent.getPendingAssertion().contains(typedAssertion)) {
							parent.getPendingAssertion().add(typedAssertion);
							parentUpdate = true;
						}
					} else {
						childProcess.setPendingCancel(true);
						childProcess.setParent(parentURI);
						CloudProcessResource.dao.update(childProcess);
						schedule(childProcess);
					}
				}
				if(parentUpdate) {
					CloudProcessResource.dao.update(parent);
				}
				return goAgain;
			}});
		}
		if(parentURI != null)
			schedule(parent);
		
	}
	
	
	/** Set process to the wait state
	 * 
	 * @param process
	 */
	private void toWait(CloudProcess process, final Date timeout) {
		final Long processId = process.getId();
		final Key<CloudProcess> processRoot = process.getRoot();
		CloudProcessResource.dao.transact(new VoidWork() {

			@Override
			public void vrun() {
				log.info("toWait");
				CloudProcess targetProcess = CloudProcessResource.dao.load(processRoot, processId);
				logExecutionTime(targetProcess);
				targetProcess.setRunning(null);
				if(!targetProcess.isFinalized()) {
					targetProcess.setWaitTimeout(timeout);
					if(targetProcess.getState() == ActionState.RUNABLE && targetProcess.hasPending()) {
						CloudProcessResource.dao.update(targetProcess);
						log.warning("Wait Re-queue process "+targetProcess.getId());
						schedule(targetProcess);
					} else {
						CloudProcessResource.dao.update(targetProcess);
					}
				} else {
					log.warning("Wait process "+targetProcess.getUri()+" is finalized");
				}
			}});

	}
	
	/** Re-evaluate process state at normal end of execution. 
	 * @param process
	 */
	private void endOfTimeSlice(CloudProcess process) {
		final Long processId = process.getId();
		final Key<CloudProcess> processRoot = process.getRoot();
		CloudProcessResource.dao.transact(new VoidWork() {

			@Override
			public void vrun() {
				log.info("endOfTimeSlice");
				CloudProcess targetProcess = CloudProcessResource.dao.load(processRoot, processId);
				logExecutionTime(targetProcess);
				targetProcess.setRunning(null);
				if(!targetProcess.isFinalized()) {
					if(targetProcess.getDependentOn() != null && targetProcess.getDependentOn().size() > 0) {
						targetProcess.setState(ActionState.BLOCKED);
					} else if(targetProcess.getState() == ActionState.RUNABLE && targetProcess.hasPending()) {
						CloudProcessResource.dao.update(targetProcess);
						log.warning("Re-queue process "+targetProcess.getId());
						schedule(targetProcess);
						return;
					}
				} 
				CloudProcessResource.dao.update(targetProcess);
			}});
	}
	
	
	/** Move process to cancelled state.
	 * The process parent is notified of the cancellation.
	 * @param process
	 */
	private void toCancelled(CloudProcess process) {
		final Long processId = process.getId();
		final Key<CloudProcess> processRoot = process.getRoot();
		List<String> dependents = CloudProcessResource.dao.transact(new Work<List<String>>() {

			@Override
			public List<String> run() {
				CloudProcess targetProcess = CloudProcessResource.dao.load(processRoot, processId);
				List<String> result = null;
				if(!targetProcess.isFinalized()) {
					if(targetProcess.getState().equals(ActionState.BLOCKED)){
						targetProcess.setPendingCancel(true);
						CloudProcessResource.dao.update(targetProcess);
						schedule(targetProcess);
					} else {
						logExecutionTime(targetProcess);
						targetProcess.setState(ActionState.CANCELLED);
						targetProcess.setComplete(new Date());
						if(targetProcess.getStart() == null) {
							targetProcess.setStart(targetProcess.getComplete());
						}
						targetProcess.setRunning(null);
						targetProcess.setFinalized(true);
						if(targetProcess.getParent() != null) {
							CloudProcess parent;
							try {
								parent = CloudProcessResource.dao.load(targetProcess.getParent());
								signal(parent, SignalKind.Cancel, targetProcess.getUri().toString());
							} catch (NotFoundException e) {
								log.severe("Unknown parent "+targetProcess);
							} catch (Exception e) {
								log.log(Level.SEVERE, "Signal failure "+targetProcess, e);
							}
						}
						result = targetProcess.getDependencyFor();
						CloudProcessResource.dao.update(targetProcess);
					}
					
				} else {
					log.warning("Cancelled process "+targetProcess.getUri()+" is finalized");
				}
				return result;
			}});
		if(dependents == null) {
			schedule(process);
		} else if(dependents.size() > 0) {
			for(String dependent : dependents) {
				CloudProcess dprocess = CloudProcessResource.dao.load(URI.create(dependent));
				if(!dprocess.isFinalized()) {
					toCancelled(dprocess);
				}
			}
		}
	}
	
	public CloudProcess spawn(URI owner, String name, n3phele.service.model.Context context, 
								     List<URI> dependency, URI parentURI, String className) throws IllegalArgumentException, NotFoundException, ClassNotFoundException {

		String canonicalClassName = "n3phele.service.actions."+className+"Action";

		User user;
		CloudProcess parent = null;
		try {
			user = UserResource.dao.load(owner);
		} catch (NotFoundException e) {
			log.warning("Cant find owner "+owner);
			throw e;
		}
		
		try {
			if(parentURI != null)
				parent = CloudProcessResource.dao.load(parentURI);
		} catch (NotFoundException e) {
			log.warning("Cant find parent "+parentURI);
			throw e;
		}
		
		CloudProcess process = this.createProcess(user, 
				name, context, dependency, parent, 
				Class.forName(canonicalClassName).asSubclass(Action.class));
		return process;
	}

	
	/** cancel running an existing process.
	 * Causes the existing process to stop current processing and to close and free any resources that the
	 * process is currently using.
	 * 
	 */
	public void cancel(CloudProcess process) {
		log.info("cancel "+process.getUri());
		final Long processId = process.getId();
		final Key<CloudProcess> groupId = process.getRoot();
		CloudProcessResource.dao.transact(new VoidWork() {
			@Override
			public void vrun() {
				CloudProcess targetProcess = CloudProcessResource.dao.load(groupId, processId);
				if(!targetProcess.isFinalized()) {
					targetProcess.setPendingCancel(true);
					CloudProcessResource.dao.update(targetProcess);
				} else {
					log.severe("Cancel on finalized process "+targetProcess.getUri());
				}
				schedule(targetProcess);
			}});
	}

	public void init(CloudProcess process) {
		log.info("init "+process.getUri()+" "+process.getName());
		final Long processId = process.getId();
		final Key<CloudProcess> processRoot = process.getRoot();
		CloudProcessResource.dao.transact(new VoidWork() {
			@Override
			public void vrun() {
				CloudProcess targetProcess = CloudProcessResource.dao.load(processRoot, processId);
				if(!targetProcess.isFinalized() && targetProcess.getState() == ActionState.NEWBORN) {
					if(targetProcess.getDependentOn() != null && targetProcess.getDependentOn().size() != 0) {
						targetProcess.setState(ActionState.INIT);
						CloudProcessResource.dao.update(targetProcess);
					} else {
						targetProcess.setState(ActionState.RUNABLE);
						targetProcess.setPendingInit(true);
						targetProcess.setPendingCall(true);
						/*
						 * NB: There is an assumption that:
						 * 1. The objectify get operation will fetch the modified process object
						 *    in the schedule transaction
						 * 2. The pendingInit will cause schedule to write the CloudProcess object
						 */
						schedule(targetProcess); 
					}
				} else {
					log.severe("Init on finalized or non-newborn process "+targetProcess.getUri()+" "+targetProcess.getState());
				}
			}});
	}
	
	/** Dump a running process, which causes the process to be stopped current processing and 
	 * to save diagnostic information that
	 * can be later reviews.
	 * 
	 */
	public void dump(CloudProcess process) {
		final Long processId = process.getId();
		final Key<CloudProcess> processRoot = process.getRoot();
		log.info("dump "+process.getUri());
		CloudProcessResource.dao.transact(new VoidWork() {
			@Override
			public void vrun() {
				CloudProcess targetProcess = CloudProcessResource.dao.load(processRoot, processId);
				if(!targetProcess.isFinalized()) {
					targetProcess.setPendingDump(true);
					CloudProcessResource.dao.update(targetProcess);
				} else {
					log.severe("Dump on finalized process "+targetProcess.getUri());
				}
				schedule(targetProcess);
			}});
		
	}
	
	/** Dump a running process, which causes the process to be stopped current processing and 
	 * to save diagnostic information that
	 * can be later reviews.
	 * 
	 */
	public void dump(URI processId) throws NotFoundException {

		CloudProcess process = CloudProcessResource.dao.load(processId);
		dump(process);
		
	}
	
	/** 
	 * @param jobAction
	 * @throws WaitForSignalRequest 
	 */
	public void waitForSignal() throws WaitForSignalRequest {
		throw new WaitForSignalRequest();
	}
	
	public static class WaitForSignalRequest extends Exception {
		private static final long serialVersionUID = 1L;
		private final Date timeout;
		
		public WaitForSignalRequest() {
			this(Calendar.HOUR, 1);
		}
		
		public WaitForSignalRequest(int unit, int count) {
			Calendar alarm = Calendar.getInstance();
			alarm.add(unit, count);
			timeout = alarm.getTime();
		}
		
		public Date getTimeout() {
			return this.timeout;
		}
	}
	

	/** Signals a process with an assertion.
	 * @param process
	 * @param kind
	 * @param assertion
	 */
	public void signal(CloudProcess process, SignalKind kind, String assertion) {
		this.signal(process.getUri(), kind, assertion);
	}
	
	/** Signals a process with an assertion.
	 * @param cloudProcessURI
	 * @param kind
	 * @param assertion
	 */
	public void signal(final URI cloudProcessURI, final SignalKind kind, final String assertion) {
		log.info("signal <"+kind+":"+assertion+"> to "+cloudProcessURI);
		CloudProcessResource.dao.transact(new VoidWork() {
			@Override
			public void vrun() {
				CloudProcess p = CloudProcessResource.dao.load(cloudProcessURI);
				if(!p.isFinalized()) {
					if(!p.getPendingAssertion().contains(kind+":"+assertion)) {
						p.getPendingAssertion().add(kind+":"+assertion);
						CloudProcessResource.dao.update(p);
						if(p.getState() == ActionState.RUNABLE) {
							schedule(p);
						}
					}
				} else {
					log.severe("Signal <"+kind+":"+assertion+"> on finalized process "+p.getUri());
				}

			}});
	}
	
	/** Signals a process with a set of assertion.
	 * @param cloudProcessURI
	 * @param kind
	 * @param assertion
	 */
	private void signal(final URI cloudProcessURI, final SignalKind kind, final List<String> assertionList) {
		log.info("signal <"+kind+":"+assertionList+"> to "+cloudProcessURI);
		CloudProcessResource.dao.transact(new VoidWork() {
			@Override
			public void vrun() {
				CloudProcess p = CloudProcessResource.dao.load(cloudProcessURI);
				boolean updated = false;
				if(!p.isFinalized()) {
					for(String assertion : assertionList) {
						if(!p.getPendingAssertion().contains(kind+":"+assertion)) {
							p.getPendingAssertion().add(kind+":"+assertion);
						}
					}
					if(updated) {
						CloudProcessResource.dao.update(p);
						if(p.getState() == ActionState.RUNABLE) {
							schedule(p);
						}
					}
				} else {
					log.severe("Signal <"+kind+":"+assertionList+"> on finalized process "+p.getUri());
				}

			}});
	}
	
	/** Signals a process parent with an assertion.
	 * @param cloudProcessURI
	 * @param kind
	 * @param assertion
	 */
	public void signalParent(final URI childProcessURI, final SignalKind kind, final String assertion) {
		log.info("signal <"+kind+":"+assertion+"> to parent of "+childProcessURI);
		CloudProcessResource.dao.transact(new VoidWork() {
			@Override
			public void vrun() {
				CloudProcess child = CloudProcessResource.dao.load(childProcessURI);
				if(child.getParent() != null) {
					log.info("signal <"+kind+":"+assertion+"> to parent "+child.getParent());
					CloudProcess p = CloudProcessResource.dao.load(child.getParent());
					if(!p.isFinalized()) {
						if(!p.getPendingAssertion().contains(kind+":"+assertion)) {
							p.getPendingAssertion().add(kind+":"+assertion);
							CloudProcessResource.dao.update(p);
							if(p.getState() == ActionState.RUNABLE) {
								schedule(p);
							}
						}
					} else {
						log.warning("Signal <"+kind+":"+assertion+"> on finalized process "+p.getUri());
					}
				} else {
					log.info("signal <"+kind+":"+assertion+"> "+childProcessURI+" has no parent");
				}

			}});
	}
	
	/*
	 * Helpers
	 * =======
	 */
	private enum DoNext {
		nothing,
		cancel,
		dump,
		init,
		assertion,
		call
	}
	
	private static class NextStep {
		public String assertion;
		public DoNext todo;
		public NextStep(DoNext todo, String assertion) {
			this.todo = todo;
			this.assertion = assertion;
		}
		
		public NextStep(DoNext todo) {
			this.todo = todo;
		}
		
		public String toString() {
			if(todo == DoNext.assertion) {
				return todo+"<"+assertion+">";
			} else {
				return ""+todo;
			}
		}
	}
	
	
	private void logExecutionTime(CloudProcess process) {
		Date started = process.getRunning();
		if(started != null) {
			Date now = new Date();
			Long duration = now.getTime() - started.getTime();
			log.info(process.getName()+" "+process.getUri()+" executed "+duration+" milliseconds");
		}
	}
	
	private boolean writeOnChange(final Action action) {
		boolean result = ActionResource.dao.transact(new Work<Boolean>() {
			@Override
			public Boolean run() {
				Action db = ActionResource.dao.load(action.getUri());
				if(!db.equals(action)) {
					ActionResource.dao.update(action);
					return true;
				}
				return false;
			}});
		log.info("Action "+action.getName()+" "+action.getUri()+" write "+result);
		return result;
	}
	
	private List<CloudProcess> getNonfinalizedChildren(CloudProcess process) {
		Key<CloudProcess> root = process.getRoot();
		if(root == null) {
			root = Key.create(process);
		}
		return ofy().load().type(CloudProcess.class).ancestor(root)
				.filter("parent", process.getUri().toString())
				.filter("finalized", false).list();
	}
	
	
	private final static ProcessLifecycle processLifecycle = new ProcessLifecycle();
	public static ProcessLifecycle mgr() {
		return processLifecycle;
	}

}
