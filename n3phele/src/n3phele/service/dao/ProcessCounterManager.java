package n3phele.service.dao;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import n3phele.service.core.NotFoundException;
import n3phele.service.core.Resource;
import n3phele.service.model.CachingAbstractManager;
import n3phele.service.model.ProcessCounter;
import n3phele.service.model.ServiceModelDao;
import n3phele.service.model.core.Collection;
import n3phele.service.model.core.GenericModelDao;
import n3phele.service.model.core.User;

public class ProcessCounterManager extends CachingAbstractManager<ProcessCounter>{

	public ProcessCounterManager()
	{
		super();			
	}

	@Override
	protected URI myPath() {
		//return UriBuilder.fromUri(Resource.get("baseURI", "http://127.0.0.1:8888/resources")).path(ProcessCounter.class).build();
		return null;
	}

	@Override
	public GenericModelDao<ProcessCounter> itemDaoFactory() {
		return new ServiceModelDao<ProcessCounter>(ProcessCounter.class);
	}
	
	public ProcessCounter load(Long id, User requestor) throws NotFoundException { return super.get(id, requestor); }
	
	public ProcessCounter load(String name, User requestor) throws NotFoundException { return super.get(name, requestor); }
	
	public ProcessCounter load(URI uri, User requestor) throws NotFoundException { return super.get(uri, requestor); }
	
	public ProcessCounter load(URI uri) throws NotFoundException { return super.get(uri); }
	
	public ProcessCounter load(URI uri, URI requestor) throws NotFoundException { return super.get(uri, requestor); }

	public Collection<ProcessCounter> getCollection(URI user) { return super.getCollection(user); }
	
	public Collection<ProcessCounter> getCollection(User user) { return super.getCollection(user); }
	
	public void add(ProcessCounter cloud) { super.add(cloud); }
	
	public void delete(ProcessCounter cloud) { super.delete(cloud); }
	
	public void update(ProcessCounter cloud) { super.update(cloud); }
	
}