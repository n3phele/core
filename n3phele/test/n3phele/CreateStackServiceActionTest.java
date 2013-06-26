package n3phele;
/**
 * (C) Copyright 2010-2013. Nigel Cook. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * Licensed under the terms described in LICENSE file that accompanied this code, (the "License"); you may not use this file
 * except in compliance with the License. 
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on 
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 *  specific language governing permissions and limitations under the License.
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import n3phele.service.actions.CountDownAction;
import n3phele.service.actions.JobAction;
import n3phele.service.actions.StackServiceAction;
import n3phele.service.lifecycle.ProcessLifecycle;
import n3phele.service.model.Action;
import n3phele.service.model.ActionState;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.Context;
import n3phele.service.model.Variable;
import n3phele.service.model.core.User;
import n3phele.service.rest.impl.ActionResource;
import n3phele.service.rest.impl.CloudProcessResource;
import n3phele.service.rest.impl.UserResource;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.googlecode.objectify.Key;

public class CreateStackServiceActionTest  {
	
	private final LocalServiceTestHelper helper =   new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig()
				.setApplyAllHighRepJobPolicy(),new LocalMemcacheServiceTestConfig()); 
		
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
	public void testExecWithJobAndStackService() throws ClassNotFoundException, URISyntaxException, InterruptedException{
		User root = UserResource.Root;
		assertNotNull(root);
		CloudResourceTestWrapper cpr = new CloudResourceTestWrapper(); cpr.addSecurityContext(null);
		List<Variable> context = new ArrayList<Variable>();
		Response result = cpr.exec("StackService", "testingExec", "", "", context);	
		System.out.println(result.getMetadata());
		URI processId = (URI) result.getMetadata().getFirst("Location");
		CloudProcess p = cpr.dao.load(processId);
		System.out.println(p);
		StackServiceAction  sAction =  (StackServiceAction) ActionResource.dao.load(p.getAction());
		assertEquals("Action has wrong name", sAction.getName(), "testingExec");
		assertEquals("Process has a father",p.getParent(),null);
		result = cpr.exec("Job", "deployTest", "NShell ", p.getUri().toString(), context);
		processId = (URI) result.getMetadata().getFirst("Location");
		CloudProcess pSon = cpr.dao.load(processId);
		assertEquals("Process don't has the right father",pSon.getParent(),p.getUri());
		sAction =  (StackServiceAction) ActionResource.dao.load(p.getAction());
		assertEquals("Stack not added", sAction.getStacks().get(0).getName(), "deployTest" );
		assertEquals("List size different than expected", sAction.getStacks().size(), 1 );
	}
	public static class CloudResourceTestWrapper extends CloudProcessResource {
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