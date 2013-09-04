package n3phele.service.rest.impl;


import java.awt.List;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import n3phele.process.CloudProcessTest.CloudResourceTestWrapper;
import n3phele.service.core.NotFoundException;
import n3phele.service.model.Cloud;
import n3phele.service.model.CloudCollection;
import n3phele.service.model.core.Credential;
import n3phele.service.model.core.ParameterType;
import n3phele.service.model.core.TypedParameter;
import n3phele.service.model.core.User;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.googlecode.objectify.Key;

@PrepareForTest({ CloudResourceTestWrapper.class })  
public class CloudResourceTest {
	
	private final LocalServiceTestHelper helper =   new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig()
				.setApplyAllHighRepJobPolicy(),
			new LocalTaskQueueTestConfig()
								.setDisableAutoTaskExecution(false)             
								.setCallbackClass(LocalTaskQueueTestConfig.DeferredTaskCallback.class)) ;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		helper.setUp();
	}
	
	 @After     
	 public void tearDown() {         helper.tearDown();     } 
	 
	 @Test
	 public void createOneCloudAndListTest() throws Exception{
		 
		 CloudResourceTestWrapper cr = PowerMockito.mock(CloudResourceTestWrapper.class);
		
		 PowerMockito.doCallRealMethod().when(cr).addSecurityContext(null);
		 
		 PowerMockito.doCallRealMethod().when(cr).list(false);
		 PowerMockito.doCallRealMethod().when(cr).list(true);
		 
		 cr.addSecurityContext(null);
		 
		 Cloud cloud = PowerMockito.mock(Cloud.class);
		 
		 PowerMockito.doCallRealMethod().when(cr).create("testCloud", "cloud for test", new URI("http://www.test.com/cloud"), new URI("http://www.test.com/resources"), "testUser", "testPassword", true, "costDriverName");
		
		 PowerMockito.doNothing().when(cr).fetchParameters(cloud);
		 
		 Response response =  cr.create("testCloud", "cloud for test", new URI("http://www.test.com/cloud"), new URI("http://www.test.com/resources"), "testUser", "testPassword", true, "costDriverName");
		 
		 Assert.assertEquals(201, response.getStatus());  
		 
		 CloudCollection list = cr.list(false);
		 
		 Assert.assertEquals(1, list.getTotal());  
		 
		 list = cr.list(true);
		 
		 Assert.assertEquals(1, list.getTotal());  
	 }
	
	 @Test (expected = NotFoundException.class )
	 public void getAndDeleteCloudTest() throws URISyntaxException{
		 
		 CloudResourceTestWrapper cr = new CloudResourceTestWrapper();
				 
		 cr.addSecurityContext(null);
		 
		 Cloud cloud = new Cloud("testCloud", "cloud for test", new URI("http://www.test.com/cloud"), new URI("http://www.test.com/resources"), new Credential("testUser",  "testPassword").encrypt(), 
					UserResource.toUser(cr.securityContext).getUri(), true, "costDriverName");
		 cloud.setId((long)2);
		
		 CloudResource.dao.add(cloud);
		 
		 String name = cloud.getName();
		 
		 URI uri = cloud.getUri();
		
		 Cloud result = cr.get((long)2);
		 
		 Assert.assertEquals(cloud, result);
		 
		 result = cr.get(name);
		 
		 Assert.assertEquals(cloud, result);
		 
		 result = CloudResource.dao.load(uri);
		 
		 Assert.assertEquals(cloud, result);
		 
		 result = CloudResource.dao.load(uri,UserResource.Root);
		 
		 Assert.assertEquals(cloud, result);
		 
		 cr.delete((long)2);
		 
		 result = cr.get((long)2);
	 }
	 
	 @Test
	 public void setInputParametersTest() throws URISyntaxException{
		 
		 CloudResourceTestWrapper cr = new CloudResourceTestWrapper();
		 
		 cr.addSecurityContext(null);
		 
		 Cloud cloud = new Cloud("testCloud", "cloud for test", new URI("http://www.test.com/cloud"), new URI("http://www.test.com/resources"), new Credential("testUser",  "testPassword").encrypt(), 
					UserResource.toUser(cr.securityContext).getUri(), true, "costDriverName");
		 cloud.setId((long)2);
		 
		 ArrayList<TypedParameter> param = new ArrayList<TypedParameter>();
		 param.add(new TypedParameter("security_groups", "Name of the security group which controls the open TCP/IP ports for the VM.", ParameterType.String, "", "defaultValue"));
		 
		 cloud.setInputParameters(param);
		
		 CloudResource.dao.add(cloud);
		 
		 Response response = cr.setInputParameter((long)2, "imageRef", "Unique ID of a machine image, returned by a call to RegisterImage", ParameterType.String, "", "75845");
		 
		 Assert.assertEquals(201, response.getStatus());  
		 
		 response = cr.setInputParameter((long)2, "security_groups", "Name of the security group which controls the open TCP/IP ports for the VM.", ParameterType.String, "", "n3phele-default");
		 
		 Assert.assertEquals(200, response.getStatus());  
	 }
	 
	 @Test
	 public void setCostMapTest() throws URISyntaxException{
		 CloudResourceTestWrapper cr = new CloudResourceTestWrapper();
		 
		 cr.addSecurityContext(null);
		 
		 Cloud cloud = new Cloud("testCloud", "cloud for test", new URI("http://www.test.com/cloud"), new URI("http://www.test.com/resources"), new Credential("testUser",  "testPassword").encrypt(), 
					UserResource.toUser(cr.securityContext).getUri(), true, "costDriverName");
		 cloud.setId((long)2);
		 cloud.setCostMap(new HashMap<String,Double>());
		 CloudResource.dao.add(cloud);
		 
		 Response response = cr.setCostMap((long)2, "100", "0.035");
		 
		 Assert.assertEquals(200, response.getStatus());  
	 }
	 
	 public static class CloudResourceTestWrapper extends CloudResource {
		 
		 		 
			public void addSecurityContext(User user) {
				final User u;
				if(user == null) {
					try {
						User temp = com.googlecode.objectify.ObjectifyService.ofy().load().type(User.class).id(UserResource.Root.getId()).safeGet();
					} catch (com.googlecode.objectify.NotFoundException e) {
						User temp = UserResource.Root;
						URI initial = temp.getUri();
						temp.setId(null);
						Key<User>key =  com.googlecode.objectify.ObjectifyService.ofy().save().entity(temp).now();
						temp = com.googlecode.objectify.ObjectifyService.ofy().load().type(User.class).id(UserResource.Root.getId()).get();
						UserResource.Root.setId(temp.getId());
						UserResource.Root.setUri(temp.getUri());
						System.out.println("============================>addSecurity notfoundexception initial="+initial.toString()+" final "+temp.toString());
					}
					u = UserResource.Root;
					System.out.println("============================>Root is "+u.getUri());

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
					}};
					
					super.securityContext = context;
			}
		}

}
