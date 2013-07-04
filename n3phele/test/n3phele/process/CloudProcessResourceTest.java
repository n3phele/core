package n3phele.process;

import java.net.URISyntaxException;
import java.util.List;

import junit.framework.Assert;

import n3phele.service.model.Action;
import n3phele.service.model.CloudProcessCollection;
import n3phele.service.rest.impl.CloudProcessResource;
import n3phele.service.rest.impl.ActionResource.ActionManager;
import n3phele.service.rest.impl.CloudProcessResource.CloudProcessManager;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

public class CloudProcessResourceTest extends DatabaseTestUtils {

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
	public void RetrieveListOfFiveProcessesThatAreStackServiceActionsTest() throws URISyntaxException {
		
		CloudProcessManager processManager = new CloudProcessManager();
		
		int processCount = 5;
		//Add processes with stack service actions
		ActionManager actionManager = new ActionManager();
		List<Action> actions = createValidServiceStackActions(processCount);
		for(Action action: actions)
		{
			actionManager.add(action);
		}
		
		populateDatabaseWithRandomProcessAndTheseActions(processManager, actionManager, actions);
		
		CloudProcessResource resource = new CloudProcessResource();
		
		CloudProcessCollection processes = resource.getStackServiceActionProcessesRunning();
		
		Assert.assertEquals(5, processes.getElements().size());
		
	}

}
