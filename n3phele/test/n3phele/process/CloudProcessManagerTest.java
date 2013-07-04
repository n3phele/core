package n3phele.process;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import junit.framework.Assert;

import n3phele.service.actions.StackServiceAction;
import n3phele.service.model.Action;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.core.Collection;
import n3phele.service.rest.impl.ActionResource.ActionManager;
import n3phele.service.rest.impl.CloudProcessResource.CloudProcessManager;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

public class CloudProcessManagerTest extends DatabaseTestUtils {

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
	public void GetCollectionExecuteCountTest() {
		CloudProcessManager manager = new CloudProcessManager();
		
		int totalInDatabase = 10;
		try {
			populateDatabaseWithRandomProcessesNoAction(manager, totalInDatabase);
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		
		//Get first 5 results
		Collection<CloudProcess> collection = manager.getCollection(0, 5, true);
		
		Assert.assertEquals(5, collection.getElements().size());
		Assert.assertEquals(totalInDatabase, collection.getTotal());		
	}
	
	@Test
	public void GetCollectionDoNotExecuteCountTest() {
		CloudProcessManager manager = new CloudProcessManager();

		int totalInDatabase = 10;
		try {
			populateDatabaseWithRandomProcessesNoAction(manager, totalInDatabase);
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		
		//Get first 5 results
		Collection<CloudProcess> collection = manager.getCollection(0, 5);
		
		Assert.assertEquals(5, collection.getElements().size());
		Assert.assertEquals(5, collection.getTotal());
	}
	
	@Test
	public void GetCollectionWithOwnerExecuteCountTest() throws URISyntaxException {
		CloudProcessManager manager = new CloudProcessManager();

		String owner = "http://127.0.0.1/account/1";
		
		int totalInDatabase = 10;
		try {
			populateDatabaseWithRandomProcessesNoAction(manager, totalInDatabase, owner);
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		
		//Get first 5 results
		Collection<CloudProcess> collection = manager.getCollection(0, 5, new URI(owner), true);

		Assert.assertEquals(5, collection.getElements().size());
		Assert.assertEquals(totalInDatabase, collection.getTotal());			
	}
	
	@Test
	public void GetCollectionWithOwnerDoNotExecuteCountTest() throws URISyntaxException {
		CloudProcessManager manager = new CloudProcessManager();
				
		String owner = "http://127.0.0.1/account/1";
		
		try {
			int totalInDatabase = 10;
			populateDatabaseWithRandomProcessesNoAction(manager, totalInDatabase, owner);
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		
		//Get first 5 results
		Collection<CloudProcess> collection = manager.getCollection(0, 5, new URI(owner));

		Assert.assertEquals(5, collection.getElements().size());
		Assert.assertEquals(5, collection.getTotal());
	}		
	
	@Test
	public void RetrieveFiveProcessesThatAreServiceStackActionsAndAreStillRunningTogetherWithOtherProcessesTest() throws URISyntaxException {
		CloudProcessManager processManager = new CloudProcessManager();

		String owner = "http://127.0.0.1/account/1";
		
		int processCount = 5;
		//Add processes with stack service actions
		ActionManager actionManager = new ActionManager();
		List<Action> actions = createValidServiceStackActions(processCount);
		for(Action action: actions)
		{
			actionManager.add(action);
		}
		
		populateDatabaseWithRandomProcessAndTheseActions(processManager, actionManager, actions);
		
		//Add processes with job actions
		actions = createValidJobActions(processCount);
		for(Action action: actions)
		{
			actionManager.add(action);
		}		
		populateDatabaseWithRandomProcessAndTheseActions(processManager, actionManager, actions);
		
		Collection<CloudProcess> collection = processManager.getServiceStackCollectionNonFinalized();

		Collection<StackServiceAction> stackServiceActionsCollection = actionManager.getStackServiceAction();
		List<StackServiceAction> stackServiceActions = stackServiceActionsCollection.getElements();
		Assert.assertEquals(processCount, stackServiceActions.size());
		Collection<CloudProcess> processes = processManager.getCollection(0, processCount*3);
		Assert.assertEquals(processCount*2, processes.getElements().size());
		List<CloudProcess> serviceActionsProcesses = collection.getElements();
		Assert.assertEquals(processCount + " elements should be found", processCount, serviceActionsProcesses.size());
	}
	
	@Test
	public void RetrieveFiveProcessesThatAreServiceStackActionsAndAreStillRunningWithNoOtherProcessTest() throws URISyntaxException {
		CloudProcessManager processManager = new CloudProcessManager();

		String owner = "http://127.0.0.1/account/1";
		
		int processCount = 5;
		//Add processes with stack service actions
		ActionManager actionManager = new ActionManager();
		List<Action> actions = createValidServiceStackActions(processCount);
		for(Action action: actions)
		{
			actionManager.add(action);
		}
		
		populateDatabaseWithRandomProcessAndTheseActions(processManager, actionManager, actions);
		
		Collection<CloudProcess> collection = processManager.getServiceStackCollectionNonFinalized();

		Collection<StackServiceAction> stackServiceActionsCollection = actionManager.getStackServiceAction();
		List<StackServiceAction> stackServiceActions = stackServiceActionsCollection.getElements();
		Assert.assertEquals(processCount, stackServiceActions.size());
		Collection<CloudProcess> processes = processManager.getCollection(0, processCount*3);
		Assert.assertEquals(processCount, processes.getElements().size());
		List<CloudProcess> serviceActionsProcesses = collection.getElements();
		Assert.assertEquals(processCount + " elements should be found", processCount, serviceActionsProcesses.size());
	}
	
	@Test
	public void RetrieveTwoProcessesThatAreServiceStackActionsAndAreStillRunningWithOtherTwoNotRunningTest() throws URISyntaxException {
		CloudProcessManager processManager = new CloudProcessManager();
		
		int processCount = 4;
		//Add processes with stack service actions
		ActionManager actionManager = new ActionManager();
		List<Action> actions = createValidServiceStackActions(processCount);
		for(Action action: actions)
		{
			actionManager.add(action);
		}
		
		List<CloudProcess> processes = populateDatabaseWithRandomProcessAndTheseActions(processManager, actionManager, actions);
		processes.get(0).setFinalized(true);
		processes.get(1).setFinalized(true);
		processManager.update(processes.get(0));
		processManager.update(processes.get(1));
		
		Collection<CloudProcess> collection = processManager.getServiceStackCollectionNonFinalized();
		
		Assert.assertEquals(2, collection.getElements().size());		
	}
	
	@Test
	public void RetrieveEmptyListOfProcessesThatAreServiceStackActionsAndAreStillRunningTest() throws URISyntaxException {
		CloudProcessManager processManager = new CloudProcessManager();
		
		int processCount = 5;

		ActionManager actionManager = new ActionManager();
		//Add processes with job actions		
		List<Action> actions = createValidJobActions(processCount);
		for(Action action: actions)
		{
			actionManager.add(action);
		}		
		populateDatabaseWithRandomProcessAndTheseActions(processManager, actionManager, actions);
		
		Collection<CloudProcess> collection = processManager.getServiceStackCollectionNonFinalized();

		Collection<StackServiceAction> stackServiceActionsCollection = actionManager.getStackServiceAction();
		List<StackServiceAction> stackServiceActions = stackServiceActionsCollection.getElements();
		Assert.assertEquals(0, stackServiceActions.size());
		Collection<CloudProcess> processes = processManager.getCollection(0, processCount*3);
		Assert.assertEquals(processCount, processes.getElements().size());
		List<CloudProcess> serviceActionsProcesses = collection.getElements();
		Assert.assertEquals("No element should be returned", 0, serviceActionsProcesses.size());
	}
}
