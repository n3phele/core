package n3phele.workloads;


import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.Calendar;

import javax.ws.rs.core.SecurityContext;

import n3phele.service.actions.CountDownAction;
import n3phele.service.model.Action;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.core.Collection;
import n3phele.service.model.core.User;
import n3phele.service.rest.impl.AccountResource;
import n3phele.service.rest.impl.CloudProcessResource;
import n3phele.service.rest.impl.UserResource;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.googlecode.objectify.Key;

public class CloudProcessWorkloads  {
	
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

	
	/** Creates and runs a simple test process verifying preservation of running task state
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws URISyntaxException 
	 */
	//@Test
//	public void cloudProcessCompletedWorkload() throws InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, URISyntaxException
//	{
//		CloudResourceTestWrapper cpr = new CloudResourceTestWrapper();
//		cpr.addSecurityContext(null);
//		CloudResourceTestWrapper.dao.clear();
//		
//		final int load = 5;
//		
//		Action task = new CountDownAction();
//		task.setUri(new URI("http://www.google.com.br"));
//		
//		// Creating "some" toms
//		for(int i=0; i < load; i++)
//		{
//			CloudProcess nTom   = new CloudProcess(UserResource.Root.getUri(), "tom-" + i, null, true, task);
//			nTom.setCostPerHour((float)1.5);
//			nTom.setComplete(Calendar.getInstance().getTime());
//			CloudResourceTestWrapper.dao.add(nTom);
//		}
//		
//		// Creating "some" spikes
//		for(int i=0; i < load; i++)
//		{
//			CloudProcess nSpike   = new CloudProcess(UserResource.Root.getUri(), "spike-" + i, null, true, task);
//			nSpike.setCostPerHour((float)0.0);
//			nSpike.setComplete(Calendar.getInstance().getTime());
//			CloudResourceTestWrapper.dao.add(nSpike);
//		}
//		
//		Calendar calendar = Calendar.getInstance();
//		calendar.add(Calendar.MONTH, -2);
//		
//		// Creating "some" jerry
//		for(int i=0; i < load; i++)
//		{
//			CloudProcess nJerry   = new CloudProcess(UserResource.Root.getUri(), "jerry-" + i, null, true, task);
//			nJerry.setCostPerHour((float)1.5);
//			nJerry.setComplete(calendar.getTime());
//			CloudResourceTestWrapper.dao.add(nJerry);
//		}
//		
//		long start = System.currentTimeMillis();
//		Collection<CloudProcess> cpList = CloudResourceTestWrapper.dao.getCollection(UserResource.Root.getUri());
//		long time = System.currentTimeMillis() - start;
//		
//		System.out.println("[WORKLOAD] cloudProcessCompletedWorkload time: "+ time);
//		System.out.println("[WORKLOAD] cloudProcessCompletedWorkload items: "+ cpList.getElements().size());
//	}
//	
	 
//	 Ancestors query test
//	 @Test
//		public void cloudProcessChildrenCostsWorkload() throws InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, URISyntaxException
//		{
//			CloudResourceTestWrapper cpr = new CloudResourceTestWrapper();
//			cpr.addSecurityContext(null);
//			CloudResourceTestWrapper.dao.clear();
//			
//			final int load = 10;
//			
//			Action task = new CountDownAction();
//			task.setUri(new URI("http://www.google.com.br"));
//			
//			CloudProcess nTom   = new CloudProcess(UserResource.Root.getUri(), "tom-", null, true, task);
//			nTom.setCostPerHour((float)1.5);
//			nTom.setComplete(Calendar.getInstance().getTime());
//			nTom.setAccount("contaTom");
//			nTom.setUri(new URI("http://www.root.com"));
//			CloudResourceTestWrapper.dao.add(nTom);
//			Long id = nTom.getId();
//			System.out.println("id: " + id);
//				
//			// Creating "some" spikes
//			for(int i=0; i < load; i++)
//			{
//				CloudProcess nSpike   = new CloudProcess(UserResource.Root.getUri(), "spike-" + i, null, true, task);
//				nSpike.setCostPerHour((float)0.0);
//				nSpike.setComplete(Calendar.getInstance().getTime());
//				nSpike.setParent(new URI("http://www.root.com"));
//				nSpike.setRoot(Key.create(CloudProcess.class,id));
//				CloudResourceTestWrapper.dao.add(nSpike);
//				System.out.println(nSpike.getRoot() + "\n");
//			}
//			
//			Calendar calendar = Calendar.getInstance();
//			calendar.add(Calendar.MONTH, -2);
//			
//			// Creating "some" jerry
//			for(int i=0; i < load; i++)
//			{
//				CloudProcess nJerry   = new CloudProcess(UserResource.Root.getUri(), "jerry-" + i, null, true, task);
//				nJerry.setCostPerHour((float)1.5);
//				nJerry.setComplete(calendar.getTime());
//				nJerry.setAccount("conta");
//				nJerry.setParent(new URI("http://www.root.com"));
//				CloudResourceTestWrapper.dao.add(nJerry);
//			}
//			
//			long start = System.currentTimeMillis();
//			System.out.println("Inicio da query:");
//			Collection<CloudProcess> cpList = CloudResourceTestWrapper.dao.getChildrenWithCostsCollection(id);
//
//			long time = System.currentTimeMillis() - start;
//			
//			System.out.println("[WORKLOAD] cloudProcessCompletedWorkload time: "+ time);
//			System.out.println("[WORKLOAD] cloudProcessCompletedWorkload items: "+ cpList.getElements().size());
//		}
	 
	 
	@Test
	public void cloudProcessCompletedWithAccountWorkload() throws InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, URISyntaxException
	{
		CloudResourceTestWrapper cpr = new CloudResourceTestWrapper();
		cpr.addSecurityContext(null);
		CloudResourceTestWrapper.dao.clear();
		
		final int load = 5;
		
		Action task = new CountDownAction();
		task.setUri(new URI("http://www.google.com.br"));
		
		// Creating "some" tom
		// tom is valid
		for(int i=0; i < load; i++)
		{
			CloudProcess nTom   = new CloudProcess(UserResource.Root.getUri(), "tom-" + i, null, true, task);
			nTom.setCostPerHour((float)1.5);
			nTom.setComplete(Calendar.getInstance().getTime());
			nTom.setAccount("conta");
			CloudResourceTestWrapper.dao.add(nTom);
		}
		
		// Creating "some" spikes
		//spike is invalid because it doesn't have cost nor account
		for(int i=0; i < load; i++)
		{
			CloudProcess nSpike   = new CloudProcess(UserResource.Root.getUri(), "spike-" + i, null, true, task);
			nSpike.setCostPerHour((float)0.0);
			nSpike.setComplete(Calendar.getInstance().getTime());
			CloudResourceTestWrapper.dao.add(nSpike);
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -2);
		
		// Creating "some" jerry
		//jerry is invalid because of the date
		for(int i=0; i < load; i++)
		{
			CloudProcess nJerry   = new CloudProcess(UserResource.Root.getUri(), "jerry-" + i, null, true, task);
			nJerry.setCostPerHour((float)1.5);
			nJerry.setComplete(calendar.getTime());
			nJerry.setAccount("conta");
			CloudResourceTestWrapper.dao.add(nJerry);
		}
		
		long start = System.currentTimeMillis();
		System.out.println("Inicio da query:");
		Collection<CloudProcess> cpList = AccountResourceTestWrapper.dao.getCostsOfAccount("conta", 30);

		long time = System.currentTimeMillis() - start;
		
		System.out.println("[WORKLOAD] cloudProcessCompletedWorkload time: "+ time);
		System.out.println("[WORKLOAD] cloudProcessCompletedWorkload items: "+ cpList.getElements().size());
	}

	public static class AccountResourceTestWrapper extends AccountResource{
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
