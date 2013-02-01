package n3phele.service.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;

import junit.framework.Assert;

import n3phele.service.core.NotFoundException;
import n3phele.service.lifecycle.ProcessLifecycle;
import n3phele.service.model.Action;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.CommandDefinition;
import n3phele.service.model.Context;
import n3phele.service.model.Narrative;
import n3phele.service.model.NarrativeLevel;
import n3phele.service.model.core.Helpers;
import n3phele.service.model.core.User;
import n3phele.service.nShell.NParser;
import n3phele.service.nShell.ParseException;
import n3phele.service.rest.impl.ActionResource;
import n3phele.service.rest.impl.CloudProcessResource;
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
	
	
	/** Invokes a simple command containing a log action
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
		shellProcess = CloudProcessResource.dao.load(shellProcess.getId());
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
	
	

	public static CommandDefinition testCommandDefinition;
	@EntitySubclass
	public static class NShellActionTestHarness extends NShellAction {
		@Override
		protected CommandDefinition loadCommandDefinition(URI uri) throws NotFoundException {
			return testCommandDefinition;
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


}
