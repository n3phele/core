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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import n3phele.service.core.NotFoundException;
import n3phele.service.core.UnprocessableEntityException;
import n3phele.service.lifecycle.ProcessLifecycle;
import n3phele.service.model.Action;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.Command;
import n3phele.service.model.CommandImplementationDefinition;
import n3phele.service.model.Context;
import n3phele.service.model.ShellFragment;
import n3phele.service.model.SignalKind;
import n3phele.service.model.core.Helpers;
import n3phele.service.rest.impl.ActionResource;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.Unindex;

/** Creates one or more execution instances of a set of statements that form the body of the for loop
 * <br> Processes the following context variables
 * <br> n <i>number of cycles in the for loop</i>
 * <br> chunkSize <i>number of concurrent instances in the for loop</i>
 * <br> iterator <i>loop iterator variable</i>
 * <br> 
 * <br> populates its context with the following:
 * <br> <i>iterator</i> an integer ranging from 0..<i>n</>-1.
 * 
 * @author Nigel Cook
 *
 */
@EntitySubclass
@XmlRootElement(name = "ForAction")
@XmlType(name = "ForAction", propOrder = { "inProgress", "failed", "command", "executableName", "cloud", "start" })
@Unindex
@Cache
public class ForAction extends Action {
	final private static java.util.logging.Logger log = java.util.logging.Logger.getLogger(ForAction.class.getName()); 
	@XmlTransient private ActionLogger logger;
	private ArrayList<String> inProgress = new ArrayList<String>();
	@XmlTransient @Embed  private List<ShellFragment> executable;
	private int start;
	private String executableName;
	private String command;
	private String cloud;

	
	
	
	
	private boolean failed = false;
	@Override
	public void init() throws Exception {
		logger = new ActionLogger(this);
		
		int n = this.context.getIntegerValue("n");
		if(n <= 0) {
			n = 1;
			this.context.putValue("n", 1);
		}
		
		int chunkSize = this.context.getIntegerValue("chunkSize");
		if(chunkSize <= 0) {
			chunkSize = n;
			this.context.putValue("chunkSize", chunkSize);
		}
		
		if(this.executable == null || this.executable.isEmpty()) {
			initalizeExecutableFromCommandImplementationDefinition(this.context.getURIValue("arg"));
		}
		

		createNextChunk();
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
		return createNextChunk();
	}

	@Override
	public void cancel() {
		this.killChildren();

	}

	@Override
	public void dump() {
		this.killChildren();

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
				this.killChildren();
				failed = true;
			}
			break;
		case Event:
			if(isChild) {
				this.inProgress.remove(assertion);
			} else if(assertion.equals("killVM"))
				killChildren();
			else
				log.warning("Ignoring event "+assertion);
			return;
		case Failed:
			log.info((isChild?"Child ":"Unknown ")+assertion+" failed");
			if(isChild) {
				this.inProgress.remove(assertion);
				this.killChildren();
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
	
	/** createNextChunk
	 * @return true end of chunk creation, no new chunks generated
	 * @throws ClassNotFoundException 
	 * @throws IllegalArgumentException 
	 * @throws NotFoundException 
	 */
	private boolean createNextChunk() throws NotFoundException, IllegalArgumentException, ClassNotFoundException {
		String iterator = this.context.getValue("iterator");
		int n = this.context.getIntegerValue("n");
		int i = this.context.getIntegerValue(iterator);
		int chunk = this.context.getIntegerValue("chunkSize");
		if(i < n ) {
			int lim = i + chunk;
			if(lim > n) 
				lim = n;
		
			Context childContext = new Context();
			childContext.putAll(this.context);
			
			for(; i < lim; i++) {
				childContext.putValue(iterator, i);
				childContext.remove("name");
				
				CloudProcess process = processLifecycle().spawn(this.getOwner(), this.getName()+"_"+i, childContext, null, this.getProcess(), "NShell");
				NShellAction action = (NShellAction) ActionResource.dao.load(process.getAction());
				
				action.setExecutable(this.executable);
				action.setCloud(this.getCloud());
				action.setCommand(this.getCommand());
				action.setExecutableName(this.getExecutableName());
				ActionResource.dao.update(action);
				this.context.putValue(this.getName()+"_"+i, action);
				this.inProgress.add(process.getUri().toString());
				processLifecycle().init(process);
			}
			this.context.putValue(iterator, i);
			return false;
		} else {
			return true;
		}
		
	}


	public void killChildren() throws NotFoundException {
		// FIXME: Not sure this is correct
		// FIXME: Perhaps we should spawn a destroyAction or
		// FIXME: directly fetch the actions and invoke killVM directly
		List<String> subShells = this.context.getListValue("forChildren");
		log.info("KillChildren killing "+subShells.size()+" "+subShells);
		for(String shell : subShells) {
			Action action = ActionResource.dao.load(URI.create(shell));
			processLifecycle().dump(action.getProcess());
		}
		
	}
	
	/** Fetches the command definition associated with a data store URI.
	 * @param uri reference to the command definition in the form: 
	 * <BR> <i>datastore-uri</i>#<i>name of CommandImplementationDefinition</i>?start=<i>start</i>
	 * <BR>
	 * For example: http://foo.com/resources/command/5#EC2?start=5
	 * If start is not specified, then the default starting point is assumed.
	 *
	 * @return
	 */
	protected CommandImplementationDefinition initalizeExecutableFromCommandImplementationDefinition(URI uri) throws NotFoundException {
		URI baseURI;
		try {
			baseURI = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), null, null);
			
		} catch (URISyntaxException e) {
			log.log(Level.SEVERE, "Parse of "+uri, e);
			throw new NotFoundException(uri.toString());
		}
		log.info("Uri ="+uri.toString());
		String name = uri.getFragment(); 
		String start = uri.getQuery();
		Command cmd = loadCommandDefinition(baseURI);
		for(CommandImplementationDefinition cid : Helpers.safeIterator(cmd.getImplementations())) {
			log.info("looking for "+name+" against "+cid.getName());
			if(cid.getName().equals(name)) {
				if(start != null) {
					int postStart = start.indexOf("start=");
					int index = Integer.valueOf(start.substring(postStart+1));
					this.start = index;
					this.executable = cid.getCompiled().subList(0, index+1);
				} else {
					this.start = -1;
					this.executable = cid.getCompiled();
				}
				this.executableName = cid.getName()+":"+cmd.getName()+" "+cmd.getVersion()+(cmd.isPreferred()?"":"*");
				this.command = baseURI.toString();
				this.cloud = cid.getName();
				return cid;
			}
		}
		throw new NotFoundException(uri.toString());
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
	 * @return the executable
	 */
	public List<ShellFragment> getExecutable() {
		return executable;
	}


	/**
	 * @param executable the executable to set
	 */
	public void setExecutable(List<ShellFragment> executable) {
		this.executable = executable;
	}


	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}


	/**
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}


	/**
	 * @return the executableName
	 */
	public String getExecutableName() {
		return executableName;
	}


	/**
	 * @param executableName the executableName to set
	 */
	public void setExecutableName(String executableName) {
		this.executableName = executableName;
	}


	/**
	 * @return the command
	 */
	public URI getCommand() {
		return Helpers.stringToURI(command);
	}


	/**
	 * @param command the command to set
	 */
	public void setCommand(URI command) {
		this.command = Helpers.URItoString(command);
	}


	/**
	 * @return the cloud
	 */
	public String getCloud() {
		return cloud;
	}


	/**
	 * @param cloud the cloud to set
	 */
	public void setCloud(String cloud) {
		this.cloud = cloud;
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
	
	/*
	 * Object Housekeeping
	 * ===================
	 */
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("ForAction [logger=%s, inProgress=%s, executable=%s, start=%s, executableName=%s, command=%s, cloud=%s, failed=%s, toString()=%s]",
						logger, inProgress, executable, start, executableName,
						command, cloud, failed, super.toString());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((cloud == null) ? 0 : cloud.hashCode());
		result = prime * result + ((command == null) ? 0 : command.hashCode());
		result = prime * result
				+ ((executable == null) ? 0 : executable.hashCode());
		result = prime * result
				+ ((executableName == null) ? 0 : executableName.hashCode());
		result = prime * result + (failed ? 1231 : 1237);
		result = prime * result
				+ ((inProgress == null) ? 0 : inProgress.hashCode());
		result = prime * result + ((logger == null) ? 0 : logger.hashCode());
		result = prime * result + start;
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
		ForAction other = (ForAction) obj;
		if (cloud == null) {
			if (other.cloud != null)
				return false;
		} else if (!cloud.equals(other.cloud))
			return false;
		if (command == null) {
			if (other.command != null)
				return false;
		} else if (!command.equals(other.command))
			return false;
		if (executable == null) {
			if (other.executable != null)
				return false;
		} else if (!executable.equals(other.executable))
			return false;
		if (executableName == null) {
			if (other.executableName != null)
				return false;
		} else if (!executableName.equals(other.executableName))
			return false;
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
		if (start != other.start)
			return false;
		return true;
	}

	
	
	/*
	 * Unit Testing
	 * ============
	 */
	
	protected ProcessLifecycle processLifecycle() {
		return ProcessLifecycle.mgr();
	}
	
	/** Fetches the command definition associated with a data store URI.
	 * Override method for unit testing
	 * @param uri
	 * @return
	 * @throws NotFoundException
	 */
	protected Command loadCommandDefinition(URI uri) throws NotFoundException {
		Command cmd = new Command();
		cmd.setUri(uri);
		return cmd;
	}

	
}
