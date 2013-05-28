package n3phele.time;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;

public class MutableTimeFactory {

	private DateTimeZone zone;
	
	public MutableTimeFactory()
	{
		this.zone = DateTimeZone.getDefault();
	}
	
	public MutableTimeFactory(DateTimeZone zone)
	{
		this.zone = zone;
	}
	
	public void setTimeZone(DateTimeZone zone)
	{
		this.zone = zone;		
	}
	
	public DateTimeZone getTimeZone()
	{
		return zone;		
	}
	
	public MutableDateTime createMutableDateTime()
	{
		MutableDateTime time = new MutableDateTime();
		time.setZone(zone);
		return time;		
	}
	
	public MutableDateTime createMutableDateTime(Long l)
	{
		MutableDateTime time = new MutableDateTime(l);
		time.setZone(zone);
		return time;		
	}
	
	public MutableDateTime createMutableDateTime(Date date)
	{
		MutableDateTime time = new MutableDateTime(date);
		time.setZone(zone);
		return time;		
	}

	public MutableDateTime createMutableDateTime(DateTime date)
	{
		MutableDateTime time = new MutableDateTime(date);
		time.setZone(zone);
		return time;		
	}
}
