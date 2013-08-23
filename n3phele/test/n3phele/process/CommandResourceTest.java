package n3phele.process;

import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import n3phele.service.actions.StackServiceAction;
import n3phele.service.core.NotFoundException;
import n3phele.service.model.Account;
import n3phele.service.model.AccountData;
import n3phele.service.model.ActivityData;
import n3phele.service.model.ActivityDataCollection;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.Command;
import n3phele.service.model.CommandCloudAccount;
import n3phele.service.model.CommandImplementationDefinition;
import n3phele.service.model.Context;
import n3phele.service.model.CostsCollection;
import n3phele.service.model.Variable;
import n3phele.service.model.core.Collection;
import n3phele.service.model.core.User;
import n3phele.service.rest.impl.AccountResource;
import n3phele.service.rest.impl.ActionResource;
import n3phele.service.rest.impl.CloudProcessResource.CloudProcessManager;
import n3phele.service.rest.impl.CommandResource;
import n3phele.service.rest.impl.UserResource;
import n3phele.service.rest.impl.AccountResource.AccountManager;
import n3phele.service.rest.impl.CommandResource.CommandManager;
import n3phele.service.rest.impl.CloudProcessResource;
import n3phele.time.MutableTimeFactory;

import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

public class CommandResourceTest {
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig().setApplyAllHighRepJobPolicy(), new LocalTaskQueueTestConfig().setDisableAutoTaskExecution(false).setCallbackClass(LocalTaskQueueTestConfig.DeferredTaskCallback.class));

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
	public void tearDown() throws IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchFieldException {
		helper.tearDown();

		//field.set(null, new AccountManager());
	}
	
	@Test
	public void getCommandWithServiceListWith1Implementation() throws NotFoundException, URISyntaxException{
		User root = UserResource.Root;
		assertNotNull(root);
		Command command = new Command();
		CommandImplementationDefinition cid1 = createCommandImplementationDefinitionWithName("HPZone1");
		CommandImplementationDefinition cid2 = createCommandImplementationDefinitionWithName("EC2");
		List<CommandImplementationDefinition> cidList = new  ArrayList<CommandImplementationDefinition>();
		cidList.add(cid1);
		command.setImplementations(cidList);
		CloudProcess cp1 = createCPwithActionAndAccount("HPZone1");
		CloudProcess cp2 = createCPwithActionAndAccount("EC2");
		CloudProcess cp3 = createCPwithActionAndAccount("HPZone1");
		ArrayList<CloudProcess> cplist = new ArrayList<CloudProcess>();
		cplist.add(cp1);
		cplist.add(cp2);
		cplist.add(cp3);
		Collection<CloudProcess> col = new Collection<CloudProcess>("col", null, cplist);
		command.initServiceList(root);
		System.out.println(command.initServiceList(root).toString());
		List<CommandCloudAccount> ccalist = command.initServiceList(root).getServiceList();
		Assert.assertEquals(2,ccalist.size());
	}
	@Test
	public void getCommandWithServiceListWith2Implementatios() throws NotFoundException, URISyntaxException{
		User root = UserResource.Root;
		assertNotNull(root);
		Command command = new Command();
		CommandImplementationDefinition cid1 = createCommandImplementationDefinitionWithName("HPZone1");
		CommandImplementationDefinition cid2 = createCommandImplementationDefinitionWithName("EC2");
		List<CommandImplementationDefinition> cidList = new  ArrayList<CommandImplementationDefinition>();
		cidList.add(cid1);
		cidList.add(cid2);
		command.setImplementations(cidList);
		CloudProcess cp1 = createCPwithActionAndAccount("HPZone1");
		CloudProcess cp2 = createCPwithActionAndAccount("EC2");
		CloudProcess cp3 = createCPwithActionAndAccount("HPZone1");
		ArrayList<CloudProcess> cplist = new ArrayList<CloudProcess>();
		cplist.add(cp1);
		cplist.add(cp2);
		cplist.add(cp3);
		Collection<CloudProcess> col = new Collection<CloudProcess>("col", null, cplist);
		command.initServiceList(root);
		System.out.println(command.initServiceList(root).toString());
		List<CommandCloudAccount> ccalist = command.initServiceList(root).getServiceList();
		Assert.assertEquals(3,ccalist.size());
	}
	
	@Test
	public void getCommandWithServiceListWith1ImplementationButNoResult() throws NotFoundException, URISyntaxException{
		User root = UserResource.Root;
		assertNotNull(root);
		Command command = new Command();
		CommandImplementationDefinition cid1 = createCommandImplementationDefinitionWithName("HPZone1");
		CommandImplementationDefinition cid2 = createCommandImplementationDefinitionWithName("EC2");
		List<CommandImplementationDefinition> cidList = new  ArrayList<CommandImplementationDefinition>();
		cidList.add(cid1);
		cidList.add(cid2);
		command.setImplementations(cidList);
		CloudProcess cp1 = createCPwithActionAndAccount("HPZone3");
		CloudProcess cp2 = createCPwithActionAndAccount("EC3");
		CloudProcess cp3 = createCPwithActionAndAccount("HPZone3");
		ArrayList<CloudProcess> cplist = new ArrayList<CloudProcess>();
		cplist.add(cp1);
		cplist.add(cp2);
		cplist.add(cp3);
		Collection<CloudProcess> col = new Collection<CloudProcess>("col", null, cplist);
		command.initServiceList(root);
		System.out.println(command.initServiceList(root).toString());
		List<CommandCloudAccount> ccalist = command.initServiceList(root).getServiceList();
		Assert.assertEquals(0,ccalist.size());
	}
	
	public CommandImplementationDefinition createCommandImplementationDefinitionWithName(String name){
		CommandImplementationDefinition cid = new CommandImplementationDefinition(name, "", "", 0);
		return cid;
	}
	
	public CloudProcess createCPwithActionAndAccount(String zoneName) throws URISyntaxException{
		Account acc = new Account("acc", "", null, zoneName, null,UserResource.Root.getUri(), true);
		AccountResource.dao.add(acc);
		Context con = new Context();
		Variable v = new Variable("account", acc.getUri().toString());
		con.put("account",v);
		StackServiceAction ssa = new StackServiceAction(UserResource.Root.toString(), "ssa", UserResource.Root,con);
		ActionResource.dao.add(ssa);
		CloudProcess cp = new CloudProcess(UserResource.Root.getUri(), "cp " + zoneName, null, true, ssa, true);
		cp.setAccount(acc.getUri().toString());
		CloudProcessResource.dao.add(cp);
		return cp;
	}

	static void setFinalStatic(Field field, Object newValue) throws Exception {
		field.setAccessible(true);

		// remove final modifier from field
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

		field.set(null, newValue);
	}

	

	
}
