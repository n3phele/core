package n3phele;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import n3phele.service.model.Cloud;
import n3phele.service.model.core.BaseEntity;
import n3phele.service.model.core.Collection;
import n3phele.service.model.core.Entity;
import n3phele.service.rest.impl.CloudResource;

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

public class CloudWebServiceTest  {
	public CloudWebServiceTest() throws Exception {

	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
		client = Client.create();
		client.addFilter(new HTTPBasicAuthFilter("test-user@gmail.com", "testit!"));
		webResource = client.resource(UriBuilder.fromUri("http://127.0.0.1:8888/resources").path(CloudResource.class).build());
	}

	private Client client;
	private WebResource webResource;

	@Test
	public void addEC2CloudTest() {
		URI cloud = null;
		Collection<BaseEntity> response = webResource.queryParam("summary", "false").get(new GenericType<Collection<BaseEntity>>() {});
		if(response.getTotal() > 0) {
			for(Entity c : response.getElements()) {
				if(c.getName().equals("EC2")) {
					cloud = c.getUri();
				}
			}
		}

		if(cloud == null) {


			String myName = "EC2";
			String description = "Amazon Elastic Compute Service";
			URI location = URI.create("https://ec2.amazonaws.com");
			URI factory =  URI.create("https://ec2factory.appspot.com/resources/virtualServer");
			String factoryId = "fred";
			String mySecret = "3hyebbehg56yeh5";
			Form form = new Form();
			form.add("name", myName);
			form.add("description", description);
			form.add("location", location);
			form.add("factory", factory);
			form.add("factoryId", factoryId);
			form.add("secret", mySecret);
			form.add("isPublic", true);

			ClientResponse result = webResource.post(ClientResponse.class, form);
			cloud = result.getLocation();


			Assert.assertEquals(201, result.getStatus());  
		}
		


	}
	
	 //@Test
	  public void testCloudDelete() throws Exception {

		Cloud cloud = webResource.path("byName").queryParam("id","EC2").accept(MediaType.APPLICATION_JSON_TYPE).get(Cloud.class);
	  	ClientResponse response = webResource.uri(cloud.getUri()).delete(ClientResponse.class);

	  	Assert.assertEquals(204, response.getStatus()); 
	  	 
	  }

}