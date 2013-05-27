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
import n3phele.service.model.core.Collection;
import n3phele.service.model.core.Credential;
import n3phele.service.model.core.GenericModelDao;
import n3phele.service.model.core.User;
import n3phele.service.rest.impl.AccountResource.AccountManager;

//Starting a new Resource, Service class will need it's JSons counterparts.
@Path("/service")
public class ServiceResource {
	private static Logger log = Logger.getLogger(AccountResource.class.getName());

	@Context
	UriInfo uriInfo;
	@Context
	protected SecurityContext securityContext;
	
	
	
//	@GET
//	@Produces("application/json")
//	@RolesAllowed("authenticated")
//	public ServiceCollection list(@DefaultValue("false") @QueryParam("summary") Boolean summary) {
//		
//		log.warning("list Services entered with summary " + summary);
//		Collection<Service> result = dao.getCollection(UserResource.toUser(securityContext),summary);
//		return new ServiceCollection(result, 0, -1);
//		//return new ServiceCollection(result);
//	}
	
	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	public ServiceCollection list() {
		
		log.warning("list Services, no summary");
		Collection<Service> result = dao.getCollection(UserResource.toUser(securityContext));
		//return new ServiceCollection(result, 0, -1);
		return new ServiceCollection(result);
	}
	
	//As Service class is modified this method will have need to be modified too
		
	@POST
	@Produces("text/plain")
	@RolesAllowed("authenticated")
	public Response add(
			@FormParam("description") String description, 
			@FormParam("name") String name, 
			@FormParam("owner") URI owner,  
			@FormParam("isPublic") boolean isPublic) {

		Service service = new Service(description, name, null, owner, isPublic);
		dao.add(service);
		//TODO Override toString of service
		log.warning("Created " +service);
		return Response.created(service.getUri()).build();
	}
	
	@GET
	@Produces("application/json")
	@Path("{id}")
	@RolesAllowed("authenticated")
	public Service get(@PathParam("id") Long id) throws NotFoundException {
		Service item = dao.load(id, UserResource.toUser(securityContext));
		return item;
	}
	
	@GET
	@Produces("application/json")
	@Path("byName")
	@RolesAllowed("authenticated")
	public Service get(@QueryParam("id") String id)throws NotFoundException{
		Service item = dao.load(id, UserResource.toUser(securityContext)); 
		return item;
	}
	@DELETE
	@RolesAllowed("authenticated")
	@Path("{id}")
	public void delete(@PathParam ("id") Long id) throws NotFoundException {
		Service item = dao.load(id, UserResource.toUser(securityContext));
		dao.delete(item);
	}
	// may have to move this inner class, creating a new independent class
//	static public class ServiceManager extends CachingAbstractManager<Service> {
//		public ServiceManager() {
//		}
//
//		@Override
//		protected URI myPath() {
//			return UriBuilder.fromUri(Resource.get("baseURI", "http://127.0.0.1:8888/resources")).path(ServiceResource.class).build();
//		}
//
//		@Override
//		public GenericModelDao<Service> itemDaoFactory() {
//			return new ServiceModelDao<Service>(Service.class);
//		}
//
//		public Service load(Long id, User requestor) throws NotFoundException {
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
//		public Service load(String name, User requestor) throws NotFoundException {
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
//		public Service load(URI uri, User requestor) throws NotFoundException {
//			return super.get(uri, requestor);
//		}
//
//		public Service load(URI uri, URI requestor) throws NotFoundException {
//			return super.get(uri, requestor);
//		}
//
//		public void add(Service account) {
//			super.add(account);
//		}
//
//		public void update(Service account) {
//			super.update(account);
//		}
//
//		public void delete(Service account) {
//			super.delete(account);
//		}
//
//		public Collection<Service> getCollection(User user) {
//			return super.getCollection(user);
//		}
//		
////		public Collection<Service> getAccountList(User user, boolean summary) {
////
////			log.warning("list Services entered with summary " + summary);
////
////			Collection<Service> result = getCollection(user);
////
////			if (result.getElements() != null) {
////				for (int i = 0; i < result.getElements().size(); i++) {
////					Service account = result.getElements().get(i);
////					if (summary)
////						result.getElements().set(i, Service.summary(account));
////				}
////			}
////			return result;
////		}
//	}

	final public static ServiceManager dao = new ServiceManager();
}
