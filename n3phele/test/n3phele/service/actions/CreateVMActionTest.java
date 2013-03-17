package n3phele.service.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import n3phele.service.actions.CreateVMAction.CreateVirtualServerResult;
import n3phele.service.core.NotFoundException;
import n3phele.service.core.Resource;
import n3phele.service.lifecycle.ProcessLifecycle;
import n3phele.service.model.Account;
import n3phele.service.model.Action;
import n3phele.service.model.ActionState;
import n3phele.service.model.Cloud;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.Context;
import n3phele.service.model.SignalKind;
import n3phele.service.model.VariableType;
import n3phele.service.model.core.Credential;
import n3phele.service.model.core.ExecutionFactoryCreateRequest;
import n3phele.service.model.core.NameValue;
import n3phele.service.model.core.User;
import n3phele.service.model.core.VirtualServer;
import n3phele.service.rest.impl.AccountResource;
import n3phele.service.rest.impl.ActionResource;
import n3phele.service.rest.impl.CloudProcessResource;
import n3phele.service.rest.impl.CloudResource;
import n3phele.service.rest.impl.UserResource;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class CreateVMActionTest {
	final private static java.util.logging.Logger log = java.util.logging.Logger.getLogger(CreateVMActionTest.class.getName()); 

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
	 public void oneVmCreation() throws NotFoundException, IllegalArgumentException, ClassNotFoundException, InterruptedException {
		 log.info("test1");
			User root = UserResource.Root;
			assertNotNull(root);
		 URI cloud = createTestCloud();
		 URI account = createTestAccount(cloud);
		 CreateVMActionWrapper.initalState = "Running";
		 VMActionWrapper.processState = "Running";
		 CreateVMActionWrapper.clientResponseResult = new CreateVirtualServerTestResult("https://myFactory/VM/1234", 201, "https://myFactory/VM/1234");
		 Context context = new Context();		 
		 context.putValue("account", account);
		 context.putValue("arg", "CreateVM");
		 CloudProcess service = ProcessLifecycleWrapper.mgr().spawn(getRoot().getUri(), "one", context, null, null, "Service");
		 ProcessLifecycleWrapper.mgr().init(service);
		 Thread.sleep(1000);
		 CloudProcessResource.dao.clear();
		 ServiceAction action = (ServiceAction) ActionResource.dao.load(service.getAction());
		 CloudProcess process = CloudProcessResource.dao.load(action.getChildProcess());
		 
		 Thread.sleep(2000);
		 CloudProcessResource.dao.clear();		
		 String refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
	 
		Thread.sleep(2000);
		CloudProcessResource.dao.clear();		
		refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		assertEquals("{\"RUNABLE_Wait\": 2}", refresh);
		process = CloudProcessResource.dao.load(process.getUri());
		assertEquals(ActionState.COMPLETE, process.getState());
		CreateVMAction cvma = (CreateVMAction) ActionResource.dao.load(process.getAction());

		VMAction vmAction = (VMAction) ActionResource.dao.load(cvma.getContext().getURIValue("cloudVM"));
		CloudProcess childVM = CloudProcessResource.dao.load(vmAction.getProcess());
		
		assertEquals("name defaults to process name", "one.CreateVM", cvma.getContext().getValue("name"));
		assertEquals("default instance count", 1L, cvma.getContext().getObjectValue("n"));
		assertEquals("account", account, cvma.getContext().getObjectValue("account"));
		

		assertEquals(URI.create("http://192.168.1.0:8887/task"), vmAction.getContext().getURIValue("agentURI"));
		assertEquals("test", vmAction.getContext().getValue("agentUser"));
		assertEquals(VariableType.Secret, vmAction.getContext().get("agentUser").getType());
		assertEquals("password", vmAction.getContext().getValue("agentSecret"));
		assertEquals(VariableType.Secret, vmAction.getContext().get("agentSecret").getType());
		assertEquals("factory", vmAction.getContext().getValue("factoryUser"));
		assertEquals(VariableType.Secret, vmAction.getContext().get("factoryUser").getType());
		assertEquals("factorySecret", vmAction.getContext().getValue("factorySecret"));
		assertEquals(VariableType.Secret, vmAction.getContext().get("factorySecret").getType());
		
		assertEquals(false, vmAction.getContext().getBooleanValue("forceAgentRestart"));
		assertEquals(1, vmAction.getContext().getIntegerValue("n"));
		assertEquals(0, vmAction.getContext().getIntegerValue("vmIndex"));
		assertEquals("10.0.0.0", vmAction.getContext().getValue("privateIpAddress"));
		assertEquals("192.168.1.0", vmAction.getContext().getValue("publicIpAddress"));
		assertEquals(URI.create("https://myFactory/VM/1234"), vmAction.getContext().getObjectValue("vmFactory"));
		assertEquals("n3phele-testAccount", vmAction.getContext().getValue("keyName"));
		assertEquals("one.CreateVM", vmAction.getContext().getValue("name"));
	 }
	 
	 @Test
	 public void oneVmCreationNotAZombie() throws NotFoundException, IllegalArgumentException, ClassNotFoundException, InterruptedException {
		 log.info("test2");
			User root = UserResource.Root;
			assertNotNull(root);
		 URI cloud = createTestCloud();
		 URI account = createTestAccount(cloud);
		 CreateVMActionWrapper.initalState = "Pending";
		 VMActionWrapper.processState = "Running";
		 CreateVMActionWrapper.clientResponseResult = new CreateVirtualServerTestResult("https://myFactory/VM/1234", 201, "https://myFactory/VM/1234");
		 Context context = new Context();		 
		 context.putValue("account", account);
		 context.putValue("arg", "CreateVM");
		 CloudProcess service = ProcessLifecycleWrapper.mgr().spawn(getRoot().getUri(), "one", context, null, null, "Service");
		 ProcessLifecycleWrapper.mgr().init(service);
		 Thread.sleep(1000);
		 CloudProcessResource.dao.clear();
		 ServiceAction action = (ServiceAction) ActionResource.dao.load(service.getAction());
		 CloudProcess process = CloudProcessResource.dao.load(action.getChildProcess());
		 
		Thread.sleep(4000);
		CloudProcessResource.dao.clear();		
		String refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		assertEquals("{\"RUNABLE_Wait\": 2}", refresh);
		process = CloudProcessResource.dao.load(process.getUri());
		assertEquals(ActionState.COMPLETE, process.getState());
		CreateVMAction cvma = (CreateVMAction) ActionResource.dao.load(process.getAction());
		
		VMAction vmAction = (VMAction) ActionResource.dao.load(cvma.getContext().getURIValue("cloudVM"));
		CloudProcess childVM = CloudProcessResource.dao.load(vmAction.getProcess());

		assertEquals("createVM context has child vm", vmAction.getUri(), cvma.getContext().getURIValue("cloudVM"));
		assertEquals("name defaults to process name", "one.CreateVM", cvma.getContext().getValue("name"));
		assertEquals("default instance count", 1L, cvma.getContext().getObjectValue("n"));
		assertEquals("account", account, cvma.getContext().getObjectValue("account"));
		

		assertEquals(URI.create("http://192.168.1.0:8887/task"), vmAction.getContext().getURIValue("agentURI"));
		assertEquals("test", vmAction.getContext().getValue("agentUser"));
		assertEquals(VariableType.Secret, vmAction.getContext().get("agentUser").getType());
		assertEquals("password", vmAction.getContext().getValue("agentSecret"));
		assertEquals(VariableType.Secret, vmAction.getContext().get("agentSecret").getType());
		assertEquals("factory", vmAction.getContext().getValue("factoryUser"));
		assertEquals(VariableType.Secret, vmAction.getContext().get("factoryUser").getType());
		assertEquals("factorySecret", vmAction.getContext().getValue("factorySecret"));
		assertEquals(VariableType.Secret, vmAction.getContext().get("factorySecret").getType());
		
		assertEquals(false, vmAction.getContext().containsKey("forceAgentRestart"));
		assertEquals(1, vmAction.getContext().getIntegerValue("n"));
		assertEquals(0, vmAction.getContext().getIntegerValue("vmIndex"));
		assertEquals("10.0.0.0", vmAction.getContext().getValue("privateIpAddress"));
		assertEquals("192.168.1.0", vmAction.getContext().getValue("publicIpAddress"));
		assertEquals(URI.create("https://myFactory/VM/1234"), vmAction.getContext().getObjectValue("vmFactory"));
		assertEquals("n3phele-testAccount", vmAction.getContext().getValue("keyName"));
		assertEquals("one.CreateVM", vmAction.getContext().getValue("name"));
	 }
	 
	 @Test
	 public void twoVmCreation() throws NotFoundException, IllegalArgumentException, ClassNotFoundException, InterruptedException {
		 log.info("test3");
			User root = UserResource.Root;
			assertNotNull(root);
		 URI cloud = createTestCloud();
		 URI account = createTestAccount(cloud);
		 CreateVMActionWrapper.initalState = "Running";
		 VMActionWrapper.processState = "Running";
		 CreateVMActionWrapper.clientResponseResult = new CreateVirtualServerTestResult("https://myFactory/VM/1234", 201, "https://myFactory/VM/1234", "https://myFactory/VM/4321");
		 Context context = new Context();		 
		 context.putValue("account", account);
		 context.putValue("n", 2);
		 context.putValue("arg", "CreateVM");
		 CloudProcess service = ProcessLifecycleWrapper.mgr().spawn(getRoot().getUri(), "two", context, null, null, "Service");
		 ProcessLifecycleWrapper.mgr().init(service);
		 Thread.sleep(1000);
		 CloudProcessResource.dao.clear();
		 ServiceAction action = (ServiceAction) ActionResource.dao.load(service.getAction());
		 CloudProcess process = CloudProcessResource.dao.load(action.getChildProcess());
		 
		Thread.sleep(4000);
		CloudProcessResource.dao.clear();		
		String refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		assertEquals("{\"RUNABLE_Wait\": 3}", refresh);
		process = CloudProcessResource.dao.load(process.getUri());
		
		assertEquals(ActionState.COMPLETE, process.getState());
		CreateVMAction cvma = (CreateVMAction) ActionResource.dao.load(process.getAction());
		@SuppressWarnings("unchecked")
		VMAction vmAction = (VMAction) ActionResource.dao.load(cvma.getContext().getURIList("cloudVM").get(0));
		VMAction vmAction2 = (VMAction) ActionResource.dao.load(cvma.getContext().getURIList("cloudVM").get(1));
		CloudProcess childVM = CloudProcessResource.dao.load(vmAction.getProcess());
		CloudProcess vm2 = CloudProcessResource.dao.load(vmAction2.getProcess());
		
		URI[] siblings = new URI[] { childVM.getAction(), vm2.getAction() };
		
		assertEquals("createVM context has 2 child vm", 2, cvma.getContext().getURIList("cloudVM").size());
		assertEquals("name defaults to process name", "two.CreateVM", cvma.getContext().getValue("name"));
		assertEquals("instance count", 2L, cvma.getContext().getObjectValue("n"));
		assertEquals("account", account, cvma.getContext().getObjectValue("account"));
		
		assertEquals(URI.create("http://192.168.1.0:8887/task"), vmAction.getContext().getURIValue("agentURI"));
		assertEquals("test", vmAction.getContext().getValue("agentUser"));
		assertEquals(VariableType.Secret, vmAction.getContext().get("agentUser").getType());
		assertEquals("password", vmAction.getContext().getValue("agentSecret"));
		assertEquals(VariableType.Secret, vmAction.getContext().get("agentSecret").getType());
		assertEquals("factory", vmAction.getContext().getValue("factoryUser"));
		assertEquals(VariableType.Secret, vmAction.getContext().get("factoryUser").getType());
		assertEquals("factorySecret", vmAction.getContext().getValue("factorySecret"));
		assertEquals(VariableType.Secret, vmAction.getContext().get("factorySecret").getType());
		
		assertEquals(false, vmAction.getContext().getBooleanValue("forceAgentRestart"));
		assertEquals(2, vmAction.getContext().getIntegerValue("n"));
		assertEquals(0, vmAction.getContext().getIntegerValue("vmIndex"));
		assertEquals("10.0.0.0", vmAction.getContext().getValue("privateIpAddress"));
		assertEquals("192.168.1.0", vmAction.getContext().getValue("publicIpAddress"));
		assertEquals(URI.create("https://myFactory/VM/1234"), vmAction.getContext().getObjectValue("vmFactory"));
		assertEquals("n3phele-testAccount", vmAction.getContext().getValue("keyName"));
		assertEquals("two.CreateVM_0", vmAction.getContext().getValue("name"));
		assertEquals(Arrays.asList(siblings), vmAction.getContext().getObjectValue("cloudVM"));
		

		vmAction = (VMAction) ActionResource.dao.load(vm2.getAction());
		assertEquals(URI.create("http://192.168.1.1:8887/task"), vmAction.getContext().getURIValue("agentURI"));
		assertEquals("test", vmAction.getContext().getValue("agentUser"));
		assertEquals(VariableType.Secret, vmAction.getContext().get("agentUser").getType());
		assertEquals("password", vmAction.getContext().getValue("agentSecret"));
		assertEquals(VariableType.Secret, vmAction.getContext().get("agentSecret").getType());
		assertEquals("factory", vmAction.getContext().getValue("factoryUser"));
		assertEquals(VariableType.Secret, vmAction.getContext().get("factoryUser").getType());
		assertEquals("factorySecret", vmAction.getContext().getValue("factorySecret"));
		assertEquals(VariableType.Secret, vmAction.getContext().get("factorySecret").getType());
		
		assertEquals(false, vmAction.getContext().getBooleanValue("forceAgentRestart"));
		assertEquals(2, vmAction.getContext().getIntegerValue("n"));
		assertEquals(1, vmAction.getContext().getIntegerValue("vmIndex"));
		assertEquals("10.0.0.1", vmAction.getContext().getValue("privateIpAddress"));
		assertEquals("192.168.1.1", vmAction.getContext().getValue("publicIpAddress"));
		assertEquals(URI.create("https://myFactory/VM/4321"), vmAction.getContext().getObjectValue("vmFactory"));
		assertEquals("n3phele-testAccount", vmAction.getContext().getValue("keyName"));
		assertEquals("two.CreateVM_1", vmAction.getContext().getValue("name"));
		assertEquals(Arrays.asList(siblings), vmAction.getContext().getObjectValue("cloudVM"));
	 }
	 
	 @Test
	 public void twoVmCreationNotAZombie() throws NotFoundException, IllegalArgumentException, ClassNotFoundException, InterruptedException {
		 log.info("test4");
			User root = UserResource.Root;
			assertNotNull(root);
		 URI cloud = createTestCloud();
		 URI account = createTestAccount(cloud);
		 CreateVMActionWrapper.initalState = "Pending";
		 VMActionWrapper.processState = "Running";
		 CreateVMActionWrapper.clientResponseResult = new CreateVirtualServerTestResult("https://myFactory/VM/1234", 201, "https://myFactory/VM/1234", "https://myFactory/VM/4321");
		 Context context = new Context();		 
		 context.putValue("account", account);
		 context.putValue("n", 2);
		 context.putValue("arg", "CreateVM");
		 CloudProcess service = ProcessLifecycleWrapper.mgr().spawn(getRoot().getUri(), "two", context, null, null, "Service");
		 ProcessLifecycleWrapper.mgr().init(service);
		 Thread.sleep(1000);
		 CloudProcessResource.dao.clear();
		 ServiceAction action = (ServiceAction) ActionResource.dao.load(service.getAction());
		 CloudProcess process = CloudProcessResource.dao.load(action.getChildProcess());
		 
		Thread.sleep(3000);
		CloudProcessResource.dao.clear();		
		String refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		assertEquals("{\"RUNABLE_Wait\": 3}", refresh);
		process = CloudProcessResource.dao.load(process.getUri());
		
		assertEquals(ActionState.COMPLETE, process.getState());
		CreateVMAction cvma = (CreateVMAction) ActionResource.dao.load(process.getAction());
		VMAction vmAction = (VMAction) ActionResource.dao.load(cvma.getContext().getURIList("cloudVM").get(0));
		VMAction vmAction2 = (VMAction) ActionResource.dao.load(cvma.getContext().getURIList("cloudVM").get(1));
		CloudProcess childVM = CloudProcessResource.dao.load(vmAction.getProcess());
		CloudProcess vm2 = CloudProcessResource.dao.load(vmAction2.getProcess());
		
		URI[] siblings = new URI[] { childVM.getAction(), vm2.getAction() };
		
		assertEquals("createVM context has 2 child vm", 2, cvma.getContext().getURIList("cloudVM").size());

		assertEquals("name defaults to process name", "two.CreateVM", cvma.getContext().getValue("name"));
		assertEquals("instance count", 2L, cvma.getContext().getObjectValue("n"));
		assertEquals("account", account, cvma.getContext().getObjectValue("account"));
		
		vmAction = (VMAction) ActionResource.dao.load(childVM.getAction());
		assertEquals(URI.create("http://192.168.1.0:8887/task"), vmAction.getContext().getURIValue("agentURI"));
		assertEquals("test", vmAction.getContext().getValue("agentUser"));
		assertEquals(VariableType.Secret, vmAction.getContext().get("agentUser").getType());
		assertEquals("password", vmAction.getContext().getValue("agentSecret"));
		assertEquals(VariableType.Secret, vmAction.getContext().get("agentSecret").getType());
		assertEquals("factory", vmAction.getContext().getValue("factoryUser"));
		assertEquals(VariableType.Secret, vmAction.getContext().get("factoryUser").getType());
		assertEquals("factorySecret", vmAction.getContext().getValue("factorySecret"));
		assertEquals(VariableType.Secret, vmAction.getContext().get("factorySecret").getType());
		
		assertEquals(false, vmAction.getContext().containsKey("forceAgentRestart"));
		assertEquals(2, vmAction.getContext().getIntegerValue("n"));
		assertEquals(0, vmAction.getContext().getIntegerValue("vmIndex"));
		assertEquals("10.0.0.0", vmAction.getContext().getValue("privateIpAddress"));
		assertEquals("192.168.1.0", vmAction.getContext().getValue("publicIpAddress"));
		assertEquals(URI.create("https://myFactory/VM/1234"), vmAction.getContext().getObjectValue("vmFactory"));
		assertEquals("n3phele-testAccount", vmAction.getContext().getValue("keyName"));
		assertEquals("two.CreateVM_0", vmAction.getContext().getValue("name"));
		assertEquals(Arrays.asList(siblings), vmAction.getContext().getObjectValue("cloudVM"));
		

		vmAction = (VMAction) ActionResource.dao.load(vm2.getAction());
		assertEquals(URI.create("http://192.168.1.1:8887/task"), vmAction.getContext().getURIValue("agentURI"));
		assertEquals("test", vmAction.getContext().getValue("agentUser"));
		assertEquals(VariableType.Secret, vmAction.getContext().get("agentUser").getType());
		assertEquals("password", vmAction.getContext().getValue("agentSecret"));
		assertEquals(VariableType.Secret, vmAction.getContext().get("agentSecret").getType());
		assertEquals("factory", vmAction.getContext().getValue("factoryUser"));
		assertEquals(VariableType.Secret, vmAction.getContext().get("factoryUser").getType());
		assertEquals("factorySecret", vmAction.getContext().getValue("factorySecret"));
		assertEquals(VariableType.Secret, vmAction.getContext().get("factorySecret").getType());
		
		assertEquals(false, vmAction.getContext().containsKey("forceAgentRestart"));
		assertEquals(2, vmAction.getContext().getIntegerValue("n"));
		assertEquals(1, vmAction.getContext().getIntegerValue("vmIndex"));
		assertEquals("10.0.0.1", vmAction.getContext().getValue("privateIpAddress"));
		assertEquals("192.168.1.1", vmAction.getContext().getValue("publicIpAddress"));
		assertEquals(URI.create("https://myFactory/VM/4321"), vmAction.getContext().getObjectValue("vmFactory"));
		assertEquals("n3phele-testAccount", vmAction.getContext().getValue("keyName"));
		assertEquals("two.CreateVM_1", vmAction.getContext().getValue("name"));
		assertEquals(Arrays.asList(siblings), vmAction.getContext().getObjectValue("cloudVM"));
	 }
	 
	 @Test
	 public void oneVmCreationTimeout() throws NotFoundException, IllegalArgumentException, ClassNotFoundException, InterruptedException {
		 log.info("test5");
			User root = UserResource.Root;
			assertNotNull(root);
		 URI cloud = createTestCloud();
		 URI account = createTestAccount(cloud);
		 CreateVMActionWrapper.initalState = "Pending";
		 VMActionWrapper.processState = "Pending";
		 Resource.getResourceMap().put("vmProvisioningTimeoutInSeconds", "1");
		 CreateVMActionWrapper.clientResponseResult = new CreateVirtualServerTestResult("https://myFactory/VM/1234", 201, "https://myFactory/VM/1234");
		 Context context = new Context();		 
		 context.putValue("account", account);
		 context.putValue("arg", "CreateVM");
		 CloudProcess service = ProcessLifecycleWrapper.mgr().spawn(getRoot().getUri(), "one", context, null, null, "Service");
		 ProcessLifecycleWrapper.mgr().init(service);
		 Thread.sleep(1000);
		 CloudProcessResource.dao.clear();
		 ServiceAction action = (ServiceAction) ActionResource.dao.load(service.getAction());
		 CloudProcess process = CloudProcessResource.dao.load(action.getChildProcess());
		 
		Thread.sleep(4000);
		CloudProcessResource.dao.clear();		
		String refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		assertEquals("{\"RUNABLE\": 1, \"RUNABLE_Wait\": 2}", refresh);
		Thread.sleep(2000);
		CloudProcessResource.dao.clear();		
		refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		assertEquals("{\"RUNABLE_Wait\": 1}", refresh);
		process = CloudProcessResource.dao.load(process.getUri());
		assertEquals(ActionState.FAILED, process.getState());
		CloudProcess childVM = CloudProcessResource.dao.getChildren(process.getUri()).toArray(new CloudProcess[1])[0];
		assertEquals(ActionState.FAILED, childVM.getState());
	 }
	 
	 @Test
	 public void twoVmCreationTimeout() throws NotFoundException, IllegalArgumentException, ClassNotFoundException, InterruptedException {
		 log.info("test6");
			User root = UserResource.Root;
			assertNotNull(root);
		 URI cloud = createTestCloud();
		 URI account = createTestAccount(cloud);
		 CreateVMActionWrapper.initalState = "Pending";
		 VMActionWrapper.processState = "Pending";
		 Resource.getResourceMap().put("vmProvisioningTimeoutInSeconds", "3");
		 CreateVMActionWrapper.clientResponseResult = new CreateVirtualServerTestResult("https://myFactory/VM/1234", 201, "https://myFactory/VM/1234","https://myFactory/VM/2222");
		 Context context = new Context();		 
		 context.putValue("account", account);
		 context.putValue("n", 2);
		 context.putValue("arg", "CreateVM");
		 CloudProcess service = ProcessLifecycleWrapper.mgr().spawn(getRoot().getUri(), "one", context, null, null, "Service");
		 ProcessLifecycleWrapper.mgr().init(service);
		 Thread.sleep(1000);
		 CloudProcessResource.dao.clear();
		 ServiceAction action = (ServiceAction) ActionResource.dao.load(service.getAction());
		 CloudProcess process = CloudProcessResource.dao.load(action.getChildProcess());
		 
		CloudProcessResource.dao.clear();
		CloudProcess childVM = CloudProcessResource.dao.getChildren(process.getUri()).toArray(new CloudProcess[1])[0];
		VMActionWrapper.processState = null;
		VMAction childAction = (VMAction) ActionResource.dao.load(childVM.getAction());
		URI childFactoryURI = childAction.getContext().getURIValue("vmFactory");
		CreateVMActionWrapper.virtualServer.get(childFactoryURI).setStatus("Running");
		String refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		assertEquals("{\"RUNABLE\": 2, \"RUNABLE_Wait\": 2}", refresh);
		Thread.sleep(1000);
		CloudProcessResource.dao.clear();
		
		refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		assertEquals("{\"RUNABLE\": 1, \"RUNABLE_Wait\": 3}", refresh);
		
		Thread.sleep(2000);
		CloudProcessResource.dao.clear();
		
		refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		assertEquals("{\"RUNABLE\": 1, \"RUNABLE_Wait\": 3}", refresh);
		
		Thread.sleep(2000);
		CloudProcessResource.dao.clear();
		
		refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		assertEquals("{\"RUNABLE_Wait\": 1}", refresh);
		
		
		
		process = CloudProcessResource.dao.load(process.getUri());
		assertEquals(ActionState.FAILED, process.getState());
		CloudProcess childVM1 = CloudProcessResource.dao.getChildren(process.getUri()).toArray(new CloudProcess[1])[0];
		CloudProcess childVM2 = CloudProcessResource.dao.getChildren(process.getUri()).toArray(new CloudProcess[2])[1];
		assertEquals(ActionState.CANCELLED, childVM1.getState());
		assertEquals(ActionState.FAILED, childVM2.getState());

	 }
	 
	 @Test
	 public void twoVmCreationOneVMDies() throws NotFoundException, IllegalArgumentException, ClassNotFoundException, InterruptedException {
		 log.info("test7");
			User root = UserResource.Root;
			assertNotNull(root);
		 URI cloud = createTestCloud();
		 URI account = createTestAccount(cloud);
		 CreateVMActionWrapper.initalState = "Pending";
		 VMActionWrapper.processState = "Running";
		 CreateVMActionWrapper.clientResponseResult = new CreateVirtualServerTestResult("https://myFactory/VM/1234", 201, "https://myFactory/VM/1234","https://myFactory/VM/2222");
		 Context context = new Context();		 
		 context.putValue("account", account);
		 context.putValue("n", 2);
		 context.putValue("arg", "CreateVM");
		 CloudProcess service = ProcessLifecycleWrapper.mgr().spawn(getRoot().getUri(), "one", context, null, null, "Service");
		 ProcessLifecycleWrapper.mgr().init(service);
		 Thread.sleep(1500);
		 CloudProcessResource.dao.clear();
		 ServiceAction action = (ServiceAction) ActionResource.dao.load(service.getAction());
		 CloudProcess process = CloudProcessResource.dao.load(action.getChildProcess());
		 
		CloudProcessResource.dao.clear();
		String refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		assertEquals("{\"RUNABLE_Wait\": 3}", refresh);
		Thread.sleep(1000);
		CloudProcessResource.dao.clear();
		VMActionWrapper.processState = null;
		CloudProcess createVMProcess = CloudProcessResource.dao.load(action.getChildProcess());
		CreateVMAction createVMAction = (CreateVMAction) ActionResource.dao.load(createVMProcess.getAction());
		
		VMAction vm1 = (VMAction) ActionResource.dao.load(createVMAction.getContext().getURIList("cloudVM").get(0));
		VMAction vm2 = (VMAction) ActionResource.dao.load(createVMAction.getContext().getURIList("cloudVM").get(1));
		CloudProcess vmProcess = CloudProcessResource.dao.load(vm1.getProcess());
		URI childFactoryURI = vm1.getContext().getURIValue("vmFactory");
		CreateVMActionWrapper.virtualServer.get(childFactoryURI).setStatus("Terminated");
		ProcessLifecycle.mgr().signal(vmProcess.getUri(), SignalKind.Event, vmProcess.getUri().toString());
		Thread.sleep(1000);
		CloudProcessResource.dao.clear();
		refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		assertEquals("{\"RUNABLE_Wait\": 2}", refresh);

		CloudProcessResource.dao.clear();
		
		
		
		
		process = CloudProcessResource.dao.load(process.getUri());
		
		assertEquals(ActionState.COMPLETE, process.getState());
		vmProcess = CloudProcessResource.dao.load(vm1.getProcess());
		assertEquals(ActionState.FAILED, vmProcess.getState());
		vmProcess = CloudProcessResource.dao.load(vm2.getProcess());
		assertEquals(ActionState.RUNABLE, vmProcess.getState());
	 }
	 
	 @Test
	 public void twoVmCreationOneVMDisappears() throws NotFoundException, IllegalArgumentException, ClassNotFoundException, InterruptedException {
		 log.info("test8");
			User root = UserResource.Root;
			assertNotNull(root);
		 URI cloud = createTestCloud();
		 URI account = createTestAccount(cloud);
		 CreateVMActionWrapper.initalState = "Pending";
		 VMActionWrapper.processState = "Running";
		 CreateVMActionWrapper.clientResponseResult = new CreateVirtualServerTestResult("https://myFactory/VM/1234", 201, "https://myFactory/VM/1234","https://myFactory/VM/2222");
		 Context context = new Context();		 
		 context.putValue("account", account);
		 context.putValue("n", 2);
		 context.putValue("arg", "CreateVM");
		 CloudProcess service = ProcessLifecycleWrapper.mgr().spawn(getRoot().getUri(), "one", context, null, null, "Service");
		 ProcessLifecycleWrapper.mgr().init(service);
		 Thread.sleep(1000);
		 CloudProcessResource.dao.clear();
		 ServiceAction action = (ServiceAction) ActionResource.dao.load(service.getAction());
		 CloudProcess process = CloudProcessResource.dao.load(action.getChildProcess());
		 
		CloudProcessResource.dao.clear();
		String refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		assertEquals("{\"RUNABLE_Wait\": 3}", refresh);
		Thread.sleep(1000);
		CloudProcessResource.dao.clear();
		VMActionWrapper.processState = null;
		CloudProcess createVMProcess = CloudProcessResource.dao.load(action.getChildProcess());
		CreateVMAction createVMAction = (CreateVMAction) ActionResource.dao.load(createVMProcess.getAction());
		VMAction vm1 = (VMAction) ActionResource.dao.load(createVMAction.getContext().getURIList("cloudVM").get(0));
		VMAction vm2 = (VMAction) ActionResource.dao.load(createVMAction.getContext().getURIList("cloudVM").get(1));
		CloudProcess vmProcess = CloudProcessResource.dao.load(vm1.getProcess());

		URI childFactoryURI = vm1.getContext().getURIValue("vmFactory");
		CreateVMActionWrapper.virtualServer.remove(childFactoryURI);
		ProcessLifecycle.mgr().signal(vmProcess.getUri(), SignalKind.Event, vmProcess.getUri().toString());
		Thread.sleep(1000);
		CloudProcessResource.dao.clear();
		refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		assertEquals("{\"RUNABLE_Wait\": 2}", refresh);

		CloudProcessResource.dao.clear();
		
		
		
		
		process = CloudProcessResource.dao.load(process.getUri());
		
		assertEquals(ActionState.COMPLETE, process.getState());
		vmProcess = CloudProcessResource.dao.load(vm1.getProcess());
		assertEquals(ActionState.FAILED, vmProcess.getState());
		vmProcess = CloudProcessResource.dao.load(vm2.getProcess());
		assertEquals(ActionState.RUNABLE, vmProcess.getState());
	 }
	 
	 
	 /*
	  * Helpers
	  */
	 
	 private static class CreateVirtualServerTestResult extends CreateVirtualServerResult {
			URI location;
			int status;
			URI[] refs;
			public CreateVirtualServerTestResult(String location, int status, String ... refs) {
				this.location = URI.create(location);
				this.status = status;
				this.refs = new URI[refs.length];
				for(int i=0; i < refs.length; i++) {
					this.refs[i] = URI.create(refs[i]);
				}
			}
			
			public URI getLocation() {
				return this.location;
			}
			
			public URI[] getRefs() {
				return this.refs;
			}
			
			public int getStatus() {
				return this.status;
			}
		}
	 
	 @EntitySubclass
	 public static class ServiceActionWrapper extends ServiceAction {
		 protected ProcessLifecycle processLifecycle() {
				return ProcessLifecycleWrapper.mgr();
			}
	 }
	 
	 
	 @EntitySubclass
	 public static class CreateVMActionWrapper extends CreateVMAction {
		 	public static CreateVirtualServerTestResult clientResponseResult = null;
		 	public static Map<URI, VirtualServer> virtualServer = new HashMap<URI, VirtualServer>();
		 	public static String initalState = "Running";
		 	protected int i=0;
			protected VirtualServer fetchVirtualServer(Client client, URI uri) {
				try {
					Thread.sleep((long) (Math.random()*200));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				VirtualServer result = virtualServer.get(uri);
					
				return result;
			}
			
			protected CreateVirtualServerResult createVirtualServers(WebResource resource, ExecutionFactoryCreateRequest createRequest) {
				int i = 0;
				
				for(URI ref : clientResponseResult.getRefs()) {
					ArrayList<NameValue> parameters = new ArrayList<NameValue>();
					parameters.add(new NameValue("publicIpAddress", "192.168.1."+i));
					parameters.add(new NameValue("privateIpAddress", "10.0.0."+i));
					VirtualServer vs = new VirtualServer(createRequest.name, createRequest.description, ref, parameters, 
							createRequest.notification, "instanceid_"+i++, null, null, null, createRequest.owner, createRequest.idempotencyKey );
					vs.setStatus(initalState);
					vs.setOutputParameters(parameters);
					virtualServer.put(ref, vs);
				}
				try {
					Thread.sleep((long) (Math.random()*200));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return clientResponseResult;

			}
			
			protected ProcessLifecycle processLifecycle() {
				return ProcessLifecycleWrapper.mgr();
			}
	 }
	 
	 public static class ProcessLifecycleWrapper extends ProcessLifecycle {
		public ProcessLifecycleWrapper() {}
		
		public CloudProcess spawn(URI owner, String name, n3phele.service.model.Context context, 
			     List<URI> dependency, URI parentURI, String className) throws IllegalArgumentException, NotFoundException, ClassNotFoundException {

			String canonicalClassName = "n3phele.service.actions.CreateVMActionTest$"+className+"ActionWrapper";
			Class<? extends Action> clazz = Class.forName(canonicalClassName).asSubclass(Action.class);;

			ObjectifyService.register(clazz);
			User user;
			CloudProcess parent=null;
			try {
				user = UserResource.dao.load(owner);
			} catch (NotFoundException e) {

				throw e;
			}
			
			try {
				if(parentURI != null)
					parent = CloudProcessResource.dao.load(parentURI);
			} catch (NotFoundException e) {
				throw e;
			}
			
			CloudProcess process = this.createProcess(user, 
			name, context, dependency, parent, false, clazz
			);
			return process;
			}
		
		private final static ProcessLifecycle processLifecycle = new ProcessLifecycleWrapper();
		public static ProcessLifecycle mgr() {
			return processLifecycle;
		}
		
	 }
	 
	 @EntitySubclass
	 public static class VMActionWrapper extends VMAction {
		 public static String processState = "Running";
		 @Override
		 protected int terminate(Client client, String factory, boolean error, boolean debug) {
			 log.info("terminate "+factory+" error="+error+" debug="+debug);
				return 200;
			}
		    @Override
			protected int forceAgentRestart(Client client, String agentURI) {
				log.info("forceAgentRestart "+agentURI);
				return 200;
			}
			@Override
			protected void aliveTest(Client client, String agentURI) {
				log.info("aliveTest "+agentURI);
			}
			@Override
			protected VirtualServer getVirtualServer(Client client, String uri) {
				log.info("Get virtualServer "+uri+" "+CreateVMActionWrapper.virtualServer.get(URI.create(uri)));
				VirtualServer result = CreateVMActionWrapper.virtualServer.get(URI.create(uri));
				if(processState != null)
					result.setStatus(processState);
				return result;
			}
	 }
	 
	 private User getRoot() {
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
			}
			return UserResource.Root;
		}
	 
	 private URI createTestAccount(URI cloud) {
		 Account account = new Account("testAccount", "Account for testing", cloud, "testCloud", new Credential("account", "accountSecret").encrypt(), getRoot().getUri(), false);
		 AccountResource.dao.add(account);
		 return account.getUri();
	 }
	 
	 private URI createTestCloud() {
		 Cloud cloud = new Cloud("testCloud", "cloud for testing", URI.create("https://mycloudprovider.com"), URI.create("https://mycloudfactory.com"), new Credential("factory", "factorySecret").encrypt(), getRoot().getUri(), true);
		 CloudResource.dao.add(cloud);
		 return cloud.getUri();
 
	 }
	 

}
