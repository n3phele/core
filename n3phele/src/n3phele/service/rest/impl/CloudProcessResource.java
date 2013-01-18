package n3phele.service.rest.impl;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import n3phele.service.core.NotFoundException;
import n3phele.service.core.Resource;
import n3phele.service.model.Action;
import n3phele.service.model.ActionState;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.ServiceModelDao;
import n3phele.service.model.SignalKind;
import n3phele.service.model.core.AbstractManager;
import n3phele.service.model.core.BaseEntity;
import n3phele.service.model.core.Collection;
import n3phele.service.model.core.GenericModelDao;
import n3phele.service.model.core.Helpers;
import n3phele.service.model.core.User;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.Work;

@Path("/process")
public class CloudProcessResource {
	final private static java.util.logging.Logger log = java.util.logging.Logger.getLogger(CloudProcessResource.class.getName()); 

	
	protected @Context UriInfo uriInfo;
	protected @Context SecurityContext securityContext;

	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	public Collection<BaseEntity> list(
			@DefaultValue("false") @QueryParam("summary") Boolean summary)  {

		Collection<BaseEntity> result = dao.getCollection(UserResource.toUser(securityContext)).collection(summary);
		return result;
	}
	
	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	@Path("{id}/children")
	public CloudProcess[] listChildren( @PathParam ("id") Long id)  {

		CloudProcess parent;
		try {
			parent = dao.load(id, UserResource.toUser(securityContext));
		} catch (NotFoundException e) {
			throw e;
		}

		java.util.Collection<CloudProcess> result = dao.getChildren(parent.getUri());
		return result.toArray(new CloudProcess[result.size()]);
	}

	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	@Path("{id}") 
	public CloudProcess get( @PathParam ("id") Long id) throws NotFoundException {

		CloudProcess item = dao.load(id, UserResource.toUser(securityContext));
		return item;
	}
	
	@DELETE
	@Produces("application/json")
	@RolesAllowed("authenticated")
	@Path("{id}") 
	public Response killProcess( @PathParam ("id") Long id) throws NotFoundException {

		CloudProcess process = null;
		try {
			process = dao.load(id, UserResource.toUser(securityContext));
		} catch (NotFoundException e) {
			return Response.status(Status.GONE).build();
		}
		cancel(process);
		return Response.status(Status.NO_CONTENT).build();
	}
		
	/*
	 * This is an eventing endpoint that can be invoked by an http request with
	 * no authentication.
	 */
	@GET
	@Produces("text/plain")
	@Path("{id}/event") 
	public Response event( @PathParam ("id") Long id) {

		log.info(String.format("Event %s", uriInfo.getRequestUri().toString()));
		CloudProcess a = null;
		try {
			a = dao.load(id);
		} catch (NotFoundException e) {
			return Response.status(Status.GONE).build();
		}
		signal(a, SignalKind.Event, uriInfo.getRequestUri().toString());
		return Response.ok().build();
	}
	
	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	@Path("exec") 
	public Response exec(@DefaultValue("Log") @QueryParam("name") String name,
						 @DefaultValue("hello world!") @QueryParam("arg") String arg) throws ClassNotFoundException  {

		HashMap<String,String> env = new HashMap<String,String>();
		env.put("arg", arg);
	
		Class<? extends Action> clazz = Class.forName("n3phele.service.actions.tasks."+name+"Action").asSubclass(Action.class);
		if(clazz != null) {
			CloudProcess p = dao.create(UserResource.toUser(securityContext), name, env, null, null, clazz);
			init(p);
			return Response.created(p.getUri()).build();
		} else {
			return Response.noContent().build();
		}
	}
	
	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	@Path("refresh") 
	public Response refresh( )  {

		Date begin = new Date();
		Map<String, Long> result = periodicScheduler();
		log.info("Refresh "+(new Date().getTime()-begin.getTime())+"ms");
		return Response.ok(result.toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": "), MediaType.APPLICATION_JSON).build();
	}
	
	/*
	 * Process execution management
	 */
	
	/** Refresh the status of active actions
	 * @return Map of states of active cloud processes, and count of processes in those states.
	 */
	protected Map<String, Long> periodicScheduler() {
		Map<String, Long> counter = new HashMap<String, Long>();
		
		for(CloudProcess p : dao.getNonfinalized()) {
			ActionState s = p.getState();
			String active = p.getRunning()!=null?"_Running":"";
			String wait = p.getWaitTimeout()!=null?"_Wait":"";
			Long count = 0L;
			if(counter.containsKey(s.toString()+active+wait))
				count = counter.get(s.toString()+active+wait);
			count = count + 1;
			counter.put(s.toString()+active+wait, count);
			log.info("Process "+p.getUri()+" "+s+" "+p.getRunning());
			if(p.getRunning() == null && s != ActionState.NEWBORN) {
				if(p.hasPending() || s == ActionState.RUNABLE)
					schedule(p);
			} 
		} 
		
		return counter;
	}
	
	/** Places a task on to the run queue
	 * @param process
	 * @return TRUE if process queued
	 */
	private boolean schedule(final CloudProcess process) {
		return dao.transact(new Work<Boolean>() {

			@Override
			public Boolean run() {
				boolean result = false;
				boolean dirty = false;
				CloudProcess p = dao.load(process.getUri());
				if(p.getRunning() == null) {
					if(p.getWaitTimeout() != null) {
						Date now = new Date();
						if(p.hasPendingAssertions() || now.after(p.getWaitTimeout())) {
							p.setWaitTimeout(null);
							dirty = true;
						}
					}
					
					if(p.getWaitTimeout() == null && !p.isPendingCall() && !(p.isPendingInit() || p.isPendingCancel() || p.isPendingDump()) ) {
						dirty = true;
						p.setPendingCall(true);
					}
					if(p.hasPending()) {
						p.setRunning(new Date());
						QueueFactory.getDefaultQueue().add(
								TaskOptions.Builder.withPayload(new Schedule(process.getUri())));
						result = true;
						dirty = true;
					}
					if(dirty)
						dao.update(p);
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
			CloudProcessResource r = new CloudProcessResource();
			dao.clear();
			r.dispatch(process);
		}
	}
	
	
	
	/** Dispatches execution to a task's entry points
	 * @param processId
	 */
	private void dispatch(final URI processId) {
		
		NextStep dispatchCode = dao.transact(new Work<NextStep>() {
		    public NextStep run() {
		        CloudProcess p = dao.load(processId);
				if(p.isFinalized()) {
					log.warning("Processing called on process "+processId+" finalized="+p.isFinalized()+" state="+p.getState());
					return new NextStep(DoNext.nothing);
				}
				
				if(p.isPendingCancel() || p.isPendingDump()) { 
					p.setState(ActionState.CANCELLED);
			       dao.update(p);
			        if(p.isPendingCancel()) { 
			        	p.setPendingCancel(false);
			        	p.setPendingDump(false);
			        	return new NextStep(DoNext.cancel);
			        } else {
			        	p.setPendingCancel(false);
			        	p.setPendingDump(false);
			        	return new NextStep(DoNext.dump);
			        } 
				} else if(p.isPendingInit()) {
					p.setPendingInit(false);
			        dao.update(p);
			        return new NextStep(DoNext.init);
				} else if(p.hasPendingAssertions()) {
						String assertion = p.getPendingAssertion().remove(0);
						 dao.update(p);
						return new NextStep(DoNext.assertion, assertion);
				} else if(p.isPendingCall()) {
						p.setPendingCall(false);
						dao.update(p);
						return new NextStep(DoNext.call);	
				}
		        return new NextStep(DoNext.nothing);
		} });
		CloudProcess p = dao.load(processId);
		Action task = null;
		try {
			task = ActionResource.dao.load(p.getTask());
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to access task "+p.getTask(), e);
			toFailed(p);
			return;
		}
		log.info("Dispatch "+dispatchCode+": process "+p.getName()+" "+p.getUri()+" task "+task.getUri());
		
		boolean error = false;
		switch(dispatchCode.todo){
		case assertion:
			try {
				
				int index = dispatchCode.assertion.indexOf(":");
				SignalKind kind = SignalKind.valueOf(dispatchCode.assertion.substring(0,index));
				task.signal(kind, dispatchCode.assertion.substring(index+1));
			} catch (Exception e) {
				log.log(Level.WARNING, "Assertion "+ dispatchCode.assertion + " exception for process "+p.getUri()+" task "+task.getUri(), e);
			}
			writeOnChange(task);
			endOfTimeSlice(p);
			break;
		case call:
			boolean complete = false;
			try {
				complete = task.call();
			} catch (WaitForSignalRequest e) {
				toWait(p, e.getTimeout());
			} catch (Exception e) {
				log.log(Level.WARNING, "Call exception for process "+p.getUri()+" task "+task.getUri(), e);
				error = true;
			}
			writeOnChange(task);
			if(error) {
				toFailed(p);
				break;
			} else if(complete) {
				toComplete(p);
			} else {
				endOfTimeSlice(p);
			}
			break;
		case cancel:
			try {
				task.cancel();
			} catch (Exception e) {
				log.log(Level.WARNING, "Cancel exception for process "+p.getUri()+" task "+task.getUri(), e);
			}
			writeOnChange(task);
			toCancelled(p);
			break;
		case dump:
			try {
				task.dump();
			} catch (Exception e) {
				log.log(Level.WARNING, "Dump exception for process "+p.getUri()+" task "+task.getUri(), e);
			}
			writeOnChange(task);
			toCancelled(p);
			break;
		case init:
			try {
				preInitMapping(p);
				task.init();
			} catch (Exception e) {
				log.log(Level.WARNING, "Init exception for process "+p.getUri()+" task "+task.getUri(), e);
				error = true;
			}
			writeOnChange(task);
			if(error) {
				toFailed(p);
			} else {
				endOfTimeSlice(p);
			}
			break;
		case nothing:
		default:
			log.severe("Nothing to do for "+p.getName()+":"+p.toString());
			toFailed(p);
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
		log.info("Complete "+process.getName()+":"+process.getUri());
		final List<String> dependents = dao.transact(new Work<List<String>>() {
			@Override
			public List<String> run() {
				CloudProcess p = dao.load(process.getUri());
				logExecutionTime(p);
				p.setState(ActionState.COMPLETE);
				p.setComplete(new Date());
				p.setRunning(null);
				p.setFinalized(true);
				if(p.getParent() != null) {
					CloudProcess parent;
					try {
						parent = dao.load(p.getParent());
						signal(parent, SignalKind.Ok, p.getUri().toString());
					} catch (NotFoundException e) {
						log.severe("Unknown parent "+p);
					} catch (Exception e) {
						log.log(Level.SEVERE, "Signal failure "+p, e);
					}
					
				}
				dao.update(p);
				return p.getDependencyFor();
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
		dao.transact(new VoidWork(){
			@Override
			public void vrun() {
			String hasFinalized = processUri.toString();
			CloudProcess dprocess = dao.load(depender);
			if(dprocess.getDependentOn() != null && dprocess.getDependentOn().size() > 0) {
				boolean found = dprocess.getDependentOn().remove(hasFinalized);
				if(found) {
					if(dprocess.getDependentOn().size() == 0 && dprocess.getState() != ActionState.NEWBORN && !dprocess.isFinalized()) {
						if(dprocess.getState() == ActionState.INIT) {
								dprocess.setPendingInit(true);
						} 
						dprocess.setState(ActionState.RUNABLE);
						dao.update(dprocess);
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
	 * @param dependent the process that when complete unblocks exection of process 
	 * @return true if the dependency causes the process to block
	 * @throws IllegalArgumentException
	 */
	public boolean addDependentOn(final CloudProcess process, CloudProcess dependent) throws IllegalArgumentException {
		return dao.setDependentOn(process, dependent.getUri());
	}
		
	/** Set process to having terminated with processing failure. The process parent is notified of
	 * the failure.
	 * @param process
	 */
	private void toFailed(CloudProcess process) {
		final Long processId = process.getId();
		List<String> dependents = dao.transact(new Work<List<String>>() {

			@Override
			public List<String> run() {
				CloudProcess p = dao.load(processId);
				List<String> result = null;
				if(!p.isFinalized()) {
					logExecutionTime(p);
					p.setState(ActionState.FAILED);
					p.setComplete(new Date());
					p.setRunning(null);
					p.setFinalized(true);
					if(p.getParent() != null) {
						CloudProcess parent;
						try {
							parent = dao.load(p.getParent());
							signal(parent, SignalKind.Failed, p.getUri().toString());
						} catch (NotFoundException e) {
							log.severe("Unknown parent "+p);
						} catch (Exception e) {
							log.log(Level.SEVERE, "Signal failure "+p, e);
						}
					}
					result = p.getDependencyFor();
					dao.update(p);
				} else {
					log.warning("Failed process "+p.getUri()+" is finalized");
				}
				return result;
			}});
		if(dependents != null && dependents.size() > 0) {
			for(String dependent : dependents) {
				CloudProcess dprocess = dao.load(URI.create(dependent));
				if(!dprocess.isFinalized()) {
					toCancelled(dprocess);
				}
			}
		}
	}
	
	/** Set process to the wait state
	 * 
	 * @param process
	 */
	private void toWait(CloudProcess process, final Date timeout) {
		final Long processId = process.getId();
		dao.transact(new VoidWork() {

			@Override
			public void vrun() {
				CloudProcess p = dao.load(processId);
				logExecutionTime(p);
				p.setRunning(null);
				if(!p.isFinalized()) {
					p.setWaitTimeout(timeout);
					if(p.getState() == ActionState.RUNABLE && p.hasPending()) {
						dao.update(p);
						log.warning("Re-queue process "+p.getId());
						schedule(p);
					} else {
						dao.update(p);
					}
				} else {
					log.warning("Wait process "+p.getUri()+" is finalized");
				}
			}});

	}
	
	/** Re-evaluate process state at normal end of execution. 
	 * @param process
	 */
	private void endOfTimeSlice(CloudProcess process) {
		final Long processId = process.getId();
		dao.transact(new VoidWork() {

			@Override
			public void vrun() {
				CloudProcess p = dao.load(processId);
				logExecutionTime(p);
				p.setRunning(null);
				if(!p.isFinalized()) {
					if(p.getDependentOn() != null && p.getDependentOn().size() > 0) {
						p.setState(ActionState.BLOCKED);
					} else if(p.getState() == ActionState.RUNABLE && p.hasPending()) {
						dao.update(p);
						log.warning("Re-queue process "+p.getId());
						schedule(p);
						return;
					}
				} 
				dao.update(p);
			}});
	}
	
	
	/** Move process to cancelled state.
	 * The process parent is notified of the cancellation.
	 * @param process
	 */
	private void toCancelled(CloudProcess process) {
		final Long processId = process.getId();
		List<String> dependents = dao.transact(new Work<List<String>>() {

			@Override
			public List<String> run() {
				CloudProcess p = dao.load(processId);
				List<String> result = null;
				if(!p.isFinalized()) {
					if(p.getState().equals(ActionState.BLOCKED)){
						p.setPendingCancel(true);
						dao.update(p);
					} else {
						logExecutionTime(p);
						p.setState(ActionState.CANCELLED);
						p.setComplete(new Date());
						if(p.getStart() == null) {
							p.setStart(p.getComplete());
						}
						p.setRunning(null);
						p.setFinalized(true);
						if(p.getParent() != null) {
							CloudProcess parent;
							try {
								parent = dao.load(p.getParent());
								signal(parent, SignalKind.Cancel, p.getUri().toString());
							} catch (NotFoundException e) {
								log.severe("Unknown parent "+p);
							} catch (Exception e) {
								log.log(Level.SEVERE, "Signal failure "+p, e);
							}
						}
						result = p.getDependencyFor();
						dao.update(p);
					}
					
				} else {
					log.warning("Cancelled process "+p.getUri()+" is finalized");
				}
				return result;
			}});
		if(dependents == null) {
			schedule(process);
		} else if(dependents.size() > 0) {
			for(String dependent : dependents) {
				CloudProcess dprocess = dao.load(URI.create(dependent));
				if(!dprocess.isFinalized()) {
					toCancelled(dprocess);
				}
			}
		}
	}
	
	public static CloudProcess spawn(URI owner, String name, HashMap<String, String> context, List<URI> dependency, URI parent, String className) throws IllegalArgumentException, NotFoundException, ClassNotFoundException {

		String canonicalClassName = "n3phele.service.actions.tasks."+className+"Action";

		User user;
		try {
			user = UserResource.dao.load(owner);
		} catch (NotFoundException e) {
			log.warning("Cant find owner "+owner);
			throw e;
		}
		
		CloudProcess process = dao.create(user, 
				name, context, dependency, parent, 
				Class.forName(canonicalClassName).asSubclass(Action.class));
		new CloudProcessResource().init(process);
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
		dao.transact(new VoidWork() {
			@Override
			public void vrun() {
				CloudProcess p = dao.load(processId);
				if(!p.isFinalized()) {
					p.setPendingCancel(true);
					dao.update(p);
				} else {
					log.severe("Cancel on finalized process "+p.getUri());
				}
				schedule(p);
			}});
	}
	
	protected void init(CloudProcess process) {
		log.info("init "+process.getUri()+" "+process.getName());
		final Long processId = process.getId();
		dao.transact(new VoidWork() {
			@Override
			public void vrun() {
				CloudProcess p = dao.load(processId);
				if(!p.isFinalized() && p.getState() == ActionState.NEWBORN) {
					if(p.getDependentOn() != null && p.getDependentOn().size() != 0) {
						p.setState(ActionState.INIT);
						dao.update(p);
					} else {
						p.setState(ActionState.RUNABLE);
						p.setPendingInit(true);
						schedule(p);
					}
				} else {
					log.severe("Init on finalized or non-newborn process "+p.getUri()+" "+p.getState());
				}
			}});
	}
	
	private void preInitMapping(CloudProcess process) {
		log.severe("*************** Unimplemented preInitMapping ***************************" );
	}
	
	/** Dump a running process, which causes the process to be stopped current processing and 
	 * to save diagnostic information that
	 * can be later reviews.
	 * 
	 */
	public static void dump(CloudProcess process) {
		final Long processId = process.getId();
		log.info("dump "+process.getUri());
		dao.transact(new VoidWork() {
			@Override
			public void vrun() {
				CloudProcessResource resource = new CloudProcessResource();
				CloudProcess p = dao.load(processId);
				if(!p.isFinalized()) {
					p.setPendingDump(true);
					dao.update(p);
				} else {
					log.severe("Dump on finalized process "+p.getUri());
				}
				resource.schedule(p);
			}});
		
	}
	
	/** Dump a running process, which causes the process to be stopped current processing and 
	 * to save diagnostic information that
	 * can be later reviews.
	 * 
	 */
	public static void dump(URI processId) throws NotFoundException {

		CloudProcess process = dao.load(processId);
		dump(process);
		
	}
	
	/** 
	 * @param jobAction
	 * @throws WaitForSignalRequest 
	 */
	public static void waitForSignal() throws WaitForSignalRequest {
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
	 * @param assertion
	 */
	private static void signal(final CloudProcess process, final SignalKind kind, final String assertion) {
		log.info("signal <"+kind+":"+assertion+"> to "+process.getUri());
		dao.transact(new VoidWork() {
			@Override
			public void vrun() {
				CloudProcessResource resource = new CloudProcessResource();
				CloudProcess p = dao.load(process.getUri());
				if(!p.isFinalized()) {
					if(!p.getPendingAssertion().contains(kind+":"+assertion)) {
						p.getPendingAssertion().add(kind+":"+assertion);
						dao.update(p);
						if(p.getState() == ActionState.RUNABLE) {
							resource.schedule(p);
						}
					}
				} else {
					log.severe("Signal <"+kind+":"+assertion+"> on finalized process "+p.getUri());
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
	
	
	private void logExecutionTime(CloudProcess p) {
		Date started = p.getRunning();
		if(started != null) {
			Date now = new Date();
			Long duration = now.getTime() - started.getTime();
			log.info(p.getName()+" "+p.getUri()+" executed "+duration+" milliseconds");
		}
	}
	
	private boolean writeOnChange(final Action task) {
		boolean result = ActionResource.dao.transact(new Work<Boolean>() {
			@Override
			public Boolean run() {
				Action db = ActionResource.dao.load(task.getUri());
				if(!db.equals(task)) {
					ActionResource.dao.update(task);
					return true;
				}
				return false;
			}});
		log.info("Action "+task.getName()+" "+task.getUri()+" write "+result);
		return result;
	}
	
	/*
	 * Data Access
	 */
	public static class CloudProcessManager extends AbstractManager<CloudProcess> {		
		public CloudProcessManager() {
		}
		final private URI myPath = UriBuilder.fromUri(Resource.get("baseURI", "http://localhost:8888/resources")).path(CloudProcessResource.class).build();
		@Override
		protected URI myPath() {
			return myPath;
		}

		@Override
		protected GenericModelDao<CloudProcess> itemDaoFactory() {
			return new ServiceModelDao<CloudProcess>(CloudProcess.class);
		}
		public void clear() { super.itemDao.clear(); }
		public CloudProcess load(Long id, User requestor) throws NotFoundException { return super.get(id, requestor); }

		/**
		 * Locate a item from the persistent store based on the item URI.
		 * @param uri
		 * @param requestor requesting user
		 * @return the item
		 * @throws NotFoundException is the object does not exist
		 */
		public CloudProcess load(URI uri, User requestor) throws NotFoundException { return super.get(uri, requestor); }
		/**
		 * Locate a item from the persistent store based on the item URI.
		 * @param uri
		 * @param requestor requesting user
		 * @return the item
		 * @throws NotFoundException is the object does not exist
		 */
		public CloudProcess load(URI uri) throws NotFoundException { return super.get(uri); }
		
		public CloudProcess load(Long id) throws NotFoundException { return super.get(id); }
		
		public CloudProcess load(Key<CloudProcess> k) throws NotFoundException { return super.itemDao.get(k); }
		
		
		public void update(CloudProcess cloudProcess) { super.update(cloudProcess); }
		
		public java.util.Collection<CloudProcess> getNonfinalized() { return super.itemDao.collectionByProperty("finalized", false); }
		
		public java.util.Collection<CloudProcess> getChildren(URI parent) { return super.itemDao.collectionByProperty("parent", parent.toString()); }
		
		public CloudProcess create(User user, String name, HashMap<String, String> context, List<URI> dependency, URI parent, Class<? extends Action> clazz) throws IllegalArgumentException {
			Action task;
			try {
				task = clazz.newInstance().create(user, name, context);
			} catch (InstantiationException e) {
				log.log(Level.SEVERE, "Class "+clazz, e);
				throw new IllegalArgumentException(e);
			} catch (IllegalAccessException e) {
				log.log(Level.SEVERE, "Class "+clazz, e);
				throw new IllegalArgumentException(e);
			}
			ActionResource.dao.add(task);	
			CloudProcess process = new CloudProcess(user, name, parent, task);
			dao.add(process);
			task.setProcess(process.getUri());
			ActionResource.dao.update(task);
			for(URI dependent : Helpers.safeIterator(dependency)) {
				setDependentOn(process, dependent);
			}
			return process;
		}
		
		/** Adds a dependency such that process runs dependent on the successful execution of dependentOn
		 * @param process 
		 * @param dependentOn
		 * @return TRUE if dependency causes process execution to block
		 * @throws IllegalArgumentException if the dependency is non-existent or has terminated abnormally
		 */
		public boolean setDependentOn(CloudProcess process, final URI dependentOn) throws IllegalArgumentException {
			final URI processUri = process.getUri();
			final Long id = process.getId();
			return dao.transact(new Work<Boolean>(){

				@Override
				public Boolean run() {
					CloudProcess depender = dao.load(id);
					if(depender.isFinalized()) {
						log.warning("Cannot add dependencies to finalized process "+processUri+" state "+depender.getState());
						throw new IllegalArgumentException("Cannot add dependencies to finalized process "+processUri+" state "+depender.getState());
					}
					CloudProcess dependency;
					try {
						dependency = dao.load(dependentOn);
					} catch (NotFoundException e) {
						throw new IllegalArgumentException("Dependency does exist "+dependentOn,e);
					}
					if(dependency.isFinalized()) {
						if(dependency.getState().equals(ActionState.COMPLETE)) {
							log.warning(dependentOn+" already finalized, removing dependency constraint from "+processUri);
							return false;
						} else {
							throw new IllegalArgumentException("Process "+processUri+" has a dependency on "+dependentOn+" which is "+dependency.getState());
						}
					} else {
						dependency.getDependencyFor().add(depender.getUri().toString());
						depender.getDependentOn().add(dependency.getUri().toString());
						dao.update(dependency);
						dao.update(depender);
						return true;
					}
				}});
			
		}
		
		public Collection<CloudProcess> getCollection(User owner) { return super.getCollection(owner); }

	}
	final public static CloudProcessManager dao = new CloudProcessManager();
	
}
