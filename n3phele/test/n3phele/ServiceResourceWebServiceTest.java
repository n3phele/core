package n3phele;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;


import n3phele.service.model.Service;
import n3phele.service.model.Stack;
import n3phele.service.model.core.BaseEntity;
import n3phele.service.model.core.Collection;
import n3phele.service.model.core.Entity;
import n3phele.service.model.core.User;
import n3phele.service.rest.impl.ServiceResource;
import n3phele.service.rest.impl.StackResource;
import n3phele.service.rest.impl.UserResource;

//import org.apache.tools.ant.types.spi.Service;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.representation.Form;

public class ServiceResourceWebServiceTest  {
	public ServiceResourceWebServiceTest() throws Exception {

	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {

		client = Client.create();
		client.addFilter(new HTTPBasicAuthFilter("test-user@gmail.com", "testit!"));
		webResource = client.resource(UriBuilder.fromUri("http://127.0.0.1:8888/resources").path(ServiceResource.class).build());
	}

	private Client client;
	private WebResource webResource;
	private URI testUser;

	@Test
	public void addServiceTest() throws URISyntaxException {
		Form form = new Form();
    	form.add("description", "teste desc");
    	form.add("name", "service");
    	form.add("owner", "http://127.0.0.1:8888/resources/user/4");
    	form.add("isPublic", true);
    	URI user = null;
    	if(user == null) {
    		ClientResponse result = webResource.post(ClientResponse.class, form);
	    	user = result.getLocation();
	 
	
	    	Assert.assertEquals(201, result.getStatus()); 
	    	client.removeAllFilters();
			client.addFilter(new HTTPBasicAuthFilter("test-user@gmail.com", "testit!"));
    	}
    	Service result = webResource.uri(user).get(Service.class);
    	System.out.println(webResource.uri(user).get(Service.class).getUri().toString());
    	Assert.assertEquals("service", result.getName());
    	Assert.assertEquals("http://127.0.0.1:8888/resources/user/4", result.getOwner().toString());
    	Assert.assertEquals("teste desc", result.getDescription());
    	Assert.assertEquals(true, result.isPublic());
    }
	@Test
	public void updateServiceTest() throws URISyntaxException {
		Form form = new Form();
    	form.add("description", "teste desc");
    	form.add("name", "service");
    	form.add("owner", "http://127.0.0.1:8888/resources/user/4");
    	form.add("isPublic", true);
    	URI user = null;
    	if(user == null) {
    		ClientResponse result = webResource.post(ClientResponse.class, form);
	    	user = result.getLocation();
	 
	
	    	Assert.assertEquals(201, result.getStatus()); 
	    	client.removeAllFilters();
			client.addFilter(new HTTPBasicAuthFilter("test-user@gmail.com", "testit!"));
    	}
    	Service result = webResource.uri(user).get(Service.class);
    	System.out.println(webResource.uri(user).get(Service.class).getUri().toString());
    	Assert.assertEquals("service", result.getName());
    	Assert.assertEquals("http://127.0.0.1:8888/resources/user/4", result.getOwner().toString());
    	Assert.assertEquals("teste desc", result.getDescription());
    	Assert.assertEquals(true, result.isPublic());

    	form = new Form();
    	form.add("description", "teste desc2");
    	form.add("name", "service2");
    	form.add("owner", "http://127.0.0.1:8888/resources/user/4");
    	form.add("isPublic", true);
    	URI uri = new URI("http://127.0.0.1:8888/resources/service/"+result.getId());
		ClientResponse resultC = webResource.uri(uri).post(ClientResponse.class,form);
		Assert.assertEquals(200, resultC.getStatus());  
    	
    	result = webResource.uri(user).get(Service.class);
    	Assert.assertEquals("service2", result.getName());
    	Assert.assertEquals("teste desc2", result.getDescription());
    	Assert.assertEquals("http://127.0.0.1:8888/resources/user/4", result.getOwner().toString());
    	Assert.assertEquals(true, result.isPublic());

	}
	@Test
	public void addStackTest() throws URISyntaxException {
		Form form = new Form();
    	form.add("description", "teste desc");
    	form.add("name", "service");
    	form.add("owner", "http://127.0.0.1:8888/resources/user/4");
    	form.add("isPublic", true);
    	URI user = null;
    	if(user == null) {
    		ClientResponse result = webResource.post(ClientResponse.class, form);
	    	user = result.getLocation();
	 
	
	    	Assert.assertEquals(201, result.getStatus()); 
	    	client.removeAllFilters();
			client.addFilter(new HTTPBasicAuthFilter("test-user@gmail.com", "testit!"));
    	}
    	Service result = webResource.uri(user).get(Service.class);
    	System.out.println(webResource.uri(user).get(Service.class).getUri().toString());
    	Assert.assertEquals("service", result.getName());
    	Assert.assertEquals("http://127.0.0.1:8888/resources/user/4", result.getOwner().toString());
    	Assert.assertEquals("teste desc", result.getDescription());
    	Assert.assertEquals(true, result.isPublic());
	
		//Stack stack =  new Stack("tested stack","stackForAdd",result.getUri(),true);
		//StackResource.dao.add(stack);
	
		//FORM FOR THE STACK
    	form = new Form();
    	form.add("description", "teste desc");
    	form.add("name", "Stack");
    	form.add("owner", "http://127.0.0.1:8888/resources/user/4");
    	form.add("isPublic", true);
    	URI uriForm = new URI("http://127.0.0.1:8888/resources/stack");
    	ClientResponse resultStack = webResource.uri(uriForm).post(ClientResponse.class,form);
    	Assert.assertEquals(201, resultStack.getStatus());  
    	Stack stackR = webResource.uri(resultStack.getLocation()).get(Stack.class);
    	System.out.println("STACK : " + stackR);
    	
    	URI uri = new URI("http://127.0.0.1:8888/resources/service/"+result.getId()+"/addStack/"+stackR.getId());
		ClientResponse resultC = webResource.uri(uri).post(ClientResponse.class,form);
		Assert.assertEquals(200, resultC.getStatus());  
	
		result = webResource.uri(user).get(Service.class);
		System.out.println("SERVICE: " + result.getStacks());
		Assert.assertEquals("service", result.getName());
		Assert.assertEquals(stackR.getUri().toString(), result.getStacks().get(0).toString());
		
	}
	@Test
	public void listServiceTest() {
    	
    	String myName = "test-user@gmail.com";
    	String mySecret = "testit!";
    	Form form = new Form();
    	form.add("description", "teste desc");
    	form.add("name", "service");
    	form.add("owner", "http://127.0.0.1:8888/resources/user/4");
    	form.add("isPublic", true);
       	
		
       	
       	URI user = null;
    	Collection<BaseEntity> response = webResource.queryParam("summary", "false").get(new GenericType<Collection<BaseEntity>>() {});
    	if(response.getTotal() > 0) {
	    	for(Entity c : response.getElements()) {
	    		if(c.getName().equals("service")) {
	    			user = c.getUri();
	    		}
	    	}
    	}
    	if(user == null) {
    		ClientResponse result = webResource.post(ClientResponse.class, form);
	    	user = result.getLocation();
	 
	
	    	Assert.assertEquals(201, result.getStatus()); 
	    	client.removeAllFilters();
	    	client.addFilter(new HTTPBasicAuthFilter(myName, mySecret));
    	}
    	
    	Service result = webResource.uri(user).get(Service.class);
    	Assert.assertEquals("http://127.0.0.1:8888/resources/user/4", result.getOwner().toString());
    	Assert.assertEquals("teste desc", result.getDescription());
    	Assert.assertEquals(true, result.isPublic());

	}
	
	@Test
	public void testServiceDelete() throws Exception {
		String myName = "service";
		Service user = webResource.path("byName").queryParam("id",myName).accept(MediaType.APPLICATION_JSON_TYPE).get(Service.class);
	  	System.out.println(user.getUri());
		ClientResponse response = webResource.uri(user.getUri()).delete(ClientResponse.class);
	
	  	Assert.assertEquals(204, response.getStatus()); 
	  	 
	 }

}