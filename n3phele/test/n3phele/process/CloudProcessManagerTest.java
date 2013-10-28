package n3phele.process;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import n3phele.QueueTestHelper;
import n3phele.service.actions.StackServiceAction;
import n3phele.service.dao.ProcessCounterManager;
import n3phele.service.model.Account;
import n3phele.service.model.Action;
import n3phele.service.model.ChangeManager;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.ProcessCounter;
import n3phele.service.model.core.Change;
import n3phele.service.model.core.Collection;
import n3phele.service.model.core.User;
import n3phele.service.rest.impl.AccountResource;
import n3phele.service.rest.impl.UserResource;
import n3phele.service.rest.impl.ActionResource.ActionManager;
import n3phele.service.rest.impl.CloudProcessResource.CloudProcessManager;

import org.hsqldb.DatabaseURL;
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
								.setDisableAutoTaskExecution(true)             
								.setCallbackClass(LocalTaskQueueTestConfig.DeferredTaskCallback.class));
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	User user = new User("test","testName","testLastName");
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		helper.setUp();
		UserResource.dao.delete(UserResource.Root);
		UserResource.dao.add(user);
	}

	@After     
	public void tearDown() {         
		helper.tearDown();     
	} 

	 
	 public ProcessCounter initializeCounter(Account account, User user, int count)
	 {
		ProcessCounter counter = new ProcessCounter();
		counter.setOwner(user.getUri());
		counter.setCount(count);
		ProcessCounterManager counterManager = new ProcessCounterManager();
		counterManager.add(counter);
			
		return counter;
	 }
	 
	@Test
	public void givenUserJerryHasFiveCloudProcessesAndUserTomHasFiveCloudProcesses_WhenWeAskForAllTheFirstFiveProcessesAndTheTotal_ThenTheResultShouldIncludeFiveProcessesAndTheTotalAsTen() {
		CloudProcessManager manager = new CloudProcessManager();
		
		//We will use other users here, delete the common one on this test
		UserResource.dao.delete(user);
		
		//Create users
		User jerry = new User("jerry@hp.com","jerry","the rat");
		User tom = new User("tom@hp.com","tom","the cat");
		UserResource.dao.add(jerry);
		UserResource.dao.add(tom);
		
		//InitializeCoutnerForJerry
		Account jerryAccount = new DatabaseTestUtils().createValidAccount(AccountResource.dao, user);
		initializeCounter(jerryAccount, jerry, 5);
		
		//InitializeCoutnerForTom
		Account tomAccount = new DatabaseTestUtils().createValidAccount(AccountResource.dao, user);
		initializeCounter(tomAccount, tom, 5);
		
		//Populate database with five processes for jerry and five for tom
		try {			
			populateDatabaseWithRandomProcessesNoAction(manager, 5, jerry);
			populateDatabaseWithRandomProcessesNoAction(manager, 5, tom);
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		
		//Get first 5 results
		Collection<CloudProcess> collection = manager.getCollection(0, 5, true);
		
		assertEquals(5, collection.getElements().size());
		assertEquals(10, collection.getTotal());		
	}
	
	@Test
	public void givenDatabaseHasTenCloudProcessesAndCounterHasTheTotal_WhenWeAskForTheFirstFiveAndTheTotal_ThenTheResultShouldIncludeTheFiveCloudProcessesAndTheTotalAsTen() {
		CloudProcessManager manager = new CloudProcessManager();
		
		Account account = new DatabaseTestUtils().createValidAccount(AccountResource.dao, user);
		int totalInDatabase = 10;
		initializeCounter(account, user, totalInDatabase);		
		try {			
			populateDatabaseWithRandomProcessesNoAction(manager, totalInDatabase, user);
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		
		//Get first 5 results
		Collection<CloudProcess> collection = manager.getCollection(0, 5, true);
		
		assertEquals(5, collection.getElements().size());
		assertEquals(totalInDatabase, collection.getTotal());		
	}
	
	@Test
	public void givenDatabaseHasNoCloudProcess_WhenWeAskForTheFirstFiveAndTheTotal_ThenTheResultShouldBeEmptyAndTheTotalZero() {
		CloudProcessManager manager = new CloudProcessManager();
		
		//Get first 5 results
		Collection<CloudProcess> collection = manager.getCollection(0, 5, true);
		
		assertEquals(0, collection.getElements().size());
		assertEquals(0, collection.getTotal());		
	}
	
	@Test
	public void givenUserHasNoCloudProcess_WhenWeAskForTheFirstFiveAndTheTotal_ThenTheResultShouldBeEmptyAndTheTotalZero() {
		CloudProcessManager manager = new CloudProcessManager();

		//Get first 5 results
		Collection<CloudProcess> collection = manager.getCollection(0, 5, user.getUri(), true);
		
		assertEquals(0, collection.getElements().size());
		assertEquals(0, collection.getTotal());		
	}
	
	@Test
	public void GetCollectionDoNotExecuteCountTest() {
		CloudProcessManager manager = new CloudProcessManager();

		int totalInDatabase = 10;
		try {
			populateDatabaseWithRandomProcessesNoAction(manager, totalInDatabase,user);
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		
		//Get first 5 results
		Collection<CloudProcess> collection = manager.getCollection(0, 5);
		
		assertEquals(5, collection.getElements().size());
		assertEquals(5, collection.getTotal());
	}
	
	@Test
	public void GetCollectionWithOwnerExecuteCountTest() throws URISyntaxException {
		CloudProcessManager manager = new CloudProcessManager();

		Account account = new DatabaseTestUtils().createValidAccount(AccountResource.dao, user);
		int totalInDatabase = 10;
		initializeCounter(account, user, totalInDatabase);		
		try {
			populateDatabaseWithRandomProcessesNoAction(manager, totalInDatabase, user);
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		
		//Get first 5 results
		Collection<CloudProcess> collection = manager.getCollection(0, 5, user.getUri(), true);

		assertEquals(5, collection.getElements().size());
		assertEquals(totalInDatabase, collection.getTotal());			
	}
	
	@Test
	public void GetCollectionWithOwnerDoNotExecuteCountTest() throws URISyntaxException {
		CloudProcessManager manager = new CloudProcessManager();
		
		try {
			int totalInDatabase = 10;
			populateDatabaseWithRandomProcessesNoAction(manager, totalInDatabase, user);
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		
		//Get first 5 results
		Collection<CloudProcess> collection = manager.getCollection(0, 5, user.getUri());

		assertEquals(5, collection.getElements().size());
		assertEquals(5, collection.getTotal());
	}		
	
	@Test
	public void RetrieveFiveProcessesThatAreServiceStackActionsAndAreStillRunningTogetherWithOtherProcessesTest() throws URISyntaxException {
		CloudProcessManager processManager = new CloudProcessManager();
		
		int processCount = 5;
		//Add processes with stack service actions
		ActionManager actionManager = new ActionManager();
		List<Action> actions = createValidServiceStackActions(processCount);
		for(Action action: actions)
		{
			actionManager.add(action);
		}
		
		populateDatabaseWithRandomProcessAndTheseActions(processManager, user, actionManager, actions);
		
		//Add processes with job actions
		actions = createValidJobActions(processCount);
		for(Action action: actions)
		{
			actionManager.add(action);
		}		
		populateDatabaseWithRandomProcessAndTheseActions(processManager, user, actionManager, actions);
		
		Collection<CloudProcess> collection = processManager.getServiceStackCollectionNonFinalized(user.getUri().toString());

		Collection<StackServiceAction> stackServiceActionsCollection = actionManager.getStackServiceAction();
		List<StackServiceAction> stackServiceActions = stackServiceActionsCollection.getElements();
		assertEquals(processCount, stackServiceActions.size());
		Collection<CloudProcess> processes = processManager.getCollection(0, processCount*3);
		assertEquals(processCount*2, processes.getElements().size());
		List<CloudProcess> serviceActionsProcesses = collection.getElements();
		assertEquals(processCount + " elements should be found", processCount, serviceActionsProcesses.size());
	}
	
	@Test
	public void RetrieveFiveProcessesThatAreServiceStackActionsAndAreStillRunningWithNoOtherProcessTest() throws URISyntaxException {
		CloudProcessManager processManager = new CloudProcessManager();
		
		int processCount = 5;
		//Add processes with stack service actions
		ActionManager actionManager = new ActionManager();
		List<Action> actions = createValidServiceStackActions(processCount);
		for(Action action: actions)
		{
			actionManager.add(action);
		}
		
		populateDatabaseWithRandomProcessAndTheseActions(processManager, user, actionManager, actions);
		
		Collection<CloudProcess> collection = processManager.getServiceStackCollectionNonFinalized(user.getUri().toString());

		Collection<StackServiceAction> stackServiceActionsCollection = actionManager.getStackServiceAction();
		List<StackServiceAction> stackServiceActions = stackServiceActionsCollection.getElements();
		assertEquals(processCount, stackServiceActions.size());
		Collection<CloudProcess> processes = processManager.getCollection(0, processCount*3);
		assertEquals(processCount, processes.getElements().size());
		List<CloudProcess> serviceActionsProcesses = collection.getElements();
		assertEquals(processCount + " elements should be found", processCount, serviceActionsProcesses.size());
	}
	
	@Test
	public void RetrieveFiveProcessesThatAreServiceStackActionsAndAreStillRunningFromTheUser() throws URISyntaxException {
		CloudProcessManager processManager = new CloudProcessManager();

		processManager.setSleepTime(1);
		
		int processCount = 5;
		//Add processes with stack service actions
		ActionManager actionManager = new ActionManager();
		List<Action> actions = createValidServiceStackActions(processCount);
		for(Action action: actions)
		{
			actionManager.add(action);
		}
		
		populateDatabaseWithRandomProcessAndTheseActionsTwoAccounts(processManager, user.getUri().toString(), "http://localhost/user/auser", actionManager, actions);
		
		Collection<CloudProcess> collection = processManager.getServiceStackCollectionNonFinalized(user.getUri().toString());
		
		List<CloudProcess> serviceActionsProcesses = collection.getElements();
		assertEquals(processCount + " elements should be found", processCount, serviceActionsProcesses.size());
	}
	
	@Test
	public void RetrieveTwoProcessesThatAreServiceStackActionsAndAreStillRunningWithOtherTwoNotRunningTest() throws URISyntaxException {
		CloudProcessManager processManager = new CloudProcessManager();

		processManager.setSleepTime(1);
		
		int processCount = 4;
		//Add processes with stack service actions
		ActionManager actionManager = new ActionManager();
		List<Action> actions = createValidServiceStackActions(processCount);
		for(Action action: actions)
		{
			actionManager.add(action);
		}
		
		List<CloudProcess> processes = populateDatabaseWithRandomProcessAndTheseActions(processManager, user, actionManager, actions);
		processes.get(0).setFinalized(true);
		processes.get(1).setFinalized(true);
		processManager.update(processes.get(0));
		processManager.update(processes.get(1));
		
		Collection<CloudProcess> collection = processManager.getServiceStackCollectionNonFinalized(user.getUri().toString());
		
		assertEquals(2, collection.getElements().size());		
	}
	
	@Test
	public void RetrieveEmptyListOfProcessesThatAreServiceStackActionsAndAreStillRunningTest() throws URISyntaxException {
		CloudProcessManager processManager = new CloudProcessManager();

		processManager.setSleepTime(1);
		int processCount = 5;

		ActionManager actionManager = new ActionManager();
		//Add processes with job actions		
		List<Action> actions = createValidJobActions(processCount);
		for(Action action: actions)
		{
			actionManager.add(action);
		}		
		populateDatabaseWithRandomProcessAndTheseActions(processManager, user, actionManager, actions);
		
		Collection<CloudProcess> collection = processManager.getServiceStackCollectionNonFinalized(user.getUri().toString());

		Collection<StackServiceAction> stackServiceActionsCollection = actionManager.getStackServiceAction();
		List<StackServiceAction> stackServiceActions = stackServiceActionsCollection.getElements();
		assertEquals(0, stackServiceActions.size());
		Collection<CloudProcess> processes = processManager.getCollection(0, processCount*3);
		assertEquals(processCount, processes.getElements().size());
		List<CloudProcess> serviceActionsProcesses = collection.getElements();
		assertEquals("No element should be returned", 0, serviceActionsProcesses.size());
	}
	
	@Test
	public void WhenOneCloudProcessTopLevelIsAddedToDatabaseThenAChangeToTheCollectionPathIsAddedToChangeManager() throws URISyntaxException, InterruptedException {
		CloudProcessManager processManager = new CloudProcessManager();

		processManager.setSleepTime(1);
		String owner = "http://localhost/user/1";
		CloudProcess process = buildValidCloudProcess(owner);
		process.setTopLevel(true);

		ChangeManager cacheManager = ChangeManager.factory();				
		Long stamp = cacheManager.initCache();
		processManager.add(process);		

		QueueTestHelper.runAllTasksOnQueue();
		
		ArrayList<Change> changes = cacheManager.getChanges(stamp, new URI(owner), true).getChange();
		assertEquals(1, changes.size() );
		assertEquals(processManager.path.toString(), changes.get(0).getUri().toString() );		
	}
	
	@Test
	public void WhenOneCloudProcessTopLevelIsUpdatedThenAChangeToTheProcessIsAddedToChangeManager() throws URISyntaxException, InterruptedException {
		CloudProcessManager processManager = new CloudProcessManager();

		processManager.setSleepTime(1);
		String owner = "http://localhost/user/1";
		CloudProcess process = buildValidCloudProcess(owner);
		process.setTopLevel(true);

		ChangeManager cacheManager = ChangeManager.factory();		
		Long stamp = cacheManager.initCache();
		processManager.add(process);
		
		QueueTestHelper.runAllTasksOnQueue();

		processManager.update(process);
		
		//Ask for stamp +1 so ignores change made by the add call
				
		ArrayList<Change> changes = cacheManager.getChanges(stamp+1, new URI(owner), true).getChange();
		assertEquals(1, changes.size() );
		assertEquals( process.getUri().toString(), changes.get(0).getUri().toString() );		
	}
	
	@Test
	public void WhenOneCloudProcessTopLevelIsDeletedThenAChangeToTheCollectionAndAChangeToTheProcessAreAddedToChangeManager() throws URISyntaxException, InterruptedException {
		CloudProcessManager processManager = new CloudProcessManager();

		processManager.setSleepTime(1);
		String owner = "http://localhost/user/1";
		CloudProcess process = buildValidCloudProcess(owner);
		process.setTopLevel(true);

		ChangeManager cacheManager = ChangeManager.factory();		
		Long stamp = cacheManager.initCache();

		processManager.add(process);
		QueueTestHelper.runAllTasksOnQueue();
		processManager.delete(process);
		
		//Ask for stamp +1 so ignores change made by the add call
		
		ArrayList<Change> changes = cacheManager.getChanges(stamp+1, new URI(owner), true).getChange();
		assertEquals(2, changes.size() );
		
		boolean collectionIsOnChange = false;
		boolean processIsOnChange = false;
		
		for(int i=0; i<changes.size(); i++)
		{
				if(processManager.path.equals(changes.get(i).getUri()) ) collectionIsOnChange = true;
				if(process.getUri().equals(changes.get(i).getUri())) processIsOnChange = true;
		}
		
		assertTrue( collectionIsOnChange);
		assertTrue( processIsOnChange );	
	}
	
	@Test
	public void WhenOneCloudProcessNonTopLevelIsAddedToDatabaseThenAChangeToItsParentIsAddedToChangeManager() throws URISyntaxException, InterruptedException {
		CloudProcessManager processManager = new CloudProcessManager();

		processManager.setSleepTime(1);
		String owner = "http://localhost/user/1";
		String parenUri = "http://localhost/process/2";
		CloudProcess parent = buildValidCloudProcess(owner);
		parent.setUri(new URI(parenUri));
		CloudProcess process = buildValidCloudProcess(owner);
		process.setTopLevel(false);
		
		process.setParent(parent.getUri());

		ChangeManager cacheManager = ChangeManager.factory();				
		Long stamp = cacheManager.initCache();

		processManager.add(process);

		QueueTestHelper.runAllTasksOnQueue();
		ArrayList<Change> changes = cacheManager.getChanges(stamp, new URI(owner), true).getChange();
		assertEquals(1, changes.size() );
		assertEquals( parenUri, changes.get(0).getUri().toString() );		
	}
	
	@Test
	public void WhenOneCloudProcessNonTopLevelIsUpdatedThenAChangeToItsParentIsAddedToChangeManager() throws URISyntaxException, InterruptedException {
		CloudProcessManager processManager = new CloudProcessManager();

		processManager.setSleepTime(1);
		String owner = "http://localhost/user/1";
		String parenUri = "http://localhost/process/2";
		CloudProcess parent = buildValidCloudProcess(owner);
		parent.setUri(new URI(parenUri));
		CloudProcess process = buildValidCloudProcess(owner);
		process.setTopLevel(false);
		process.setParent(parent.getUri());

		ChangeManager cacheManager = ChangeManager.factory();		
		Long stamp = cacheManager.initCache();
		
		processManager.add(process);
		QueueTestHelper.runAllTasksOnQueue();

		processManager.update(process);
		
		//Ask for stamp +1 so ignores change made by the add call
		ArrayList<Change> changes = cacheManager.getChanges(stamp+1, new URI(owner), true).getChange();
		assertEquals(1, changes.size() );
		assertEquals( parenUri, changes.get(0).getUri().toString() );		
	}
	
	@Test
	public void WhenOneCloudProcessNonTopLevelIsDeletedThenAChangeToItsParentAndAChangeToTheProcessAreAddedToChangeManager() throws URISyntaxException, InterruptedException {
		CloudProcessManager processManager = new CloudProcessManager();

		processManager.setSleepTime(1);
		String owner = "http://localhost/user/1";
		String parenUri = "http://localhost/process/2";
		CloudProcess parent = buildValidCloudProcess(owner);
		parent.setUri(new URI(parenUri));
		CloudProcess process = buildValidCloudProcess(owner);
		process.setTopLevel(false);
		process.setParent(parent.getUri());

		ChangeManager cacheManager = ChangeManager.factory();		
		Long stamp = cacheManager.initCache();

		processManager.add(process);
		QueueTestHelper.runAllTasksOnQueue();

		processManager.delete(process);
		
		//Ask for stamp +1 so ignores change made by the add call
		ArrayList<Change> changes = cacheManager.getChanges(stamp+1, new URI(owner), true).getChange();
		assertEquals(2, changes.size() );
		
		boolean parentIsOnChange = false;
		boolean processIsOnChange = false;
		
		for(int i=0; i<changes.size(); i++)
		{
				if(process.getParent().equals(changes.get(i).getUri()) ) parentIsOnChange = true;
				if(process.getUri().equals(changes.get(i).getUri())) processIsOnChange = true;
		}
		
		assertTrue( parentIsOnChange);
		assertTrue( processIsOnChange );	
	}
	
	@Test
	public void add_AddedCloudProcessWith1SecondDelay() throws URISyntaxException, InterruptedException {
		CloudProcessManager processManager = new CloudProcessManager();
				
		String owner = "http://localhost/user/1";
		CloudProcess process = buildValidCloudProcess(owner);
		process.setTopLevel(true);

		long start = System.currentTimeMillis();
		
		ChangeManager cacheManager = ChangeManager.factory();				
		Long stamp = cacheManager.initCache();
		processManager.setSleepTime(1000);
		processManager.add(process);	
		QueueTestHelper.runAllTasksOnQueue();
		
		long end = System.currentTimeMillis();
		
		ArrayList<Change> changes = cacheManager.getChanges(stamp, new URI(owner), true).getChange();
		assertEquals(1, changes.size() );
		assertTrue((end-start)>1000);
	}
	
	
}
