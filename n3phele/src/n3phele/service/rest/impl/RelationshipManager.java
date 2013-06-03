package n3phele.service.rest.impl;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import n3phele.service.core.NotFoundException;
import n3phele.service.core.Resource;
import n3phele.service.model.CachingAbstractManager;
import n3phele.service.model.Relationship;
import n3phele.service.model.ServiceModelDao;
import n3phele.service.model.core.Collection;
import n3phele.service.model.core.GenericModelDao;
import n3phele.service.model.core.User;

//Instead of having an inner class on StackResource we're using this external class
public class RelationshipManager extends CachingAbstractManager<Relationship> {
	public RelationshipManager() {
	}

	@Override
	protected URI myPath() {
		return UriBuilder.fromUri(Resource.get("baseURI", "http://127.0.0.1:8888/resources")).path(RelationshipResource.class).build();
	}

	@Override
	public GenericModelDao<Relationship> itemDaoFactory() {
		return new ServiceModelDao<Relationship>(Relationship.class);
	}

	public Relationship load(Long id, User requestor) throws NotFoundException {
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
	public Relationship load(String name, User requestor) throws NotFoundException {
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
	public Relationship load(URI uri, User requestor) throws NotFoundException {
		return super.get(uri, requestor);
	}

	public Relationship load(URI uri, URI requestor) throws NotFoundException {
		return super.get(uri, requestor);
	}

	public void add(Relationship account) {
		super.add(account);
	}

	public void update(Relationship account) {
		super.update(account);
	}

	public void delete(Relationship account) {
		super.delete(account);
	}

	public Collection<Relationship> getCollection(User user) {
		return super.getCollection(user);
	}

}