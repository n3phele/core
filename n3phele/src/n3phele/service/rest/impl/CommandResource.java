/**
 * @author Nigel Cook
 *
 * (C) Copyright 2010-2012. Nigel Cook. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * Licensed under the terms described in LICENSE file that accompanied this code, (the "License"); you may not use this file
 * except in compliance with the License. 
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on 
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 *  specific language governing permissions and limitations under the License.
 */
package n3phele.service.rest.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import n3phele.service.core.NotFoundException;
import n3phele.service.core.Resource;
import n3phele.service.model.Account;
import n3phele.service.model.CachingAbstractManager;
import n3phele.service.model.Command;
import n3phele.service.model.CommandCloudAccount;
import n3phele.service.model.CommandCollection;
import n3phele.service.model.CommandImplementationDefinition;
import n3phele.service.model.ServiceModelDao;
import n3phele.service.model.core.Collection;
import n3phele.service.model.core.GenericModelDao;
import n3phele.service.model.core.Helpers;
import n3phele.service.model.core.User;
import n3phele.service.nShell.NParser;
import n3phele.service.nShell.ParseException;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

@Path("/command")
public class CommandResource {

	private static Logger log = Logger.getLogger(CommandResource.class.getName()); 

	@Context UriInfo uriInfo;
	@Context SecurityContext securityContext;

	public CommandResource() {

	}

	@SuppressWarnings("null")
	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	public CommandCollection list(
			@DefaultValue("false") 	@QueryParam("summary") Boolean summary,
			@DefaultValue("false") 	@QueryParam("preferred") boolean preferred,
			@DefaultValue("0") 		@QueryParam("start") int start,
			@DefaultValue("-1") 	@QueryParam("end") int end,
									@QueryParam("search") String search) {

		log.warning("list entered with summary "+summary+" start "+start+" end "+end+" preferred "+preferred);
		int n = 0;
		if(start < 0)
			start = 0;
		if(end >= 0) {
			n = end - start + 1;
			if(n <= 0) n = 1;
		}
		if(search != null) {
			search = search.trim().toLowerCase(Locale.ENGLISH);
			if(search.length()==0)
				search = null;
		}
		Collection<Command> result = null;
		if(search != null || preferred == false) {
			result = dao.getCollection(UserResource.toUser(securityContext), preferred);

			List<Command> filtered;
			if(search != null) {
				filtered = new ArrayList<Command>(result.getElements().size());
				for(Command item : result.getElements()) {
					Boolean nameMatch=null;
					Boolean descriptionMatch=null;
					if((nameMatch =item.getName().toLowerCase(Locale.ENGLISH).contains(search)) || 
					   (item.getDescription()!=null && 
					   (descriptionMatch=item.getDescription().toLowerCase(Locale.ENGLISH).contains(search)))) {
						log.info("Adding "+item.getName()+" under "+search+" nameMatch "+nameMatch+" descriptionMatch "+descriptionMatch);
						filtered.add(item);
					}
				}
				result.setElements(filtered);
			} 


			return new CommandCollection(result, start, end);
		} else {
			result = getPreferredCommandDefinitionFromCache(UserResource.toUser(securityContext), start, n);
			CommandCollection reply = new CommandCollection(result);
			return reply;
		}

	}
	
	
	
	private Map<URI, String> cache=null;
	private String getOwnerName(URI owner) throws NotFoundException {
		String result=null;
		if(cache == null) cache = new HashMap<URI, String>();
		if(cache.containsKey(owner))
				result = cache.get(owner);
		else {
			User user = UserResource.dao.load(owner);
			result = user.toString();
			cache.put(owner, result);
		}
		return result;
	}
	

	
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Path("import")
	@RolesAllowed("authenticated")
	public Response importer(
			@Context HttpServletRequest request) {	
		try {
			ServletFileUpload upload = new ServletFileUpload();              
			FileItemIterator iterator = upload.getItemIterator(request);       
			while (iterator.hasNext()) {         
				FileItemStream item = iterator.next();         
				InputStream stream = item.openStream();          
				if (item.isFormField()) {           
					log.warning("Got a form field: " + item.getFieldName());         
				} else {           
					log.warning("Got an uploaded file: " + item.getFieldName() +", name = " + item.getName()); 
					
					NParser n = new NParser(stream);
					Command command = n.parse();
					User owner = UserResource.toUser(securityContext);
					command.setOwner(owner.getUri());
					command.setOwnerName(owner.toString());
					
					StringBuilder response = new StringBuilder("Processing "+command.getImplementations().size()+" command implementations\n");
					URI requestor = owner.getUri();
					
					Command existing = null;

					java.util.Collection<Command> others = null;
					try {
						others = dao.getList(command.getName());
						for(Command c : others) {
							if(c.getVersion().equals(command.getVersion()) ||
									!requestor.equals(c.getOwner())) {
								existing = c;
							} else {
								if(c.isPreferred() && command.isPreferred()) {
									c.setPreferred(false);
									dao.update(c);
									response.append("preferred over "+c.getVersion()+" ");
								}
							}
						}	
					} catch (Exception e) {
						// not found
					}
					if(existing == null) {
						dao.add(command);
						response.append(command.getUri());
					} else {
						// update or illegal operation
						boolean isOwner = requestor.equals(existing.getOwner());
						log.info("requestor is "+requestor+" owner is "+existing.getOwner()+" isOwner "+isOwner);
						if(!isOwner) {
							response.append("ignored: already exists");
						} else {
							command.setUri(existing.getUri());
							command.setId(existing.getId());
							dao.update(command);
							response.append("updated");
						}	
					}
					return Response.ok(response.toString()).build();
				}
			}	
		} catch (FileUploadException e) {
			log.log(Level.WARNING, "FileUploadException", e);
		} catch (IOException e) {
			log.log(Level.WARNING, "IOException", e);
		} catch (ParseException e) {
			return Response.notModified(e.getMessage()+" cause:"+e.getCause().getMessage()).build();
		}
		return Response.notModified().build();
	}


	@GET
	@RolesAllowed("authenticated")
	// @Produces("application/vnd.com.n3phele.CatalogEntry+json")
	@Produces("application/json")
	@Path("{Id}") 
	public Command get( @PathParam ("Id") Long id,
						@DefaultValue("false") @QueryParam("full") boolean full) throws NotFoundException {

		Command item = dao.load(id, UserResource.toUser(securityContext));
		
		User user = UserResource.toUser(securityContext);
		Map<URI, List<Account>> accountMap = new HashMap<URI, List<Account>>();
		if(item.getImplementations() != null) {
			Collection<Account> accounts = AccountResource.dao.getAccountList(user, false);
			if(accounts != null && accounts.getElements() != null) {
				for(Account account : accounts.getElements()) {
					if(accountMap.containsKey(account.getCloud())) {
						List<Account> list = accountMap.get(account.getCloud());
						list.add(account);
					} else {
						List<Account> list = new ArrayList<Account>();
						list.add(account);
						accountMap.put(account.getCloud(), list);
					}
				}
			}
			List<CommandImplementationDefinition> profiles = item.getImplementations();
			ArrayList<CommandCloudAccount> decoratedProfiles = new ArrayList<CommandCloudAccount>();
			for(CommandImplementationDefinition profile : profiles) {
				if(accountMap.containsKey(profile.getName())) {
					for(Account account : accountMap.get(profile.getName())) {
						decoratedProfiles.add(new CommandCloudAccount(account.getName(), 
																	  account.getCloudName(), 
																	  account.getUri()));
					}
				}
			}
			if(!full) item.setImplementations(null);
			item.setCloudAccounts(decoratedProfiles);
		}
		return item;
	}

	@DELETE
	@RolesAllowed("authenticated")
	@Path("{id}")
	public void delete(@PathParam ("id") Long id) throws NotFoundException {
		Command item = dao.load(id, UserResource.toUser(securityContext));
		dao.delete(item);
	}
	
	/*
	 * ------------------------------------------------------------------------------------------ *
	 *                      	== Private & Internal support functions ==
	 * ------------------------------------------------------------------------------------------ *
	 */
	
	private final static String commandCacheKey = "n3phele-command-cache";

	private Collection<Command> getPreferredCommandDefinitionFromCache(User user, int start, int n) {
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
		CmdLet[] cachedCommandDefinitions = (CmdLet[]) memcache.get(commandCacheKey);
		if(cachedCommandDefinitions == null) {
			log.info("Command cache miss");
			cachedCommandDefinitions = loadCommandDefinitionCache();
		}

		List<Command> commands = new ArrayList<Command>();
		for(CmdLet cl : cachedCommandDefinitions) {
			if(cl.isPublic || user.isAdmin() || user.getUri().toString().equals(cl.owner)) {
				Command c = new Command();
				c.setUri(URI.create(cl.URI));
				c.setName(cl.name);
				c.setDescription(cl.description);
				c.setOwnerName(cl.ownerName);
				c.setVersion(cl.version);
				c.setIcon(URI.create(cl.icon));
				c.setOwner(URI.create(cl.owner));
				c.setPublic(cl.isPublic);
				c.setPreferred(true);
				commands.add(c);
			}
		}
		int end = start+n;
		if(end > commands.size()) end = commands.size(); 
		Collection<Command> collection = dao.collectionFactory(commands.subList(start, end));
		collection.setTotal(commands.size());
		return collection;
	}
	
	private CmdLet[] loadCommandDefinitionCache() {
		log.info("Init command cache");
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
		Collection<Command> preferredCommandDefinitions = dao.getPreferredCollection();
		CmdLet[] cachedCommandDefinitions = new CmdLet[preferredCommandDefinitions.getElements().size()];
		for(int i=0; i < preferredCommandDefinitions.getElements().size(); i++) {
			Command c = preferredCommandDefinitions.getElements().get(i);
			CmdLet cl = new CmdLet();
			cl.URI = Helpers.URItoString(c.getUri());
			cl.name = c.getName();
			cl.description = c.getDescription();
			cl.ownerName = getOwnerName(c.getOwner());
			cl.version = c.getVersion();
			cl.icon = Helpers.URItoString(c.getIcon());
			cl.owner = Helpers.URItoString(c.getOwner());
			cl.isPublic = c.isPublic();
			cachedCommandDefinitions[i] = cl;
		}
		memcache.put(commandCacheKey, cachedCommandDefinitions);
		return cachedCommandDefinitions;
	}
	
	
	private static class CmdLet implements Serializable {
		private static final long serialVersionUID = 1L;
		String URI;
		String name;
		String description;
		String ownerName;
		String version;
		String icon;
		String owner;
		boolean isPublic;
	}
	
	
	
	public static class CommandManager extends CachingAbstractManager<Command> {
		public CommandManager() {
		}
		public Collection<Command> collectionFactory(List<Command> list) {
			return new Collection<Command>(itemDao.clazz.getSimpleName(), URI.create(myPath().toString()),list);
		}
		@Override
		protected URI myPath() {
			return UriBuilder.fromUri(Resource.get("baseURI", "http://localhost:8888/resources")).path(CommandResource.class).build();
		}

		@Override
		protected GenericModelDao<Command> itemDaoFactory() {
			return new ServiceModelDao<Command>(Command.class);
		}


		public Command load(Long id, User requestor) throws NotFoundException { return super.get(id, requestor); }
		/**
		 * Locate a item from the persistent store based on the item name.
		 * @param name
		 * @param requestor requesting user
		 * @return the item
		 * @throws NotFoundException is the object does not exist
		 */
		public Command load(String name, User requestor) throws NotFoundException { return super.get(name, requestor); }
		/**
		 * Locate a item from the persistent store based on the item URI.
		 * @param uri
		 * @param requestor requesting user
		 * @return the item
		 * @throws NotFoundException is the object does not exist
		 */
		public Command load(URI uri, User requestor) throws NotFoundException { return super.get(uri, requestor); }
	
		/**
		 * Locate a item from the persistent store based on the item URI.
		 * @param uri
		 * @return the item
		 * @throws NotFoundException is the object does not exist
		 */
		public Command load(URI uri) throws NotFoundException { return super.get(uri); }
		
		/** Update existing Command
		 * @param item
		 * @throws NotFoundException
		 */
		public void update(Command item)throws NotFoundException { 
			MemcacheServiceFactory.getMemcacheService().delete(commandCacheKey); 
			super.update(item);
			MemcacheServiceFactory.getMemcacheService().delete(commandCacheKey);
		}

		/* (non-Javadoc)
		 * @see n3phele.service.model.CachingAbstractManager#add(n3phele.service.model.core.Entity)
		 */
		public void add(Command item) throws IllegalArgumentException {
			MemcacheServiceFactory.getMemcacheService().delete(commandCacheKey);
			super.add(item);
			MemcacheServiceFactory.getMemcacheService().delete(commandCacheKey);
		}
		
		/* (non-Javadoc)
		 * @see n3phele.service.model.CachingAbstractManager#delete(n3phele.service.model.core.Entity)
		 */
		public void delete(Command item) {
			MemcacheServiceFactory.getMemcacheService().delete(commandCacheKey);
			super.add(item);
			MemcacheServiceFactory.getMemcacheService().delete(commandCacheKey);
		}
		/**
		 * Collection of all preferred resources of a particular class in the persistent store. 
		 * @return the collection
		 */
		public Collection<Command> getPreferredCollection() {
			Collection<Command> result = null;
			try {
				java.util.Collection<Command> children = super.itemDao().orderedCollectionByProperty("preferred", true, "name");
				result = new Collection<Command>(itemDao.clazz.getSimpleName(), URI.create(myPath().toString()), new ArrayList<Command>(children));
			} catch (NotFoundException e) {
			}
			result.setTotal(result.getElements().size());
			return result;
		}
		
		public java.util.Collection<Command> getList(String name) { return super.getList(name); }
		
		/**
		 * Collection of all preferred resources of a particular class in the persistent store. 
		 * @return the collection
		 */
		public List<Command> getAllPreferredPublic() {
			List<Command> result = null;
			try {
				result = com.googlecode.objectify.ObjectifyService.ofy().load().type(Command.class).filter("preferred", true).filter("isPublic", true).order("name").list();
			} catch (NotFoundException e) {
			}
			return result;
		}

		/** Collection of preferred resources of a particular class accessible by an owner in the persistent store. 
		 * @param owner
		 * @return the collection
		 */
		public Collection<Command> getPreferredCollection(URI owner) {
			Collection<Command> result = null;
			try {
				List<Command> owned = com.googlecode.objectify.ObjectifyService.ofy().load().type(itemDao.clazz).filter("owner", owner.toString()).filter("preferred", true).list();
				List<Command> shared = com.googlecode.objectify.ObjectifyService.ofy().load().type(itemDao.clazz).filter("preferred", true).filter("isPublic", true).list();
				List<Command> items = mergeResults(owned, shared, owner);			
				result = new Collection<Command>(itemDao.clazz.getSimpleName(), URI.create(myPath().toString()), items);
			} catch (NotFoundException e) {
			}
			result.setTotal(result.getElements().size());
			return result;
		}
		
		/**
		 * Collection of resources of a particular class in the persistent store. 
		 * @return the collection
		 */
		public Collection<Command> getCollection(User owner, boolean preferred) {
			if(preferred) {
				return owner.isAdmin()? getPreferredCollection():getPreferredCollection(owner.getUri());
			} else {
				return owner.isAdmin()? getCollection():getCollection(owner.getUri());
			}
		}
		
	

	}
	final public static CommandManager dao = new CommandManager();
}
