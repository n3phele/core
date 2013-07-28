package n3phele.process;

import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import n3phele.service.model.Account;
import n3phele.service.model.AccountData;
import n3phele.service.model.ActivityData;
import n3phele.service.model.ActivityDataCollection;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.CostsCollection;
import n3phele.service.model.core.Collection;
import n3phele.service.model.core.User;
import n3phele.service.rest.impl.AccountResource;
import n3phele.service.rest.impl.UserResource;
import n3phele.service.rest.impl.AccountResource.AccountManager;
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

public class AccountResourceTest {
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
						
		Field field = AccountResource.class.getField("dao");
		field.setAccessible(true);

		// remove final modifier from field
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() | Modifier.FINAL);

		field.set(null, new AccountManager());
	}
	@Test
	public void accountDataValidation() throws Exception{
		User root = UserResource.Root;
		assertNotNull(root);
		AccountResource accr = PowerMockito.spy(new AccountResource());
		AccountManager accm = PowerMockito.mock(AccountManager.class);
		PowerMockito.mockStatic(AccountManager.class);
		try {
			setFinalStatic(AccountResource.class.getField("dao"), accm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Account acc1 = new Account("HP1", null, new URI("uri"), "cloud1", null, null, false);
		Account acc2 = new Account("HP2", null, new URI("uriHP2"), "cloud2", null, null, false);
		acc1.setUri(new URI("uri"));
		acc2.setUri(new URI("uri2"));
		acc1.setId((long)1);
		acc2.setId((long)2);
		List<Account> listAcc = new ArrayList<Account>();
		listAcc.add(acc1);
		listAcc.add(acc2);
		Collection<Account>  colAcc= new Collection<Account>("test", null, listAcc);
		List<ActivityData> list  = new ArrayList<ActivityData>();
		ActivityData data1 = new ActivityData("jerry", "data1", "", "5", "", "");
		list.add(data1);
		ActivityDataCollection col = new ActivityDataCollection(list);
		List<ActivityData> list3 = new ArrayList<ActivityData>();
		
		ActivityDataCollection col2 = new ActivityDataCollection(list3);

		
		List<Double> list2 = new ArrayList<Double>();
		list2.add(1.5);
		CostsCollection col3 = new CostsCollection(list2);
		List<Double> listCost = new ArrayList<Double>();
		listCost.add(0.0);
		CostsCollection col4 = new CostsCollection(listCost);
		
		
		//PowerMockito.doReturn(col).when(accr.listRunningCloudProcessWithCostsActivityData("1"));		
		PowerMockito.when(accm.getAccountList(root, false)).thenReturn(colAcc);
		PowerMockito.doReturn(col).when(accr, "listRunningCloudProcessWithCostsActivityData", "1");
		PowerMockito.doReturn(col2).when(accr, "listRunningCloudProcessWithCostsActivityData", "2");
		PowerMockito.doReturn(col3).when(accr, "totalCost24Hour", "1");
		PowerMockito.doReturn(col4).when(accr, "totalCost24Hour", "2");		
		PowerMockito.doReturn(root).when(accr, "getUser");
		List<AccountData> data = accr.listAccountOnlyData(false).getElements();
		Assert.assertEquals("Wrong Value", "US$1.5", data.get(0).getCost());
		Assert.assertEquals("Wrong Name", "HP1", data.get(0).getAccountName());
		Assert.assertEquals("Wrong Cloud Name", "cloud1", data.get(0).getCloud());
		Assert.assertEquals("Wrong Size",2, data.size());
		Assert.assertEquals("Wrong Value", "US$0.0", data.get(1).getCost());
		Assert.assertEquals("Wrong Name", "HP2", data.get(1).getAccountName());
		Assert.assertEquals("Wrong Cloud Name", "cloud2", data.get(1).getCloud());
		
	}
	@Test
	public void listCost24Hour() {
		AccountResource accr = PowerMockito.spy(new AccountResource());
		AccountManager accm = PowerMockito.mock(AccountManager.class);
		PowerMockito.mockStatic(AccountManager.class);
		//DateTimeUtils.setCurrentMillisFixed(1000200000000L);
		
		MutableTimeFactory factory = mockTimeFactory();
		accr.setTimeFactory(factory);
		
		ArrayList<CloudProcess> list = new ArrayList<CloudProcess>();
		CloudProcess p = new CloudProcess();
		p.setAccount("acc");
		MutableDateTime c = createMutableTime(1000200000000L);
		p.setComplete(c.toDate());
		c.addHours(-10);
		c.addMinutes(-2);
		p.setEpoch(c.toDate());
		p.setStart(c.toDate());
		p.setCostPerHour(0.5);
		list.add(p);

		// second process
		p = new CloudProcess();
		p.setAccount("acc");
		c = createMutableTime(1000200000000L);
		c.addHours(-20);
		p.setComplete(c.toDate());
		c.addHours(-5);
		c.addMinutes(2);
		p.setEpoch(c.toDate());
		p.setStart(c.toDate());
		p.setCostPerHour(0.5);
		list.add(p);

		// third process
		p = new CloudProcess();
		p.setAccount("acc");
		c = createMutableTime(1000200000000L);
		c.addHours(-1);
		c.addMinutes(2);
		p.setEpoch(c.toDate());
		p.setStart(c.toDate());
		p.setCostPerHour(0.5);
		list.add(p);
		Collection<CloudProcess> col = new Collection<CloudProcess>("acc", null, list);
		try {
			setFinalStatic(AccountResource.class.getField("dao"), accm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PowerMockito.when(accm.getAllProcessByDays("acc", 1)).thenReturn(col);
		CostsCollection costs = accr.listCostPerDays("acc", 1);

		List<Double> listfinal = costs.getElements();

		Assert.assertEquals("Wrong Value", 0.5, listfinal.get(0));
		Assert.assertEquals("Wrong Value", 0.5, listfinal.get(1));
		Assert.assertEquals("Wrong Value", 0.5, listfinal.get(2));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(3));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(4));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(5));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(6));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(7));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(8));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(9));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(10));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(11));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(12));
		Assert.assertEquals("Wrong Value", 0.5, listfinal.get(13));
		Assert.assertEquals("Wrong Value", 0.5, listfinal.get(14));
		Assert.assertEquals("Wrong Value", 0.5, listfinal.get(15));
		Assert.assertEquals("Wrong Value", 0.5, listfinal.get(16));
		Assert.assertEquals("Wrong Value", 0.5, listfinal.get(17));
		Assert.assertEquals("Wrong Value", 0.5, listfinal.get(18));
		Assert.assertEquals("Wrong Value", 0.5, listfinal.get(19));
		Assert.assertEquals("Wrong Value", 0.5, listfinal.get(20));
		Assert.assertEquals("Wrong Value", 0.5, listfinal.get(21));
		Assert.assertEquals("Wrong Value", 1.0, listfinal.get(22));
		Assert.assertEquals("Wrong Value", 0.5, listfinal.get(23));
	}

	protected MutableTimeFactory mockTimeFactory() {
		MutableTimeFactory factory = Mockito.spy(new MutableTimeFactory());
		Mockito.when(factory.createMutableDateTime()).thenReturn(createMutableTime(1000200000000L));
		factory.setTimeZone( DateTimeZone.forOffsetHours(-3));
//		Mockito.when(factory.createMutableDateTime(Mockito.anyLong())).thenReturn(createMutableTime(1000200000000L));
//		Mockito.when(factory.createMutableDateTime(Mockito.any(Date.class))).thenReturn(createMutableTime(1000200000000L));
//		Mockito.when(factory.createMutableDateTime(Mockito.any(DateTime.class))).thenReturn(createMutableTime(1000200000000L));
		return factory;
	}

	static void setFinalStatic(Field field, Object newValue) throws Exception {
		field.setAccessible(true);

		// remove final modifier from field
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

		field.set(null, newValue);
	}

	@Test
	public void listCost7days() {
		AccountResource accr = PowerMockito.spy(new AccountResource());
		AccountManager accm = PowerMockito.mock(AccountManager.class);
		PowerMockito.mockStatic(AccountManager.class);
		ArrayList<CloudProcess> list = new ArrayList<CloudProcess>();
		//DateTimeUtils.setCurrentMillisFixed(1000200000000L);

		MutableTimeFactory factory = mockTimeFactory();
		accr.setTimeFactory(factory);
		
		CloudProcess p = new CloudProcess();
		p.setAccount("acc");
		MutableDateTime c = createMutableTime(1000200000000L);
		c.addDays(-1);
		p.setComplete(c.toDate());
		c = createMutableTime(1000200000000L);
		c.addDays(-3);
		c.addMinutes(3);
		p.setEpoch(c.toDate());
		p.setStart(c.toDate());
		p.setCostPerHour(0.5);
		list.add(p);
		
		p = new CloudProcess();
		c = createMutableTime(1000200000000L);
		c.addDays(-5);
		p.setComplete(c.toDate());
		c = createMutableTime(1000200000000L);
		c.addDays(-8);
		c.addMinutes(1);
		p.setEpoch(c.toDate());
		p.setStart(c.toDate());
		p.setCostPerHour(0.5);
		list.add(p);
		
		p = new CloudProcess();
		c = createMutableTime(1000200000000L);
		c.addDays(-4);
		p.setComplete(c.toDate());
		c = createMutableTime(1000200000000L);
		c.addDays(-5);
		c.addMinutes(-1);
		p.setEpoch(c.toDate());
		p.setStart(c.toDate());
		p.setCostPerHour(0.5);
		list.add(p);

		Collection<CloudProcess> col = new Collection<CloudProcess>("acc", null, list);
		try {
			setFinalStatic(AccountResource.class.getField("dao"), accm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PowerMockito.when(accm.getAllProcessByDays("acc", 7)).thenReturn(col);
		CostsCollection costs = accr.listCostPerDays("acc", 7);

		List<Double> listfinal = costs.getElements();

		Assert.assertEquals("Wrong Value", 12.0, listfinal.get(0));
		Assert.assertEquals("Wrong Value", 12.0, listfinal.get(1));
		Assert.assertEquals("Wrong Value", 3.5, listfinal.get(2));
		Assert.assertEquals("Wrong Value", 9.0, listfinal.get(3));
		Assert.assertEquals("Wrong Value", 12.0, listfinal.get(4));
		Assert.assertEquals("Wrong Value", 3.0, listfinal.get(5));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(6));
	}

	protected MutableDateTime createMutableTime(long time) {
		System.out.println("THIS WAS CALLED");
		MutableDateTime date = new MutableDateTime(time);
		date.setZone(DateTimeZone.forOffsetHours(-3));
		return date;
	}

	@Test
	public void listCost30days() {
		AccountResource accr = PowerMockito.spy(new AccountResource());
		AccountManager accm = PowerMockito.mock(AccountManager.class);
		PowerMockito.mockStatic(AccountManager.class);
		ArrayList<CloudProcess> list = new ArrayList<CloudProcess>();
		//DateTimeUtils.setCurrentMillisFixed(1000200000000L);

		MutableTimeFactory factory = mockTimeFactory();
		accr.setTimeFactory(factory);
		
		CloudProcess p = new CloudProcess();
		p.setAccount("acc");
		MutableDateTime c = createMutableTime(1000200000000L);
		c.addDays(-1);
		p.setComplete(c.toDate());
		c = createMutableTime(1000200000000L);
		c.addDays(-3);
		c.addMinutes(3);
		p.setEpoch(c.toDate());
		p.setStart(c.toDate());
		p.setCostPerHour(0.5);
		list.add(p);
		
		p = new CloudProcess();
		c = createMutableTime(1000200000000L);
		c.addDays(-5);
		p.setComplete(c.toDate());
		c = createMutableTime(1000200000000L);
		c.addDays(-8);
		c.addMinutes(1);
		p.setEpoch(c.toDate());
		p.setStart(c.toDate());
		p.setCostPerHour(0.5);
		list.add(p);
		
		p = new CloudProcess();
		c = createMutableTime(1000200000000L);
		c.addDays(-28);
		p.setComplete(c.toDate());
		c = createMutableTime(1000200000000L);
		c.addDays(-30);
		c.addMinutes(-1);
		p.setEpoch(c.toDate());
		p.setStart(c.toDate());
		p.setCostPerHour(0.5);
		list.add(p);
		
		p = new CloudProcess();
		c = createMutableTime(1000200000000L);
		c.addDays(-5);
		p.setComplete(c.toDate());
		c = createMutableTime(1000200000000L);
		c.addDays(-4);
		c.addMinutes(3);
		p.setEpoch(c.toDate());
		p.setStart(c.toDate());
		p.setCostPerHour(0.5);
		list.add(p);

		p = new CloudProcess();
		c = createMutableTime(1000200000000L);
		c.addDays(-10);
		p.setComplete(c.toDate());
		c = createMutableTime(1000200000000L);
		c.addDays(-10);
		c.addHours(-3);
		p.setEpoch(c.toDate());
		p.setStart(c.toDate());
		p.setCostPerHour(0.5);
		list.add(p);
		
		Collection<CloudProcess> col = new Collection<CloudProcess>("acc", null, list);
		try {
			setFinalStatic(AccountResource.class.getField("dao"), accm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PowerMockito.when(accm.getAllProcessByDays("acc", 30)).thenReturn(col);
		CostsCollection costs = accr.listCostPerDays("acc", 30);

		List<Double> listfinal = costs.getElements();
		Assert.assertEquals("Wrong Value", 12.0, listfinal.get(0));
		Assert.assertEquals("Wrong Value", 3.5, listfinal.get(1));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(2));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(3));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(4));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(5));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(6));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(7));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(8));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(9));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(10));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(11));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(12));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(13));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(14));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(15));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(16));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(17));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(18));
		Assert.assertEquals("Wrong Value", 1.5, listfinal.get(19));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(20));
		Assert.assertEquals("Wrong Value", 9.0, listfinal.get(21));
		Assert.assertEquals("Wrong Value", 12.0, listfinal.get(22));
		Assert.assertEquals("Wrong Value", 12.0, listfinal.get(23));
		Assert.assertEquals("Wrong Value", 6.0, listfinal.get(24));
		Assert.assertEquals("Wrong Value", 9.0, listfinal.get(25));
		Assert.assertEquals("Wrong Value", 9.0, listfinal.get(26));
		Assert.assertEquals("Wrong Value", 12.0, listfinal.get(27));
		Assert.assertEquals("Wrong Value", 3.0, listfinal.get(28));
		Assert.assertEquals("Wrong Value", 0.0, listfinal.get(29));

	}
}
