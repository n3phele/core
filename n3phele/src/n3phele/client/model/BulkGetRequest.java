/**
 * @author Nigel Cook
 *
 * (C) Copyright 2010-2012. Nigel Cook. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * Licensed under the terms described in LICENSE file that accompanied this code, (the "License"); you may not use this file
 * except in compliance with the License. 
 * 
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on 
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 *  specific language governing permissions and limitations under the License.
 */
package n3phele.client.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.http.client.URL;


public class BulkGetRequest {
	public BulkGetRequest() {}
	
	private Set<String> progressSet = null;
	
	
	public void add(Progress p) {
		if(this.progressSet == null)
			progressSet = new HashSet<String>();
		this.progressSet.add(p.getUri());
	}


	public void add(CacheItem cacheItem) {
		switch (cacheItem.getKind()) {
		case Progress:
			add((Progress)cacheItem.getObject());
			break;
			
		default:
			break;
		}
		
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toString(this.progressSet, "progressSet", true);
	}


	private String toString(Collection<?> collection, final String name, boolean last) {
		if(collection == null || collection.size() == 0) return "";
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext(); i++) {
			if (i > 0)
				builder.append("&");
			builder.append(name).append("=");
			builder.append(URL.encodeQueryString(String.valueOf(iterator.next())));
		}
		if(!last) builder.append("&");
		return builder.toString();		
	}
	
	
}
