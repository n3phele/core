package n3phele.process;

import static org.junit.Assert.*;
import n3phele.service.dao.ProcessCounterManager;
import n3phele.service.model.Account;
import n3phele.service.model.ProcessCounter;
import n3phele.service.rest.impl.AccountResource;
import n3phele.service.rest.impl.UserResource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

public class ProcessCounterManagerTest {

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
	public void tearDown() {         
		helper.tearDown();   
	} 
	
	public Account createAnAccount()
	{
		return new DatabaseTestUtils().createValidAccount(AccountResource.dao);
	}

	@Test
	public void GivenAValidProcessCounterWhenItIsPersistedThenIsCanBeRetrieved() {
		ProcessCounter counter = new ProcessCounter(createAnAccount().getUri().toString());
		counter.setOwner(UserResource.Root.getUri());
		ProcessCounterManager manager = new ProcessCounterManager();
		
		manager.add(counter);
		ProcessCounter counter2 = manager.load(counter.getUri());
		
		assertEquals(counter.toString(), counter2.toString());
		assertEquals(counter.getCount(), counter2.getCount());
	}
	
	@Test
	public void GivenAValidProcessCounterAndThatItWasIncrementedWhenItIsPersistedThenIsCanBeRetrieved() {
		ProcessCounter counter = new ProcessCounter(createAnAccount().getUri().toString());
		counter.setOwner(UserResource.Root.getUri());
		counter.increment();
		
		ProcessCounterManager manager = new ProcessCounterManager();
		
		manager.add(counter);
		ProcessCounter counter2 = manager.load(counter.getUri());
		
		assertEquals(counter.toString(), counter2.toString());
		assertEquals(counter.getCount(), counter2.getCount());
	}
	
	@Test
	public void GivenAValidProcessCounterAlreadyWasPersistedWhenIsSearchedByOwnerThenItShouldBeReturned() {
		ProcessCounter counter = new ProcessCounter(createAnAccount().getUri().toString());
		counter.setOwner(UserResource.Root.getUri());
		
		ProcessCounterManager manager = new ProcessCounterManager();
		
		manager.add(counter);
		ProcessCounter counter2 = manager.load(UserResource.Root);

		assertEquals(counter.getUri().toString(), counter2.getUri().toString());
		assertEquals(counter.toString(), counter2.toString());
		assertEquals(counter.getCount(), counter2.getCount());
	}

}
