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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.core.NotFoundException;
import n3phele.service.lifecycle.ProcessLifecycle;
import n3phele.service.model.Command;
import n3phele.service.model.Context;
import n3phele.service.model.SignalKind;
import n3phele.service.model.core.ParameterType;
import n3phele.service.model.core.TypedParameter;
import n3phele.service.model.core.User;
import n3phele.service.rest.impl.ActionResource;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.Unindex;

/** Dumps all child process and logs a message
 * <br> Processes the following context elements
 * <br> arg [--<i>logLevel ] <i>log message text <\i>
 * <br> where:
 * <br> logLevel one of success, info, warning, error 
 * 
 * @author Nigel Cook
 *
 */
@EntitySubclass
@XmlRootElement(name = "DumpAction")
@XmlType(name = "DumpAction", propOrder = {"adopted"})
@Unindex
@Cache
public class DumpAction extends LogAction {
	final private static java.util.logging.Logger log = java.util.logging.Logger.getLogger(DumpAction.class.getName()); 
	private ActionLogger logger;
	private Set<String> adopted = new HashSet<String>();			/* adopted children */
	public DumpAction() {}
	
	protected DumpAction(User owner, String name,
			Context context) {
		super(owner, name, context);
	}
	
	/* (non-Javadoc)
	 * @see n3phele.service.model.Action#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Dump "+this.getContext().getValue("arg");
	}
	
	/*
	 * (non-Javadoc)
	 * @see n3phele.service.model.Action#getPrototype()
	 */
	@Override
	public Command getPrototype() {
		Command command = new Command();
		command.setUri(UriBuilder.fromUri(ActionResource.dao.path).path("history").path(this.getClass().getSimpleName()).build());
		command.setName("Dump");
		command.setOwner(this.getOwner());
		command.setOwnerName(this.getOwner().toString());
		command.setPublic(false);
		command.setDescription("Dump processes");
		command.setPreferred(true);
		command.setVersion("1");
		command.setIcon(URI.create("https://www.n3phele.com/icons/log"));
		List<TypedParameter> myParameters = new ArrayList<TypedParameter>();
		command.setExecutionParameters(myParameters);
		
		myParameters.add(new TypedParameter("arg", "argument", ParameterType.String, "", ""));
		for(TypedParameter param : command.getExecutionParameters()) {
			param.setDefaultValue(this.context.getValue(param.getName()));
		}
		return command;
	}
	
	
	
	
	@Override
	public void init() throws Exception {
		super.init();
	}
	
	@Override
	public boolean call() throws Exception {
		dumpAll();
		return true;
	}

	@Override
	public void cancel() {
		dumpAll();
	}

	@Override
	public void dump() {
		dumpAll();
	}

	@Override
	public void signal(SignalKind kind, String assertion) throws NotFoundException {
		log.info("Signal "+kind+":"+assertion);
		switch(kind) {
		case Adoption:
			adopted.add(assertion);
			return;
		case Cancel:
		case Dump:
			adopted.remove(assertion);
			break;
		case Event:
			log.warning("Ignoring event "+assertion);
			return;
		case Failed:
			adopted.remove(assertion);
			break;
		case Ok:
			adopted.remove(assertion);
			break;
		default:
			return;		
		}
	}
	
	private void dumpAll() {
		for(String process : adopted) {
			URI processURI = URI.create(process);
			ProcessLifecycle.mgr().dump(processURI);
		}
	}

	/**
	 * @return the adopted
	 */
	public Set<String> getAdopted() {
		return this.adopted;
	}

	/**
	 * @param adopted the adopted to set
	 */
	public void setAdopted(Set<String> adopted) {
		this.adopted = adopted;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("DumpAction [adopted=%s, %s]",
				this.adopted, super.toString());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((this.adopted == null) ? 0 : this.adopted.hashCode());
		result = prime * result
				+ ((this.logger == null) ? 0 : this.logger.hashCode());
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
		DumpAction other = (DumpAction) obj;
		if (this.adopted == null) {
			if (other.adopted != null)
				return false;
		} else if (!this.adopted.equals(other.adopted))
			return false;
		if (this.logger == null) {
			if (other.logger != null)
				return false;
		} else if (!this.logger.equals(other.logger))
			return false;
		return true;
	}
}