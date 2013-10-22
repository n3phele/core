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
 *  
 *  @author Lucio P. Cossio
 */
package n3phele.service.model;
import static com.googlecode.objectify.ObjectifyService.ofy;

import java.net.URI;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import n3phele.service.core.NotFoundException;
import n3phele.service.model.core.AbstractManager;
import n3phele.service.rest.impl.AccountResource;
import n3phele.service.rest.impl.CloudProcessResource;

public abstract class ProcessCachingAbstractManager extends AbstractManager<CloudProcess> {
	private static Logger log = Logger.getLogger(ProcessCachingAbstractManager.class.getName());
	private int sleepTime = 1000;


	@Override
	protected void add(CloudProcess item) throws IllegalArgumentException {
			super.add(item);
			Date date = new Date(System.currentTimeMillis());
			QueueFactory.getDefaultQueue().add(ofy().getTxn(),
					TaskOptions.Builder.withPayload(new AddChangeTask(item.getUri(), date, super.path,sleepTime)));
	}
	
	private static class AddChangeTask implements DeferredTask {
		private static final long serialVersionUID = 1L;
		final private URI process;
		final private Date stamp;
		final private URI path;
		private int sleepTime;
		
		
		public AddChangeTask(URI process, Date stamp, URI path, int sleepTime ) {
			this.process = process;
			this.stamp = stamp;
			this.path = path;
			this.sleepTime = sleepTime;
		}

		@Override
		public void run(){
			CloudProcess item = CloudProcessResource.dao.load(process);
			Date date = new Date(System.currentTimeMillis());
			long result = date.getTime() - stamp.getTime();
			log.log(Level.INFO, "!Calling changes after: " + result);
			if(result < sleepTime){
				try {
					Thread.sleep(sleepTime - result);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(item != null) 
				if(item.isTopLevel())
				{
					ChangeManager.factory().addChange(path);
				}
				else
				{
					ChangeManager.factory().addChange(item.getParent());				
				}
		}
	}

	@Override
	protected void update(CloudProcess item)throws NotFoundException {
		try {
			super.update(item);
		} catch (NotFoundException e) {
			ChangeManager.factory().addChange(super.path);
			throw e;
		}
		
		if(item != null) 
		if(item.isTopLevel())
		{
			ChangeManager.factory().addChange(item);
		}
		else
		{
			ChangeManager.factory().addChange(item.getParent());
		}
	}

	@Override
	protected void delete(CloudProcess item) {
		if(item != null) {
			super.delete(item);
			if(item.isTopLevel())
			{
				ChangeManager.factory().addChange(super.path);				
			}
			else
			{
				ChangeManager.factory().addChange(item.getParent());				
			}
			ChangeManager.factory().addDelete(item);			
		}
	}
	public int getSleepTime() {
		return this.sleepTime;
	}

	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}
}
