package n3phele;

import static org.junit.Assert.fail;

import java.net.URI;
import java.util.Date;
import java.util.List;

import n3phele.service.core.ForbiddenException;
import n3phele.service.core.NotFoundException;
import n3phele.service.model.core.Credential;
import n3phele.service.model.repository.FileNode;
import n3phele.service.model.repository.Repository;
import n3phele.storage.CloudStorage;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class SwiftTest {
	public SwiftTest() throws Exception {

	}
	
	private final LocalServiceTestHelper helper =   new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig()
				.setApplyAllHighRepJobPolicy()) ;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
		helper.setUp();
	}
	
	@Test
	public void testBadCredential() {
		Credential credential = new Credential("accessid:nigel.cook@hp.com", "secret").encrypt();
		Repository repo = new Repository("test", "test repo", credential, URI.create("https://region-a.geo-1.identity.hpcloudsvc.com:35357/v2.0/"), "n3phele-agent", "swift", null, false);
		List<FileNode> result;
		try {
			result = CloudStorage.factory().getFileList(repo, null, 10);
			fail("Exception expected");
		} catch (ForbiddenException e) {
			return;
		} catch(Exception e) {
			fail("Unexpected exception");
		}
	}
	
	@Test
	public void testBadContainer() {
		Date start = new Date();
		Credential credential = new Credential("accessid:nigel.cook@hp.com", "secret").encrypt();
		Repository repo = new Repository("test", "test repo", credential, URI.create("https://region-a.geo-1.identity.hpcloudsvc.com:35357/v2.0/"), "badContainer", "swift", null, false);
		try {
			CloudStorage.factory().getFileList(repo, "", 10);
			fail("Exception expected");
		} catch (NotFoundException e) {
			return;
		} catch(Exception e) {
			fail("Unexpected exception");
		}
		List<FileNode> result = CloudStorage.factory().getFileList(repo, "", 10);
		Date end = new Date();
		System.out.println("Result "+result);
		System.out.println("Diff = "+(end.getTime()-start.getTime())+" ms");

	}
	
	@Test
	public void testList() {
		Credential credential = new Credential("accessid:nigel.cook@hp.com", "secret").encrypt();
		Repository repo = new Repository("test", "test repo", credential, URI.create("https://region-a.geo-1.identity.hpcloudsvc.com:35357/v2.0/"), "n3phele-agent", "swift", null, false);
		List<FileNode> result = CloudStorage.factory().getFileList(repo, null, 10);
		for(FileNode n : result) {
			System.out.println(n.toString());
		}

	}
	
	 //@Test
	  public void testCloudDelete() throws Exception {

		//Cloud cloud = webResource.path("byName").queryParam("id","EC2").accept(MediaType.APPLICATION_JSON_TYPE).get(Cloud.class);
	  	//ClientResponse response = webResource.uri(cloud.getUri()).delete(ClientResponse.class);

	  	//Assert.assertEquals(204, response.getStatus()); 
	  	 
	  }
}