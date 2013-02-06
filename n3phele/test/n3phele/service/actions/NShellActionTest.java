package n3phele.service.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import n3phele.service.actions.CreateVMAction.CreateVirtualServerResult;
import n3phele.service.core.NotFoundException;
import n3phele.service.lifecycle.ProcessLifecycle;
import n3phele.service.model.Account;
import n3phele.service.model.Action;
import n3phele.service.model.ActionState;
import n3phele.service.model.Cloud;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.CommandDefinition;
import n3phele.service.model.Context;
import n3phele.service.model.Narrative;
import n3phele.service.model.NarrativeLevel;
import n3phele.service.model.core.Credential;
import n3phele.service.model.core.ExecutionFactoryCreateRequest;
import n3phele.service.model.core.NameValue;
import n3phele.service.model.core.User;
import n3phele.service.model.core.VirtualServer;
import n3phele.service.nShell.NParser;
import n3phele.service.nShell.ParseException;
import n3phele.service.rest.impl.AccountResource;
import n3phele.service.rest.impl.ActionResource;
import n3phele.service.rest.impl.CloudProcessResource;
import n3phele.service.rest.impl.CloudResource;
import n3phele.service.rest.impl.NarrativeResource;
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

public class NShellActionTest {
	
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

	

	/** Invokes a simple command containing a log action
	 * @throws InterruptedException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	@Test
	public void simpleLogProcessInvokeTest() throws InterruptedException, ClassNotFoundException, FileNotFoundException, ParseException {
		User root = getRoot();
		ObjectifyService.register(NShellActionTestHarness.class);
		assertNotNull(root);

		
		NParser n = new NParser(new FileInputStream("./test/logTest.n"));
		CommandDefinition cd = n.parse();
		cd.setUri(URI.create("http://n3phele.com/test"));
		Assert.assertEquals("log", cd.getName());
		Assert.assertEquals("produce a log message", cd.getDescription());
		Assert.assertTrue(cd.isPublic());
		Assert.assertTrue(cd.isPreferred());
		Assert.assertEquals("1.0", cd.getVersion());
		Assert.assertEquals(URI.create("http://www.n3phele.com/icons/custom"), cd.getIcon());
		testCommandDefinition = cd;
		
		Context context = new Context();
		
		context.putValue("arg", "http://n3phele.com/test#EC2");
		context.putValue("message", "--warning my first message");
		
		CloudProcess shellProcess = ProcessLifecycle.mgr().createProcess(root, "shell", context, null, null, NShellActionTestHarness.class);
		ProcessLifecycle.mgr().init(shellProcess);
		Thread.sleep(2000);
		CloudProcessResource.dao.clear();
		
		String refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		assertEquals("{}", refresh);
		Narrative log = NarrativeResource.dao.getNarratives(shellProcess.getUri()).toArray(new Narrative[1])[0];
		assertEquals("my first message", log.getText());
		assertEquals(NarrativeLevel.warning, log.getState());
		Thread.sleep(1000);
	}
	
	/** Tests shell expression assignment
	 * @throws InterruptedException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	@Test
	public void expressionTest() throws InterruptedException, ClassNotFoundException, FileNotFoundException, ParseException {
		User root = getRoot();
		ObjectifyService.register(NShellActionTestHarness.class);
		assertNotNull(root);

		
		NParser n = new NParser(new FileInputStream("./test/expressionTest.n"));
		CommandDefinition cd = n.parse();
		cd.setUri(URI.create("http://n3phele.com/test"));
		Assert.assertEquals("expressionTest", cd.getName());
		Assert.assertEquals("produce a log message", cd.getDescription());
		Assert.assertTrue(cd.isPublic());
		Assert.assertTrue(cd.isPreferred());
		Assert.assertEquals("1.0", cd.getVersion());
		Assert.assertEquals(URI.create("http://www.n3phele.com/icons/custom"), cd.getIcon());
		testCommandDefinition = cd;
		
		Context context = new Context();
		
		context.putValue("arg", "http://n3phele.com/test#EC2");
		context.putValue("message", "hello!");
		
		CloudProcess shellProcess = ProcessLifecycle.mgr().createProcess(root, "shell", context, null, null, NShellActionTestHarness.class);
		ProcessLifecycle.mgr().init(shellProcess);
		Thread.sleep(2000);
		CloudProcessResource.dao.clear();
		
		String refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		assertEquals("{}", refresh);
		Narrative log = NarrativeResource.dao.getNarratives(shellProcess.getUri()).toArray(new Narrative[1])[0];
		/*
		 * Test that the assemble honors the whitespace on the log line. Note 2 spaces before true
		 */
		assertEquals("hello! is what I say  true", log.getText());
		assertEquals(NarrativeLevel.info, log.getState());
		Thread.sleep(1000);
	}
	
	
	/** Invokes a simple command containing two log action
	 * @throws InterruptedException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	@Test
	public void doubleLogProcessInvokeTest() throws InterruptedException, ClassNotFoundException, FileNotFoundException, ParseException {
		User root = getRoot();
		ObjectifyService.register(NShellActionTestHarness.class);
		assertNotNull(root);

		
		NParser n = new NParser(new FileInputStream("./test/doubleLogTest.n"));
		CommandDefinition cd = n.parse();
		cd.setUri(URI.create("http://n3phele.com/test"));
		Assert.assertEquals("doubleLog", cd.getName());
		Assert.assertEquals("produce a log message with a suffix", cd.getDescription());
		Assert.assertTrue(cd.isPublic());
		Assert.assertTrue(cd.isPreferred());
		Assert.assertEquals("1.0", cd.getVersion());
		Assert.assertEquals(URI.create("http://www.n3phele.com/icons/custom"), cd.getIcon());
		testCommandDefinition = cd;
		
		Context context = new Context();
		
		context.putValue("arg", "http://n3phele.com/test#EC2");
		context.putValue("message", "--success my 2nd message");
		
		CloudProcess shellProcess = ProcessLifecycle.mgr().createProcess(root, "shell", context, null, null, NShellActionTestHarness.class);
		ProcessLifecycle.mgr().init(shellProcess);
		Thread.sleep(2000);
		CloudProcessResource.dao.clear();
		
		/*
		 * Check the variable $$log got created in the shell parent context and that
		 * it points to the first log action
		 */
		shellProcess = CloudProcessResource.dao.load(shellProcess.getUri());
		NShellAction shell = (NShellAction) ActionResource.dao.load(shellProcess.getAction());
		URI logURI = (URI) shell.getContext().getObjectValue("log") ;
		Action logAction = ActionResource.dao.load(logURI);
		assertEquals("--success my 2nd message", logAction.getContext().getValue("arg"));
		
		
		String refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		assertEquals("{}", refresh);
		Narrative log = NarrativeResource.dao.getNarratives(shellProcess.getUri()).toArray(new Narrative[1])[0];
		assertEquals("my 2nd message", log.getText());
		assertEquals(NarrativeLevel.success, log.getState());
		log = NarrativeResource.dao.getNarratives(shellProcess.getUri()).toArray(new Narrative[2])[1];
		assertEquals("my 2nd message and a suffix", log.getText());
		assertEquals(NarrativeLevel.success, log.getState());
		Thread.sleep(1000);
	}
	
	/** Invokes a simple command that creates a single vm and then explicitly deletes it
	 * @throws InterruptedException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	@Test
	public void oneVMCreateExplicitDeleteTest() throws InterruptedException, ClassNotFoundException, FileNotFoundException, ParseException {
		User root = getRoot();
		ObjectifyService.register(NShellActionTestHarness.class);
		assertNotNull(root);
		 URI cloud = createTestCloud();
		 URI account = createTestAccount(cloud);
		 CreateVMActionWrapper.initalState = "Pending";
		 VMActionWrapper.processState = "Running";
		 CreateVMActionWrapper.clientResponseResult = new CreateVirtualServerTestResult("https://myFactory/VM/1234", 201, "https://myFactory/VM/1234");
		
		NParser n = new NParser(new FileInputStream("./test/vmCreateExplicitDeleteTest.n"));
		CommandDefinition cd = n.parse();
		cd.setUri(URI.create("http://n3phele.com/test"));
		Assert.assertEquals("vmCreateAndDelete", cd.getName());
		Assert.assertEquals("create n vm and then kill them", cd.getDescription());
		Assert.assertTrue(cd.isPublic());
		Assert.assertTrue(cd.isPreferred());
		Assert.assertEquals("1.1", cd.getVersion());
		Assert.assertEquals(URI.create("http://www.n3phele.com/icons/custom"), cd.getIcon());
		testCommandDefinition = cd;
		
		Context context = new Context();
		
		context.putValue("arg", "http://n3phele.com/test#EC2");
		context.putValue("n", 1);
		context.putValue("account", account);
		
		CloudProcess shellProcess = ProcessLifecycleWrapper.mgr().createProcess(root, "shell", context, null, null, NShellActionTestHarness.class);
		ProcessLifecycleWrapper.mgr().init(shellProcess);
		Thread.sleep(2000);
		CloudProcessResource.dao.clear();
		
		/*
		 * Check the variable $$log got created in the shell parent context and that
		 * it points to the first log action
		 */
		shellProcess = CloudProcessResource.dao.load(shellProcess.getUri());
		NShellAction shell = (NShellAction) ActionResource.dao.load(shellProcess.getAction());
		URI vmURI = (URI) shell.getContext().getObjectValue("my_vm") ;
		Action vmAction = ActionResource.dao.load(vmURI);
		Assert.assertEquals("myVM", vmAction.getContext().getValue("name"));
		Assert.assertEquals(null, shell.getContext().getValue("myVM"));
		
		
		String refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		Assert.assertEquals("{}", refresh);
		
		Thread.sleep(1000);
		shellProcess = CloudProcessResource.dao.load(shellProcess.getUri());
		CloudProcess createVMProcess = CloudProcessResource.dao.load(vmAction.getProcess());
		vmAction = ActionResource.dao.load(vmURI);
		URI vm  = vmAction.getContext().getURIValue("cloudVM");
		CloudProcess vmProcess = CloudProcessResource.dao.load(vm);
		
		Assert.assertEquals(ActionState.COMPLETE, shellProcess.getState());
		Assert.assertEquals(ActionState.COMPLETE, createVMProcess.getState());
		Assert.assertEquals(ActionState.CANCELLED, vmProcess.getState());
	}
	
	/** Invokes a simple command that creates a two vm and then explicitly deletes them
	 * @throws InterruptedException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	@Test
	public void twoVMCreateExplicitDeleteTest() throws InterruptedException, ClassNotFoundException, FileNotFoundException, ParseException {
		User root = getRoot();
		ObjectifyService.register(NShellActionTestHarness.class);
		assertNotNull(root);
		 URI cloud = createTestCloud();
		 URI account = createTestAccount(cloud);
		 CreateVMActionWrapper.initalState = "Pending";
		 VMActionWrapper.processState = "Running";
		 CreateVMActionWrapper.clientResponseResult = new CreateVirtualServerTestResult("https://myFactory/VM/1234", 201, "https://myFactory/VM/1234", "https://myFactory/VM/4321");
		
		NParser n = new NParser(new FileInputStream("./test/vmCreateExplicitDeleteTest.n"));
		CommandDefinition cd = n.parse();
		cd.setUri(URI.create("http://n3phele.com/test"));
		Assert.assertEquals("vmCreateAndDelete", cd.getName());
		Assert.assertEquals("create n vm and then kill them", cd.getDescription());
		Assert.assertTrue(cd.isPublic());
		Assert.assertTrue(cd.isPreferred());
		Assert.assertEquals("1.1", cd.getVersion());
		Assert.assertEquals(URI.create("http://www.n3phele.com/icons/custom"), cd.getIcon());
		testCommandDefinition = cd;
		
		Context context = new Context();
		
		context.putValue("arg", "http://n3phele.com/test#EC2");
		context.putValue("n", 2);
		context.putValue("account", account);
		
		CloudProcess shellProcess = ProcessLifecycleWrapper.mgr().createProcess(root, "shell", context, null, null, NShellActionTestHarness.class);
		ProcessLifecycleWrapper.mgr().init(shellProcess);
		Thread.sleep(2000);
		CloudProcessResource.dao.clear();
		
		/*
		 * Check the variable $$log got created in the shell parent context and that
		 * it points to the first log action
		 */
		shellProcess = CloudProcessResource.dao.load(shellProcess.getUri());
		NShellAction shell = (NShellAction) ActionResource.dao.load(shellProcess.getAction());
		URI vmURI = (URI) shell.getContext().getObjectValue("my_vm") ;
		Action vmAction = ActionResource.dao.load(vmURI);
		Assert.assertEquals("myVM", vmAction.getContext().getValue("name"));
		Assert.assertEquals(null, shell.getContext().getValue("myVM"));
		
		
		String refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		Assert.assertEquals("{}", refresh);
		
		Thread.sleep(1000);
		shellProcess = CloudProcessResource.dao.load(shellProcess.getUri());
		CloudProcess createVMProcess = CloudProcessResource.dao.load(vmAction.getProcess());
		vmAction = ActionResource.dao.load(vmURI);
		List<String> vm  = vmAction.getContext().getListValue("cloudVM");
		Assert.assertEquals(2, vm.size());
		CloudProcess vmProcess = CloudProcessResource.dao.load(URI.create(vm.get(0)));
		CloudProcess vmProcess2 = CloudProcessResource.dao.load(URI.create(vm.get(1)));
		
		Assert.assertEquals(ActionState.COMPLETE, shellProcess.getState());
		Assert.assertEquals(ActionState.COMPLETE, createVMProcess.getState());
		Assert.assertEquals(ActionState.CANCELLED, vmProcess.getState());
		Assert.assertEquals(ActionState.CANCELLED, vmProcess2.getState());
	}
	
	/** Invokes a simple command that creates a single vm and then explicitly deletes it
	 * @throws InterruptedException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	@Test
	public void oneVMCreateImplicitDeleteTest() throws InterruptedException, ClassNotFoundException, FileNotFoundException, ParseException {
		User root = getRoot();
		ObjectifyService.register(NShellActionTestHarness.class);
		assertNotNull(root);
		 URI cloud = createTestCloud();
		 URI account = createTestAccount(cloud);
		 CreateVMActionWrapper.initalState = "Pending";
		 VMActionWrapper.processState = "Running";
		 CreateVMActionWrapper.clientResponseResult = new CreateVirtualServerTestResult("https://myFactory/VM/1234", 201, "https://myFactory/VM/1234");
		
		NParser n = new NParser(new FileInputStream("./test/vmCreateImplicitDeleteTest.n"));
		CommandDefinition cd = n.parse();
		cd.setUri(URI.create("http://n3phele.com/test"));
		Assert.assertEquals("vmCreateAndImplicitDelete", cd.getName());
		Assert.assertEquals("create n vm and then have them terminate as part of process clean up", cd.getDescription());
		Assert.assertTrue(cd.isPublic());
		Assert.assertTrue(cd.isPreferred());
		Assert.assertEquals("1.1", cd.getVersion());
		Assert.assertEquals(URI.create("http://www.n3phele.com/icons/custom"), cd.getIcon());
		testCommandDefinition = cd;
		
		Context context = new Context();
		
		context.putValue("arg", "http://n3phele.com/test#EC2");
		context.putValue("n", 1);
		context.putValue("account", account);
		
		CloudProcess shellProcess = ProcessLifecycleWrapper.mgr().createProcess(root, "shell", context, null, null, NShellActionTestHarness.class);
		ProcessLifecycleWrapper.mgr().init(shellProcess);
		Thread.sleep(2000);
		CloudProcessResource.dao.clear();
		
		/*
		 * Check the variable $$log got created in the shell parent context and that
		 * it points to the first log action
		 */
		shellProcess = CloudProcessResource.dao.load(shellProcess.getUri());
		NShellAction shell = (NShellAction) ActionResource.dao.load(shellProcess.getAction());
		URI vmURI = (URI) shell.getContext().getObjectValue("my_vm") ;
		Action vmAction = ActionResource.dao.load(vmURI);
		Assert.assertEquals("myVM", vmAction.getContext().getValue("name"));
		Assert.assertEquals(null, shell.getContext().getValue("myVM"));
		
		CloudProcessResource.dao.clear();
		String refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		Assert.assertEquals("{}", refresh);
		
		shellProcess = CloudProcessResource.dao.load(shellProcess.getUri());
		CloudProcess createVMProcess = CloudProcessResource.dao.load(vmAction.getProcess());
		vmAction = ActionResource.dao.load(vmURI);
		URI vm  = vmAction.getContext().getURIValue("cloudVM");
		CloudProcess vmProcess = CloudProcessResource.dao.load(vm);
		
		Assert.assertEquals(ActionState.COMPLETE, shellProcess.getState());
		Assert.assertEquals(ActionState.COMPLETE, createVMProcess.getState());
		Assert.assertEquals(ActionState.CANCELLED, vmProcess.getState());
	}
	
	/** Invokes a simple command that creates a two vm and then explicitly deletes them
	 * @throws InterruptedException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	@Test
	public void twoVMCreateImplicitDeleteTest() throws InterruptedException, ClassNotFoundException, FileNotFoundException, ParseException {
		User root = getRoot();
		ObjectifyService.register(NShellActionTestHarness.class);
		assertNotNull(root);
		 URI cloud = createTestCloud();
		 URI account = createTestAccount(cloud);
		 CreateVMActionWrapper.initalState = "Pending";
		 VMActionWrapper.processState = "Running";
		 CreateVMActionWrapper.clientResponseResult = new CreateVirtualServerTestResult("https://myFactory/VM/1234", 201, "https://myFactory/VM/1234", "https://myFactory/VM/4321");
		
		NParser n = new NParser(new FileInputStream("./test/vmCreateImplicitDeleteTest.n"));
		CommandDefinition cd = n.parse();
		cd.setUri(URI.create("http://n3phele.com/test"));
		Assert.assertEquals("vmCreateAndImplicitDelete", cd.getName());
		Assert.assertEquals("create n vm and then have them terminate as part of process clean up", cd.getDescription());
		Assert.assertTrue(cd.isPublic());
		Assert.assertTrue(cd.isPreferred());
		Assert.assertEquals("1.1", cd.getVersion());
		Assert.assertEquals(URI.create("http://www.n3phele.com/icons/custom"), cd.getIcon());
		testCommandDefinition = cd;
		
		Context context = new Context();
		
		context.putValue("arg", "http://n3phele.com/test#EC2");
		context.putValue("n", 2);
		context.putValue("account", account);
		
		CloudProcess shellProcess = ProcessLifecycleWrapper.mgr().createProcess(root, "shell", context, null, null, NShellActionTestHarness.class);
		ProcessLifecycleWrapper.mgr().init(shellProcess);
		Thread.sleep(2000);
		CloudProcessResource.dao.clear();
		
		/*
		 * Check the variable $$log got created in the shell parent context and that
		 * it points to the first log action
		 */
		shellProcess = CloudProcessResource.dao.load(shellProcess.getUri());
		NShellAction shell = (NShellAction) ActionResource.dao.load(shellProcess.getAction());
		URI vmURI = (URI) shell.getContext().getObjectValue("my_vm") ;
		Action vmAction = ActionResource.dao.load(vmURI);
		Assert.assertEquals("myVM", vmAction.getContext().getValue("name"));
		Assert.assertEquals(null, shell.getContext().getValue("myVM"));
		
		
		String refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		Assert.assertEquals("{}", refresh);
		
		Thread.sleep(1000);
		shellProcess = CloudProcessResource.dao.load(shellProcess.getUri());
		CloudProcess createVMProcess = CloudProcessResource.dao.load(vmAction.getProcess());
		vmAction = ActionResource.dao.load(vmURI);
		List<String> vm  = vmAction.getContext().getListValue("cloudVM");
		Assert.assertEquals(2, vm.size());
		CloudProcess vmProcess = CloudProcessResource.dao.load(URI.create(vm.get(0)));
		CloudProcess vmProcess2 = CloudProcessResource.dao.load(URI.create(vm.get(1)));
		
		Assert.assertEquals(ActionState.COMPLETE, shellProcess.getState());
		Assert.assertEquals(ActionState.COMPLETE, createVMProcess.getState());
		Assert.assertEquals(ActionState.CANCELLED, vmProcess.getState());
		Assert.assertEquals(ActionState.CANCELLED, vmProcess2.getState());
	}
	

	public static CommandDefinition testCommandDefinition;
	@EntitySubclass
	public static class NShellActionTestHarness extends NShellAction {
		@Override
		protected CommandDefinition loadCommandDefinition(URI uri) throws NotFoundException {
			return testCommandDefinition;
		}
		@Override
		 protected ProcessLifecycle processLifecycle() {
				return ProcessLifecycleWrapper.mgr();
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
		 Account account = new Account("testAccount", "Account for testing", cloud, new Credential("account", "accountSecret").encrypt(), getRoot().getUri(), false);
		 AccountResource.dao.add(account);
		 return account.getUri();
	 }
	 
	 private URI createTestCloud() {
		 Cloud cloud = new Cloud("testCloud", "cloud for testing", URI.create("https://mycloudprovider.com"), URI.create("https://mycloudfactory.com"), new Credential("factory", "factorySecret").encrypt(), getRoot().getUri(), true);
		 CloudResource.dao.add(cloud);
		 return cloud.getUri();
 
	 }
	 
	 @EntitySubclass
	 public static class ServiceActionWrapper extends ServiceAction {
		 protected ProcessLifecycle processLifecycle() {
				return ProcessLifecycleWrapper.mgr();
			}
	 }
	 
	 @EntitySubclass
	 public static class LogActionWrapper extends LogAction {
		 
	 }
	 
	 
	 @EntitySubclass
	 public static class CreateVMActionWrapper extends CreateVMAction {
		 	public static CreateVirtualServerTestResult clientResponseResult = null;
		 	public static Map<URI, VirtualServer> virtualServer = new HashMap<URI, VirtualServer>();
		 	public static String initalState = "Running";
		 	protected int i=0;
			protected VirtualServer fetchVirtualServer(Client client, URI uri) {
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

			String canonicalClassName = "n3phele.service.actions.NShellActionTest$"+className+"ActionWrapper";
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
			name, context, dependency, parent, clazz
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
		 
		 protected int terminate(Client client, String factory, boolean error, boolean debug) {
			 log.info("terminate "+factory+" error="+error+" debug="+debug);
				return 200;
			}
			
			protected int forceAgentRestart(Client client, String agentURI) {
				log.info("forceAgentRestart "+agentURI);
				return 200;
			}
			
			protected VirtualServer getVirtualServer(Client client, String uri) {
				log.info("Get virtualServer "+uri+" "+CreateVMActionWrapper.virtualServer.get(URI.create(uri)));
				VirtualServer result = CreateVMActionWrapper.virtualServer.get(URI.create(uri));
				if(processState != null)
					result.setStatus(processState);
				return result;
			}
	 }
	 
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
	 

}
