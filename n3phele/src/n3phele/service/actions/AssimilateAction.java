package n3phele.service.actions;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.Unindex;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import n3phele.service.lifecycle.ProcessLifecycle.WaitForSignalRequest;
import n3phele.service.model.Action;
import n3phele.service.model.Command;
import n3phele.service.model.Context;
import n3phele.service.model.SignalKind;
import n3phele.service.model.core.Helpers;
import n3phele.service.model.core.ParameterType;
import n3phele.service.model.core.TypedParameter;
import n3phele.service.model.core.User;
import n3phele.service.rest.impl.ActionResource;


@EntitySubclass
@XmlRootElement(name = "AssimilateAction")
@XmlType(name = "AssimilateAction", propOrder = { "failed", "target" })
@Unindex
@Cache
public class AssimilateAction extends Action {
	final protected static java.util.logging.Logger log = java.util.logging.Logger.getLogger(AssimilateAction.class.getName());
	@XmlTransient private ActionLogger logger;
	private boolean failed = false;
	private String target;
	
	public AssimilateAction() {}
	
	public AssimilateAction(User owner, String name, Context context){
		super(owner.getUri(), name, context);
	}
	
	@Override
	public void init() throws Exception {
		logger = new ActionLogger(this);
		
		URI accountURI = Helpers.stringToURI(this.context.getValue("account"));
		if(accountURI == null)
			throw new IllegalArgumentException("Missing account");
		
		this.target = this.getContext().getValue("target");
		
	}

	@Override
	public boolean call() throws WaitForSignalRequest, Exception {
		
		Client client = ClientFactory.create();
		ClientFilter factoryAuth = new HTTPBasicAuthFilter(this.context.getValue("factoryUser"), 
				this.context.getValue("factorySecret"));
		client.setReadTimeout(90000);
		client.setConnectTimeout(5000);
		client.addFilter(factoryAuth);
		
		
		
		return false;
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
	public void signal(SignalKind kind, String assertion) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDescription() {
		return "Assimilates an existing VM to n3phele";
	}

	@Override
	public Command getPrototype() {
		Command command = new Command();
		command.setUri(UriBuilder.fromUri(ActionResource.dao.path).path("history").path(this.getClass().getSimpleName()).build());
		command.setName("Assimilate");
		command.setOwner(this.getOwner());
		command.setOwnerName(this.getOwner().toString());
		command.setPublic(false);
		command.setDescription("Assimilates an existing VM to n3phele");
		command.setPreferred(true);
		command.setVersion("1");
		//TODO: create icon
		//command.setIcon(URI.create("https://www.n3phele.com/icons/on"));
		List<TypedParameter> myParameters = new ArrayList<TypedParameter>();
		command.setExecutionParameters(myParameters);
		
		myParameters.add(new TypedParameter("target", "VM IP address", ParameterType.String, "", ""));
		for(TypedParameter param : command.getExecutionParameters()) {
			param.setDefaultValue(this.context.getValue(param.getName()));
		}
		return command;
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("AssimilateAction [failed=%s, target=%s, toString()=%s]",
						failed, target,
						super.toString());
	}
}
