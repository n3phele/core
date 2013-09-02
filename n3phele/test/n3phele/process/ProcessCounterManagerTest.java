package n3phele.process;

import static org.junit.Assert.*;
import n3phele.service.dao.ProcessCounterManager;
import n3phele.service.model.Account;
import n3phele.service.model.ProcessCounter;
import n3phele.service.model.core.User;
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
	
	public Account createAnAccount(User user)
	{
		return new DatabaseTestUtils().createValidAccount(AccountResource.dao,user);
	}

	@Test
	public void GivenAValidProcessCounterWhenItIsPersistedThenItCanBeRetrieved() {
		User user = UserResource.Root;
		ProcessCounter counter = new ProcessCounter();
		counter.setOwner(user.getUri());
		ProcessCounterManager manager = new ProcessCounterManager();
		
		manager.add(counter);
		ProcessCounter counter2 = manager.load(counter.getUri());
		
		assertEquals(counter.toString(), counter2.toString());
		assertEquals(counter.getCount(), counter2.getCount());
	}
	
	@Test
	public void GivenAValidProcessCounterAndThatItWasIncrementedWhenItIsPersistedThenItCanBeRetrieved() {
		User user = UserResource.Root;
		ProcessCounter counter = new ProcessCounter();
		counter.setOwner(user.getUri());
		counter.increment();
		
		ProcessCounterManager manager = new ProcessCounterManager();
		
		manager.add(counter);
		ProcessCounter counter2 = manager.load(counter.getUri());
		
		assertEquals(counter.toString(), counter2.toString());
		assertEquals(counter.getCount(), counter2.getCount());
	}
	
	@Test
	public void GivenAValidProcessCounterAlreadyWasPersistedWhenIsSearchedByOwnerThenItShouldBeReturned() {
		User user = UserResource.Root;
		ProcessCounter counter = new ProcessCounter();
		counter.setOwner(user.getUri());
		
		ProcessCounterManager manager = new ProcessCounterManager();
		
		manager.add(counter);
		ProcessCounter counter2 = manager.loadByUser(user.getUri());

		assertEquals(counter.getUri().toString(), counter2.getUri().toString());
		assertEquals(counter.toString(), counter2.toString());
		assertEquals(counter.getCount(), counter2.getCount());
	}

}
