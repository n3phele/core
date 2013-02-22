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
import n3phele.service.model.Narrative;
import n3phele.service.model.NarrativeLevel;
import n3phele.service.model.ServiceModelDao;
import n3phele.service.model.core.Helpers;

import com.googlecode.objectify.VoidWork;


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
			com.googlecode.objectify.ObjectifyService.ofy().transact(new VoidWork(){

				@Override
				public void vrun() {
					Narrative n = dao.get(msgId);
					n.setText(message);	
					dao.put(n);
				}});
			return msgId;
		}

		public Long addNarrative(URI processUri, String tag,
				NarrativeLevel state, String message) {
				Narrative n = new Narrative();
				n.setState(state);
				n.setTag(tag);
				n.setProcess(processUri);
				n.setStamp(new Date());
				n.setText(message);
				dao.put(n);
				return n.getId();
		}

		public Collection<Narrative> getNarratives(URI processUri) {
			return dao.orderedCollectionByProperty("process", Helpers.URItoString(processUri), "stamp");
			
		}
		
		public Narrative getLastNarrative(URI processUri) {
			return  dao.getByPropertyOrdered("process", Helpers.URItoString(processUri), "stamp") ;
		}
	}
	final public static NarrativeManager dao = new NarrativeManager();
}
