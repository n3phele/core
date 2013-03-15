package n3phele.service.rest.impl;
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
import java.util.Collection;
import java.util.Date;

import n3phele.service.core.NotFoundException;
import n3phele.service.model.ChangeManager;
import n3phele.service.model.Narrative;
import n3phele.service.model.NarrativeLevel;
import n3phele.service.model.ServiceModelDao;

import com.googlecode.objectify.Work;


public class NarrativeResource {
	//private static java.util.logging.Logger log = java.util.logging.Logger.getLogger(NarrativeResource.class.getName()); 
	public NarrativeResource() {
	}


	
	/*
	 * ------------------------------------------------------------------------------------------ *
	 *                      	== Private & Internal support functions ==
	 * ------------------------------------------------------------------------------------------ *
	 */
	
	public static class NarrativeManager extends ServiceModelDao<Narrative> {
		public NarrativeManager() {
			super(Narrative.class);
		}
	
		public String getNarrative(Long msgId) throws NotFoundException {
			return super.get(msgId).getText();
		}

		public Long updateNarrativeText(final Long msgId, final String message) {
			Narrative n = com.googlecode.objectify.ObjectifyService.ofy().transact(new Work<Narrative>(){

				@Override
				public Narrative run() {
					Narrative n = dao.get(msgId);
					n.setText(message);	
					dao.put(n);
					return n;
				}});
			ChangeManager.factory().addChange(URI.create(n.getProcessUri()));
			ChangeManager.factory().addChange(n.getGroup());
			return msgId;
		}

		public String addNarrative(URI processUri, URI group, String tag,
				NarrativeLevel state, String message) {
				
				Narrative n = new Narrative();
				n.setState(state);
				n.setTag(tag);
				n.setProcessUri(processUri);
				n.setGroup(group);
				n.setStamp(new Date());
				n.setText(message);
				dao.put(n);
				ChangeManager.factory().addChange(processUri);
				ChangeManager.factory().addChange(group);
				return n.getId();
		}

		public Collection<Narrative> getNarratives(URI processUri) {
			String uri = processUri.toString();
			String ids = uri.substring(uri.lastIndexOf("/")+1);

			int split = ids.indexOf('_');
			if(split == -1) {
				return dao.orderedCollectionByProperty("rootProcess", Long.valueOf(ids), "stamp");
			} else {
				return dao.orderedCollectionByProperty("group", ids, "stamp");
			}
			
			
		}
		
		public Collection<Narrative> getProcessNarratives(URI processUri) {
			String uri = processUri.toString();
			String ids = uri.substring(uri.lastIndexOf("/")+1);
			return dao.orderedCollectionByProperty("process", ids, "stamp");
			
		}
		
		public Narrative getLastNarrative(URI processUri) {
			String uri = processUri.toString();
			String ids = uri.substring(uri.lastIndexOf("/")+1);

			int split = ids.indexOf('_');
			if(split == -1) {
				return dao.getByPropertyOrdered("rootProcess", Long.valueOf(ids), "stamp");
			} else {
				return dao.getByPropertyOrdered("group", ids, "stamp");
			}
		}
	}
	final public static NarrativeManager dao = new NarrativeManager();
}
