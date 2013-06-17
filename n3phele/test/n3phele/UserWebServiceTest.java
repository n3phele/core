package n3phele;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import n3phele.service.model.core.BaseEntity;
import n3phele.service.model.core.Collection;
import n3phele.service.model.core.Entity;
import n3phele.service.model.core.User;
import n3phele.service.rest.impl.UserResource;

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

public class UserWebServiceTest  {
	public UserWebServiceTest() throws Exception {

	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {

		client = Client.create();
		client.addFilter(new HTTPBasicAuthFilter("test-user@gmail.com", "testit!"));
		webResource = client.resource(UriBuilder.fromUri("http://127.0.0.1:8888/resources").path(UserResource.class).build());
	}

	private Client client;
	private WebResource webResource;

	@Test
	public void addTestUserTest() {
    	
    	String myName = "test-user@gmail.com";
    	String firstName = "Test";
    	String lastName = "User";
    	String mySecret = "testit!";
    	Form form = new Form();
		form.add("email", myName);
		form.add("firstName", firstName);
		form.add("lastName", lastName);
		form.add("secret", mySecret);
       	
       	
       	URI user = null;

    	
    	if(user == null) {
	    	client.removeAllFilters();
	    	client.addFilter(new HTTPBasicAuthFilter("signup", "newuser"));
    		ClientResponse result = webResource.post(ClientResponse.class, form);
	    	user = result.getLocation();
	 
	
	    	Assert.assertEquals(201, result.getStatus()); ;
	    	client.removeAllFilters();
	    	client.addFilter(new HTTPBasicAuthFilter(myName, mySecret));
    	}
    	
    	n3phele.service.model.core.User result = webResource.uri(user).get(n3phele.service.model.core.User.class);
    	Assert.assertEquals(myName, result.getName());
    	Assert.assertEquals(firstName, result.getFirstName());
    	Assert.assertEquals(lastName, result.getLastName());

	}
	
	@Test
	public void listUserTest() {
    	
    	String myName = "test-user@gmail.com";
    	String firstName = "Test";
    	String lastName = "User";
    	String mySecret = "testit!";
    	Form form = new Form();
		form.add("email", myName);
		form.add("firstName", firstName);
		form.add("lastName", lastName);
		form.add("secret", mySecret);
       	
       	
       	URI user = null;
    	Collection<BaseEntity> response = webResource.queryParam("summary", "false").get(new GenericType<Collection<BaseEntity>>() {});
    	if(response.getTotal() > 0) {
	    	for(Entity c : response.getElements()) {
	    		if(c.getName().equals(myName)) {
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
    	
    	n3phele.service.model.core.User result = webResource.uri(user).get(n3phele.service.model.core.User.class);
    	Assert.assertEquals(myName, result.getName());
    	Assert.assertEquals(firstName, result.getFirstName());
    	Assert.assertEquals(lastName, result.getLastName());

	}
	
 //@Test
  public void testAccountDelete() throws Exception {
	 String myName = "test-user@gmail.com";
	User user = webResource.path("byName").queryParam("id",myName).accept(MediaType.APPLICATION_JSON_TYPE).get(User.class);
  	ClientResponse response = webResource.uri(user.getUri()).delete(ClientResponse.class);

  	Assert.assertEquals(204, response.getStatus()); 
  	 
  }

}