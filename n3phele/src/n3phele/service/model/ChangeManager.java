/*
 *
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
package n3phele.service.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import n3phele.service.model.core.Entity;
import n3phele.service.rest.impl.UserResource;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class ChangeManager {
	private final static Logger log = Logger.getLogger(ChangeManager.class.getName()); 
	final static String key = "n3phele-change-stamp";
	private MemcacheService cache;
	private static ChangeManager instance =  null;
	
	public static ChangeManager factory() {
		if(instance == null) {
			instance = new ChangeManager();
		}
		return instance;
	}
	public ChangeManager() {
		
	}
	/** get a list of incremental changes 
	 * @param stamp identifier of previous incremental set
	 * @param isAdmin 
	 * @param owner 
	 * @return null is a list of changes cannot be determined, or a definitive group of changes.
	 * The returned ChangeGroup.getStamp() provides an identifier that should be used in the successive
	 * getChanges call.
	 */
	public ChangeGroup getChanges(long stamp, URI owner, boolean isAdmin) {
		Long cacheCount = (Long) getCache().get(key);
		ChangeGroup result = new ChangeGroup();
		if(cacheCount == null) {
			initCache();
			return null; // no valid cache list of changes
		}
		
		if(cacheCount < stamp)
			return null;		// "should never happen"
		result.setStamp(cacheCount);
		
		Map<String, Change> changes = new HashMap<String, Change>();
		for(long i = stamp+1; i <= cacheCount; i++) {
			Change c = (Change) getCache().get(key+i);
			if(c == null) {
				log.severe("Missing cache key "+key+i+" currently "+cacheCount);
				return null;
			}
			if(isAdmin || c.isPublic() || c.getOwner().equals(owner))
				changes.put((c.isDeleted()?"d":"u")+c.getUri(), c);
		}
		result.setChange(new ArrayList<Change>(changes.values()));
		return result;
	}
	
	public void addChange(Change change) {
		Long cacheCount = getCache().increment(key, 1, initCache());
		change.setStamp(cacheCount);
		getCache().put(key+cacheCount, change, Expiration.byDeltaSeconds(60*60));
	}
	
	
	/**
	 * @return the cache
	 */
	protected MemcacheService getCache() {
		if(cache == null) cache = MemcacheServiceFactory.getMemcacheService();
		return cache;
	}

	/** Initialize the cache. The cache is initialized with a guaranteed
	 * monotonically increasing value;
	 */
	public long initCache() {
		Long result;
		if((result = (Long) getCache().get(key)) == null) {
			getCache().put(key, result = Calendar.getInstance().getTimeInMillis());
		}
		return result;
	}
	public void addChange(URI myPath) {
		Change change = new Change(myPath, UserResource.Root.getUri(), true, false);
		addChange(change);
	}
	public void addChange(Entity entity) {
		addChange(new Change(entity,false));
		
	}
	public void addDelete(Entity entity) {
		addChange(new Change(entity,true));
		
	}

}
