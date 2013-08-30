package n3phele.service.model;

import n3phele.service.model.core.Entity;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Unindex;
import com.googlecode.objectify.condition.IfNotNull;

@Unindex
@Cache
@com.googlecode.objectify.annotation.Entity
public class ProcessCounter extends Entity {
	@Id protected Long id;
	
	protected Integer count;

	@Index(IfNotNull.class)protected String accountUri = null;
	
	public ProcessCounter()
	{
		count = 0;
	}

	public ProcessCounter(String accountUri)
	{
		if(accountUri == null) throw new IllegalArgumentException("Account URI can't be null");
		this.accountUri = accountUri;
		count = 0;
	}

	public int getCount() {
		return count;
	}

	public void increment() {
		count++;
	}
		
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAccountUri() {
		return this.accountUri;
	}

	public void setAccountUri(String accountUri) {
		this.accountUri = accountUri;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
}
