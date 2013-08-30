package n3phele.process;

import static org.junit.Assert.*;
import junit.framework.Assert;
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

public class ProcessCounterTests {

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
		return new DatabaseTestUtils().createValidAccount(AccountResource.dao, user);
	}

	@Test
	public void WhenAProcessCounterIsCreatedThenItsCountIsStartedAsZero() {
		User user = UserResource.Root;
		ProcessCounter counter = new ProcessCounter(createAnAccount(user).getUri().toString());
		
		assertEquals(0, counter.getCount() );
	}
	
	@Test( expected = IllegalArgumentException.class )
	public void WhenACounterIsCreatedWithANullAccountThenAnExceptionIsThrow() {
		ProcessCounter counter = new ProcessCounter(null);		
	}
	
	@Test
	public void GivenAValidCounterWhenItIsIncrementedThenTheCountValueShouldBeUpdatedByOne() {
		User user = UserResource.Root;
		ProcessCounter counter = new ProcessCounter(createAnAccount(user).getUri().toString());
		assertEquals(0, counter.getCount() );
		counter.increment();
		assertEquals(1, counter.getCount() );
	}

}
