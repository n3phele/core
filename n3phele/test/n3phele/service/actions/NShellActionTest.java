package n3phele.service.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;

import junit.framework.Assert;
import n3phele.service.actions.CreateVMAction.CreateVirtualServerResult;
import n3phele.service.core.NotFoundException;
import n3phele.service.lifecycle.ProcessLifecycle;
import n3phele.service.model.Account;
import n3phele.service.model.Action;
import n3phele.service.model.ActionState;
import n3phele.service.model.Cloud;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.Command;
import n3phele.service.model.Context;
import n3phele.service.model.Narrative;
import n3phele.service.model.NarrativeLevel;
import n3phele.service.model.core.CommandRequest;
import n3phele.service.model.core.Credential;
import n3phele.service.model.core.ExecutionFactoryCreateRequest;
import n3phele.service.model.core.NameValue;
import n3phele.service.model.core.Task;
import n3phele.service.model.core.User;
import n3phele.service.model.core.VirtualServer;
import n3phele.service.model.repository.Repository;
import n3phele.service.nShell.NParser;
import n3phele.service.nShell.ParseException;
import n3phele.service.rest.impl.AccountResource;
import n3phele.service.rest.impl.ActionResource;
import n3phele.service.rest.impl.CloudProcessResource;
import n3phele.service.rest.impl.CloudResource;
import n3phele.service.rest.impl.NarrativeResource;
import n3phele.service.rest.impl.RepositoryResource;
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
import com.sun.jersey.api.representation.Form;

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
		Command cd = n.parse();
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
		
		CloudProcess shellProcess = ProcessLifecycle.mgr().createProcess(root, "shell", context, null, null, true, NShellActionTestHarness.class);
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
		Command cd = n.parse();
		cd.setUri(URI.create("http://n3phele.com/test"));
		Assert.assertEquals("expressionTest", cd.getName());
		Assert.assertEquals("test expression handling", cd.getDescription());
		Assert.assertTrue(cd.isPublic());
		Assert.assertTrue(cd.isPreferred());
		Assert.assertEquals("1.0", cd.getVersion());
		Assert.assertEquals(URI.create("http://www.n3phele.com/icons/custom"), cd.getIcon());
		testCommandDefinition = cd;
		
		Context context = new Context();
		
		context.putValue("arg", "http://n3phele.com/test#EC2");
		context.putValue("message", "hello!");
		
		CloudProcess shellProcess = ProcessLifecycle.mgr().createProcess(root, "shell", context, null, null, true, NShellActionTestHarness.class);
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
		Command cd = n.parse();
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
		
		CloudProcess shellProcess = ProcessLifecycle.mgr().createProcess(root, "shell", context, null, null, true, NShellActionTestHarness.class);
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
		 CloudProcess createVMProcess;
		 CloudProcess vmProcess;
		 
		 CreateVMActionWrapper.initalState = "Pending";
		 VMActionWrapper.processState = "Running";
		 CreateVMActionWrapper.clientResponseResult = new CreateVirtualServerTestResult("https://myFactory/VM/1234", 201, "https://myFactory/VM/1234");
		
		NParser n = new NParser(new FileInputStream("./test/vmCreateExplicitDeleteTest.n"));
		Command cd = n.parse();
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
		
		CloudProcess shellProcess = ProcessLifecycleWrapper.mgr().createProcess(root, "shell", context, null, null, true, NShellActionTestHarness.class);
		ProcessLifecycleWrapper.mgr().init(shellProcess);
		Thread.sleep(2000);
		CloudProcessResource.dao.clear();
		
		shellProcess = CloudProcessResource.dao.load(shellProcess.getUri());
		NShellAction shell = (NShellAction) ActionResource.dao.load(shellProcess.getAction());
		URI vmURI = (URI) shell.getContext().getObjectValue("my_vm") ;
		Action vmAction = ActionResource.dao.load(vmURI);
		Assert.assertEquals("myVM", vmAction.getContext().getValue("name"));
		Assert.assertEquals(null, shell.getContext().getValue("myVM"));
		
		String refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		Assert.assertEquals("{}", refresh);
		
		Thread.sleep(1000);
		CloudProcessResource.dao.clear();
		
		shellProcess = CloudProcessResource.dao.load(shellProcess.getUri());
		createVMProcess = CloudProcessResource.dao.load(vmAction.getProcess());
		vmAction = ActionResource.dao.load(vmURI);
		Action action = ActionResource.dao.load(vmAction.getContext().getURIValue("cloudVM"));
		vmProcess = CloudProcessResource.dao.load(action.getProcess());
		
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
		Command cd = n.parse();
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
		
		CloudProcess shellProcess = ProcessLifecycleWrapper.mgr().createProcess(root, "shell", context, null, null, true, NShellActionTestHarness.class);
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
		List<URI> vm  = vmAction.getContext().getURIList("cloudVM");
		Assert.assertEquals(2, vm.size());
		Action action1 = ActionResource.dao.load(vm.get(0));
		Action action2 = ActionResource.dao.load(vm.get(1));
		CloudProcess vmProcess = CloudProcessResource.dao.load(action1.getProcess());
		CloudProcess vmProcess2 = CloudProcessResource.dao.load(action2.getProcess());
		
		Assert.assertEquals(ActionState.COMPLETE, shellProcess.getState());
		Assert.assertEquals(ActionState.COMPLETE, createVMProcess.getState());
		Assert.assertEquals(ActionState.CANCELLED, vmProcess.getState());
		Assert.assertEquals(ActionState.CANCELLED, vmProcess2.getState());
	}
	
	/** Invokes a simple command that creates a single vm and then implicitly deletes it
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
		Command cd = n.parse();
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
		
		CloudProcess shellProcess = ProcessLifecycleWrapper.mgr().createProcess(root, "shell", context, null, null, true, NShellActionTestHarness.class);
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
		Action action = ActionResource.dao.load(vm);
		CloudProcess vmProcess = CloudProcessResource.dao.load(action.getProcess());
		
		Assert.assertEquals(ActionState.COMPLETE, shellProcess.getState());
		Assert.assertEquals(ActionState.COMPLETE, createVMProcess.getState());
		Assert.assertEquals(ActionState.CANCELLED, vmProcess.getState());
	}
	
	/** Invokes a simple command that creates a two vm and then implicitly deletes them
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
		Command cd = n.parse();
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
		
		CloudProcess shellProcess = ProcessLifecycleWrapper.mgr().createProcess(root, "shell", context, null, null, true, NShellActionTestHarness.class);
		ProcessLifecycleWrapper.mgr().init(shellProcess);
		Thread.sleep(4000);
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
		List<URI> vm  = vmAction.getContext().getURIList("cloudVM");
		Assert.assertEquals(2, vm.size());
		Action action1 = ActionResource.dao.load(vm.get(0));
		Action action2 = ActionResource.dao.load(vm.get(1));
		CloudProcess vmProcess = CloudProcessResource.dao.load(action1.getProcess());
		CloudProcess vmProcess2 = CloudProcessResource.dao.load(action2.getProcess());
		
		Assert.assertEquals(ActionState.COMPLETE, shellProcess.getState());
		Assert.assertEquals(ActionState.COMPLETE, createVMProcess.getState());
		Assert.assertEquals(ActionState.CANCELLED, vmProcess.getState());
		Assert.assertEquals(ActionState.CANCELLED, vmProcess2.getState());
	}
	
	
	/** Invokes an on command that has no file dependencies
	 * @throws InterruptedException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	@Test
	public void onCommandNoFilesTest() throws InterruptedException, ClassNotFoundException, FileNotFoundException, ParseException {
		User root = getRoot();
		ObjectifyService.register(NShellActionTestHarness.class);
		ObjectifyService.register(OnActionWrapper.class);
		assertNotNull(root);
		 URI cloud = createTestCloud();
		 URI account = createTestAccount(cloud);
		 CreateVMActionWrapper.initalState = "Pending";
		 VMActionWrapper.processState = "Running";
		 CreateVMActionWrapper.clientResponseResult = new CreateVirtualServerTestResult("https://myFactory/VM/1234", 201, "https://myFactory/VM/1234");
		
		NParser n = new NParser(new FileInputStream("./test/onCommandNoFilesTest.n"));
		Command cd = n.parse();
		cd.setUri(URI.create("http://n3phele.com/test"));
		Assert.assertEquals("onCommandNoFiles", cd.getName());
		Assert.assertEquals("run a command that has no files", cd.getDescription());
		Assert.assertTrue(cd.isPublic());
		Assert.assertTrue(cd.isPreferred());
		Assert.assertEquals("1.1", cd.getVersion());
		Assert.assertEquals(URI.create("http://www.n3phele.com/icons/custom"), cd.getIcon());
		testCommandDefinition = cd;
		
		/*
		 * Setup agents replies
		 */
		OnActionWrapper.commandRequestReply = URI.create("http://123.123.123.1/task/15");
		
		Context context = new Context();
		
		context.putValue("arg", "http://n3phele.com/test#EC2");
		context.putValue("account", account);
		
		CloudProcess shellProcess = ProcessLifecycleWrapper.mgr().createProcess(root, "shell", context, null, null, true, NShellActionTestHarness.class);
		ProcessLifecycleWrapper.mgr().init(shellProcess);
		Thread.sleep(2000);
		CloudProcessResource.dao.clear();
		

		shellProcess = CloudProcessResource.dao.load(shellProcess.getUri());
		NShellAction shell = (NShellAction) ActionResource.dao.load(shellProcess.getAction());
		URI vmURI = (URI) shell.getContext().getObjectValue("my_vm") ;
		Action vmAction = ActionResource.dao.load(vmURI);
		Assert.assertEquals("my_vm", vmAction.getContext().getValue("name"));
		
		CloudProcessResource.dao.clear();
		String refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		Assert.assertEquals("{\"RUNABLE\": 1, \"RUNABLE_Wait\": 2}", refresh);
		
		OnAction on_0 = (OnAction) ActionResource.dao.load(shell.getContext().getURIValue("On_0"));
		Assert.assertEquals("echo hello world!\n", on_0.getContext().getValue("command"));
		Assert.assertEquals(OnActionWrapper.commandRequestReply, on_0.getInstance());
		Assert.assertEquals("echo hello world!\n", OnActionWrapper.request.getCmd());
		Assert.assertEquals(UriBuilder.fromUri(on_0.getProcess()).scheme("http").path("event").build(), OnActionWrapper.request.getNotification());
		Assert.assertEquals(null, OnActionWrapper.request.getStdin());
		
		
		Thread.sleep(1000);
		CloudProcessResource.dao.clear();
		refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		Assert.assertEquals("{}", refresh);
		
		
		shellProcess = CloudProcessResource.dao.load(shellProcess.getUri());
		shell = (NShellAction) ActionResource.dao.load(shellProcess.getAction());
		CloudProcess createVMProcess = CloudProcessResource.dao.load(vmAction.getProcess());
		vmAction = ActionResource.dao.load(vmURI);
		URI vm  = vmAction.getContext().getURIValue("cloudVM");
		Action action = ActionResource.dao.load(vm);
		CloudProcess vmProcess = CloudProcessResource.dao.load(action.getProcess());
		CloudProcess onProcess = CloudProcessResource.dao.load(on_0.getProcess());
		
		Assert.assertEquals(ActionState.COMPLETE, shellProcess.getState());
		Assert.assertEquals(ActionState.COMPLETE, createVMProcess.getState());
		Assert.assertEquals(ActionState.CANCELLED, vmProcess.getState());
		Assert.assertEquals(ActionState.COMPLETE, onProcess.getState());
	}
	
	/** Invokes an on command that has a single input file
	 * @throws InterruptedException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	@Test
	public void onCommandSingleInputFileTest() throws InterruptedException, ClassNotFoundException, FileNotFoundException, ParseException {
		User root = getRoot();
		ObjectifyService.register(NShellActionTestHarness.class);
		ObjectifyService.register(OnActionWrapper.class);
		assertNotNull(root);
		 URI cloud = createTestCloud();
		 URI account = createTestAccount(cloud);
		 CreateVMActionWrapper.initalState = "Pending";
		 VMActionWrapper.processState = "Running";
		 CreateVMActionWrapper.clientResponseResult = new CreateVirtualServerTestResult("https://myFactory/VM/1234", 201, "https://myFactory/VM/1234");
		
		NParser n = new NParser(new FileInputStream("./test/onCommandSingleInputFileTest.n"));
		Command cd = n.parse();
		cd.setUri(URI.create("http://n3phele.com/test"));
		Assert.assertEquals("onCommandSingleInputFile", cd.getName());
		Assert.assertEquals("run a command that has a single input file", cd.getDescription());
		Assert.assertTrue(cd.isPublic());
		Assert.assertTrue(cd.isPreferred());
		Assert.assertEquals("1.1", cd.getVersion());
		Assert.assertEquals(URI.create("http://www.n3phele.com/icons/custom"), cd.getIcon());
		testCommandDefinition = cd;
		
		Repository testRepo = new Repository("myRepo", "test repo", new Credential("foo", "secret").encrypt(), URI.create("http://s3.amazon.com"), "testBucket", "S3", UserResource.Root.getUri(), false);
		RepositoryResource.dao.add(testRepo);
		/*
		 * Setup agents replies
		 */
		OnActionWrapper.commandRequestReply = URI.create("http://192.168.1.0:8887/task/15");
		FileTransferActionWrapper.commandRequestReply = URI.create("http://192.168.1.0:8887/task/4");
		
		Context context = new Context();
		
		context.putValue("arg", "http://n3phele.com/test#EC2");
		context.putValue("account", account);
		context.putValue("flowgram.sff.txt", URI.create("myRepo:///root/file.doc"));
		
		CloudProcess shellProcess = ProcessLifecycleWrapper.mgr().createProcess(root, "shell", context, null, null, true, NShellActionTestHarness.class);
		ProcessLifecycleWrapper.mgr().init(shellProcess);
		Thread.sleep(2000);
		CloudProcessResource.dao.clear();
		

		shellProcess = CloudProcessResource.dao.load(shellProcess.getUri());
		NShellAction shell = (NShellAction) ActionResource.dao.load(shellProcess.getAction());
		URI vmURI = (URI) shell.getContext().getObjectValue("my_vm") ;
		Action createVMAction = ActionResource.dao.load(vmURI);
		CloudProcess createVMProcess;
		CloudProcess vmProcess;
		CloudProcess fileTransfer_0Process;
		OnAction on_0 = (OnAction) ActionResource.dao.load(shell.getContext().getURIValue("On_0"));
		CloudProcess on_0Process = CloudProcessResource.dao.load(on_0.getProcess());
		createVMProcess = CloudProcessResource.dao.load(createVMAction.getProcess());

		VMAction vmAction = (VMAction) ActionResource.dao.load(createVMAction.getContext().getURIList("cloudVM").get(0));
		vmProcess = CloudProcessResource.dao.load(vmAction.getProcess());
		FileTransferAction fileTransfer_0 = (FileTransferAction) ActionResource.dao.load(shell.getContext().getURIValue("FileTransfer_0"));
		fileTransfer_0Process = CloudProcessResource.dao.load(fileTransfer_0.getProcess());
		
		Assert.assertEquals("my_vm", createVMAction.getContext().getValue("name"));
		
		CloudProcessResource.dao.clear();
		
		on_0 = (OnAction) ActionResource.dao.load(on_0.getUri());
		on_0Process = CloudProcessResource.dao.load(on_0.getProcess());

		Assert.assertEquals("cat < flowgram.sff.txt | wc -l\n", on_0.getContext().getValue("command"));
		Assert.assertEquals("Command line awaits file copy", ActionState.INIT, on_0Process.getState());
		Assert.assertEquals(ActionState.INIT, on_0Process.getState());
		Assert.assertEquals(ActionState.RUNABLE, fileTransfer_0Process.getState());
		Assert.assertEquals(ActionState.RUNABLE, vmProcess.getState());
		Assert.assertEquals(ActionState.COMPLETE, createVMProcess.getState());
		Assert.assertEquals(ActionState.RUNABLE, shellProcess.getState());
		
		String refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		Assert.assertEquals("file copy running", "{\"RUNABLE\": 1, \"RUNABLE_Wait\": 2, \"INIT\": 1}", refresh);
		
		Thread.sleep(1000);
		CloudProcessResource.dao.clear();

		on_0Process = CloudProcessResource.dao.load(on_0.getProcess());
		fileTransfer_0Process = CloudProcessResource.dao.load(fileTransfer_0Process.getUri());
		vmProcess = CloudProcessResource.dao.load(vmProcess.getUri());
		createVMProcess = CloudProcessResource.dao.load(createVMProcess.getUri());
		shellProcess = CloudProcessResource.dao.load(shellProcess.getUri());
		Assert.assertEquals("Command line starts to run", ActionState.RUNABLE, on_0Process.getState());
		Assert.assertEquals("File copy comlete", ActionState.COMPLETE, fileTransfer_0Process.getState());
		Assert.assertEquals(ActionState.RUNABLE, vmProcess.getState());
		Assert.assertEquals(ActionState.COMPLETE, createVMProcess.getState());
		Assert.assertEquals(ActionState.RUNABLE, shellProcess.getState());	
		
		refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		Assert.assertEquals("command runs", "{\"RUNABLE\": 1, \"RUNABLE_Wait\": 2}", refresh);
//		Thread.sleep(500);
//		CloudProcessResource.dao.clear();
//		
//		on_0Process = CloudProcessResource.dao.load(on_0.getProcess());
//		fileTransfer_0Process = CloudProcessResource.dao.load(fileTransfer_0Process.getUri());
//		vmProcess = CloudProcessResource.dao.load(vmProcess.getUri());
//		createVMProcess = CloudProcessResource.dao.load(createVMProcess.getUri());
//		shellProcess = CloudProcessResource.dao.load(shellProcess.getUri());
//		Assert.assertEquals("Command line polls", ActionState.RUNABLE, on_0Process.getState());
//		Assert.assertEquals(ActionState.COMPLETE, fileTransfer_0Process.getState());
//		Assert.assertEquals(ActionState.RUNABLE, vmProcess.getState());
//		Assert.assertEquals(ActionState.COMPLETE, createVMProcess.getState());
//		Assert.assertEquals(ActionState.RUNABLE, shellProcess.getState());	
//
//		
//		refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
//		Assert.assertEquals("command line polling", "{\"RUNABLE\": 1, \"RUNABLE_Wait\": 2}", refresh);
		
		Thread.sleep(1000);
		CloudProcessResource.dao.clear();
		on_0Process = CloudProcessResource.dao.load(on_0.getProcess());
		fileTransfer_0Process = CloudProcessResource.dao.load(fileTransfer_0Process.getUri());
		vmProcess = CloudProcessResource.dao.load(vmProcess.getUri());
		createVMProcess = CloudProcessResource.dao.load(createVMProcess.getUri());
		shellProcess = CloudProcessResource.dao.load(shellProcess.getUri());
		Assert.assertEquals("Command line finishes", ActionState.COMPLETE, on_0Process.getState());
		Assert.assertEquals(ActionState.COMPLETE, fileTransfer_0Process.getState());
		Assert.assertEquals("VM destroyed", ActionState.CANCELLED, vmProcess.getState());
		Assert.assertEquals(ActionState.COMPLETE, createVMProcess.getState());
		Assert.assertEquals("Shell completes", ActionState.COMPLETE, shellProcess.getState());
		
		
		refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		Assert.assertEquals("filecopy completes, command runs", "{}", refresh);

		Thread.sleep(500);
		CloudProcessResource.dao.clear();
		on_0Process = CloudProcessResource.dao.load(on_0.getProcess());
		fileTransfer_0Process = CloudProcessResource.dao.load(fileTransfer_0Process.getUri());
		vmProcess = CloudProcessResource.dao.load(vmProcess.getUri());
		createVMProcess = CloudProcessResource.dao.load(createVMProcess.getUri());
		shellProcess = CloudProcessResource.dao.load(shellProcess.getUri());
		Assert.assertEquals(ActionState.COMPLETE, on_0Process.getState());
		Assert.assertEquals(ActionState.COMPLETE, fileTransfer_0Process.getState());
		Assert.assertEquals(ActionState.CANCELLED, vmProcess.getState());
		Assert.assertEquals(ActionState.COMPLETE, createVMProcess.getState());
		Assert.assertEquals(ActionState.COMPLETE, shellProcess.getState());

		refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		Assert.assertEquals("all complete", "{}", refresh);
		
		Thread.sleep(500);
		CloudProcessResource.dao.clear();
		on_0Process = CloudProcessResource.dao.load(on_0.getProcess());
		fileTransfer_0Process = CloudProcessResource.dao.load(fileTransfer_0Process.getUri());
		vmProcess = CloudProcessResource.dao.load(vmProcess.getUri());
		createVMProcess = CloudProcessResource.dao.load(createVMProcess.getUri());
		shellProcess = CloudProcessResource.dao.load(shellProcess.getUri());
		
		on_0 = (OnAction) ActionResource.dao.load(on_0.getUri());
		fileTransfer_0 = (FileTransferAction) ActionResource.dao.load(fileTransfer_0.getUri());

		
		Assert.assertEquals(OnActionWrapper.commandRequestReply, on_0.getInstance());
		Assert.assertEquals("cat < flowgram.sff.txt | wc -l\n", OnActionWrapper.request.getCmd());
		Assert.assertEquals(UriBuilder.fromUri(on_0.getProcess()).scheme("http").path("event").build(), OnActionWrapper.request.getNotification());
		Assert.assertEquals(null, OnActionWrapper.request.getStdin());
		Assert.assertEquals(FileTransferActionWrapper.commandRequestReply, fileTransfer_0.getInstance());
		Assert.assertEquals(URI.create("http://192.168.1.0:8887/task"), FileTransferActionWrapper.sendRequestTarget);
		Assert.assertEquals(URI.create("http://192.168.1.0:8887/task"), OnActionWrapper.sendRequestTarget);
		Assert.assertEquals(URI.create("http://192.168.1.0:8887/task/4"), FileTransferActionWrapper.getTaskTarget);
		Assert.assertEquals(URI.create("http://192.168.1.0:8887/task/15"), OnActionWrapper.getTaskTarget);	
	}
	
	/** Invokes an on command that has a single input and output file
	 * @throws InterruptedException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	@Test
	public void onCommandSingleInputSingleOutFileTest() throws InterruptedException, ClassNotFoundException, FileNotFoundException, ParseException {
		User root = getRoot();
		ObjectifyService.register(NShellActionTestHarness.class);
		ObjectifyService.register(OnActionWrapper.class);
		assertNotNull(root);
		 URI cloud = createTestCloud();
		 URI account = createTestAccount(cloud);
		 CreateVMActionWrapper.initalState = "Pending";
		 VMActionWrapper.processState = "Running";
		 CreateVMActionWrapper.clientResponseResult = new CreateVirtualServerTestResult("https://myFactory/VM/1234", 201, "https://myFactory/VM/1234");
		
		NParser n = new NParser(new FileInputStream("./test/onCommandSingleOutputFileTest.n"));
		Command cd = n.parse();
		cd.setUri(URI.create("http://n3phele.com/test"));
		Assert.assertEquals("onCommandSingleOutputFile", cd.getName());
		Assert.assertEquals("run a command that has a single input and file", cd.getDescription());
		Assert.assertTrue(cd.isPublic());
		Assert.assertTrue(cd.isPreferred());
		Assert.assertEquals("1.1", cd.getVersion());
		Assert.assertEquals(URI.create("http://www.n3phele.com/icons/custom"), cd.getIcon());
		testCommandDefinition = cd;
		
		Repository testRepo = new Repository("myRepo", "test repo", new Credential("foo", "secret").encrypt(), URI.create("http://s3.amazon.com"), "testBucket", "S3", UserResource.Root.getUri(), false);
		RepositoryResource.dao.add(testRepo);
		/*
		 * Setup agents replies
		 */
		OnActionWrapper.commandRequestReply = URI.create("http://192.168.1.0:8887/task/15");
		FileTransferActionWrapper.commandRequestReply = URI.create("http://192.168.1.0:8887/task/4");
		
		Context context = new Context();
		
		context.putValue("arg", "http://n3phele.com/test#EC2");
		context.putValue("account", account);
		context.putValue("flowgram.sff.txt", URI.create("myRepo:///root/file.doc"));
		context.putValue("denoiser.log", URI.create("myRepo:///root/denoiserLog.txt"));
		
		CloudProcess shellProcess = ProcessLifecycleWrapper.mgr().createProcess(root, "shell", context, null, null, true, NShellActionTestHarness.class);
		ProcessLifecycleWrapper.mgr().init(shellProcess);
		Thread.sleep(2000);
		CloudProcessResource.dao.clear();
		

		shellProcess = CloudProcessResource.dao.load(shellProcess.getUri());
		NShellAction shell = (NShellAction) ActionResource.dao.load(shellProcess.getAction());
		URI vmURI = (URI) shell.getContext().getObjectValue("my_vm") ;
		Action createVMAction = ActionResource.dao.load(vmURI);
		CloudProcess createVMProcess;
		CloudProcess vmProcess;
		CloudProcess fileTransfer_0Process;
		CloudProcess fileTransfer_1Process;
		OnAction on_0 = (OnAction) ActionResource.dao.load(shell.getContext().getURIValue("On_0"));
		CloudProcess on_0Process = CloudProcessResource.dao.load(on_0.getProcess());
		createVMProcess = CloudProcessResource.dao.load(createVMAction.getProcess());

		VMAction vmAction = (VMAction) ActionResource.dao.load(createVMAction.getContext().getURIList("cloudVM").get(0));
		vmProcess = CloudProcessResource.dao.load(vmAction.getProcess());
		FileTransferAction fileTransfer_0 = (FileTransferAction) ActionResource.dao.load(shell.getContext().getURIValue("FileTransfer_0"));
		fileTransfer_0Process = CloudProcessResource.dao.load(fileTransfer_0.getProcess());
		FileTransferAction fileTransfer_1 = (FileTransferAction) ActionResource.dao.load(shell.getContext().getURIValue("FileTransfer_1"));
		fileTransfer_1Process = CloudProcessResource.dao.load(fileTransfer_1.getProcess());
		
		Assert.assertEquals("my_vm", createVMAction.getContext().getValue("name"));
		
		CloudProcessResource.dao.clear();
		
		on_0 = (OnAction) ActionResource.dao.load(on_0.getUri());
		on_0Process = CloudProcessResource.dao.load(on_0.getProcess());

		Assert.assertEquals("mkdir output; cat < flowgram.sff.txt | wc -l > output/denoiser.log\n", on_0.getContext().getValue("command"));
		Assert.assertEquals("Command line awaits file copy", ActionState.INIT, on_0Process.getState());
		Assert.assertEquals(ActionState.INIT, on_0Process.getState());
		Assert.assertEquals(ActionState.RUNABLE, fileTransfer_0Process.getState());
		Assert.assertEquals(ActionState.INIT, fileTransfer_1Process.getState());
		Assert.assertEquals(ActionState.RUNABLE, vmProcess.getState());
		Assert.assertEquals(ActionState.COMPLETE, createVMProcess.getState());
		Assert.assertEquals(ActionState.RUNABLE, shellProcess.getState());
		
		String refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		Assert.assertEquals("input file copy running", "{\"RUNABLE\": 1, \"RUNABLE_Wait\": 2, \"INIT\": 2}", refresh);
		
		Thread.sleep(1000);
		CloudProcessResource.dao.clear();

		on_0Process = CloudProcessResource.dao.load(on_0.getProcess());
		fileTransfer_0Process = CloudProcessResource.dao.load(fileTransfer_0Process.getUri());
		fileTransfer_1Process = CloudProcessResource.dao.load(fileTransfer_1Process.getUri());
		vmProcess = CloudProcessResource.dao.load(vmProcess.getUri());
		createVMProcess = CloudProcessResource.dao.load(createVMProcess.getUri());
		shellProcess = CloudProcessResource.dao.load(shellProcess.getUri());
		Assert.assertEquals("Command line starts to run", ActionState.RUNABLE, on_0Process.getState());
		Assert.assertEquals("File copy comlete", ActionState.COMPLETE, fileTransfer_0Process.getState());
		Assert.assertEquals(ActionState.INIT, fileTransfer_1Process.getState());
		Assert.assertEquals(ActionState.RUNABLE, vmProcess.getState());
		Assert.assertEquals(ActionState.COMPLETE, createVMProcess.getState());
		Assert.assertEquals(ActionState.RUNABLE, shellProcess.getState());	
		
		refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		Assert.assertEquals("command runs", "{\"RUNABLE\": 1, \"RUNABLE_Wait\": 2, \"INIT\": 1}", refresh);
//		Thread.sleep(500);
//		CloudProcessResource.dao.clear();
//		
//		on_0Process = CloudProcessResource.dao.load(on_0.getProcess());
//		fileTransfer_0Process = CloudProcessResource.dao.load(fileTransfer_0Process.getUri());
//		fileTransfer_1Process = CloudProcessResource.dao.load(fileTransfer_1Process.getUri());
//		vmProcess = CloudProcessResource.dao.load(vmProcess.getUri());
//		createVMProcess = CloudProcessResource.dao.load(createVMProcess.getUri());
//		shellProcess = CloudProcessResource.dao.load(shellProcess.getUri());
//		Assert.assertEquals("Command line polls", ActionState.RUNABLE, on_0Process.getState());
//		Assert.assertEquals(ActionState.COMPLETE, fileTransfer_0Process.getState());
//		Assert.assertEquals(ActionState.INIT, fileTransfer_1Process.getState());
//		Assert.assertEquals(ActionState.RUNABLE, vmProcess.getState());
//		Assert.assertEquals(ActionState.COMPLETE, createVMProcess.getState());
//		Assert.assertEquals(ActionState.RUNABLE, shellProcess.getState());	
//
//		
//		refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
//		Assert.assertEquals("command line polling", "{\"RUNABLE\": 1, \"RUNABLE_Wait\": 2, \"INIT\": 1}", refresh);
		
		Thread.sleep(500);
		CloudProcessResource.dao.clear();
		on_0Process = CloudProcessResource.dao.load(on_0.getProcess());
		fileTransfer_0Process = CloudProcessResource.dao.load(fileTransfer_0Process.getUri());
		fileTransfer_1Process = CloudProcessResource.dao.load(fileTransfer_1Process.getUri());
		vmProcess = CloudProcessResource.dao.load(vmProcess.getUri());
		createVMProcess = CloudProcessResource.dao.load(createVMProcess.getUri());
		shellProcess = CloudProcessResource.dao.load(shellProcess.getUri());
		Assert.assertEquals("Command line finishes", ActionState.COMPLETE, on_0Process.getState());
		Assert.assertEquals(ActionState.COMPLETE, fileTransfer_0Process.getState());
		Assert.assertEquals("output file copy starts", ActionState.RUNABLE, fileTransfer_1Process.getState());
		Assert.assertEquals("VM still running", ActionState.RUNABLE, vmProcess.getState());
		Assert.assertEquals(ActionState.COMPLETE, createVMProcess.getState());
		Assert.assertEquals("Shell awaiting copy", ActionState.RUNABLE, shellProcess.getState());
		
		
		refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		Assert.assertEquals("output filecopy running", "{\"RUNABLE\": 1, \"RUNABLE_Wait\": 2}", refresh);

//		Thread.sleep(500);
//		CloudProcessResource.dao.clear();
//		on_0Process = CloudProcessResource.dao.load(on_0.getProcess());
//		fileTransfer_0Process = CloudProcessResource.dao.load(fileTransfer_0Process.getUri());
//		fileTransfer_1Process = CloudProcessResource.dao.load(fileTransfer_1Process.getUri());
//		vmProcess = CloudProcessResource.dao.load(vmProcess.getUri());
//		createVMProcess = CloudProcessResource.dao.load(createVMProcess.getUri());
//		shellProcess = CloudProcessResource.dao.load(shellProcess.getUri());
//		Assert.assertEquals(ActionState.COMPLETE, on_0Process.getState());
//		Assert.assertEquals(ActionState.COMPLETE, fileTransfer_0Process.getState());
//		Assert.assertEquals("output file copy running", ActionState.RUNABLE, fileTransfer_1Process.getState());
//		Assert.assertEquals(ActionState.RUNABLE, vmProcess.getState());
//		Assert.assertEquals(ActionState.COMPLETE, createVMProcess.getState());
//		Assert.assertEquals(ActionState.RUNABLE, shellProcess.getState());
//		
//		refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
//		Assert.assertEquals("output filecopy running", "{\"RUNABLE\": 1, \"RUNABLE_Wait\": 2}", refresh);

		Thread.sleep(500);
		CloudProcessResource.dao.clear();
		
		on_0Process = CloudProcessResource.dao.load(on_0.getProcess());
		fileTransfer_0Process = CloudProcessResource.dao.load(fileTransfer_0Process.getUri());
		fileTransfer_1Process = CloudProcessResource.dao.load(fileTransfer_1Process.getUri());
		vmProcess = CloudProcessResource.dao.load(vmProcess.getUri());
		createVMProcess = CloudProcessResource.dao.load(createVMProcess.getUri());
		shellProcess = CloudProcessResource.dao.load(shellProcess.getUri());
		Assert.assertEquals(ActionState.COMPLETE, on_0Process.getState());
		Assert.assertEquals(ActionState.COMPLETE, fileTransfer_0Process.getState());
		Assert.assertEquals("output file copy finished", ActionState.COMPLETE, fileTransfer_1Process.getState());
		Assert.assertEquals("VM deleted", ActionState.CANCELLED, vmProcess.getState());
		Assert.assertEquals(ActionState.COMPLETE, createVMProcess.getState());
		Assert.assertEquals("Shell exits", ActionState.COMPLETE, shellProcess.getState());

		refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		Assert.assertEquals("all complete", "{}", refresh);
		
		Thread.sleep(500);
		CloudProcessResource.dao.clear();
		on_0Process = CloudProcessResource.dao.load(on_0.getProcess());
		fileTransfer_0Process = CloudProcessResource.dao.load(fileTransfer_0Process.getUri());
		vmProcess = CloudProcessResource.dao.load(vmProcess.getUri());
		createVMProcess = CloudProcessResource.dao.load(createVMProcess.getUri());
		shellProcess = CloudProcessResource.dao.load(shellProcess.getUri());
		
		on_0 = (OnAction) ActionResource.dao.load(on_0.getUri());
		fileTransfer_0 = (FileTransferAction) ActionResource.dao.load(fileTransfer_0.getUri());

		
		Assert.assertEquals(OnActionWrapper.commandRequestReply, on_0.getInstance());
		Assert.assertEquals("mkdir output; cat < flowgram.sff.txt | wc -l > output/denoiser.log\n", OnActionWrapper.request.getCmd());
		Assert.assertEquals(UriBuilder.fromUri(on_0.getProcess()).scheme("http").path("event").build(), OnActionWrapper.request.getNotification());
		Assert.assertEquals(null, OnActionWrapper.request.getStdin());
		Assert.assertEquals(FileTransferActionWrapper.commandRequestReply, fileTransfer_0.getInstance());
		Assert.assertEquals(URI.create("http://192.168.1.0:8887/task"), FileTransferActionWrapper.sendRequestTarget);
		Assert.assertEquals(URI.create("http://192.168.1.0:8887/task"), OnActionWrapper.sendRequestTarget);
		Assert.assertEquals(URI.create("http://192.168.1.0:8887/task/4"), FileTransferActionWrapper.getTaskTarget);
		Assert.assertEquals(URI.create("http://192.168.1.0:8887/task/15"), OnActionWrapper.getTaskTarget);	
	}
	
	
	/** Invokes a log command in a for loop
	 * @throws InterruptedException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	@Test
	public void forLoopOfLogTest() throws InterruptedException, ClassNotFoundException, FileNotFoundException, ParseException {
		User root = getRoot();
		ObjectifyService.register(NShellActionTestHarness.class);
		assertNotNull(root);
		
		NParser n = new NParser(new FileInputStream("./test/forCommandLogTest.n"));
		Command cd = n.parse();
		cd.setUri(URI.create("http://n3phele.com/test"));
		Assert.assertEquals("forCommandLog", cd.getName());
		Assert.assertEquals("a simple for loop", cd.getDescription());
		Assert.assertTrue(cd.isPublic());
		Assert.assertTrue(cd.isPreferred());
		Assert.assertEquals("1.0", cd.getVersion());
		Assert.assertEquals(URI.create("http://www.n3phele.com/icons/custom"), cd.getIcon());
		testCommandDefinition = cd;
		
		Context context = new Context();
		
		context.putValue("n", 3);
		context.putValue("concurrent", 2);
		context.putValue("arg", "http://n3phele.com/test#EC2");
		
		CloudProcess shellProcess = ProcessLifecycle.mgr().createProcess(root, "shell", context, null, null, true, NShellActionTestHarness.class);
		ProcessLifecycle.mgr().init(shellProcess);
		Thread.sleep(2000);
		CloudProcessResource.dao.clear();
		
		String refresh = ProcessLifecycle.mgr().periodicScheduler().toString().replaceAll("([0-9a-zA-Z_]+)=", "\"$1\": ");
		assertEquals("{}", refresh);
		List<Narrative> logs = new ArrayList<Narrative>();
		logs.addAll(NarrativeResource.dao.getNarratives(shellProcess.getUri()));
		assertEquals(6, logs.size());
		assertEquals("log For_0_2 2", logs.get(4).getText());
		assertEquals("log2 For_0_2 2", logs.get(5).getText());
		for(Narrative log : logs) {
			System.out.println(log.toString());
		}
		Assert.assertTrue("loop concurrent", 
			(	"log For_0_0 0".equals(logs.get(0).getText()) && 
				"log For_0_1 1".equals(logs.get(1).getText()) ) ||
			(	"log For_0_1 1".equals(logs.get(0).getText()) && 
				"log For_0_0 0".equals(logs.get(1).getText()) ) ||
			(	"log For_0_1 1".equals(logs.get(0).getText()) && 
				"log2 For_0_1 1".equals(logs.get(1).getText()) ) ||
			(	"log For_0_0 0".equals(logs.get(0).getText()) && 
				"log2 For_0_0 0".equals(logs.get(1).getText()) ) ||
			(	"log shell For_0_0 0".equals(logs.get(1).getText()) && 
					"log For_0_1 1".equals(logs.get(0).getText()) ));
		

		
		Thread.sleep(1000);
	}


		

	public static Command testCommandDefinition;
	@EntitySubclass
	public static class NShellActionTestHarness extends NShellAction {
		@Override
		protected Command loadCommandDefinition(URI uri) throws NotFoundException {
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
		 Account account = new Account("testAccount", "Account for testing", cloud, "testCloud", new Credential("account", "accountSecret").encrypt(), getRoot().getUri(), false);
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
			name, context, dependency, parent, true, clazz
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
	 
	 @EntitySubclass
	 public static class OnActionWrapper extends OnAction {
		 static List<Task> reply;
		 static CommandRequest request;
		 static URI commandRequestReply;
		 static URI sendRequestTarget;
		 static URI getTaskTarget;
		 int i = 0;
		 @Override
		 protected Task getTask(Client client, String target) {
			 getTaskTarget = URI.create(target);
			 if(i < reply.size())
				 i++;
			 return reply.get(i-1);
		}
		@Override
		protected URI sendRequest(Client client, URI target, CommandRequest form) {
				sendRequestTarget = target;
				request = form;
				Task reply0 = new Task();
				reply0.setId("1");
				reply0.setUri(commandRequestReply);
				reply0.setStarted(new Date());
				reply0.setStdin(form.getStdin());
				reply0.setStdout("stdout");
				reply0.setStderr("stderr");
				reply0.setNotification(form.getNotification());
				
				Task reply1 = new Task();
				reply1.setId("1");
				reply1.setUri(commandRequestReply);
				reply1.setStarted(reply0.getStarted());
				reply1.setStdin(form.getStdin());
				reply1.setStdout("stdout");
				reply1.setStderr("stderr");
				reply1.setNotification(form.getNotification());
				reply1.setFinished(new Date());
				
				reply = Arrays.asList(reply0, reply1);
				
				
				return commandRequestReply;

				
			}
	 }
	 
	 
	 @EntitySubclass
	 public static class FileTransferActionWrapper extends FileTransferAction {
		 static List<Task> reply;
		 static Form request;
		 static URI commandRequestReply;
		 static URI sendRequestTarget;
		 static URI getTaskTarget;
		 int i = 0;
		 @Override
		 protected Task getTask(Client client, String target) {
			 getTaskTarget = URI.create(target);
			 if(i < reply.size())
				 i++;
			 return reply.get(i-1);
			}
			@Override
			protected URI sendRequest(Client client, URI target, Form form) {
				sendRequestTarget = target;
				request = form;
				Task reply0 = new Task();
				reply0.setId("1");
				reply0.setUri(commandRequestReply);
				reply0.setStarted(new Date());
				reply0.setStdin(form.getFirst("stdin"));
				reply0.setStdout("stdout");
				reply0.setStderr("stderr");
				reply0.setNotification(URI.create(form.getFirst("notification")));
				
				Task reply1 = new Task();
				reply1.setId("1");
				reply1.setUri(commandRequestReply);
				reply1.setStarted(reply0.getStarted());
				reply0.setStdin(form.getFirst("stdin"));
				reply1.setStdout("stdout");
				reply1.setStderr("stderr");
				reply0.setNotification(URI.create(form.getFirst("notification")));
				reply1.setFinished(new Date());
				
				reply = Arrays.asList(reply0, reply1);
				
				
				return commandRequestReply;

				
			}
	 }
	 
	 @EntitySubclass
	 public static class ForActionWrapper extends ForAction {
		 
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
