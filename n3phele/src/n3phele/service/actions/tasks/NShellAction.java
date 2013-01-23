package n3phele.service.actions.tasks;
/**
 *
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
 */
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.Unindex;

import n3phele.service.core.NotFoundException;
import n3phele.service.model.Action;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.Context;
import n3phele.service.model.ShellFragment;
import n3phele.service.model.ShellFragmentKind;
import n3phele.service.model.SignalKind;
import n3phele.service.model.Variable;
import n3phele.service.model.VariableType;
import n3phele.service.model.core.Helpers;
import n3phele.service.model.core.User;
import n3phele.service.nShell.ExpressionEngine;
import n3phele.service.nShell.UnexpectedTypeException;
import n3phele.service.rest.impl.ActionResource;
import n3phele.service.rest.impl.CloudProcessResource;
import n3phele.service.rest.impl.CloudProcessResource.WaitForSignalRequest;

@EntitySubclass
@XmlRootElement(name = "NShellAction")
@XmlType(name = "NShellAction", propOrder = { "context", "executable", "childProcess", "childComplete", "childStatus" })
@Unindex
@Cache
public class NShellAction extends Action {
	private final static Logger log = Logger.getLogger(NShellAction.class.getName()); 
	@Embed private List<ShellFragment> executable;
	private String parent;
	private int pc = 0;
	private String watchFor = null;
	
	public NShellAction(List<ShellFragment> executable, String name, Context context, User owner, URI parent) {
		super(owner.getUri(), name, context);
		this.executable = executable;
		this.parent = Helpers.URItoString(parent);
	}
	
	@Override
	public void init() throws Exception {
		this.pc = executable.size()-1;
		
	}
	
	@Override
	public boolean call() throws IllegalArgumentException, UnexpectedTypeException, NotFoundException, ClassNotFoundException, WaitForSignalRequest { 
		ShellFragment script = this.executable.get(this.executable.size()-1);
		for(int i = this.pc; i < script.children.size(); i++) {
			if(this.watchFor != null) {
				this.pc = i;
				throw new CloudProcessResource.WaitForSignalRequest();
			}
			this.execute(script.children.get(i));
		}
		return true;
		
	}
	
	
	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void signal(SignalKind kind, String assertion) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	

	/** Processes the executable at the pc, creating a series of executable subshell processes
	 *  responsible for the top level command objects in the syntax tree.
	 *  script:: (
	 *  	  createvm()
	 *		| on()
	 *		| log()
	 * 		| destroy()
	 *		| forCommand
	 *		| variableAssign()
  	 *	) *
  	 *  
	 * @param pc
	 * @return
	 * @throws UnexpectedTypeException
	 * @throws IllegalArgumentException
	 * @throws ClassNotFoundException 
	 * @throws NotFoundException 
	 * @throws WaitForSignalRequest 
	 */
	Variable execute(int pc) throws UnexpectedTypeException, IllegalArgumentException, NotFoundException, ClassNotFoundException, WaitForSignalRequest {
		ShellFragment s = executable.get(pc);
		switch (s.kind){
		case createvm:
			return createVM(s, null);
		case destroy:
		case on:
			return on(s, null);
		case log:
			throw new IllegalArgumentException("unimplemented");
		case forCommand:
			return forCommand(s);
		case variableAssign:
			return variableAssign(s);
		default:
			throw new IllegalArgumentException("Unexpected node "+s.kind);
		}		
	}
	
	/** createVM shell command
	 * 
	 * createvm:: < CREATEVM > ( option() )+ 
	 * option:: (
	 *				(< OPTION > arg() )
	 *			  |  < NO_ARG_OPTION >
  	 *			)
	 * @param createVMFragment createVM parse tree fragment
	 * @param specifiedName	name of context variable object will be assigned to
	 * @return
	 * @throws UnexpectedTypeException 
	 * @throws IllegalArgumentException 
	 * @throws ClassNotFoundException 
	 * @throws NotFoundException 
	 */
	private Variable createVM(ShellFragment createVMFragment, String specifiedName) throws IllegalArgumentException, UnexpectedTypeException, NotFoundException, ClassNotFoundException {
		Context childContext = new Context();
		Set<URI> dependencies = getDependencies(createVMFragment);
		for(int i : createVMFragment.children){
			ShellFragment option = this.executable.get(i);
			String optionName = option.value;
			if(option.children != null && !option.children.isEmpty()) {
				Variable v = optionParse(this.executable.get(option.children.get(0)));
				childContext.put(optionName, v);
			} else {
				childContext.putValue(optionName, true);
			}
		}
		boolean isAsync = childContext.getBooleanValue("async");
		String optionName = childContext.getValue("name");
		
		int created = this.context.getIntegerValue(createVMFragment.kind+"Created");
		this.context.putValue(createVMFragment.kind+"Created", created+1);
		String name = specifiedName;
		if(Helpers.isBlankOrNull(name)) {
			name = optionName;
			if(Helpers.isBlankOrNull(name)) {
				String nameSeed = this.context.getValue(createVMFragment.kind+"NameSeed");
				if(nameSeed == null)
					nameSeed = createVMFragment.kind.toString();
				name = nameSeed +"_"+created;
			}
		}
		if(Helpers.isBlankOrNull(specifiedName)) {
			specifiedName = name;
		}
		
		CloudProcessResource cpr = new CloudProcessResource();
		List<URI> dependency = null;
		if(!dependencies.isEmpty()) {
			dependency = new ArrayList<URI>(dependencies.size());
			for(Action action: ActionResource.dao.load(dependencies)) {
				dependency.add(action.getProcess());
			}
		}
		
		CloudProcess child = cpr.spawn(this.getOwner(), name, childContext, dependency, getParent(), "CreateVM");
		if(!isAsync) {
			setWatchFor(child.getUri());
			cpr.init(child);
		}
		this.context.putActionValue(specifiedName, child.getTask());
		return this.context.get(specifiedName);
	}
	
	/** ON shell command
	 * 
	 * ON:: < ON > expression() ( option() )*  pieces()
	 * PIECES:: ( expression | passThru )+
	 * option:: (
	 *				(< OPTION > arg() )
	 *			  |  < NO_ARG_OPTION >
  	 *			)
	 * 
	 * @param onFragment
	 * @param specifiedName
	 * @return
	 * @throws IllegalArgumentException
	 * @throws UnexpectedTypeException
	 * @throws NotFoundException
	 * @throws ClassNotFoundException
	 */
	private Variable on(ShellFragment onFragment, String specifiedName) throws IllegalArgumentException, UnexpectedTypeException, NotFoundException, ClassNotFoundException {
		Context childContext = new Context();
		Set<URI> dependencies = getDependencies(onFragment);
		ShellFragment expression = this.executable.get(onFragment.children.get(0));
		ExpressionEngine ee = new ExpressionEngine(this.executable, this.context);
		URI uri = URI.create(ee.expression(expression).toString());
		CreateVMAction target =  (CreateVMAction) ActionResource.dao.load(uri);
									 // NB: Assuming that the reference is to an action which inserts a dependency
									 // $$myCreateAction.vmList[$$i]
		
		ShellFragment pieces = this.executable.get(onFragment.children.get(onFragment.children.size()-1));
		for(int i=1; i < onFragment.children.size()-1; i++){
			ShellFragment option = this.executable.get(i);
			String optionName = option.value;
			if(option.children != null && !option.children.isEmpty()) {
				Variable v = optionParse(this.executable.get(option.children.get(0)));
				childContext.put(optionName, v);
			} else {
				childContext.putValue(optionName, true);
			}
		}
		boolean isAsync = childContext.getBooleanValue("async");
		String optionName = childContext.getValue("name");
		
		int created = this.context.getIntegerValue(onFragment.kind+"Created");
		this.context.putValue(onFragment.kind+"Created", created+1);
		String name = specifiedName;
		if(Helpers.isBlankOrNull(name)) {
			name = optionName;
			if(Helpers.isBlankOrNull(name)) {
				String nameSeed = this.context.getValue(onFragment.kind+"NameSeed");
				if(nameSeed == null)
					nameSeed = onFragment.kind.toString();
				name = nameSeed +"_"+created;
			}
		}
		if(Helpers.isBlankOrNull(specifiedName)) {
			specifiedName = name;
		}
		
		CloudProcessResource cpr = new CloudProcessResource();
		List<URI> dependency = null;
		if(!dependencies.isEmpty()) {
			dependency = new ArrayList<URI>(dependencies.size());
			for(Action action: ActionResource.dao.load(dependencies)) {
				dependency.add(action.getProcess());
			}
		}
		
		childContext.putValue("shellCommand", assemblePieces(pieces));
		childContext.putObjectValue("target", target.getUri());
		
		/* TODO
		 * Ensure required files are on the vm. If not, launch copy processes to put them there.
		 * If a command is listed as producing a required output file, then create a copy command to transfer
		 * the output to the repo, dependent on that command completing.
		 */
		
		CloudProcess child = cpr.spawn(this.getOwner(), name, childContext, dependency, this.getParent(), "On");
		if(!isAsync) {
			this.setWatchFor(child.getUri());	
		}
		this.context.putActionValue(specifiedName, child.getTask());
		cpr.init(child);
		return this.context.get(specifiedName);
	}
	
	
	
	
	private Variable optionParse(ShellFragment option) throws IllegalArgumentException, UnexpectedTypeException {
		Variable result = null;
		String optionName = option.value;
		if(option.children != null && option.children.size() == 1) {
			ShellFragment arg = this.executable.get(option.children.get(0));
			switch(arg.kind) {
			case literalArg:
				result = new Variable(optionName, arg.value);
				break;
			case fileList:
				Map<String,String> fileList = new HashMap<String,String>();
				for(int i : Helpers.safeIterator(arg.children)){
					String combo = this.executable.get(i).value;
					String[] parts = combo.split("|");
					fileList.put(parts[0], parts[1]);
				}
				result = new Variable(optionName, fileList);
				break;
			case expression:
				result = new Variable(optionName, new ExpressionEngine(this.executable, this.context).expression(arg));
				break;
			default:
				throw new IllegalArgumentException("Found "+arg.kind);
			}
		}
		
		return result;
	}
	
	private String assemblePieces(ShellFragment pieces) throws IllegalArgumentException, UnexpectedTypeException {
		StringBuffer result = new StringBuffer();
		ExpressionEngine ee = new ExpressionEngine(this.executable, this.context);
		for(int i : pieces.children) {
			ShellFragment piece = this.executable.get(i);
			if(piece.kind == ShellFragmentKind.passThru) {
				result.append(piece.value);
			} else {
				result.append(ee.expression(piece).toString());
			}
		}
		return result.toString();
	}
	
	
	
	
	private Variable forCommand(ShellFragment shellFragment) throws NotFoundException, IllegalArgumentException, ClassNotFoundException {
		int children = 0;
		if(shellFragment.children != null && (children = shellFragment.children.size()) != 3)
			throw new IllegalArgumentException("For command has "+children+" children");
		ShellFragment expression = this.executable.get(shellFragment.children.get(1));
		Set<URI> dependency = getDependencies(expression);
		
		int forCreated = this.context.getIntegerValue("forCreated");
		this.context.putValue("forCreated", forCreated+1);
		
		String forNameSeed = this.context.getValue("forNameSeed");
		if(forNameSeed == null)
			forNameSeed = "for";
		String name = forNameSeed +"_"+forCreated;

		//return makeSubshell(shellFragment, name, dependency, this.context);
		throw new IllegalArgumentException("unimplemented");
	}
	
	private Variable makeSubshell(ShellFragment shellFragment, String name, List<URI>dependency, Context context) throws NotFoundException, IllegalArgumentException, ClassNotFoundException {
		Context childContext = new Context();
		childContext.putAll(context);
		int myIndex = this.executable.indexOf(shellFragment);
		
		CloudProcessResource cpr = new CloudProcessResource();
		CloudProcess process = cpr.spawn(this.getOwner(), name, childContext, dependency, this.getParent(), "NShell");
		NShellAction action = (NShellAction) ActionResource.dao.load(process.getTask());
		action.setExecutable(this.executable.subList(0, myIndex+1));
		this.context.putValue(name, action);
		cpr.init(process);
		return this.context.get(name);
		
	}
	

	private Variable variableAssign(ShellFragment varAssign) throws NotFoundException, IllegalArgumentException, ClassNotFoundException {
		/*
		 * < VARIABLEASSIGN > ( simpleCommand() | expression() )
		 */
		String variableName = varAssign.value;
		ShellFragment simpleCommand= this.executable.get(varAssign.children.get(0));
		//if(simpleCommand.kind == ShellFragmentKind.expression) {
			// not a simple command - wrap in a shell and delay eval
		//	Variable wrappedAssign = cloudCommandSubshell(varAssign, null);

		//	return wrappedAssign;
		//} else {
		//	return cloudCommandSubshell(simpleCommand, variableName);
		//}
		throw new IllegalArgumentException("unimplemented");
	}

	/*
	 * Helpers
	 * =======
	 */
	
	private Variable lookup(String name) {
		//TODO:
		// This function needs to parse the name looking for "."
		// The lookup should be on the before the dot part.
		// The post dot part is context of the referenced object, or a reflection access
		// $$foo.bar => access the foo object and then get bar from the context of foo or perhaps getBar();
		// the processing might be dependent on the type of object that foo is.
		// If foo is an URI then get the object from the dao. 
		//
		// Easier that objects conform to the current set and it includes action objects. Assume createVMAction objects exist in
		// the context, until the VM is deleted. Or when the vm is deleted the createVMaction object starts to throw exceptions.
		// in this way, we dont need to have a virtualmachine table class, it is all proxied by the createVM action and its context.
		// We can delete the Vm and update the object reference even if it is finalized.
		Variable result = this.context.get(name);
		return result;
	}
	
	private Set<URI> getDependencies(ShellFragment s)   {
		Set<String> references = new HashSet<String>();
		Set<URI> dependencies = new HashSet<URI>();
		
		scanExpressionTreeForDependencies(s, references);
		if(!references.isEmpty()) {
			for(String identifier : references) {
				Variable v = lookup(identifier);
				
				if(v != null) {
					if(v.getType() == VariableType.Action) {
						URI actionURI = URI.create(v.getValue());
						dependencies.add(actionURI);
					}
				} 
			}
		}
		return dependencies;
	}
	
	
	private void scanExpressionTreeForDependencies(ShellFragment s, Set<String> dependencies) {
		for(int i : Helpers.safeIterator(s.children)) {
			ShellFragment f = this.executable.get(i);
			if(f.kind == ShellFragmentKind.identifier) {
				dependencies.add(f.value);
			}
			if(f.children != null && !f.children.isEmpty()) {
				scanExpressionTreeForDependencies(f, dependencies);
			}
		}
	}




	/*
	 * Getters and Setters
	 * ===================
	 * 
	 */
	
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
	 * @return the parent
	 */
	public URI getParent() {
		return Helpers.stringToURI(parent);
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(URI parent) {
		this.parent = Helpers.URItoString(parent);
	}

	/**
	 * @return the pc
	 */
	public int getPc() {
		return pc;
	}

	/**
	 * @param pc the pc to set
	 */
	public void setPc(int pc) {
		this.pc = pc;
	}

	/**
	 * @return the watchFor
	 */
	public URI getWatchFor() {
		return Helpers.stringToURI(watchFor);
	}

	/**
	 * @param watchFor the watchFor to set
	 */
	public void setWatchFor(URI watchFor) {
		this.watchFor = Helpers.URItoString(watchFor);
	}
	
	
	/*
	 * Object housekeeping
	 * ===================
	 */
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("NShellAction [executable=%s, parent=%s, pc=%s, watchFor=%s, toString()=%s]",
						executable, parent, pc, watchFor, super.toString());
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((executable == null) ? 0 : executable.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + pc;
		result = prime * result
				+ ((watchFor == null) ? 0 : watchFor.hashCode());
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
		NShellAction other = (NShellAction) obj;
		if (executable == null) {
			if (other.executable != null)
				return false;
		} else if (!executable.equals(other.executable))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (pc != other.pc)
			return false;
		if (watchFor == null) {
			if (other.watchFor != null)
				return false;
		} else if (!watchFor.equals(other.watchFor))
			return false;
		return true;
	}
	
}
