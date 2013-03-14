/**
 * @author Nigel Cook
 *
 * (C) Copyright 2010-2011. All rights reserved.
 */
package n3phele.service.rest.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
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
import n3phele.service.model.CachingAbstractManager;
import n3phele.service.model.Cloud;
import n3phele.service.model.CloudCollection;
import n3phele.service.model.ParameterType;
import n3phele.service.model.ServiceModelDao;
import n3phele.service.model.TypedParameter;
import n3phele.service.model.core.Collection;
import n3phele.service.model.core.Credential;
import n3phele.service.model.core.GenericModelDao;
import n3phele.service.model.core.User;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.representation.Form;

@Path("/cloud")
public class CloudResource {
	private static Logger log = Logger.getLogger(CloudResource.class.getName()); 

	@Context UriInfo uriInfo;
	@Context SecurityContext securityContext;
	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	public CloudCollection list(
			@DefaultValue("false") @QueryParam("summary") Boolean summary) {

		log.warning("getCloud entered with summary "+summary);

		Collection<Cloud> result = dao.getCollection(UserResource.toUser(securityContext));
		if(summary) {
			if(result.getElements() != null) {
				for(int i=0; i < result.getElements().size(); i++) {
					result.getElements().set(i, Cloud.summary(result.getElements().get(i)));
				}
			}
		}
		return new CloudCollection(result,0,-1);
	}

	@POST
	@Produces("text/plain")
	@RolesAllowed("authenticated")
	public Response create(@FormParam("name") String name,
			@FormParam("description") String description,
			@FormParam("location") URI location,
			@FormParam("factory") URI factory,
			@FormParam("factoryId") String factoryId,
			@FormParam("secret") String secret,
			@FormParam("isPublic") boolean isPublic)  {

		Cloud result = new Cloud(name, description, location, factory, new Credential(factoryId, secret).encrypt(), 
				UserResource.toUser(securityContext).getUri(), isPublic);
		fetchParameters(result);
		dao.add(result);

		log.warning("Created "+result);
		return Response.created(result.getUri()).build();
	}
	
	@POST
	@RolesAllowed("authenticated")
	@Produces("application/json")
	@Path("{id}/inputParameter") 
	public Response setInputParameter(@PathParam ("id") Long id,
									  @FormParam("key") String key,
									  @FormParam("description") String description,
			  @DefaultValue("String") @FormParam("type") ParameterType type,
									  @FormParam("value") String value,
									  @FormParam("defaultValue") String defaultValue
			  						 ) throws NotFoundException {
		
		Cloud cloud = dao.load(id, UserResource.toUser(securityContext));
		ArrayList<TypedParameter> inputParameters = cloud.getInputParameters();
		for(TypedParameter typedParameter:inputParameters){
			if(typedParameter.getName().equals(key)){
				typedParameter.setValue(value);
				typedParameter.setDefaultValue(defaultValue);
				if(description != null && !description.trim().isEmpty())
					typedParameter.setDescription(description);
				log.info("Updated "+cloud.getName()+" "+typedParameter);
				dao.update(cloud);
				return Response.ok().build();
			}
		}
		TypedParameter newValue = new TypedParameter(key, description, type, value, defaultValue);
		inputParameters.add(newValue);
		dao.update(cloud);
		log.info("Added "+cloud.getName()+" "+newValue);
		return Response.created(URI.create(cloud.getUri().toString()+"/inputParameter")).build();
	}
	

	@GET
	@RolesAllowed("authenticated")
	// @Produces("application/vnd.com.n3phele.CloudEntry+json")
	@Produces("application/json")
	@Path("{id}") 
	public Cloud get( @PathParam ("id") Long id) throws NotFoundException {

		Cloud item = dao.load(id, UserResource.toUser(securityContext));
		return item;
	}
	
	@GET
	@RolesAllowed("authenticated")
	@Produces("application/json")
	@Path("byName") 
	public Cloud get( @QueryParam ("id") String id) throws NotFoundException {

		Cloud item = dao.load(id, UserResource.toUser(securityContext));
		return item;
	}

	@DELETE
	@RolesAllowed("authenticated")
	@Path("{id}")
	public void delete(@PathParam ("id") Long id) throws NotFoundException {
		Cloud item = dao.load(id, UserResource.toUser(securityContext));
		dao.delete(item);
	}

	/*
	 * ------------------------------------------------------------------------------------------ *
	 *                      	== Private & Internal support functions ==
	 * ------------------------------------------------------------------------------------------ *
	 */
	private void fetchParameters(Cloud cloud) {
		Client client = Client.create();
		Credential plain = Credential.unencrypted(cloud.getFactoryCredential());
		client.addFilter(new HTTPBasicAuthFilter(plain.getAccount(), plain.getSecret()));

		TypedParameter inputParameters[] = client.resource(cloud.getFactory()).path("inputParameters").get(TypedParameter[].class);
		TypedParameter outputParameters[] = client.resource(cloud.getFactory()).path("outputParameters").get(TypedParameter[].class);
		ArrayList<TypedParameter> in = new ArrayList<TypedParameter>();
		in.addAll(Arrays.asList(inputParameters));
		ArrayList<TypedParameter> out = new ArrayList<TypedParameter>();
		out.addAll(Arrays.asList(outputParameters));
		cloud.setInputParameters(in);
		cloud.setOutputParameters(out);

	}
	
	public static String testAccount(Cloud cloud, User user, Account account, boolean fix) {
		String result;
		Client client = Client.create();
		Credential plain = Credential.unencrypted(cloud.getFactoryCredential());
		client.addFilter(new HTTPBasicAuthFilter(plain.getAccount(), plain.getSecret()));
		WebResource resource = client.resource(cloud.getFactory()).path("accountTest");
		Credential c = Credential.reencrypt(account.getCredential(), Credential.unencrypted(cloud.getFactoryCredential()).getSecret());

		Form form = new Form();
		form.add("fix", fix);
		form.add("id", c.getAccount());
		form.add("secret", c.getSecret());
		form.add("key", account.getName());
		form.add("location", cloud.getLocation().toString());
		form.add("email", user.getName());
		form.add("firstName", user.getFirstName());
		form.add("lastName", user.getLastName());
		form.add("securityGroup", "default");
		try {
			result = resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, form);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Test Account exception ", e);
			result = e.getMessage();
		}
		return result;
	}

	public static class CloudManager extends CachingAbstractManager<Cloud> {		
		public CloudManager() {
		}
		@Override
		protected URI myPath() {
			return UriBuilder.fromUri(Resource.get("baseURI", "http://127.0.0.1:8888/resources")).path(CloudResource.class).build();
		}

		@Override
		protected GenericModelDao<Cloud> itemDaoFactory() {
			return new ServiceModelDao<Cloud>(Cloud.class);
		}

		public Cloud load(Long id, User requestor) throws NotFoundException { return super.get(id, requestor); }
		/**
		 * Locate a item from the persistent store based on the item name.
		 * @param name
		 * @param requestor requesting user
		 * @return the item
		 * @throws NotFoundException is the object does not exist
		 */
		public Cloud load(String name, User requestor) throws NotFoundException { return super.get(name, requestor); }
		/**
		 * Locate a item from the persistent store based on the item URI.
		 * @param uri
		 * @param requestor requesting user
		 * @return the item
		 * @throws NotFoundException is the object does not exist
		 */
		public Cloud load(URI uri, User requestor) throws NotFoundException { return super.get(uri, requestor); }
		/**
		 * Locate a item from the persistent store based on the item URI.
		 * @param uri
		 * @param requestor requesting user
		 * @return the item
		 * @throws NotFoundException is the object does not exist
		 */
		public Cloud load(URI uri) throws NotFoundException { return super.get(uri); }
		public Cloud load(URI uri, URI requestor) throws NotFoundException { return super.get(uri, requestor); }

		public Collection<Cloud> getCollection(URI user) { return super.getCollection(user); }
		public Collection<Cloud> getCollection(User user) { return super.getCollection(user); }
		public void add(Cloud cloud) { super.add(cloud); }
		public void delete(Cloud cloud) { super.delete(cloud); }
		public void update(Cloud cloud) { super.update(cloud); }

	}
	final public static CloudManager dao = new CloudManager();
}
