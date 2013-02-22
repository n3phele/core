package n3phele;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import n3phele.service.core.Resource;
import n3phele.service.rest.impl.AccountResource;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class CloudWebServiceTest  {
	public CloudWebServiceTest() throws Exception {
		
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
		client = Client.create();
		client.addFilter(new HTTPBasicAuthFilter("testUser", "testSecret"));
		baseURI = URI.create(Resource.get("baseURI", "http://localhost:8888/resources"));
		webResource = client.resource(UriBuilder.fromUri(baseURI).path(AccountResource.class).build());
	}
	
	private Client client;
	private URI baseURI;
	private WebResource webResource;

	@Test
	public void testListCommands() {


		
		 webResource.path("task").get(Integer.class);
		
		//assertEquals("Hello World", responseMsg);

	}
   
}