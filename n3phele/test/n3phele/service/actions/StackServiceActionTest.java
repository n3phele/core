package n3phele.service.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import junit.framework.Assert;

import n3phele.service.model.Context;
import n3phele.service.model.Relationship;
import n3phele.service.model.Stack;
import n3phele.service.model.core.User;
import n3phele.service.rest.impl.ActionResource;
import n3phele.service.rest.impl.UserResource;


import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;


public class StackServiceActionTest {
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
	public void tearDown() {
		helper.tearDown();
	}


	@Test
	public void addStackServiceAction() {
		User root = UserResource.Root;
		assertNotNull(root);
		Context c = new Context();
		StackServiceAction sAction = new StackServiceAction("adding Test", "stackServiceAction", root,c);
		ActionResource.dao.add(sAction);
		StackServiceAction load = (StackServiceAction) ActionResource.dao.load(sAction.getUri());
		Assert.assertEquals(sAction.getDescription(), load.getDescription());
		Assert.assertEquals(sAction.getName(), load.getName());
		Assert.assertEquals(sAction.getOwner(), load.getOwner());
	}
	
	/*
	 * A simple test to add a Stack in the StackServiceAction
	 */
	@Test
	public void addStack() {
		User root = UserResource.Root;
		assertNotNull(root);
		Context c = new Context();
		StackServiceAction sAction = new StackServiceAction("adding Test", "stackServiceAction", root,c);
		Stack stack = new Stack("name","a description");		
		ActionResource.dao.add(sAction);
		StackServiceAction load = (StackServiceAction) ActionResource.dao.load(sAction.getUri());
		load.addStack(stack);
		ActionResource.dao.update(load);
		StackServiceAction updated = (StackServiceAction) ActionResource.dao.load(sAction.getUri());
		Assert.assertEquals(sAction.getDescription(), updated.getDescription());
		Assert.assertEquals(sAction.getName(), updated.getName());
		Assert.assertEquals(sAction.getOwner(), updated.getOwner());
		Assert.assertEquals(0, updated.getStacks().get(0).getId());
		Assert.assertEquals(stack.getName(), updated.getStacks().get(0).getName());
		Assert.assertEquals(stack.getDescription(), updated.getStacks().get(0).getDescription());
	}
	@Test
	public void addRelationship() {
		User root = UserResource.Root;
		assertNotNull(root);
		Context c = new Context();
		StackServiceAction sAction = new StackServiceAction("adding Test", "stackServiceAction", root,c);
		Stack stack = new Stack("name","a description");	
		Stack stack2 = new Stack("name2","a description2");	
		ActionResource.dao.add(sAction);
		StackServiceAction load = (StackServiceAction) ActionResource.dao.load(sAction.getUri());
		load.addStack(stack);
		load.addStack(stack2);
		ActionResource.dao.update(load);
		StackServiceAction updated = (StackServiceAction) ActionResource.dao.load(sAction.getUri());
		
		Relationship relation = new Relationship(updated.getStacks().get(0).getId(),
				updated.getStacks().get(1).getId()
				, "dataBase", "a wordPress and a db");
		updated.addRelationhip(relation);
		updated = (StackServiceAction) ActionResource.dao.load(updated.getUri());
		
		Assert.assertEquals(sAction.getDescription(), updated.getDescription());
		Assert.assertEquals(sAction.getName(), updated.getName());
		Assert.assertEquals(sAction.getOwner(), updated.getOwner());
		Assert.assertEquals(0, updated.getStacks().get(0).getId());
		Assert.assertEquals(stack.getName(), updated.getStacks().get(0).getName());
		Assert.assertEquals(stack.getDescription(), updated.getStacks().get(0).getDescription());
		Assert.assertEquals(0, updated.getRelationships().get(0).getIdStackMaster());
		Assert.assertEquals(1, updated.getRelationships().get(0).getidStackSubordinate());
		Assert.assertEquals("dataBase", updated.getRelationships().get(0).getType());
		Assert.assertEquals("a wordPress and a db", updated.getRelationships().get(0).getDescription());
	}
}
