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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import n3phele.service.core.ForbiddenException;
import n3phele.service.core.NotFoundException;
import n3phele.service.core.Resource;
import n3phele.service.core.UnprocessableEntityException;
import n3phele.service.model.CachingAbstractManager;
import n3phele.service.model.FileSpecification;
import n3phele.service.model.Origin;
import n3phele.service.model.RepoListResponse;
import n3phele.service.model.RepositoryCollection;
import n3phele.service.model.ServiceModelDao;
import n3phele.service.model.ValidationResponse;
import n3phele.service.model.core.Collection;
import n3phele.service.model.core.Credential;
import n3phele.service.model.core.GenericModelDao;
import n3phele.service.model.core.Helpers;
import n3phele.service.model.core.User;
import n3phele.service.model.repository.FileNode;
import n3phele.service.model.repository.Repository;
import n3phele.storage.CloudStorage;

import com.google.appengine.api.backends.BackendServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.RetryOptions;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Builder;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

@Path("/repository")
public class RepositoryResource {
	private static Logger log = Logger.getLogger(RepositoryResource.class.getName());  

	@Context UriInfo uriInfo;
	@Context SecurityContext securityContext;

	@GET
	@RolesAllowed("authenticated")
	@Produces("application/json")
	public RepositoryCollection list(
			@DefaultValue("false") @QueryParam("summary") Boolean summary) throws NotFoundException {

		log.warning("get Repository entered with summary "+summary);


		Collection<Repository> result = dao.getCollection(UserResource.toUser(securityContext));
		if(summary) {
			if(result.getElements() != null) {
				for(int i=0; i < result.getElements().size(); i++) {
					result.getElements().set(i, Repository.summary(result.getElements().get(i)));
				}
			}
		}
		return new RepositoryCollection(result,0,-1);
	}

	@POST
	@RolesAllowed("authenticated")
	@Produces("text/plain")
	//@Path("")
	public Response add(@FormParam("name") String name,
			@FormParam("description") String description,
			@FormParam("repositoryId") String repoId,
			@FormParam("secret") String secret,
			@FormParam("target") URI target,
			@FormParam("root") String root,
			@FormParam("kind") String kind,
			@DefaultValue("true") @FormParam("create") boolean create,
			@FormParam("isPublic") boolean isPublic) throws URISyntaxException {

		Repository result = new Repository(name, description, new Credential(repoId, secret).encrypt(), target, root, kind, 
				UserResource.toUser(securityContext).getUri(), isPublic);
		dao.add(result);
		boolean created=true;
		if(create) {
			try {
				created = createBucket(result);
			} catch (ForbiddenException e) {
				dao.delete(result);
				throw e;
			}
		}
		log.warning("Created "+((kind.equals("S3") && create)?" with repo response "+created:"")+result.getUri());
		return Response.created(result.getUri()).build();
	}
	
//	@GET
//	@RolesAllowed("authenticated")
//	@Produces("application/json")
//	@Path("{id}/sign")
//	public UploadSignature sign(@PathParam ("id") Long id,
//			@QueryParam("name") String name
//			) throws EntityNotFoundException {
//
//		Repository item = dao.load(id, UserResource.toUser(securityContext));
//		if(!CloudStorage.factory().hasTemporaryURL(item))
//			warmupBackend(item);
//		
//		UploadSignature result = CloudStorage.factory().getUploadSignature(item, name);
//		log.info(result.toString());
//		
//		return result;
//	}
	
	@GET
	@RolesAllowed("authenticated")
	@Produces("text/plain")
	@Path("{id}/redirect")
	public Response redirect(@PathParam ("id") Long id,
			@DefaultValue("") @QueryParam("name") String name,
			@DefaultValue("") @QueryParam("path") String path) throws NotFoundException {

		Repository item = dao.load(id, UserResource.toUser(securityContext));
		if(!CloudStorage.factory().hasTemporaryURL(item)) {
			warmupBackend(item);
		}
		return Response.seeOther(CloudStorage.factory().getRedirectURL(item, path, name)).build();
	}

	@GET
	@RolesAllowed("authenticated")
	@Produces("application/json")
	@Path("{id}") 
	public Repository get( @PathParam ("id") Long id) throws URISyntaxException, EntityNotFoundException {

		Repository item = dao.load(id, UserResource.toUser(securityContext));
		return item;
	}
	
	@GET
	@RolesAllowed("authenticated")
	@Produces("application/json")
	@Path("{id}/list") 
	public RepoListResponse list( @PathParam ("id") Long id,
							@QueryParam("prefix") String prefix,
							@QueryParam("max") int max
							) throws EntityNotFoundException {

		Repository item = dao.load(id, UserResource.toUser(securityContext));
		if(!CloudStorage.factory().hasTemporaryURL(item)) {
			warmupBackend(item);
		}
		RepoListResponse result = new RepoListResponse();
		List<FileNode> crumbs = new ArrayList<FileNode>();
		FileNode f = FileNode.newFolder(item.getName(), null, item, false);
		crumbs.add(f);
		if(prefix != null) {
			String bits[] = prefix.split("/");
			String path = null;
			String name = null;
			for(String s : bits) {
				if(path != null) {
					path += name + "/";
				} else if(name != null) {
					path = name + "/";
				}
				name = s;
				f = FileNode.newFolder(name, path, item, false);
				crumbs.add(f);
			}
		}
		List<FileNode> files=null;
		files = CloudStorage.factory().getFileList(item, prefix, max); 
		result.setCrumbs(crumbs);
		result.setFiles(files);
		return result;
	}
	
	@GET
	@RolesAllowed("authenticated")
	@Produces("application/json")
	@Path("{id}/validate") 
	public ValidationResponse validate( @PathParam ("id") Long id,
							@QueryParam("filename") String filename
							) throws EntityNotFoundException {

		Repository item = dao.load(id, UserResource.toUser(securityContext));
		if(!CloudStorage.factory().hasTemporaryURL(item)) {
			warmupBackend(item);
		}
		boolean exists = CloudStorage.factory().checkExists(item, filename);
		ValidationResponse result = new ValidationResponse(exists);

		return result;
	}
	@POST
	@RolesAllowed("authenticated")
	@Produces("application/json")
	@Path("{id}/permissions")
	public Response permissions(@PathParam("id") Long id,
			@FormParam("filename") String name,
			@FormParam("isPublic") boolean isPublic) throws IllegalArgumentException, NotFoundException {

		Repository item = dao.load(id, UserResource.toUser(securityContext));
		setPermissions(item, name, isPublic);
		/*
		 * Here magic happens and the folder is made public
		 */
	
		return Response.ok().build();
	}
	@GET
	@RolesAllowed("authenticated")
	@Produces("application/json")
	@Path("{id}/origin") 
	public Response origin( 
			@PathParam("id") long id,
			@DefaultValue("") @QueryParam ("path") String path,
			@QueryParam ("name") String name) throws NotFoundException  {

		Origin origin = null;
		Repository item = dao.load(id, UserResource.toUser(securityContext));
		UriBuilder ref = UriBuilder.fromUri(item.getTarget());
		ref.path(item.getRoot()).path(path).path(name);
		origin = Origin.getCurrentReference(ref.build().toString());

		return Response.ok(origin).build();
	}
	
	@DELETE
	@RolesAllowed("authenticated")
	@Produces("application/json")
	@Path("{id}/file")
	public Response deleteFile(@PathParam("id") long id,
			@QueryParam ("filename") String filename) throws NotFoundException  {

		if(Helpers.isBlankOrNull(filename)) {
			throw new UnprocessableEntityException("Filename must be specified");
		}
		log.info("Delete file "+filename);
		Repository item = dao.load(id, UserResource.toUser(securityContext));
		CloudStorage.factory().deleteFile(item, filename);
		//ChangeManager.factory().addChange(item);

		return Response.ok().build();
	}
	
	@DELETE
	@RolesAllowed("authenticated")
	@Produces("application/json")
	@Path("{id}/folder")
	public Response deleteFolder(@PathParam("id") long id,
			@QueryParam ("filename") String filename) throws NotFoundException  {

		if(Helpers.isBlankOrNull(filename)) {
			throw new UnprocessableEntityException("Filename must be specified");
		}
		log.info("Delete folder "+filename);
		Repository item = dao.load(id, UserResource.toUser(securityContext));
		deleteFolder(item, filename);

		return Response.ok().build();
	}
	
	@POST
	@RolesAllowed("authenticated")
	@Produces("application/json")
	@Path("{id}")
	public Repository update(@PathParam("id") Long id,
			@FormParam("name") String name,
			@FormParam("description") String description,
			@FormParam("repositoryId") String repoId,
			@FormParam("secret") String secret,
			@FormParam("target") URI target,
			@FormParam("root") String root,
			@FormParam("kind") String kind,
			@DefaultValue("true") @FormParam("create") boolean create,
			@FormParam("isPublic") boolean isPublic) throws IllegalArgumentException, NotFoundException {

		Repository item = dao.load(id, UserResource.toUser(securityContext));
		if(name == null || name.trim().length()==0) {
			throw new IllegalArgumentException("bad name");
		}
		if(root == null || root.trim().length()==0) {
			throw new IllegalArgumentException("bad root");
		}
		if(target == null) {
			throw new IllegalArgumentException("bad target");
		}
		if(kind == null || kind.trim().length()==0) {
			throw new IllegalArgumentException("bad kind");
		}
		Credential credential = null;
		if(secret != null && secret.trim().length() != 0) {
			credential = new Credential(repoId, secret).encrypt();
		}
		
		item.setName(name);
		item.setDescription(description==null?null:description.trim());
		item.setTarget(target);
		item.setRoot(root);
		if(credential != null)
			item.setCredential(credential);
		item.setPublic(isPublic);
		dao.update(item);
		if(credential != null) {
			boolean exists=false;
			if(create) {
				exists = createBucket(item);
			}
			log.warning("Updated "+((kind.equals("S3") && create)?" with repo response "+exists:"")+item.getUri());

		}
	
		log.warning("Updated "+ item.getUri()+((credential != null)?" including credential "+item:""));
		return item;
	}
	
	
	@GET
	@RolesAllowed("authenticated")
	@Produces("application/json")
	@Path("/find") 
	public Repository get( @QueryParam ("name") String name)  {

		Repository item = dao.load(name, UserResource.toUser(securityContext));
		return item;
	}

	@DELETE
	@RolesAllowed("authenticated")
	@Path("{id}")
	public void delete(@PathParam ("id") Long id) throws NotFoundException {
		Repository item = dao.load(id, UserResource.toUser(securityContext));
		dao.delete(item);
	}
	


	public  boolean createRepoForUser(User user, String accountId,
			String secret) {
		Credential credential = null;
		if(secret != null && secret.trim().length() != 0) {
			credential = new Credential(accountId, secret).encrypt();
		}
		Repository s3 = new Repository("s3-desktop", "n3phele desktop on Amazon S3", 
				credential, URI.create("https://s3.amazonaws.com"), "n3phele", 
				"S3", user.getUri(), false);
		
		dao.add(s3);
		s3.setRoot("n3phele-"+s3.getId());
		dao.update(s3);
		createBucket(s3);
		return true;
		
	}


	/** Creates an Cloud storage bucket. 
	 * @param repo specifies the bucket with the root field. The target and credential are used to access the cloud storage.
	 * @return true if the bucket was created, false if it already existed for the specified credential.
	 * @throws ForbiddenException the specified repo already exists and is inaccessible using the supplied credential.
	 */
	public boolean createBucket(Repository repo) throws ForbiddenException {
		return CloudStorage.factory().createBucket(repo);
	}
	
	
	/** Fills in size, modified and canonicalFilename attributes
	 * @param f
	 * @param owner
	 */
	public void updateFileSpecificationInfo(FileSpecification f,
			User owner) throws NotFoundException {

		Repository repo = dao.load(f.getRepository(), owner);
		UriBuilder result = UriBuilder.fromUri(repo.getTarget());
		result.path(repo.getRoot()).path(f.getFilename());			
		f.setCanonicalPath(result.build().toString());
		FileNode fn = CloudStorage.factory().getMetadata(repo, f.getFilename());
		f.setModified(fn.getModified());
		f.setSize(fn.getSize());
	}
	
	public boolean deleteFolder(Repository repo, String filename) {
		boolean result = false;
		try {
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(Builder.withUrl(String.format("/admin/worker/repo/%d/deleteFolder",repo.getId())).param("filename", filename).retryOptions(RetryOptions.Builder.withTaskRetryLimit(1)).method(Method.GET));
			result = true;
		} catch (Exception ignore) {
			log.log(Level.SEVERE, "delete folder processing exception", ignore);
		}
		return result;
	}
	
	public boolean do_deleteFolder(Long id, String filename) {
		boolean result = false;
		Repository repo = dao.load(id);
		result = CloudStorage.factory().deleteFolder(repo, filename);

		//ChangeManager.factory().addChange(repo);
		return result;
	}
	
	private void warmupBackend(Repository repo) {
		TaskOptions ping = Builder.withUrl("/repository/start").method(Method.GET).param("id", repo.getId().toString()).header("Host", BackendServiceFactory.getBackendService().getBackendAddress("gateway"));
		Queue queue = QueueFactory.getQueue("BackendQueue");
		queue.add(ping);
	}
	
	public boolean setPermissions(Repository repo, String filename, boolean isPublic) {
		boolean result = false;
	
		try {
			result = CloudStorage.factory().setPermissions(repo, filename, isPublic);
		} catch(Exception e) {
			log.log(Level.SEVERE, "folder Permissions", e);
		}
		//ChangeManager.factory().addChange(repo);
		return result;
	}
	
	
	public static class RepositoryManager extends CachingAbstractManager<Repository> {
		public RepositoryManager() {
		}
		@Override
		protected URI myPath() {
			return UriBuilder.fromUri(Resource.get("baseURI", "http://localhost:8888/resources")).path(RepositoryResource.class).build();
		}

		@Override
		protected GenericModelDao<Repository> itemDaoFactory() {
			return new ServiceModelDao<Repository>(Repository.class);
		}
	
		/**
		 * Locate a item from the persistent store based on the item name.
		 * @param name
		 * @return the item
		 * @throws NotFoundException is the object does not exist
		 */
		protected Repository load(Long id) throws NotFoundException { return super.get(id); }
		/**
		 * Locate a item from the persistent store based on the item name.
		 * @param name
		 * @param requestor requesting user
		 * @return the item
		 * @throws NotFoundException is the object does not exist
		 */

		public Repository load(Long id, User requestor) throws NotFoundException { return super.get(id, requestor); }
		/**
		 * Locate a item from the persistent store based on the item name.
		 * @param name
		 * @param requestor requesting user
		 * @return the item
		 * @throws NotFoundException is the object does not exist
		 */
		public Repository load(String name, User requestor) throws NotFoundException { return super.get(name, requestor); }
		/**
		 * Locate a item from the persistent store based on the item URI.
		 * @param uri
		 * @param requestor requesting user
		 * @return the item
		 * @throws NotFoundException is the object does not exist
		 */
		public Repository load(URI uri, User requestor) throws NotFoundException { return super.get(uri, requestor); }
		/**
		 * Locate a item from the persistent store based on the item URI.
		 * @param uri
		 * @return the item
		 * @throws NotFoundException is the object does not exist
		 */
		public Repository load(URI uri) throws NotFoundException { return super.get(uri); }
		
		public Collection<Repository> getCollection(User user) { return super.getCollection(user); }
		public void add(Repository repository) { super.add(repository); }
		public void delete(Repository repository) { super.delete(repository); }
		public void update(Repository repository) { super.update(repository); }

	}
	final public static RepositoryManager dao = new RepositoryManager();

}
