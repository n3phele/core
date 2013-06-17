package n3phele.process;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.Assert;

import n3phele.service.model.CloudProcess;
import n3phele.service.model.core.Collection;
import n3phele.service.rest.impl.CloudProcessResource.CloudProcessManager;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

public class CloudProcessManagerTest {

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
			PopulateDatabaseWithRandomProcesses(manager, totalInDatabase);
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
			PopulateDatabaseWithRandomProcesses(manager, totalInDatabase);
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
			PopulateDatabaseWithRandomProcesses(manager, totalInDatabase, owner);
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
			PopulateDatabaseWithRandomProcesses(manager, totalInDatabase, owner);
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		
		//Get first 5 results
		Collection<CloudProcess> collection = manager.getCollection(0, 5, new URI(owner));

		Assert.assertEquals(5, collection.getElements().size());
		Assert.assertEquals(5, collection.getTotal());
	}
	
	public void PopulateDatabaseWithRandomProcesses(CloudProcessManager manager, int count, String owner) throws URISyntaxException
	{
		for(int i=1; i <= count; i++)
		{
			CloudProcess process = buildValidCloudProcess(owner);
			process.setId((long)i);
			process.setTopLevel(true);
			manager.add(process);
		}
	}
	
	public void PopulateDatabaseWithRandomProcesses(CloudProcessManager manager, int count) throws URISyntaxException
	{
		for(int i=1; i <= count; i++)
		{
			CloudProcess process = buildValidCloudProcess();
			process.setId((long)i);
			process.setTopLevel(true);
			manager.add(process);
		}
	}
	
	public CloudProcess buildValidCloudProcess(String ownerUri) throws URISyntaxException
	{
		CloudProcess process = new CloudProcess();		
		process.setAccount("http://127.0.0.1/account/1");
		process.setCostPerHour(1.0d);
		process.setOwner(new URI(ownerUri));
		return process;	
	}
	
	public CloudProcess buildValidCloudProcess() throws URISyntaxException
	{
		return buildValidCloudProcess("http://127.0.0.1/account/1");
	}
}
