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
import n3phele.service.model.Relationship;
import n3phele.service.model.RelationshipCollection;
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
@Path("/relationship")
public class RelationshipResource {
	private static Logger log = Logger.getLogger(RelationshipResource.class.getName());

	@Context
	UriInfo uriInfo;
	@Context
	protected SecurityContext securityContext;
	
	
	@GET
	@Produces("application/json")
	@RolesAllowed("authenticated")
	public RelationshipCollection list() {
		
		log.warning("list relationship no summary");
		Collection<Relationship> result = dao.getCollection(UserResource.toUser(securityContext));
		//return new ServiceCollection(result, 0, -1);
		return new RelationshipCollection(result);
	}
	
	//As Service class is modified this method will have need to be modified too
	//testService don't exists... may have to create a testService method
		
	@POST
	@Produces("text/plain")
	@RolesAllowed("authenticated")
	public Response add(
			@FormParam("uriStackMaster") String uriStackMaster,
			@FormParam("uriStackSubordinate") String uriStackSubordinate,
			@FormParam("type") String type,
			@FormParam("description") String description, 
			@FormParam("name") String name, 
			@FormParam("owner") URI owner, 
			@FormParam("isPublic") boolean isPublic) {

		Relationship relationship = new Relationship(uriStackMaster,uriStackSubordinate,type,description, name, owner, isPublic);
		dao.add(relationship);
		//TODO Override toString of service
		log.warning("Created " +relationship);
		return Response.created(relationship.getUri()).build();
	}
	
	@GET
	@Produces("application/json")
	@Path("{id}")
	@RolesAllowed("authenticated")
	public Relationship get(@PathParam("id") Long id) throws NotFoundException {
		Relationship item = dao.load(id, UserResource.toUser(securityContext));
		return item;
	}
	
	@GET
	@Produces("application/json")
	@Path("byName")
	@RolesAllowed("authenticated")
	public Relationship get(@QueryParam("id") String id){
		Relationship item = dao.load(id, UserResource.toUser(securityContext)); 
		return item;
	}
	
	@POST
	@Produces("application/json")
	@Path("{id}")
	@RolesAllowed("authenticated")
	public Relationship update(@PathParam("id") Long id, 
			@FormParam("name") String name, 
			@FormParam("description") String description
			) {

		Relationship item = dao.load(id, UserResource.toUser(securityContext));
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("bad name");
		}
		
		item.setName(name);
		item.setDescription(description == null ? null : description.trim());
	
		dao.update(item);
		log.warning("Updated " + item.getUri() );
		return item;
	}
	@DELETE
	@RolesAllowed("authenticated")
	@Path("{id}")
	public void delete(@PathParam ("id") Long id) throws NotFoundException {
		Relationship item = dao.load(id, UserResource.toUser(securityContext));
		dao.delete(item);
	}
	

	final public static RelationshipManager dao = new RelationshipManager();
}
