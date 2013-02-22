/**
 * @author Nigel Cook
 *
 * (C) Copyright 2010-2011. All rights reserved.
 */
package n3phele.service.rest.impl;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
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
import n3phele.service.model.AccountCollection;
import n3phele.service.model.CachingAbstractManager;
import n3phele.service.model.Cloud;
import n3phele.service.model.ServiceModelDao;
import n3phele.service.model.core.Collection;
import n3phele.service.model.core.Credential;
import n3phele.service.model.core.GenericModelDao;
import n3phele.service.model.core.User;

@Path("/account")
public class AccountResource {
	private static Logger log = Logger.getLogger(AccountResource.class.getName()); 

	@Context UriInfo uriInfo;
	@Context SecurityContext securityContext;

	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	public AccountCollection list(
			@DefaultValue("false") @QueryParam("summary") Boolean summary) {

		log.warning("list Accounts entered with summary "+summary);
		
		Collection<Account> result = dao.getAccountList(UserResource.toUser(securityContext), summary);

		return new AccountCollection(result,0,-1);
	}

	@POST
	@Produces("text/plain")
	@RolesAllowed("authenticated")
	public Response add(@FormParam("name") String name,
			@FormParam("description") String description,
			@FormParam("cloud") URI cloud,
			@FormParam("accountId") String accountId,
			@FormParam("secret") String secret) {

		Cloud myCloud = CloudResource.dao.load(cloud, UserResource.toUser(securityContext));
		if(name == null || name.trim().length()==0) {
			throw new IllegalArgumentException("bad name");
		}
		Account account = new Account(name, description, cloud, new Credential(accountId, secret).encrypt(),
				UserResource.toUser(securityContext).getUri(), false);

		dao.add(account);
		String result = CloudResource.testAccount(myCloud, UserResource.toUser(securityContext), account, true);
		if(result == null || result.trim().length()==0) {
			log.warning("Created "+account.getUri());
			return Response.created(account.getUri()).build();
		} else {
			log.warning("Created "+account.getUri()+" with warnings "+result);
			return Response.ok(result,MediaType.TEXT_PLAIN_TYPE).location(account.getUri()).build();
		}	
	}
	
	@POST
	@Produces("application/json")
	@Path("{id}")
	@RolesAllowed("authenticated")
	public Account update(@PathParam ("id") Long id,
			@FormParam("name") String name,
			@FormParam("description") String description,
			@FormParam("cloud") URI cloud,
			@FormParam("accountId") String accountId,
			@FormParam("secret") String secret) {

		Cloud myCloud = CloudResource.dao.load(cloud, UserResource.toUser(securityContext));
		Account item = dao.load(id, UserResource.toUser(securityContext));
		if(name == null || name.trim().length()==0) {
				throw new IllegalArgumentException("bad name");
		}
		Credential credential = null;
		if(secret != null && secret.trim().length() != 0) {
			credential = new Credential(accountId, secret).encrypt();
		}
			
		item.setName(name);
		item.setDescription(description==null?null:description.trim());
		item.setCloud(cloud);
		if(credential != null)
			item.setCredential(credential);
		dao.update(item);
		String result = CloudResource.testAccount(myCloud, UserResource.toUser(securityContext), item, true);

		log.warning("Updated "+ item.getUri()+((credential != null)?" including credential "+result:""));
		return item;
	}

	@GET
	@Produces("application/json")
	@Path("{id}")
	@RolesAllowed("authenticated")
	public Account get( @PathParam ("id") Long id) throws NotFoundException {

		Account item = dao.load(id, UserResource.toUser(securityContext));
		return item;
	}
	
	@GET
	@Path("{id}/init")
	@Produces("text/plain")
	@RolesAllowed("authenticated")
	public Response init(@PathParam ("id") Long id) throws NotFoundException {
		Account item = dao.load(id, UserResource.toUser(securityContext));
		Cloud myCloud = CloudResource.dao.load(item.getCloud(), UserResource.toUser(securityContext));
		String result = CloudResource.testAccount(myCloud, UserResource.toUser(securityContext), item, true);
		if(result == null || result.trim().length()==0) {
			return Response.ok("ok",MediaType.TEXT_PLAIN_TYPE).location(item.getUri()).build();
		} else {
			log.warning("Init "+item.getUri()+" with warnings "+result);
			return Response.ok(result,MediaType.TEXT_PLAIN_TYPE).location(item.getUri()).build();
		}	
	}

	@DELETE
	@Path("{id}")
	@RolesAllowed("authenticated")
	public void delete(@PathParam ("id") Long id) throws NotFoundException {
		Account item = dao.load(id, UserResource.toUser(securityContext));
		dao.delete(item);
	}
	
	/*
	 * Helpers
	 */

	public String createAccountForUser(User user, String accountId,
			String secret) throws NotFoundException {
		Cloud ec2 = CloudResource.dao.load("EC2", user);
		Credential credential = null;
		if(secret != null && secret.trim().length() != 0) {
			credential = new Credential(accountId, secret).encrypt();
		}
		Account defaultEC2 = new Account("EC2", "Amazon EC2 account", ec2.getUri(), credential, user.getUri(), false);
		dao.add(defaultEC2);
		String result = CloudResource.testAccount(ec2, user, defaultEC2, true);
		return result;
	}
	
	
	
	static public class AccountManager extends CachingAbstractManager<Account> {
		public AccountManager() {
		}
		@Override
		protected URI myPath() {
			return UriBuilder.fromUri(Resource.get("baseURI", "http://localhost:8888/resources")).path(AccountResource.class).build();
		}

		@Override
		protected GenericModelDao<Account> itemDaoFactory() {
			return new ServiceModelDao<Account>(Account.class);
		}

		public Account load(Long id, User requestor) throws NotFoundException { return super.get(id, requestor); }
		/**
		 * Locate a item from the persistent store based on the item name.
		 * @param name
		 * @param requestor requesting user
		 * @return the item
		 * @throws NotFoundException is the object does not exist
		 */
		public Account load(String name, User requestor) throws NotFoundException { return super.get(name, requestor); }
		/**
		 * Locate a item from the persistent store based on the item URI.
		 * @param uri
		 * @param requestor requesting user
		 * @return the item
		 * @throws NotFoundException is the object does not exist
		 */
		public Account load(URI uri, User requestor) throws NotFoundException { return super.get(uri, requestor); }
		public Account load(URI uri, URI requestor) throws NotFoundException { return super.get(uri, requestor); }
		public void add(Account account) { super.add(account); }
		public void update(Account account) { super.update(account); }
		public void delete(Account account) { super.delete(account); }
		public Collection<Account> getCollection(User user) { return super.getCollection(user); }
		
		public Collection<Account> getAccountList(User user, boolean summary) {

			log.warning("list Accounts entered with summary "+summary);
			
			Collection<Account> result = getCollection(user);
			Map<URI, String> cloudMap = new HashMap<URI, String>();
			
			if(result.getElements() != null) {
				for(int i=0; i < result.getElements().size(); i++) {
					Account account = result.getElements().get(i);
					URI cloud = account.getCloud();
					if(cloud != null) {
						String cloudName = cloudMap.get(cloud);
						if(cloudName == null) {
							try {
								Cloud c = CloudResource.dao.load(cloud);
								cloudName = c.getName();
								cloudMap.put(cloud, cloudName);
							} catch (NotFoundException nfe) {
								log.severe("Unknown cloud on account "+account.getUri());
								cloudName = cloud.toString();
							}
						}
						account.setCloudName(cloudName);
					}
					if(summary)
						result.getElements().set(i, Account.summary(account));
				}
			}
			return result;
		}
	}
	final public static AccountManager dao = new AccountManager();
}
