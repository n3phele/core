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
	
	public ProcessCounter()
	{
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

	public void setCount(Integer count) {
		this.count = count;
	}
}
