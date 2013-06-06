package n3phele;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;

import n3phele.process.CloudProcessTest.CloudResourceTestWrapper;
import n3phele.service.actions.CountDownAction;
import n3phele.service.model.Action;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.Relationship;
import n3phele.service.model.Service;
import n3phele.service.model.Stack;
import n3phele.service.model.core.BaseEntity;
import n3phele.service.model.core.Collection;
import n3phele.service.model.core.Entity;
import n3phele.service.model.core.User;
import n3phele.service.rest.impl.CloudProcessResource;
import n3phele.service.rest.impl.ServiceResource;
import n3phele.service.rest.impl.StackResource;
import n3phele.service.rest.impl.UserResource;

//import org.apache.tools.ant.types.spi.Service;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.googlecode.objectify.Key;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.representation.Form;

public class StackResourceWebServiceTest {
	public StackResourceWebServiceTest() throws Exception {

	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
		helper.setUp();
		client = Client.create();
		client.addFilter(new HTTPBasicAuthFilter("test-user@gmail.com", "testit!"));
		webResource = client.resource(UriBuilder.fromUri("http://127.0.0.1:8888/resources").path(StackResource.class).build());
	}

	private Client client;
	private WebResource webResource;
	private URI testUser;
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig().setApplyAllHighRepJobPolicy(), new LocalTaskQueueTestConfig().setDisableAutoTaskExecution(false).setCallbackClass(LocalTaskQueueTestConfig.DeferredTaskCallback.class));

	@Test
	public void addStackTest() throws URISyntaxException {
		Form form = new Form();
		form.add("description", "teste desc");
		form.add("name", "Stack");
		form.add("owner", "http://127.0.0.1:8888/resources/user/4");
		form.add("isPublic", true);
		URI user = null;
		if (user == null) {
			ClientResponse result = webResource.post(ClientResponse.class, form);
			user = result.getLocation();
			Assert.assertEquals(201, result.getStatus());
			client.removeAllFilters();
			client.addFilter(new HTTPBasicAuthFilter("test-user@gmail.com", "testit!"));
		}
		Stack result = webResource.uri(user).get(Stack.class);
		System.out.println(webResource.uri(user).get(Stack.class).getUri().toString());
		Assert.assertEquals("Stack", result.getName());
		Assert.assertEquals("http://127.0.0.1:8888/resources/user/4", result.getOwner().toString());
		Assert.assertEquals("teste desc", result.getDescription());
		Assert.assertEquals(true, result.isPublic());
	}

	@Test
	public void updateStackTest() throws URISyntaxException {
		Form form = new Form();
		form.add("description", "teste desc");
		form.add("name", "service");
		form.add("owner", "http://127.0.0.1:8888/resources/user/4");
		form.add("isPublic", true);
		URI user = null;
		if (user == null) {
			ClientResponse result = webResource.post(ClientResponse.class, form);
			user = result.getLocation();
			Assert.assertEquals(201, result.getStatus());
			client.removeAllFilters();
			client.addFilter(new HTTPBasicAuthFilter("test-user@gmail.com", "testit!"));
		}
		Stack result = webResource.uri(user).get(Stack.class);
		System.out.println(webResource.uri(user).get(Stack.class).getUri().toString());
		Assert.assertEquals("service", result.getName());
		Assert.assertEquals("http://127.0.0.1:8888/resources/user/4", result.getOwner().toString());
		Assert.assertEquals("teste desc", result.getDescription());
		Assert.assertEquals(true, result.isPublic());

		form = new Form();
		form.add("description", "teste desc2");
		form.add("name", "service2");
		form.add("owner", "http://127.0.0.1:8888/resources/user/4");
		form.add("isPublic", true);
		URI uri = new URI("http://127.0.0.1:8888/resources/stack/" + result.getId());
		ClientResponse resultC = webResource.uri(uri).post(ClientResponse.class, form);
		Assert.assertEquals(200, resultC.getStatus());

		result = webResource.uri(user).get(Stack.class);
		Assert.assertEquals("service2", result.getName());
		Assert.assertEquals("teste desc2", result.getDescription());
		Assert.assertEquals("http://127.0.0.1:8888/resources/user/4", result.getOwner().toString());
		Assert.assertEquals(true, result.isPublic());

	}

	@Test
	public void addRelationshipTest() throws URISyntaxException {
		Form form = new Form();
		form.add("description", "teste desc");
		form.add("name", "Stack");
		form.add("owner", "http://127.0.0.1:8888/resources/user/4");
		form.add("isPublic", true);
		URI user = null;
		if (user == null) {
			ClientResponse result = webResource.post(ClientResponse.class, form);
			user = result.getLocation();
			Assert.assertEquals(201, result.getStatus());
			client.removeAllFilters();
			client.addFilter(new HTTPBasicAuthFilter("test-user@gmail.com", "testit!"));
		}
		Stack result = webResource.uri(user).get(Stack.class);
		Assert.assertEquals("Stack", result.getName());
		Assert.assertEquals("http://127.0.0.1:8888/resources/user/4", result.getOwner().toString());
		Assert.assertEquals("teste desc", result.getDescription());
		Assert.assertEquals(true, result.isPublic());

		// Stack stack = new
		// Stack("tested stack","stackForAdd",result.getUri(),true);
		// StackResource.dao.add(stack);

		// FORM FOR THE RELATIONSHIP
		form = new Form();
		form.add("uriStackMaster", "oneUriOfAStack");
		form.add("uriStackSubordinate", "AnotherUriOfAStack");
		form.add("type", "DataBase");
		form.add("description", "teste desc");
		form.add("name", "service");
		form.add("owner", "http://127.0.0.1:8888/resources/user/4");
		form.add("isPublic", true);
		URI uriForm = new URI("http://127.0.0.1:8888/resources/relationship");
		ClientResponse resultRelation = webResource.uri(uriForm).post(ClientResponse.class, form);
		Assert.assertEquals(201, resultRelation.getStatus());
		Relationship relationship = webResource.uri(resultRelation.getLocation()).get(Relationship.class);

		URI uri = new URI("http://127.0.0.1:8888/resources/stack/" + result.getId() + "/addRelation/" + relationship.getId());
		form = new Form();
		ClientResponse resultC = webResource.uri(uri).post(ClientResponse.class, form);
		Assert.assertEquals(200, resultC.getStatus());

		result = webResource.uri(user).get(Stack.class);
		Assert.assertEquals("Stack", result.getName());
		Assert.assertEquals(relationship.getUri().toString(), result.getRelations().get(0).toString());

		// Have to check if the uri is leading to the right place
		int index = relationship.getUri().toString().lastIndexOf('/');
		int id = Integer.parseInt(relationship.getUri().toString().substring(index + 1));
		Assert.assertEquals((long) relationship.getId(), id);
		uriForm = new URI("http://127.0.0.1:8888/resources/relationship/" + id);
		Relationship relationshipFromStack = webResource.uri(uriForm).get(Relationship.class);
		Assert.assertEquals(relationshipFromStack.getUriStackMaster().toString(), "oneUriOfAStack");
		Assert.assertEquals(relationshipFromStack.getUriStackSubordinate().toString(), "AnotherUriOfAStack");
	}

	@Test
	public void addVMtoStack() throws InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, URISyntaxException {
		client.removeAllFilters();
		client.addFilter(new HTTPBasicAuthFilter("test-user@gmail.com", "testit!"));
		User root = UserResource.Root;
		Action task = new CountDownAction();
		task.setUri(new URI("http://www.google.com.br"));
		CloudResourceTestWrapper crtw = new CloudResourceTestWrapper();
		crtw.addSecurityContext(root);
		CloudProcess cpParent = new CloudProcess(UserResource.Root.getUri(), "jerrysParent", null, true, task);
		CloudProcessResource.dao.add(cpParent);
		CloudProcess cp = new CloudProcess(UserResource.Root.getUri(), "jerry", cpParent, false, task);
		CloudProcessResource.dao.add(cp);
		System.out.println("URI : " + cp.getUri() + " ID: + " + cp.getId());
		// add a stack
		Stack stack = new Stack("teste desc", "Stack", new URI("http://127.0.0.1:8888/resources/user/4"), true);
		StackResource.dao.add(stack);
		Stack result = StackResource.dao.load(stack.getUri(), root);
		Assert.assertEquals("Stack", result.getName());
		Assert.assertEquals("http://127.0.0.1:8888/resources/user/4", result.getOwner().toString());
		Assert.assertEquals("teste desc", result.getDescription());
		Assert.assertEquals(true, result.isPublic());
		StackResourceTestWrapper stackResource = new StackResourceTestWrapper();
		stackResource.addSecurityContext(root);

		stackResource.addCloudProcessToStack(result.getId(), cpParent.getId() + "_", cp.getId());
		cp = CloudProcessResource.dao.load(cp.getUri());
		// result = webResource.uri(user).get(Stack.class);
		// Assert.assertEquals("Stack", result.getName());
		// Assert.assertEquals(cp.getUri().toString(),
		// result.getVms().get(0).toString());

		Assert.assertEquals(cp.getUri().toString(), result.getVms().get(0).toString());
	}

	@Test
	public void listStackTest() {

		String myName = "test-user@gmail.com";
		String mySecret = "testit!";
		Form form = new Form();
		form.add("description", "teste desc");
		form.add("name", "Stack");
		form.add("owner", "http://127.0.0.1:8888/resources/user/4");
		form.add("isPublic", true);

		URI user = null;
		Collection<BaseEntity> response = webResource.queryParam("summary", "false").get(new GenericType<Collection<BaseEntity>>() {
		});
		if (response.getTotal() > 0) {
			for (Entity c : response.getElements()) {
				if (c.getName().equals("Stack")) {
					user = c.getUri();
				}
			}
		}
		if (user == null) {
			ClientResponse result = webResource.post(ClientResponse.class, form);
			user = result.getLocation();

			Assert.assertEquals(201, result.getStatus());
			client.removeAllFilters();
			client.addFilter(new HTTPBasicAuthFilter(myName, mySecret));
		}

		Stack result = webResource.uri(user).get(Stack.class);
		Assert.assertEquals("http://127.0.0.1:8888/resources/user/4", result.getOwner().toString());
		Assert.assertEquals("teste desc", result.getDescription());
		Assert.assertEquals(true, result.isPublic());

	}

	@Test
	public void testStackDelete() throws Exception {
		String myName = "Stack";
		Stack user = webResource.path("byName").queryParam("id", myName).accept(MediaType.APPLICATION_JSON_TYPE).get(Stack.class);
		System.out.println(user.getUri());
		ClientResponse response = webResource.uri(user.getUri()).delete(ClientResponse.class);
		Assert.assertEquals(204, response.getStatus());
	}

	public static class StackResourceTestWrapper extends StackResource {
		public void addSecurityContext(User user) {
			final User u;
			if (user == null) {
				try {
					User temp = com.googlecode.objectify.ObjectifyService.ofy().load().type(User.class).id(UserResource.Root.getId()).safeGet();
				} catch (com.googlecode.objectify.NotFoundException e) {
					User temp = UserResource.Root;
					URI initial = temp.getUri();
					temp.setId(null);
					Key<User> key = com.googlecode.objectify.ObjectifyService.ofy().save().entity(temp).now();
					temp = com.googlecode.objectify.ObjectifyService.ofy().load().type(User.class).id(UserResource.Root.getId()).get();
					UserResource.Root.setId(temp.getId());
					UserResource.Root.setUri(temp.getUri());
					System.out.println("============================>addSecurity notfoundexception initial=" + initial.toString() + " final " + temp.toString());
				}
				u = UserResource.Root;
				System.out.println("============================>Root is " + u.getUri());

			} else {
				u = user;
			}
			SecurityContext context = new SecurityContext() {

				@Override
				public String getAuthenticationScheme() {
					return "Basic";
				}

				@Override
				public Principal getUserPrincipal() {
					return u;
				}

				@Override
				public boolean isSecure() {
					return true;
				}

				@Override
				public boolean isUserInRole(String arg0) {
					return true;
				}
			};

			super.securityContext = context;
		}
	}
}