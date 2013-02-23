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
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import n3phele.service.core.NotFoundException;
import n3phele.service.core.Resource;
import n3phele.service.model.Action;
import n3phele.service.model.CachingAbstractManager;
import n3phele.service.model.ServiceModelDao;
import n3phele.service.model.core.BaseEntity;
import n3phele.service.model.core.Collection;
import n3phele.service.model.core.GenericModelDao;
import n3phele.service.model.core.User;


@Path("/action")
public class ActionResource {
	private static Logger log = Logger.getLogger(ActionResource.class.getName()); 
	public ActionResource() {
	}

	@Context UriInfo uriInfo;
	@Context SecurityContext securityContext;

	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	public Collection<BaseEntity> list(
			@DefaultValue("false") @QueryParam("summary") Boolean summary)  {

		log.warning("getAction entered with summary "+summary);

		Collection<BaseEntity> result = dao.getCollection(UserResource.toUser(securityContext)).collection(summary);
		return result;
	}


	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	@Path("{tag}") 
	public Action get( @PathParam ("tag") Long id) throws NotFoundException {

		Action item = dao.load(id, UserResource.toUser(securityContext));
		return item;
	}
	
	/*
	 * ------------------------------------------------------------------------------------------ *
	 *                      	== Private & Internal support functions ==
	 * ------------------------------------------------------------------------------------------ *
	 */
	
	public static class ActionManager extends CachingAbstractManager<Action> {
		public ActionManager() {
		}
		@Override
		protected URI myPath() {
			return UriBuilder.fromUri(Resource.get("baseURI", "http://127.0.0.1:8888/resources")).path(ActionResource.class).build();
		}

		@Override
		protected GenericModelDao<Action> itemDaoFactory() {
			return new ServiceModelDao<Action>(Action.class);
		}
	
		/**
		 * Locate a item from the persistent store based on the item tag.
		 * @param tag
		 * @param requestor requesting user
		 * @return the item
		 * @throws NotFoundException if the object does not exist
		 */	
		public Action load(Long id, User requestor) throws NotFoundException { return super.get(id, requestor); }
		/**
		 * Locate a item from the persistent store based on the item name.
		 * @param name
		 * @param requestor requesting user
		 * @return the item
		 * @throws NotFoundException if the object does not exist
		 */
		public Action load(String name, User requestor) throws NotFoundException { return super.get(name, requestor); }
		/**
		 * Locate a item from the persistent store based on the item URI.
		 * @param uri
		 * @param requestor requesting user
		 * @return the item
		 * @throws NotFoundException if the object does not exist
		 */
		public Action load(URI uri, User requestor) throws NotFoundException { return super.get(uri, requestor); }
		
		/**
		 * Locate a item from the persistent store based on the item URI.
		 * @param uri
		 * @return the item
		 * @throws NotFoundException if the object does not exist
		 */
		public Action load(URI uri) throws NotFoundException { return super.get(uri); }
		
		public Action load(Long id) throws NotFoundException { return super.get(id); }
		
		public java.util.Collection<Action> load(java.util.Collection<URI>list) throws NotFoundException { return super.itemDao.listByURI(list); }
		
		public void update(Action action) throws NotFoundException { super.update(action); }
		
		public void add(Action action) { super.add(action); }
		
		public void delete(Action action) { super.delete(action); }
	
	
		public void updateAll(List<Action> set) { super.itemDao().putAll(set); }
		
		public Collection<Action> getCollection(User owner) { return super.getCollection(owner); }

	}
	final public static ActionManager dao = new ActionManager();
}
