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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.core.NotFoundException;
import n3phele.service.core.Resource;
import n3phele.service.core.UnprocessableEntityException;
import n3phele.service.lifecycle.ProcessLifecycle;
import n3phele.service.model.Account;
import n3phele.service.model.Action;
import n3phele.service.model.Cloud;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.Context;
import n3phele.service.model.SignalKind;
import n3phele.service.model.TypedParameter;
import n3phele.service.model.Variable;
import n3phele.service.model.VariableType;
import n3phele.service.model.core.CreateVirtualServerResponse;
import n3phele.service.model.core.Credential;
import n3phele.service.model.core.ExecutionFactoryCreateRequest;
import n3phele.service.model.core.Helpers;
import n3phele.service.model.core.NameValue;
import n3phele.service.model.core.VirtualServer;
import n3phele.service.rest.impl.AccountResource;
import n3phele.service.rest.impl.ActionResource;
import n3phele.service.rest.impl.CloudResource;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.Unindex;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/** Creates one or more VMs on a specified cloud
 * <br> Processes the following context variables
 * <br> n <i>number of vms</i>
 * <br> name <i>vm names</i>. Where n > 1 the names will be name_<i>i</i> <i>i</i>= 0 .. n-1
 * <br> account <i> URI </i> of account on which VMs are to be created
 * <br> 
 * <br> populates its context with the following:
 * <br> cloudVM a list length <i>n</> of virtual machine actions.
 * 
 * @author Nigel Cook
 *
 */
@EntitySubclass
@XmlRootElement(name = "CreateVMAction")
@XmlType(name = "CreateVMAction", propOrder = { "inProgress", "failed" })
@Unindex
@Cache
public class CreateVMAction extends Action {
	final private static java.util.logging.Logger log = java.util.logging.Logger.getLogger(CreateVMAction.class.getName()); 
	@XmlTransient private ActionLogger logger;
	private ArrayList<String> inProgress = new ArrayList<String>();
	private boolean failed = false;
	@Override
	public void init() throws Exception {
		logger = new ActionLogger(this);

		int n = this.context.getIntegerValue("n");
		if(n <= 0) {
			n = 1;
			this.context.putValue("n", 1);
		}
		URI accountURI = Helpers.stringToURI(this.context.getValue("account"));
		if(accountURI == null)
			throw new IllegalArgumentException("Missing account");
		Account account = AccountResource.dao.load(accountURI, this.getOwner());
		Cloud cloud = CloudResource.dao.load(account.getCloud(), this.getOwner());
		
		mergeCloudDefaultsIntoContext(cloud);
		createVMs(account, cloud);
	}
	
	
	@Override
	public boolean call() throws n3phele.service.lifecycle.ProcessLifecycle.WaitForSignalRequest, Exception {
		int myChildren = this.inProgress.size();
		log.info("waiting for children "+myChildren);
		if(myChildren != 0) {
			throw new ProcessLifecycle.WaitForSignalRequest();
		}
		if(failed) {
			throw new UnprocessableEntityException("creation failed");
		}
		return true;
	}

	@Override
	public void cancel() {
		this.killVM();

	}

	@Override
	public void dump() {
		this.killVM();

	}

	@Override
	public void signal(SignalKind kind, String assertion) {
		log.info("Signal "+kind+":"+assertion);
		boolean isChild = this.inProgress.contains(assertion);
		switch(kind) {
		case Adoption:
			log.info((isChild?"Child ":"Unknown ")+assertion+" adoption");
			URI processURI = URI.create(assertion);
			processLifecycle().dump(processURI);
			return;
		case Cancel:
			log.info((isChild?"Child ":"Unknown ")+assertion+" cancelled");
			if(isChild) {
				// FIXME: Not sure if the failure semantics belong here
				// FIXME: Of if there are parameterized..
				// FIXME: --onFailure: killAll
				// FIXME: --onFailure: continue
				this.inProgress.remove(assertion);
				this.killVM();
				failed = true;
			}
			break;
		case Event:
			if(isChild) {
				this.inProgress.remove(assertion);
			} else if(assertion.equals("killVM"))
				killVM();
			else
				log.warning("Ignoring event "+assertion);
			return;
		case Failed:
			log.info((isChild?"Child ":"Unknown ")+assertion+" failed");
			if(isChild) {
				this.inProgress.remove(assertion);
				this.killVM();
				failed = true;
			}
			break;
		case Ok:
			log.info((isChild?"Child ":"Unknown ")+assertion+" ok");
			if(isChild) {
				this.inProgress.remove(assertion);
			}
			break;
		default:
			return;		
		}

	}


	public void killVM() throws NotFoundException {
		List<String> vms = this.context.getListValue("cloudVM");
		log.info("KillVM killing "+vms.size()+" "+vms);
		for(String vm : vms) {
			Action action = ActionResource.dao.load(URI.create(vm));
			processLifecycle().dump(action.getProcess());
		}
		
	}

	private void mergeCloudDefaultsIntoContext(Cloud cloud) {
		for(TypedParameter p : Helpers.safeIterator(cloud.getInputParameters())) {
			if(!this.context.containsKey(p.getName())) {
					if(!Helpers.isBlankOrNull(p.valueOf())) {
					Variable v = new Variable();
					v.setName(p.getName());
					v.setValue(p.valueOf());
					v.setType(VariableType.valueOf(p.getType().toString()));
					this.context.put(v.getName(), v);
					log.info("Inserting "+v);
				}
			} else {
				log.info(p.getName()+" already exists ");
			}
		}
	}
	private void createVMs(Account account, Cloud myCloud) throws Exception {
		Client client = ClientFactory.create();

		ClientFilter factoryAuth = new HTTPBasicAuthFilter(Credential.unencrypted(myCloud.getFactoryCredential()).getAccount(), Credential.unencrypted(myCloud.getFactoryCredential()).getSecret());
		client.addFilter(factoryAuth);
		client.setReadTimeout(90000);
		client.setConnectTimeout(5000);
		WebResource resource = client.resource(myCloud.getFactory());
		
		ExecutionFactoryCreateRequest cr = new ExecutionFactoryCreateRequest();
		cr.name = this.name;
		cr.description = "VM Creation for "+this.getName()+" "+this.getUri();
		cr.location = myCloud.getLocation();
		String keyName = this.context.getValue("keyName");
		if(Helpers.isBlankOrNull(keyName)) {
			this.context.putValue("keyName", "n3phele-"+account.getName());
		}
		cr.parameters = contextToNameValue(myCloud, this.context);
		cr.notification = null; // UriBuilder.fromUri(this.getProcess()).scheme("http").path("event").build();
		cr.idempotencyKey = this.getProcess().toString();

		Credential factoryCredential = Credential.reencrypt(account.getCredential(), Credential.unencrypted(myCloud.getFactoryCredential()).getSecret());
		cr.accessKey = factoryCredential.getAccount(); 
		cr.encryptedSecret = factoryCredential.getSecret();
		cr.owner = this.getProcess();
		Credential agentCredential = new Credential(Resource.get("agentUser", "test"), 
				Resource.get("agentSecret", "password")).encrypt();
		try {

			CreateVirtualServerResult response = createVirtualServers(resource, cr);

			URI location = response.getLocation();
			if(location != null) {
				URI[] refs = response.getRefs();

				log.info(this.name+" "+Integer.toString(refs.length)+" vm(s) creation started. Factory "+location.toString()+" initiating status "+response.getStatus());
				log.info(this.name+" "+Arrays.asList(refs));
				
				if(refs.length == 1) {
					logger.info("vm creation started.");
					boolean forceAgentRestart = false;
					try {
						VirtualServer vs = fetchVirtualServer(client, refs[0]);
						log.info("Server status is "+vs.getStatus());
						if(vs.getStatus().equalsIgnoreCase("Running")) {
							forceAgentRestart  = true;
							log.info("forcing agent restart");
						} 
					} catch (Exception e) {
						log.log(Level.SEVERE, "VM fetch", e);
					}
					createVMProcesses(refs, forceAgentRestart, myCloud.getFactoryCredential(), agentCredential);
				} else {
					createVMProcesses(refs, false, myCloud.getFactoryCredential(), agentCredential);
					logger.info(Integer.toString(refs.length)+" vm(s) creation started.");
				}
			} else {
				log.log(Level.SEVERE, this.name+" vm creation initiation FAILED with status "+response.getStatus());
				logger.error("vm creation initiation FAILED with status "+response.getStatus());
				throw new UnprocessableEntityException("vm creation initiation FAILED with status "+response.getStatus());
			}	
		} catch (Exception e) {
			logger.error("vm creation FAILED with exception "+e.getMessage());
			log.log(Level.SEVERE, "vm creation FAILED with exception ", e);
			throw e;
		} finally {
			ClientFactory.give(client);
		}
		
	}
	
	private void createVMProcesses(URI[] refs, boolean forceAgentRestart, Credential factoryCredential, Credential agentCredential) throws NotFoundException, IllegalArgumentException, ClassNotFoundException {
		
		CloudProcess[] children = new CloudProcess[refs.length];
		URI[] siblingActions = new URI[refs.length];
		String name = this.context.getValue("name");
		if(Helpers.isBlankOrNull(name)) {
			name = this.getName();
		}
		
		this.inProgress.clear();
		for(int i=0; i < refs.length; i++) {
			Context childContext = new Context();
			childContext.putAll(this.context);
			childContext.putValue("name", name);
			if(forceAgentRestart) {
				childContext.putValue("forceAgentRestart", true);
			}
			childContext.putValue("vmIndex", i);
			childContext.putValue("vmFactory", refs[i]);
			childContext.putSecretValue("factoryUser", Credential.unencrypted(factoryCredential).getAccount());
			childContext.putSecretValue("factorySecret", Credential.unencrypted(factoryCredential).getSecret());
			childContext.putSecretValue("agentUser", Credential.unencrypted(agentCredential).getAccount());
			childContext.putSecretValue("agentSecret", Credential.unencrypted(agentCredential).getSecret());
	
			if(refs.length > 1)
				childContext.putValue("name", name+"_"+i );
			children[i] = processLifecycle().spawn(this.getOwner(), childContext.getValue("name"), 
					childContext, null, this.getProcess(), "VM");
			URI siblingProcess = children[i].getUri();
			siblingActions[i] = children[i].getAction();
			this.inProgress.add(siblingProcess.toString());
		}

		this.context.putValue("cloudVM", siblingActions);


		for(CloudProcess child : children) {
			VMAction action = (VMAction) ActionResource.dao.load(child.getAction());
			action.getContext().putValue("cloudVM", siblingActions);
			ActionResource.dao.update(action);
			processLifecycle().init(child);
		}
	}


	private ArrayList<NameValue> contextToNameValue(Cloud cloud, Context context) {
		ArrayList<NameValue> result = new ArrayList<NameValue>();
		for(TypedParameter param : Helpers.safeIterator(cloud.getInputParameters())) {
			String name = param.getName();
			if(this.context.containsKey(name)) {
				String value = this.context.getValue(name);
				NameValue nv = new NameValue(name, value);
				result.add(nv);
			}
		}
		return result;
	}


	/**
	 * @return the inProgress
	 */
	public ArrayList<String> getInProgress() {
		return inProgress;
	}


	/**
	 * @param inProgress the inProgress to set
	 */
	public void setInProgress(ArrayList<String> inProgress) {
		this.inProgress = inProgress;
	}
	
	/**
	 * @return the failed
	 */
	public boolean isFailed() {
		return failed;
	}
	
	/**
	 * @return the failed
	 */
	public boolean getFailed() {
		return failed;
	}


	/**
	 * @param failed the failed to set
	 */
	public void setFailed(boolean failed) {
		this.failed = failed;
	}
	
		/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(
				"CreateVMAction [inProgress=%s, failed=%s, toString()=%s]",
				inProgress, failed, super.toString());
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (failed ? 1231 : 1237);
		result = prime * result
				+ ((inProgress == null) ? 0 : inProgress.hashCode());
		result = prime * result + ((logger == null) ? 0 : logger.hashCode());
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
		CreateVMAction other = (CreateVMAction) obj;
		if (failed != other.failed)
			return false;
		if (inProgress == null) {
			if (other.inProgress != null)
				return false;
		} else if (!inProgress.equals(other.inProgress))
			return false;
		if (logger == null) {
			if (other.logger != null)
				return false;
		} else if (!logger.equals(other.logger))
			return false;
		return true;
	}

	
	
	/*
	 * Unit Testing
	 * ============
	 */


	protected VirtualServer fetchVirtualServer(Client client, URI uri) {
		return client.resource(uri).get(VirtualServer.class);
	}
	
	protected CreateVirtualServerResult createVirtualServers(WebResource resource, ExecutionFactoryCreateRequest createRequest) {
		return new CreateVirtualServerResult(resource.type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, createRequest));

	}
	
	protected ProcessLifecycle processLifecycle() {
		return ProcessLifecycle.mgr();
	}
	
	protected static class CreateVirtualServerResult {
		private ClientResponse response;
		protected CreateVirtualServerResult() {}
		public CreateVirtualServerResult(ClientResponse response) {
			this.response = response;
		}
		
		public URI getLocation() {
			return response.getLocation();
		}
		
		public URI[] getRefs() {
			return response.getEntity(CreateVirtualServerResponse.class).vmList;
		}
		
		public int getStatus() {
			return response.getStatus();
		}
	}
}
