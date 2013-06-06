package n3phele;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;


import n3phele.service.model.Relationship;
import n3phele.service.model.Service;
import n3phele.service.model.Stack;
import n3phele.service.model.core.BaseEntity;
import n3phele.service.model.core.Collection;
import n3phele.service.model.core.Entity;
import n3phele.service.model.core.User;
import n3phele.service.rest.impl.RelationshipResource;
import n3phele.service.rest.impl.ServiceResource;
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

public class RelationshipResourceWebServiceTest  {
	public RelationshipResourceWebServiceTest() throws Exception {

	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {

		client = Client.create();
		client.addFilter(new HTTPBasicAuthFilter("test-user@gmail.com", "testit!"));
		webResource = client.resource(UriBuilder.fromUri("http://127.0.0.1:8888/resources").path(RelationshipResource.class).build());
	}

	private Client client;
	private WebResource webResource;
	private URI testUser;

	@Test
	public void addRelationshipTest() throws URISyntaxException {
		Form form = new Form();
		form.add("uriStackMaster","oneUriOfAStack");
		form.add("uriStackSubordinate","AnotherUriOfAStack");
		form.add("type", "DataBase");
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
    	Relationship result = webResource.uri(user).get(Relationship.class);
    	System.out.println(webResource.uri(user).get(Relationship.class).getUri().toString());
    	Assert.assertEquals("oneUriOfAStack", result.getUriStackMaster());
    	Assert.assertEquals("AnotherUriOfAStack", result.getUriStackSubordinate());
    	Assert.assertEquals("DataBase", result.getType());
    	Assert.assertEquals("service", result.getName());
    	Assert.assertEquals("http://127.0.0.1:8888/resources/user/4", result.getOwner().toString());
    	Assert.assertEquals("teste desc", result.getDescription());
    	Assert.assertEquals(true, result.isPublic());
    }
	@Test
	public void updateRelationshipTest() throws URISyntaxException {
		Form form = new Form();
		form.add("uriStackMaster","oneUriOfAStack");
		form.add("uriStackSubordinate","AnotherUriOfAStack");
		form.add("type", "DataBase");
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
    	Relationship result = webResource.uri(user).get(Relationship.class);
    	Assert.assertEquals("oneUriOfAStack", result.getUriStackMaster());
    	Assert.assertEquals("AnotherUriOfAStack", result.getUriStackSubordinate());
    	Assert.assertEquals("DataBase", result.getType());
    	Assert.assertEquals("service", result.getName());
    	Assert.assertEquals("http://127.0.0.1:8888/resources/user/4", result.getOwner().toString());
    	Assert.assertEquals("teste desc", result.getDescription());
    	Assert.assertEquals(true, result.isPublic());

    	form = new Form();
    	form.add("description", "teste desc2");
    	form.add("name", "service2");
    	URI uri = new URI("http://127.0.0.1:8888/resources/relationship/"+result.getId());
		ClientResponse resultC = webResource.uri(uri).post(ClientResponse.class,form);
		Assert.assertEquals(200, resultC.getStatus());  
    	
    	result = webResource.uri(user).get(Relationship.class);
    	Assert.assertEquals("oneUriOfAStack", result.getUriStackMaster());
    	Assert.assertEquals("AnotherUriOfAStack", result.getUriStackSubordinate());
    	Assert.assertEquals("DataBase", result.getType());
    	Assert.assertEquals("service2", result.getName());
    	Assert.assertEquals("teste desc2", result.getDescription());
    	Assert.assertEquals("http://127.0.0.1:8888/resources/user/4", result.getOwner().toString());
    	Assert.assertEquals(true, result.isPublic());

	}
	@Test
	public void listRelationshipTest() {
    	
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
    	
    	Relationship result = webResource.uri(user).get(Relationship.class);
    	Assert.assertEquals("http://127.0.0.1:8888/resources/user/4", result.getOwner().toString());
    	Assert.assertEquals("teste desc", result.getDescription());
    	Assert.assertEquals(true, result.isPublic());

	}
	
	@Test
	public void testRelationshipDelete() throws Exception {
		String myName = "service";
		Relationship user = webResource.path("byName").queryParam("id",myName).accept(MediaType.APPLICATION_JSON_TYPE).get(Relationship.class);
	  	System.out.println(user.getUri());
		ClientResponse response = webResource.uri(user.getUri()).delete(ClientResponse.class);
	
	  	Assert.assertEquals(204, response.getStatus()); 
	  	 
	 }

}