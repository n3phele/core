package n3phele.process;

import static org.junit.Assert.*;
import static com.googlecode.objectify.ObjectifyService.ofy;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import n3phele.service.actions.CountDownAction;
import n3phele.service.core.NotFoundException;
import n3phele.service.model.Account;
import n3phele.service.model.Action;
import n3phele.service.model.ActionState;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.Variable;
import n3phele.service.model.core.User;
import n3phele.service.rest.impl.AccountResource;
import n3phele.service.rest.impl.ActionResource;
import n3phele.service.rest.impl.CloudProcessResource;
import n3phele.service.rest.impl.UserResource;
import n3phele.service.rest.impl.UserResource.UserManager;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.googlecode.objectify.Key;

public class CloudProcessExecAccountAccessTest {

	static private final LocalServiceTestHelper helper =   new LocalServiceTestHelper(
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
		//FIXME If we don't put this, we are getting weird results for two test methods. The ofy().load.type(User.class).count() returns 0, 
		//but the root will be still on database on load (maybe inside a cache area). We need to delete it from db. Also, after deleting, we need to add it 
		//again, and let it generate a new id for it, because if we use the same id (1), the next user to be inserted was being setting with id 1 too. Maybe
		//It does not know that 1 already exist and repeat it once... we should investigate this problem for a better solution.
		UserResource.dao.delete(UserResource.Root);
		UserResource.Root.setId(null);
		UserResource.Root.setUri(null);
		UserResource.dao.add(UserResource.Root);
	}

	@After     
	public void tearDown() {
		helper.tearDown();     
	} 

	@Test (expected = NotFoundException.class )
	public void noAccountOnContextNoParentTest() throws Exception{
		
		User root = UserResource.Root;
		
		CloudResourceTestWrapper cpr = new CloudResourceTestWrapper(); 
		cpr.addSecurityContext(root);
		
		List<Variable> context = new ArrayList<Variable>();
				
		cpr.exec("Job", "JobTest", "", null, context);
	}
	
	@Test (expected = NotFoundException.class)
	public void noAccountOnContextWithParentTest() throws Exception{

		User root = UserResource.Root;
		
		CloudResourceTestWrapper cpr = new CloudResourceTestWrapper(); 
		cpr.addSecurityContext(root);
		
		List<Variable> context = new ArrayList<Variable>();

		CloudProcess jerry = createAValidCloudProcess(root);		
		
		cpr.exec("Job", "JobTest", "", jerry.getUri().toString(), context);
		
	}
	
	@Test (expected = NotFoundException.class )
	public void emptyAccountOnContextNoParentTest() throws Exception{
		
		User root = UserResource.Root;
				
		CloudResourceTestWrapper cpr = new CloudResourceTestWrapper(); 
		cpr.addSecurityContext(root);
		
		List<Variable> context = new ArrayList<Variable>();
		Variable var = new Variable("account","");
		context.add(var);
		
		cpr.exec("Job", "JobTest", "", null, context);
	}
	
	@Test (expected = NotFoundException.class)
	public void emptyAccountOnContextWithParentTest() throws Exception{
		User root = UserResource.Root;
		
		CloudResourceTestWrapper cpr = new CloudResourceTestWrapper(); 
		cpr.addSecurityContext(root);
		
		List<Variable> context = new ArrayList<Variable>();
		Variable var = new Variable("account","");
		context.add(var);

		CloudProcess jerry = createAValidCloudProcess(root);		
		
		cpr.exec("Job", "JobTest", "", jerry.getUri().toString(), context);
		
	}
	
	@Test (expected = NotFoundException.class )
	public void noAccessToAccountNoParentTest() throws Exception{
		
		User root = UserResource.Root;
		root = UserResource.dao.load(root.getUri());
				
 		User user = new User("user", "firstname", "lastname");
		//user.setId(2l);
		UserResource.dao.add(user);
		
		Account acc1 = new Account("HP1", null, null, "cloud1", null, root.getUri(), false);
		AccountResource.dao.add(acc1);
		
		CloudResourceTestWrapper cpr = new CloudResourceTestWrapper(); 
		cpr.addSecurityContext(user);
		
		List<Variable> context = new ArrayList<Variable>();
		Variable var = new Variable("account",acc1.getUri());
		context.add(var);
		
		cpr.exec("Job", "JobTest", "", null, context);
	}
	
	@Test (expected = NotFoundException.class)
	public void noAccessToAccountWithParentTest() throws Exception{
		User root = UserResource.Root;
		root = UserResource.dao.load(root.getUri());

		User user = new User("user", "firstname", "lastname");
		//user.setId(2l);
		UserResource.dao.add(user);
		
		Account acc1 = new Account("HP1", null, null, "cloud1", null, root.getUri(), false);
		AccountResource.dao.add(acc1);
		
		CloudResourceTestWrapper cpr = new CloudResourceTestWrapper(); 
		cpr.addSecurityContext(user);
		
		List<Variable> context = new ArrayList<Variable>();
		Variable var = new Variable("account",acc1.getUri());
		context.add(var);

		CloudProcess parent = createAValidCloudProcess(user);	
		
		cpr.exec("Job", "JobTest", "", parent.getUri().toString(), context);		
	}
	
	@Test
	public void accessToAccountNoParentTest() throws Exception{
		
		User root = UserResource.Root;
				
		Account acc1 = new Account("HP1", null, null, "cloud1", null, root.getUri(), false);
		
		AccountResource.dao.add(acc1);
		
		CloudResourceTestWrapper cpr = new CloudResourceTestWrapper(); 
		cpr.addSecurityContext(root);		
		
		List<Variable> context = new ArrayList<Variable>();
		Variable var = new Variable("account",acc1.getUri());
		context.add(var);		
		
		Response response = cpr.exec("StackService", "StackServiceTest", "", null, context);
		assertEquals(201,response.getStatus());
	}
	
	@Test
	public void accessToAccountParentTest() throws Exception{
		User root = UserResource.Root;
				
		Account acc1 = new Account("HP1", null, null, "cloud1", null, root.getUri(), false);
		AccountResource.dao.add(acc1);
		
		CloudResourceTestWrapper cpr = new CloudResourceTestWrapper(); 
		cpr.addSecurityContext(root);		
		
		List<Variable> context = new ArrayList<Variable>();
		Variable var = new Variable("account",acc1.getUri());
		context.add(var);			
		
		CloudProcess jerry = createAValidCloudProcess(root);	
		
		Response response = cpr.exec("StackService", "StackServiceTest", "", jerry.getUri().toString(), context);
		assertEquals(201,response.getStatus());		
	}

	protected CloudProcess createAValidCloudProcess(User user)
			throws URISyntaxException {
		Action task = new CountDownAction();
		task.setOwner(user.getUri());
		ActionResource.dao.add(task);
		
		CloudProcess jerry = new CloudProcess(user.getUri(), "jerry", null, true, task, false);
		jerry.setState(ActionState.RUNABLE);
		CloudProcessResource.dao.add(jerry);
		return jerry;
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
