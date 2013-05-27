package n3phele.service.rest.impl;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import n3phele.service.core.NotFoundException;
import n3phele.service.core.Resource;
import n3phele.service.model.CachingAbstractManager;
import n3phele.service.model.Service;
import n3phele.service.model.ServiceModelDao;
import n3phele.service.model.core.Collection;
import n3phele.service.model.core.GenericModelDao;
import n3phele.service.model.core.User;


//Starting a new Resource, Stack class will need it's JSons counterparts.
 public class ServiceManager extends CachingAbstractManager<Service> {
	public ServiceManager() {
	}

	@Override
	protected URI myPath() {
		return UriBuilder.fromUri(Resource.get("baseURI", "http://127.0.0.1:8888/resources")).path(ServiceResource.class).build();
	}

	@Override
	public GenericModelDao<Service> itemDaoFactory() {
		return new ServiceModelDao<Service>(Service.class);
	}

	public Service load(Long id, User requestor) throws NotFoundException {
		return super.get(id, requestor);
	}

	/**
	 * Locate a item from the persistent store based on the item name.
	 * 
	 * @param name
	 * @param requestor
	 *            requesting user
	 * @return the item
	 * @throws NotFoundException
	 *             is the object does not exist
	 */
	public Service load(String name, User requestor) throws NotFoundException {
		return super.get(name, requestor);
	}

	/**
	 * Locate a item from the persistent store based on the item URI.
	 * 
	 * @param uri
	 * @param requestor
	 *            requesting user
	 * @return the item
	 * @throws NotFoundException
	 *             is the object does not exist
	 */
	public Service load(URI uri, User requestor) throws NotFoundException {
		return super.get(uri, requestor);
	}

	public Service load(URI uri, URI requestor) throws NotFoundException {
		return super.get(uri, requestor);
	}

	public void add(Service account) {
		super.add(account);
	}

	public void update(Service account) {
		super.update(account);
	}

	public void delete(Service account) {
		super.delete(account);
	}

	public Collection<Service> getCollection(User user) {
		return super.getCollection(user);
	}
	
//	public Collection<Service> getAccountList(User user, boolean summary) {
//
//		log.warning("list Services entered with summary " + summary);
//
//		Collection<Service> result = getCollection(user);
//
//		if (result.getElements() != null) {
//			for (int i = 0; i < result.getElements().size(); i++) {
//				Service account = result.getElements().get(i);
//				if (summary)
//					result.getElements().set(i, Service.summary(account));
//			}
//		}
//		return result;
//	}
}
