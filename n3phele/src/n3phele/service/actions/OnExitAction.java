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
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("OnExitAction [%s]", super.toString());
	}
	
}
