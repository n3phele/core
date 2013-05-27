package n3phele.service.rest.impl;

import static com.googlecode.objectify.ObjectifyService.ofy;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.mortbay.jetty.Server;

import java.text.SimpleDateFormat;

import n3phele.service.actions.CountDownAction;
import n3phele.service.core.NotFoundException;
import n3phele.service.core.Resource;
import n3phele.service.model.Account;
import n3phele.service.model.AccountCollection;
import n3phele.service.model.Action;
import n3phele.service.model.ActivityData;
import n3phele.service.model.ActivityDataCollection;
import n3phele.service.model.CachingAbstractManager;
import n3phele.service.model.Cloud;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.CloudProcessCollection;
import n3phele.service.model.CostsCollection;
import n3phele.service.model.Service;
import n3phele.service.model.ServiceCollection;
import n3phele.service.model.ServiceModelDao;
import n3phele.service.model.Stack;
import n3phele.service.model.StackCollection;
import n3phele.service.model.core.Collection;
import n3phele.service.model.core.Credential;
import n3phele.service.model.core.GenericModelDao;
import n3phele.service.model.core.User;
import n3phele.service.rest.impl.AccountResource.AccountManager;

//Starting a new Resource, Stack class will need it's JSons counterparts.
@Path("/stack")
public class StackResource {
	private static Logger log = Logger.getLogger(AccountResource.class.getName());

	@Context
	UriInfo uriInfo;
	@Context
	protected SecurityContext securityContext;
	
	
	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	public StackCollection list() {
		
		log.warning("list Services, no summary");
		Collection<Stack> result = dao.getCollection(UserResource.toUser(securityContext));
		//return new ServiceCollection(result, 0, -1);
		return new StackCollection(result);
	}
	
	//As Service class is modified this method will have need to be modified too
	//testService don't exists... may have to create a testService method
		
	@POST
	@Produces("text/plain")
	@RolesAllowed("authenticated")
	public Response add(
			@FormParam("description") String description, 
			@FormParam("name") String name, 
			@FormParam("owner") URI owner, 
			@FormParam("isPublic") boolean isPublic) {

		Stack stack = new Stack(description, name, null, owner, isPublic);
		dao.add(stack);
		//TODO Override toString of service
		log.warning("Created " +stack);
		return Response.created(stack.getUri()).build();
	}
	
	@GET
	@Produces("application/json")
	@Path("{id}")
	@RolesAllowed("authenticated")
	public Stack get(@PathParam("id") Long id) throws NotFoundException {
		Stack item = dao.load(id, UserResource.toUser(securityContext));
		return item;
	}
	
	@GET
	@Produces("application/json")
	@Path("byName")
	@RolesAllowed("authenticated")
	public Stack get(@QueryParam("id") String id){
		Stack item = dao.load(id, UserResource.toUser(securityContext)); 
		return item;
	}
	@DELETE
	@RolesAllowed("authenticated")
	@Path("{id}")
	public void delete(@PathParam ("id") Long id) throws NotFoundException {
		Stack item = dao.load(id, UserResource.toUser(securityContext));
		dao.delete(item);
	}
	
	// may have to move this inner class, creating a new independent class
//	static public class StackManager extends CachingAbstractManager<Stack> {
//		public StackManager() {
//		}
//
//		@Override
//		protected URI myPath() {
//			return UriBuilder.fromUri(Resource.get("baseURI", "http://127.0.0.1:8888/resources")).path(StackResource.class).build();
//		}
//
//		@Override
//		public GenericModelDao<Stack> itemDaoFactory() {
//			return new ServiceModelDao<Stack>(Stack.class);
//		}
//
//		public Stack load(Long id, User requestor) throws NotFoundException {
//			return super.get(id, requestor);
//		}
//
//		/**
//		 * Locate a item from the persistent store based on the item name.
//		 * 
//		 * @param name
//		 * @param requestor
//		 *            requesting user
//		 * @return the item
//		 * @throws NotFoundException
//		 *             is the object does not exist
//		 */
//		public Stack load(String name, User requestor) throws NotFoundException {
//			return super.get(name, requestor);
//		}
//
//		/**
//		 * Locate a item from the persistent store based on the item URI.
//		 * 
//		 * @param uri
//		 * @param requestor
//		 *            requesting user
//		 * @return the item
//		 * @throws NotFoundException
//		 *             is the object does not exist
//		 */
//		public Stack load(URI uri, User requestor) throws NotFoundException {
//			return super.get(uri, requestor);
//		}
//
//		public Stack load(URI uri, URI requestor) throws NotFoundException {
//			return super.get(uri, requestor);
//		}
//
//		public void add(Stack account) {
//			super.add(account);
//		}
//
//		public void update(Stack account) {
//			super.update(account);
//		}
//
//		public void delete(Stack account) {
//			super.delete(account);
//		}
//
//		public Collection<Stack> getCollection(User user) {
//			return super.getCollection(user);
//		}
//	
//	}
	final public static StackManager dao = new StackManager();
}
