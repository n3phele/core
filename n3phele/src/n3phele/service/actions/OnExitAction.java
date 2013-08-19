package n3phele.service.actions;
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.lifecycle.ProcessLifecycle.WaitForSignalRequest;
import n3phele.service.model.Command;
import n3phele.service.model.Context;
import n3phele.service.model.core.User;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.Unindex;

/** Runs a command on a cloud server 
 * 
 * @author Nigel Cook
 *
 */
@EntitySubclass
@XmlRootElement(name = "OnExitAction")
@XmlType(name = "OnExitAction", propOrder = {})
@Unindex
@Cache
public class OnExitAction extends OnAction {
	private static java.util.logging.Logger log = java.util.logging.Logger.getLogger(OnExitAction.class.getName()); 
	
	public OnExitAction() {}
	
	protected OnExitAction(User owner, String name,
			Context context) {
		super(owner, name, context);
	}
	
	/* (non-Javadoc)
	 * @see n3phele.service.model.Action#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Run OnExit";
	}
	
	/*
	 * (non-Javadoc)
	 * @see n3phele.service.model.Action#getPrototype()
	 */
	@Override
	public Command getPrototype() {
		Command command = super.getPrototype();
		command.setName("OnExit");
		return command;
	}
	

	/* (non-Javadoc)
	 * @see n3phele.service.actions.OnAction#init()
	 */
	@Override
	public void init() throws Exception
	{
		try
		{
			super.init();
		}
		catch(Exception e)
		{
			logger = new ActionLogger(this);
			logger.error("Error initializing OnExitAction class");
			
			log.info("Exception into init() of OnExitAction: " + e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see n3phele.service.actions.OnAction#call()
	 */
	@Override
	public boolean call() throws WaitForSignalRequest
	{
		logger = new ActionLogger(this);
		
		try
		{
			boolean success = super.call();
			if(!success)
				log.info("Error into super call() of OnExitAction.");
			
			return true;
		}
		catch(WaitForSignalRequest we)
		{
			throw we;
		}
		catch(Exception e)
		{
			logger.error("Error into calling OnExitAction.");
			log.info("Exception into call() of OnExitAction: " + e.getMessage());
			return true;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("OnExitAction [%s]", super.toString());
	}
	
}
