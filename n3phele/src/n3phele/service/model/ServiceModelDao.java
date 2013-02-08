package n3phele.service.model;

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

import n3phele.service.actions.CountDownAction;
import n3phele.service.actions.CreateVMAction;
import n3phele.service.actions.FileTransferAction;
import n3phele.service.actions.JobAction;
import n3phele.service.actions.LogAction;
import n3phele.service.actions.NShellAction;
import n3phele.service.actions.OnAction;
import n3phele.service.actions.ServiceAction;
import n3phele.service.actions.VMAction;
import n3phele.service.model.core.GenericModelDao;
import n3phele.service.model.core.User;
import n3phele.service.model.repository.Repository;

import com.googlecode.objectify.ObjectifyService;

public class ServiceModelDao<T> extends GenericModelDao<T> {

	static {
		// Register all your entity classes here
		ObjectifyService.register(CloudProcess.class);
		ObjectifyService.register(Cloud.class);
		ObjectifyService.register(Account.class);
		ObjectifyService.register(User.class);
		ObjectifyService.register(Narrative.class);
		ObjectifyService.register(Origin.class);
		ObjectifyService.register(Repository.class);
		
		ObjectifyService.register(Action.class);
		ObjectifyService.register(LogAction.class);
		ObjectifyService.register(CountDownAction.class);
		ObjectifyService.register(JobAction.class);
		ObjectifyService.register(ServiceAction.class);
		ObjectifyService.register(NShellAction.class);
		ObjectifyService.register(CreateVMAction.class);
		ObjectifyService.register(VMAction.class);
		ObjectifyService.register(OnAction.class);
		ObjectifyService.register(FileTransferAction.class);

	}
	public ServiceModelDao(Class<T> clazz) {
		super(clazz);
	}

}
