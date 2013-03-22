package n3phele;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.ws.rs.core.UriBuilder;

import n3phele.service.model.core.Credential;
import n3phele.service.model.core.ExecutionFactoryCreateRequest;
import n3phele.service.model.core.NameValue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class NovaFactoryTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	@Before
	public void setUp() throws Exception {
		client = Client.create();
		
		try
		{
			testResource = new TestResource("n3phele.testCredentials");
		}
		catch(FileNotFoundException e)
		{			
			throw new FileNotFoundException("The necessary file with test credentials was not found. Manually create the file and put real credentials there so integration tests can reach the factory. See tests for necessary variables.");
		}
		
		String serverAddress = testResource.get("novaFactoryURI", "");
		userName = testResource.get("novaFactoryUser", "");
		userPwd = testResource.get("novaFactorySecret", "");
		webResource = client.resource(UriBuilder.fromUri(serverAddress).build());
		
		client.addFilter(new HTTPBasicAuthFilter(userName, userPwd));
		
	}

	@After
	public void tearDown() throws Exception {
	}
	
	private Client client;
	private WebResource webResource;
	private TestResource testResource;
	
	static String userName;
	static String userPwd;
	
	@Test
	public void createVM() throws URISyntaxException{
		
		Credential cred = new Credential();
		cred.setAccount(testResource.get("accessKey", ""));
		cred.setSecret(testResource.get("secret", ""));

		Credential encrypted = Credential.encrypt(cred,"password");
		//Credential test = Credential.decrypt(encrypted, "password");
		
		ExecutionFactoryCreateRequest request = new ExecutionFactoryCreateRequest();
		request.accessKey = encrypted.getAccount();
		request.encryptedSecret = encrypted.getSecret();
		request.location = new URI("https://az-1.region-a.geo-1.ec2-compute.hpcloudsvc.com/services/Cloud");
		request.description = "description";
		request.name = "name"+System.currentTimeMillis();
		request.owner = new URI("http://test/");
		ArrayList<NameValue> parameters = new ArrayList<NameValue>();
		parameters.add(new NameValue("minCount", "1"));
		parameters.add(new NameValue("maxCount", "1"));
		parameters.add(new NameValue("imageId", "75845"));
		parameters.add(new NameValue("instanceType", "100"));
		parameters.add(new NameValue("securityGroup", "default"));
		parameters.add(new NameValue("keyName", "liskey"));
		parameters.add(new NameValue("locationId", "az-1.region-a.geo-1"));
		parameters.add(new NameValue("userData", ""));
		request.parameters = parameters;
		
		ClientResponse result = webResource.post(ClientResponse.class, request);

	}
}
