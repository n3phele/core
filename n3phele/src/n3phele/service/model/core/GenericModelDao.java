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

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import n3phele.service.core.NotFoundException;

import com.googlecode.objectify.Key;

public class GenericModelDao<T> {
final static Logger log = Logger.getLogger(GenericModelDao.class.getName());
public final Class<T> clazz;
	
	public GenericModelDao(Class<T> clazz) {
		this.clazz = clazz;
	}
	
	/**
	 * We've got to get the associated domain class somehow
	 * 
	 * @param clazz
	 */

	public Key<T> put(T entity)

	{
		return ofy().save().entity(entity).now();
	}

	public Map<Key<T>,T> putAll(Iterable<T> entities) {
		return ofy().save().entities(entities).now();
	}

	public void delete(T entity) {
		ofy().delete().entity(entity).now();
	}

	public void deleteKey(Key<T> entityKey) {
		ofy().delete().key(entityKey).now();
	}

	public void deleteAll(Iterable<T> entities) {
		ofy().delete().entities(entities).now();
	}

	public void deleteKeys(Iterable<Key<T>> keys) {
		ofy().delete().keys(keys).now();
	}

	public T get(Long id) throws NotFoundException {
		try {
			return ofy().load().type(clazz).id(id).safeGet();
		} catch (com.googlecode.objectify.NotFoundException e) {
			throw new NotFoundException();
		}
	}
	
	public T get(String id) throws NotFoundException {
		try {
			return ofy().load().type(clazz).id(id).safeGet();
		} catch (com.googlecode.objectify.NotFoundException e) {
			throw new NotFoundException();
		}
	}

	public T get(Key<T> key) throws NotFoundException {
		try {
			return ofy().load().key(key).safeGet();
		} catch (com.googlecode.objectify.NotFoundException e) {
			throw new NotFoundException();
		}
	}
	
	public Map<?,T> get(List<?> list) {
		return ofy().load().type(clazz).ids(list);
	}
	
	/**
	 * Locate a item from the persistent store based on the item URI.
	 * @param uri
	 * @return the item
	 */
	public T get(URI uri) throws NotFoundException {
		try {
			String s = uri.getPath();
			String identity = s.substring(s.lastIndexOf("/")+1);
			int split = identity.indexOf('_');
			if(split == -1) {
				long id = Long.valueOf(identity);
				return ofy().load().type(clazz).id(id).safeGet();
			} else {
				long parent = Long.valueOf(identity.substring(0,split));
				long id = Long.valueOf(identity.substring(split+1));
				return ofy().load().key(Key.create(Key.create(clazz, parent), clazz, id)).safeGet();
			}
		} catch (com.googlecode.objectify.NotFoundException e) {
			throw new NotFoundException("URI "+uri);
		} catch (Exception e) {
			log.log(Level.WARNING, "Exception on fetch of "+uri,e);
		}
		throw new NotFoundException("URI "+uri);
	}
	
	/**
	 * Locate a item from the persistent store based on the item URI.
	 * @param uri
	 * @return the item
	 */
	public T freshGet(URI uri) throws NotFoundException {
		try {
			String s = uri.getPath();
			long id = Long.valueOf(s.substring(s.lastIndexOf("/")+1));
			ofy().clear();
			return ofy().load().type(clazz).id(id).safeGet();
		} catch (com.googlecode.objectify.NotFoundException e) {
			throw new NotFoundException();
		} catch (Exception e) {
			log.log(Level.WARNING, "Exception on fetch of "+uri.toString(),e);
		}
		throw new NotFoundException();
	}
	
	/**
	 * Locate a item from the persistent store based on the item URI.
	 * @param uri
	 * @return the item
	 */
	public T transactionlessGet(URI uri) throws NotFoundException {
		try {
			String s = uri.getPath();
			long id = Long.valueOf(s.substring(s.lastIndexOf("/")+1));
			ofy().clear();
			return ofy().transactionless().load().type(clazz).id(id).safeGet();
		} catch (com.googlecode.objectify.NotFoundException e) {
			throw new NotFoundException();
		} catch (Exception e) {
			log.log(Level.WARNING, "Exception on fetch of "+uri.toString(),e);
		}
		throw new NotFoundException();
	}


	

	/**
	 * Convenience method to get all objects matching a single property
	 * 
	 */


	public Collection<T> collectionByProperty(URI member, String propName, Object propValue) {
		String s = member.getPath();
		String identity = s.substring(s.lastIndexOf("/")+1);
		int split = identity.indexOf('_');
		if(split == -1) {
			return collectionByProperty(propName, propValue);
		} else {
			long parent = Long.valueOf(identity.substring(0,split));
			List<Key<T>> keys = ofy().load().type(clazz).ancestor(Key.create(clazz, parent)).filter(propName, propValue).keys().list();
			if(keys == null || keys.size() == 0) {
				return new ArrayList<T>();
			} else {
				return ofy().load().keys(keys).values();
			}
		}
		// return ofy().load().type(clazz).filter(propName, propValue).list();
	}
	
	public Collection<T> collectionByProperty(String propName, Object propValue) {
		List<Key<T>> keys = ofy().load().type(clazz).filter(propName, propValue).keys().list();
		if(keys == null || keys.size() == 0) {
			return new ArrayList<T>();
		} else {
			return ofy().load().keys(keys).values();
		}
		// return ofy().load().type(clazz).filter(propName, propValue).list();
	}
	
	public Collection<T> orderedCollectionByProperty(String propName, Object propValue, String sortBy) {
		List<Key<T>> keys = ofy().load().type(clazz).filter(propName, propValue).order(sortBy).keys().list();
		if(keys == null || keys.size() == 0) {
			return new ArrayList<T>();
		} else {
			return ofy().load().keys(keys).values();
		}
		// return ofy().load().type(clazz).filter(propName, propValue).list();
	}
	
	public T getByProperty(String propName, Object propValue) throws NotFoundException  {
		try {
			return ofy().load().type(clazz).filter(propName, propValue).first().safeGet();
		} catch (com.googlecode.objectify.NotFoundException e) {
			throw new NotFoundException();
		}
	}
	
	public T getByPropertyOrdered(String propName, Object propValue, String sortBy) throws NotFoundException  {
		try {
			return ofy().load().type(clazz).filter(propName, propValue).order(sortBy).first().safeGet();
		} catch (com.googlecode.objectify.NotFoundException e) {
			throw new NotFoundException();
		}
	}

	public List<Key<T>> listKeysByProperty(String propName, Object propValue) {
		return ofy().load().type(clazz).filter(propName, propValue).keys().list();
	}
	
	public List<Key<T>> listKeys() {
		return ofy().load().type(clazz).keys().list();
	}
	
	public List<T> listByPropertyForUser(URI user, String propName, Object propValue) {
		return ofy().load().type(clazz).filter("owner", user.toString()).filter(propName, propValue).list();
	}
	
	
	public Collection<T> listByURI(Collection<URI> ids) {
		List<Long> idList = new ArrayList<Long>();
		for(URI uri : ids) {
			String s = uri.getPath();
			Long id = Long.valueOf(s.substring(s.lastIndexOf("/")+1));
			idList.add(id);
		}
		return ofy().load().type(clazz).ids(idList).values();

	}
	
	public List<T> listAll() {
		return ofy().load().type(clazz).list();
	}
	
	public void clear() {
		ofy().clear();
	}
}
