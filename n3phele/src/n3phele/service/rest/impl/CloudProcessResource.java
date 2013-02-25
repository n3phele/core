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

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import n3phele.service.core.NotFoundException;
import n3phele.service.core.Resource;
import n3phele.service.lifecycle.ProcessLifecycle;
import n3phele.service.model.Action;
import n3phele.service.model.CachingAbstractManager;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.CloudProcessCollection;
import n3phele.service.model.ServiceModelDao;
import n3phele.service.model.SignalKind;
import n3phele.service.model.Variable;
import n3phele.service.model.core.Collection;
import n3phele.service.model.core.GenericModelDao;
import n3phele.service.model.core.Helpers;
import n3phele.service.model.core.User;

import com.googlecode.objectify.Key;

@Path("/process")
public class CloudProcessResource {
	final private static java.util.logging.Logger log = java.util.logging.Logger.getLogger(CloudProcessResource.class.getName()); 

	public CloudProcessResource() { }
	
	protected @Context UriInfo uriInfo;
	protected @Context SecurityContext securityContext;

	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	public CloudProcessCollection list(
			@DefaultValue("false") @QueryParam("summary") Boolean summary,
			@DefaultValue("0") @QueryParam("start") int start,
			@DefaultValue("-1") @QueryParam("end") int end) throws NotFoundException {

		log.info("getProgress entered with summary "+summary+" from start="+start+" to end="+end);

		int n = 0;
		if(start < 0)
			start = 0;
		if(end >= 0) {
			n = end - start + 1;
			if(n <= 0)
				n = 1;
		}
		Collection<CloudProcess> result = dao.getCollection(start, n, UserResource.toUser(securityContext));// .collection(summary);

		return new CloudProcessCollection(result);
	}
	
	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	@Path("{group:.*_}{id}/children")
	public CloudProcess[] listChildren( @PathParam ("group") String group, @PathParam ("id") Long id)  {

		CloudProcess parent;
		try {
			Key<CloudProcess> root = null;
			if(group != null) {
				root = Key.create(CloudProcess.class, Long.valueOf(group.substring(0,group.length()-1)));
			}
			parent = dao.load(root, id, UserResource.toUser(securityContext));
		} catch (NotFoundException e) {
			throw e;
		}

		java.util.Collection<CloudProcess> result = dao.getChildren(parent.getUri());
		return result.toArray(new CloudProcess[result.size()]);
	}

	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	@Path("{group:.*_}{id}") 
	public CloudProcess get( @PathParam ("group") String group, @PathParam ("id") Long id) throws NotFoundException {
		Key<CloudProcess> root = null;
		if(group != null) {
			root = Key.create(CloudProcess.class, Long.valueOf(group.substring(0,group.length()-1)));
		}
		CloudProcess item = dao.load(root, id, UserResource.toUser(securityContext));
		return item;
	}
	
	@DELETE
	@Produces("application/json")
	@RolesAllowed("authenticated")
	@Path("{group:.*_}{id}")
	public Response killProcess( @PathParam ("group") String group, @PathParam ("id") Long id) throws NotFoundException {

		CloudProcess process = null;
		Key<CloudProcess> root = null;
		if(group != null) {
			root = Key.create(CloudProcess.class, Long.valueOf(group.substring(0,group.length()-1)));
		}
		try {
			process = dao.load(root, id, UserResource.toUser(securityContext));
		} catch (NotFoundException e) {
			return Response.status(Status.GONE).build();
		}
		ProcessLifecycle.mgr().cancel(process);
		return Response.status(Status.NO_CONTENT).build();
	}
		
	/*
	 * This is an eventing endpoint that can be invoked by an http request with
	 * no authentication.
	 */
	@GET
	@Produces("text/plain")
	@Path("{group:.*_}{id}/event") 
	public Response event( @PathParam ("group") String group, @PathParam ("id") Long id) {

		log.info(String.format("Event %s", uriInfo.getRequestUri().toString()));
		CloudProcess a = null;
		Key<CloudProcess> root = null;
		if(group != null) {
			root = Key.create(CloudProcess.class, Long.valueOf(group.substring(0,group.length()-1)));
		}
		try {
			a = dao.load(root, id);
		} catch (NotFoundException e) {
			return Response.status(Status.GONE).build();
		}
		ProcessLifecycle.mgr().signal(a, SignalKind.Event, uriInfo.getRequestUri().toString());
		return Response.ok().build();
	}
	
	@POST
	@Produces("application/json")
	@RolesAllowed("authenticated")
	@Path("exec")
	public Response exec(@DefaultValue("Log") @QueryParam("action") String action,
						 					  @QueryParam("name") String name,
						 @DefaultValue("hello world!") @QueryParam("arg") String arg, 
						 List<Variable> context) throws ClassNotFoundException  {

		n3phele.service.model.Context env = new n3phele.service.model.Context();
		env.putValue("arg", arg);
		for(Variable v : Helpers.safeIterator(context)) {
			env.put(v.getName(), v);
		}
		
	
		Class<? extends Action> clazz = Class.forName("n3phele.service.actions."+action+"Action").asSubclass(Action.class);
		if(clazz != null) {
			CloudProcess p = ProcessLifecycle.mgr().createProcess(UserResource.toUser(securityContext), name, env, null, null, clazz);
			ProcessLifecycle.mgr().init(p);
			return Response.created(p.getUri()).build();
		} else {
			return Response.noContent().build();
		}
	}
	
	
	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	@Path("exec") 
	public Response exec(@DefaultValue("Log") @QueryParam("action") String action,
										      @QueryParam("name") String name,
						 @DefaultValue("hello world!") @QueryParam("arg") String arg) throws ClassNotFoundException  {

		n3phele.service.model.Context env = new n3phele.service.model.Context();
		env.putValue("arg", arg);
	
		Class<? extends Action> clazz = Class.forName("n3phele.service.actions."+action+"Action").asSubclass(Action.class);
		if(clazz != null) {
			CloudProcess p = ProcessLifecycle.mgr().createProcess(UserResource.toUser(securityContext), name, env, null, null, clazz);
			ProcessLifecycle.mgr().init(p);
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
		Map<String, Long> result = ProcessLifecycle.mgr().periodicScheduler();
		log.info("Refresh "+(new Date().getTime()-begin.getTime())+"ms");
		return Response.ok(result.toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": "), MediaType.APPLICATION_JSON).build();
	}
	

	
	/*
	 * Data Access
	 */
	public static class CloudProcessManager extends CachingAbstractManager<CloudProcess> {		
		public CloudProcessManager() {
		}
		@Override
		protected URI myPath() {
			return UriBuilder.fromUri(Resource.get("baseURI", "http://127.0.0.1:8888/resources")).path(CloudProcessResource.class).build();
		}

		@Override
		protected GenericModelDao<CloudProcess> itemDaoFactory() {
			return new ServiceModelDao<CloudProcess>(CloudProcess.class);
		}
		public void clear() { super.itemDao.clear(); }
		public CloudProcess load(Key<CloudProcess> group, Long id, User requestor) throws NotFoundException { return super.get(group, id, requestor); }

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
		
		public CloudProcess load(Key<CloudProcess> group, Long id) throws NotFoundException { return super.get(group, id); }
		
		public CloudProcess load(Key<CloudProcess> k) throws NotFoundException { return super.itemDao.get(k); }
		
		
		public void update(CloudProcess cloudProcess) { super.update(cloudProcess); }
		
		public java.util.Collection<CloudProcess> getNonfinalized() { return super.itemDao.collectionByProperty("finalized", false); }
		public java.util.Collection<CloudProcess> getChildren(URI parent) { return super.itemDao.collectionByProperty(parent, "parent", parent.toString()); }
		public java.util.Collection<CloudProcess> getList(List<URI>ids) { return super.itemDao.listByURI(ids) ; }		

		public void add(CloudProcess process) { super.add(process); }
		public void delete(CloudProcess process) { super.delete(process); }
		
		public Collection<CloudProcess> getCollection2(User owner) { return super.getCollection(owner); }
		
		/**
		 * Collection of resources of a particular class in the persistent store. The will be extended
		 * in the future to return the collection of resources accessible to a particular user.
		 * @return the collection
		 */
		public Collection<CloudProcess> getCollection(int start, int n, User owner) {
			return owner.isAdmin()? getCollection(start, n):getCollection(start, n, owner.getUri());
		}
		
		/**
		 * Collection of resources of a particular class in the persistent store. The will be extended
		 * in the future to return the collection of resources accessible to a particular user.
		 * @return the collection
		 */
		public Collection<CloudProcess> getCollection(int start, int n) {
			log.info("admin query");
			Collection<CloudProcess> result = null;
			
			List<CloudProcess> items = ofy().load().type(CloudProcess.class).filter("topLevel", true).order("-started").offset(start).limit(n).list();

			result = new Collection<CloudProcess>(itemDao.clazz.getSimpleName(), super.path, items);

			result.setTotal(ofy().load().type(CloudProcess.class).filter("topLevel", true).count());

			log.info("admin query total (with sort) -is- "+result.getTotal());

			return result;
		}
		
		/**
		 * Collection of resources of a particular class in the persistent store. The will be extended
		 * in the future to return the collection of resources accessible to a particular user.
		 * @return the collection
		 */
		public Collection<CloudProcess> getCollection(int start, int n, URI owner) {
			log.info("non-admin query");
			Collection<CloudProcess> result = null;
			List<CloudProcess> items = ofy().load().type(CloudProcess.class).filter("owner", owner.toString()).filter("topLevel", true).order("-started").offset(start).limit(n).list();

			result = new Collection<CloudProcess>(itemDao.clazz.getSimpleName(), super.path, items);

			result.setTotal(ofy().load().type(CloudProcess.class).filter("owner", owner.toString()).filter("topLevel", true).count());
			return result;
		}


	}
	final public static CloudProcessManager dao = new CloudProcessManager();
	final private static Key<CloudProcess> nullAncestor = Key.create(Key.create(CloudProcess.class, 1), CloudProcess.class, 1);
	
}
