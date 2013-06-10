package n3phele.service.model.core;
/**
 * (C) Copyright 2010-2013. Nigel Cook. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * Licensed under the terms described in LICENSE file that accompanied this code, (the "License"); you may not use this file
 * except in compliance with the License. 
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on 
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 *  specific language governing permissions and limitations under the License.
 */

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import n3phele.service.core.NotFoundException;

import com.googlecode.objectify.Key;

public abstract class AbstractManager<Item extends Entity> {

	protected Logger log = Logger.getLogger(this.getClass().getName());
	protected GenericModelDao<Item> itemDao;
	public final URI path;

	public AbstractManager() {
		super();
		itemDao = itemDaoFactory();
		path = myPath();
	}
	
	/**
	 * Locate an item from the persistent store based on the item id.
	 * @param id
	 * @return the item
	 * @throws NotFoundException is the object does not exist
	 */
	protected Item get(Long id) throws NotFoundException {
		return itemDao.get(id);
	 }
	
	/**
	 * Locate an item from the persistent store based on the item id and parent.
	 * @param parent
	 * @param id
	 * @return the item
	 * @throws NotFoundException is the object does not exist
	 */
	public Item get(Key<Item> parent, Long id) {
		if(parent == null) {
			return itemDao.get(id);
		} else {
			return itemDao.get(Key.create(parent, itemDao.clazz, id));
		}
	}
	
	/**
	 * Locate a item from the persistent store based on the item URI.
	 * @param uri
	 * @return the item
	 */
	protected Item get(URI uri) throws NotFoundException {
		return itemDao.get(uri);
	}

	/**
	 * Locate a item from the persistent store based on the item name.
	 * @param name
	 * @return the item
	 */
	@Deprecated
	protected Item get(String name) throws NotFoundException {
		try {
			Item item = itemDao.getByProperty("name", name);
			if(item != null)
				return item;
		} catch (NotFoundException e) {
			throw e;
		} catch (Exception e) {
			log.log(Level.WARNING, "Exception on fetch of "+this.path+"with name="+name,e);
		} 
		throw new NotFoundException();
	}
	
	/**
	 * Locate a list of item from the persistent store based on the item name.
	 * @param name
	 * @return the List of item
	 */
	@Deprecated
	protected java.util.Collection<Item> getList(String name) throws NotFoundException {
		try {
			java.util.Collection<Item> item = itemDao.collectionByProperty("name", name);
			if(item != null)
				return item;
		} catch (Exception e) {
			log.log(Level.WARNING, "Exception on fetch of "+this.path+"with name="+name,e);
		}
		throw new NotFoundException();
	}
	
	/**
	 * Locate an item from the persistent store based on the item id.
	 * @param id
	 * @param owner
	 * @return the item
	 * @throws NotFoundException is the object does not exist
	 */
	private Item get(Long id, URI owner) throws NotFoundException {

		Item item = itemDao.get(id);
		if(item.isPublic() || owner.equals(item.getOwner()))
			return item;
		else
			throw new NotFoundException();

	 }
	
	/**
	 * Locate an item from the persistent store based on the item id and parent.
	 * @param parent
	 * @param id
	 * @param owner
	 * @return the item
	 * @throws NotFoundException is the object does not exist
	 */
	private Item get(Key<Item> parent, Long id, URI owner) throws NotFoundException {

		Item item = this.get(parent, id);
		if(item.isPublic() || owner.equals(item.getOwner()))
			return item;
		else
			throw new NotFoundException();

	 }
	
	/**
	 * Locate a item from the persistent store based on the item URI.
	 * @param uri
	 * @param owner
	 * @return the item
	 */
	protected Item get(URI uri, URI owner) throws NotFoundException {
		try {
			Item item = itemDao.get(uri);
			if(item != null && (item.isPublic() || owner.equals(item.getOwner())))
				return item;
		} catch (Exception e) {
			log.log(Level.WARNING, "Exception on fetch of "+uri.toString(),e);
		}
		throw new NotFoundException();
	}

	/**
	 * Locate a item from the persistent store based on the item name.
	 * @param name
	 * @param owner
	 * @return the item
	 */
	@Deprecated
	private Item get(String name, URI owner) throws NotFoundException {
		try {
			Item item = get(name);
			if(item != null && (item.isPublic() || owner.equals(item.getOwner())))
				return item;
		} catch (Exception e) {
			log.log(Level.WARNING, "Exception on fetch of "+this.path+"with name="+name,e);
		}
		throw new NotFoundException();
	}
	
	/**
	 * Locate an item from the persistent store based on the item id.
	 * @param id
	 * @param owner
	 * @return the item
	 * @throws NotFoundException is the object does not exist
	 */
	protected Item get(Long id, User owner) throws NotFoundException {
		return owner.isAdmin()? get(id):get(id, owner.getUri());
	 }
	
	/**
	 * Locate an item from the persistent store based on the item id and parent.
	 * @param parent
	 * @param id
	 * @param owner
	 * @return the item
	 * @throws NotFoundException is the object does not exist
	 */
	protected Item get(Key<Item> parent, Long id, User owner) throws NotFoundException {
		return owner.isAdmin()? get(parent, id):get(parent, id, owner.getUri());
	 }
	
	/**
	 * Locate a item from the persistent store based on the item URI.
	 * @param uri
	 * @param owner
	 * @return the item
	 */
	protected Item get(URI uri, User owner) throws NotFoundException {
		return owner.isAdmin()? get(uri):get(uri, owner.getUri());
	}

	/**
	 * Locate a item from the persistent store based on the item name.
	 * @param name
	 * @param owner
	 * @return the item
	 */
	protected Item get(String name, User owner) throws NotFoundException {
		return owner.isAdmin()? get(name):get(name, owner.getUri());
	}
	
	/** Add a new item to the persistent data store. The item will be updated with a unique key, as well
	 * the item URI will be updated to include that defined unique team.
	 * @param item to be added
	 * @throws IllegalArgumentException for a null argument
	 */
	protected void add(Item item) throws IllegalArgumentException {
		if(item != null) {
			if(item.getOwner()==null)
				throw new IllegalArgumentException("attempt to persist a private object without owner");
			Key<Item>key = itemDao.put(item);
			if(key.getParent() == null) {
				item.setUri(URI.create(this.path+"/"+key.getId()));
			} else {
				item.setUri(URI.create(this.path+"/"+key.getParent().getId()+"_"+key.getId()));
			}
			itemDao.put(item);
			//log.info("item "+item.getName()+" has id "+item.getUri());
		} else 
			throw new IllegalArgumentException("attempt to persist a null object");
	}
	
	/** Update a particular object in the persistent data store
	 * @param item the item to update
	 */
	protected void update(Item item)throws NotFoundException {
		if(item != null) {
			Key<Item>key = itemDao.put(item);
		}
	}
	
	/**
	 * Delete item from the persistent store
	 * @param item to be deleted
	 */
	protected void delete(Item item) {
		if(item != null) {
			log.info("Delete of item "+item.toString());
			itemDao.delete(item);
		}
	}
	

	/**
	 * Collection of resources of a particular class in the persistent store. 
	 * @return the collection
	 */
	protected Collection<Item> getCollection() {
		Collection<Item> result = null;
		try {
			List<Item> children = itemDao.listAll();
			result = new Collection<Item>(itemDao.clazz.getSimpleName(), this.path, children);
		} catch (NotFoundException e) {
		}
		result.setTotal(result.getElements().size());
		return result;
	}
	
	/**
	 * Collection of resources of a particular class in the persistent store.  
	 * @return the collection
	 */
	protected Collection<Item> getCollection(URI owner) {
		Collection<Item> result = null;
		try {
			java.util.Collection<Item> owned = itemDao.collectionByProperty("owner", owner.toString());
			java.util.Collection<Item> shared = itemDao.collectionByProperty("isPublic", true);
			List<Item> items = mergeResults(owned, shared, owner);			
			result = new Collection<Item>(itemDao.clazz.getSimpleName(), this.path, items);
		} catch (NotFoundException e) {
		}
		result.setTotal(result.getElements().size());
		return result;
	}
	
	protected List<Item> mergeResults(java.util.Collection<Item>owned, java.util.Collection<Item>shared, URI owner) {
		List<Item> items = null;
		if(owned != null && owned.size()>0 && shared != null && shared.size()>0) {
			items = new ArrayList<Item>(owned.size()+shared.size());
			if(owned.size() > shared.size()) {
				String ownerId = owner.toString();
				for(Item i : shared) {
					if(!ownerId.equals(i.owner))
						items.add(i);
				}
				items.addAll(owned);
				
			} else {
				for(Item i : owned) {
					if(!i.isPublic)
						items.add(i);
				}
				items.addAll(shared);
			}
		} else if(owned != null && owned.size() > 0) {
			items = new ArrayList<Item>(owned.size());
			items.addAll(owned);
		} else {
			items = new ArrayList<Item>(shared.size());
			items.addAll(shared);
		}
		return items;
		
	}
	
	/**
	 * Collection of resources of a particular class in the persistent store. The will be extended
	 * in the future to return the collection of resources accessible to a particular user.
	 * @return the collection
	 */
	protected Collection<Item> getCollection(User owner) {
		return owner.isAdmin()? getCollection():getCollection(owner.getUri());
	}
	
	protected List<Item> getAll() {
		List<Item> result = null;
		try {
			result = itemDao.listAll();
		} catch (NotFoundException e) {
		}
		return result;

	}
	
	protected GenericModelDao<Item> itemDao() {
		return this.itemDao;
	}
	/**
	 * 
	 * @return the base URI path for this class
	 */
	protected abstract URI myPath();

	/** Model Data access object for this class of object
	 * @param transactional true to create a transactional persistent model management, false for non-transactional 
	 * @return the modelDao
	 */
	public abstract GenericModelDao<Item> itemDaoFactory();
	
	public <R> R transact(com.googlecode.objectify.Work<R> codeBody) {
		return com.googlecode.objectify.ObjectifyService.ofy().transact(codeBody);
	}
	
	public Void transact(com.googlecode.objectify.VoidWork codeBody) {
		return com.googlecode.objectify.ObjectifyService.ofy().transact(codeBody);
	}

}