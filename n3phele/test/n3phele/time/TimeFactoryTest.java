package n3phele.time;

import junit.framework.Assert;

import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TimeFactoryTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	private Long twelveHoursUTC = 43200000l;
	
	@Test
	public void differentTimeZoneGenerateCorrectDifferentTimesTest() {
		MutableTimeFactory factory1 = new MutableTimeFactory();
		MutableTimeFactory factory2 = new MutableTimeFactory();
		
		factory1.setTimeZone(DateTimeZone.forOffsetHours(-3));
		factory2.setTimeZone(DateTimeZone.forOffsetHours(-2));		
		
		MutableDateTime time1 = factory1.createMutableDateTime(twelveHoursUTC);
		MutableDateTime time2 = factory2.createMutableDateTime(twelveHoursUTC);
		
		Assert.assertEquals(1, time2.getHourOfDay() - time1.getHourOfDay());
	}
	
	@Test
	public void setCorrectTimeZoneTest() {
		MutableTimeFactory factory = new MutableTimeFactory();
		
		factory.setTimeZone(DateTimeZone.forOffsetHours(-1));
				
		Assert.assertEquals(factory.getTimeZone(), DateTimeZone.forOffsetHours(-1));
		MutableDateTime time = factory.createMutableDateTime(twelveHoursUTC);
		
		Assert.assertEquals(11, time.getHourOfDay());		
	}

}
