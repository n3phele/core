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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.core.NotFoundException;
import n3phele.service.lifecycle.ProcessLifecycle;
import n3phele.service.lifecycle.ProcessLifecycle.WaitForSignalRequest;
import n3phele.service.model.Action;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.Command;
import n3phele.service.model.Context;
import n3phele.service.model.ParameterType;
import n3phele.service.model.SignalKind;
import n3phele.service.model.TypedParameter;
import n3phele.service.model.core.Helpers;
import n3phele.service.model.core.User;
import n3phele.service.rest.impl.ActionResource;
import n3phele.service.rest.impl.CloudProcessResource;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.Unindex;


/** Destroy a VM
 * 
 * 
 * @author Nigel Cook
 *
 */
@EntitySubclass
@XmlRootElement(name = "DestroyAction")
@XmlType(name = "DestroyAction", propOrder = { "actionName", "childProcess" })
@Unindex
@Cache
public class DestroyAction extends Action {
	final private static java.util.logging.Logger log = java.util.logging.Logger.getLogger(DestroyAction.class.getName()); 
	@XmlTransient private ActionLogger logger;

	private String target;	
	
	public DestroyAction() {}
	
	protected DestroyAction(User owner, String name, Context context) {
		super(owner.getUri(), name, context);
	}
	
	/* (non-Javadoc)
	 * @see n3phele.service.model.Action#getDescription()
	 */
	@Override
	public String getDescription() {
		String[] targets = this.target.split("[ ,]+");
		if(targets.length == 1)
			return "Destroy "+this.target;
		else
			return "Destroy "+targets.length+" VMs";
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see n3phele.service.model.Action#getPrototype()
	 */
	@Override
	public Command getPrototype() {
		Command command = new Command();
		command.setUri(UriBuilder.fromUri(ActionResource.dao.path).path("history").path(this.getClass().getSimpleName()).build());
		command.setName("Destroy");
		command.setOwner(this.getOwner());
		command.setOwnerName(this.getOwner().toString());
		command.setPublic(false);
		command.setDescription("Destroy one or more virtual machines");
		command.setPreferred(true);
		command.setVersion("1");
		command.setIcon(URI.create("https://www.n3phele.com/icons/destroy"));
		List<TypedParameter> myParameters = new ArrayList<TypedParameter>();
		command.setExecutionParameters(myParameters);
		
		myParameters.add(new TypedParameter("target", "VMs to terminate", ParameterType.String, "", ""));
		for(TypedParameter param : command.getExecutionParameters()) {
			param.setDefaultValue(this.context.getValue(param.getName()));
		}
		return command;
	}

	@Override
	public void init() throws Exception {
		logger = new ActionLogger(this);
	
		this.target = this.getContext().getValue("target");
		String arg = this.getContext().getValue("arg");
		if(Helpers.isBlankOrNull(this.target)) {
			this.target = arg;
		}
	}
	

	@Override
	public boolean call() throws WaitForSignalRequest {
		String[] targets = this.target.split("[ ,]+");

		for(String string : targets) {
			URI target = URI.create(string);
			if(target.toString().contains("process")) {
				CloudProcess process = CloudProcessResource.dao.load(target);
				target = process.getAction();
			}
			
			Action action;
			if(target.toString().contains("action")) {
				action = ActionResource.dao.load(target);
				if(name == null) {
					name = action.getName();
				}
				if(action instanceof CreateVMAction) {
					CreateVMAction createVMAction = (CreateVMAction) action;
					createVMAction.killVM();
				} else  {
					processLifecycle().dump(action.getProcess());
				} 
			} else {
				log.warning(target+" is not a valid termination target");
				logger.error(target+" is not a valid termination target");
				throw new IllegalArgumentException(target+" is not a valid termination target");
			}
		}
		
		return true;	
	}

	@Override
	public void cancel() {
		log.warning("Cancel");
	}

	@Override
	public void dump() {
		log.warning("Dump");
	}

	@Override
	public void signal(SignalKind kind, String assertion) throws NotFoundException {
		log.info("Signal "+kind+":"+assertion);
		switch(kind) {
		case Adoption:
			log.info(assertion+" adoption");
			return;
		case Cancel:
			log.info(assertion+" cancelled");
			break;
		case Event:
			log.warning("Ignoring event "+assertion);
			return;
		case Failed:
			log.info(assertion+" failed");
			break;
		case Ok:
			log.info(assertion+" ok");
			break;
		default:
			return;		
		}
	}

	/**
	 * @return the target
	 */
	public URI getTarget() {
		return Helpers.stringToURI(target);
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(URI target) {
		this.target = Helpers.URItoString(target);
	}

	/*
	 * Object Management
	 * =================
	 */
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((logger == null) ? 0 : logger.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DestroyAction other = (DestroyAction) obj;
		if (logger == null) {
			if (other.logger != null)
				return false;
		} else if (!logger.equals(other.logger))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(
				"DestroyAction [logger=%s, target=%s, %s]", logger,
				target, super.toString());
	}

	/*
	 * Unit Testing
	 * ============
	 */
	protected ProcessLifecycle processLifecycle() {
		return ProcessLifecycle.mgr();
	}
}
	
