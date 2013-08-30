package n3phele.process;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import n3phele.process.CloudProcessTest.CloudResourceTestWrapper;
import n3phele.service.actions.CountDownAction;
import n3phele.service.core.NotFoundException;
import n3phele.service.dao.ProcessCounterManager;
import n3phele.service.model.Account;
import n3phele.service.model.Action;
import n3phele.service.model.ActionState;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.ProcessCounter;
import n3phele.service.model.Variable;
import n3phele.service.model.core.User;
import n3phele.service.rest.impl.AccountResource;
import n3phele.service.rest.impl.ActionResource;
import n3phele.service.rest.impl.CloudProcessResource;
import n3phele.service.rest.impl.UserResource;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

public class CloudProcessCountTest {

	private final LocalServiceTestHelper helper =   new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig()
			.setApplyAllHighRepJobPolicy(),
			new LocalTaskQueueTestConfig()
			.setDisableAutoTaskExecution(false)             
			.setCallbackClass(LocalTaskQueueTestConfig.DeferredTaskCallback.class)) ;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
		helper.setUp();
	}

	@After     
	public void tearDown() {         
		helper.tearDown();     
	}
	
	@Test
	public void givenCounterIsEmptyWhenExecIsSuccessfulExecutedWithNoParentThenCounterShouldBeOne() throws Exception{
		
		User root = UserResource.Root;
				
		Account acc1 = new Account("HP1", null, null, "cloud1", null, root.getUri(), false);
		AccountResource.dao.add(acc1);
		
		ProcessCounter counter = new ProcessCounter(acc1.getUri().toString());
		counter.setOwner(root.getUri());
		ProcessCounterManager counterManager = new ProcessCounterManager();
		counterManager.add(counter);
		
		CloudResourceTestWrapper cpr = new CloudResourceTestWrapper(); 
		cpr.addSecurityContext(root);	
		
		List<Variable> context = new ArrayList<Variable>();
		Variable var = new Variable("account",acc1.getUri());
		context.add(var);		
		
		Response response = cpr.exec("StackService", "StackServiceTest", "", null, context);
		assertEquals(201,response.getStatus());
		assertEquals(1, counterManager.load(counter.getId(), root).getCount());
	}
	
	@Test
	public void givenCounterIsEmptyWhenExecIsSuccessfulExecutedWithParentThenCounterShouldBeOne() throws Exception{
		
		User root = UserResource.Root;
				
		Account acc1 = new Account("HP1", null, null, "cloud1", null, root.getUri(), false);
		AccountResource.dao.add(acc1);
		
		ProcessCounter counter = new ProcessCounter(acc1.getUri().toString());
		counter.setOwner(root.getUri());
		ProcessCounterManager counterManager = new ProcessCounterManager();
		counterManager.add(counter);
		
		CloudResourceTestWrapper cpr = new CloudResourceTestWrapper(); 
		cpr.addSecurityContext(root);	
		
		List<Variable> context = new ArrayList<Variable>();
		Variable var = new Variable("account",acc1.getUri());
		context.add(var);		
		
		CloudProcess parent = createAValidCloudProcess(root);	
		
		Response response = cpr.exec("StackService", "StackServiceTest", "", parent.getUri().toString(), context);
		assertEquals(201,response.getStatus());
		assertEquals(1, counterManager.load(counter.getId(), root).getCount());
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
	
	@Test
	public void givenNoProcessCounterExistForTheUserWhenExecIsSuccessfulExecutedWithNoParentThenCounterShouldBeCreated() throws Exception{
		
		User root = UserResource.Root;
				
		Account acc1 = new Account("HP1", null, null, "cloud1", null, root.getUri(), false);
		AccountResource.dao.add(acc1);
		
		ProcessCounterManager counterManager = new ProcessCounterManager();
		
		CloudResourceTestWrapper cpr = new CloudResourceTestWrapper();
		cpr.addSecurityContext(root);
		
		List<Variable> context = new ArrayList<Variable>();
		Variable var = new Variable("account",acc1.getUri());
		context.add(var);
		
		Response response = cpr.exec("StackService", "StackServiceTest", "", null, context);
		assertEquals(201,response.getStatus());
		assertNotNull(counterManager.loadByUser(root.getUri()));
	}
	
	@Test
	public void givenNoProcessCounterExistForTheUserWhenExecIsSuccessfulExecutedWithParentThenCounterShouldBeCreated() throws Exception{
		
		User root = UserResource.Root;
				
		Account acc1 = new Account("HP1", null, null, "cloud1", null, root.getUri(), false);
		AccountResource.dao.add(acc1);
		
		ProcessCounterManager counterManager = new ProcessCounterManager();
		
		CloudResourceTestWrapper cpr = new CloudResourceTestWrapper();
		cpr.addSecurityContext(root);
		
		List<Variable> context = new ArrayList<Variable>();
		Variable var = new Variable("account",acc1.getUri());
		context.add(var);
		
		CloudProcess parent = createAValidCloudProcess(root);	
		
		Response response = cpr.exec("StackService", "StackServiceTest", "", parent.getUri().toString(), context);
		assertEquals(201,response.getStatus());
		assertNotNull(counterManager.loadByUser(root.getUri()));
	}
	
	@Test
	public void givenCounterIsEmptyWhenExecFailsBecauseNoAccountWasPassedThenCounterShouldStayZero() throws Exception{
		
		User root = UserResource.Root;
				
		Account acc1 = new Account("HP1", null, null, "cloud1", null, root.getUri(), false);
		AccountResource.dao.add(acc1);
		
		ProcessCounter counter = new ProcessCounter(acc1.getUri().toString());
		counter.setOwner(root.getUri());
		ProcessCounterManager counterManager = new ProcessCounterManager();
		counterManager.add(counter);
		
		CloudResourceTestWrapper cpr = new CloudResourceTestWrapper(); 
		cpr.addSecurityContext(root);	
		
		List<Variable> context = new ArrayList<Variable>();
		
		try
		{
			//Should fail because account is not inside context
			Response response = cpr.exec("StackService", "StackServiceTest", "", null, context);
		}
		catch(NotFoundException e)
		{
			assertEquals(0, counterManager.load(counter.getId(), root).getCount());
		}
	}

}
