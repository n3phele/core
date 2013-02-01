package n3phele.service.actions;
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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.Unindex;

import n3phele.service.core.NotFoundException;
import n3phele.service.lifecycle.ProcessLifecycle;
import n3phele.service.lifecycle.ProcessLifecycle.WaitForSignalRequest;
import n3phele.service.model.Action;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.CommandDefinition;
import n3phele.service.model.CommandImplementationDefinition;
import n3phele.service.model.Context;
import n3phele.service.model.FileSpecification;
import n3phele.service.model.FileTracker;
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
import n3phele.service.rest.impl.UserResource;

@EntitySubclass
@XmlRootElement(name = "NShellAction")
@XmlType(name = "NShellAction", propOrder = { "executableName", "pc", "watchFor", "abnormalTermination", "command", "cloud", "start", "executable" })
@Unindex
@Cache
public class NShellAction extends Action {
	private final static Logger log = Logger.getLogger(NShellAction.class.getName()); 
	
	private String executableName;
	private int pc = 0;
	private String watchFor = null;
	private String abnormalTermination = null;
	private String command;
	private String cloud;
	private int start;
	@Embed private List<ShellFragment> executable;
	@XmlTransient 
	@Embed private ActionLogger logger;

	
	public NShellAction() {}
	
	public NShellAction(User owner, String name, Context context, CommandDefinition command, int commandImplementation) {
		super(owner.getUri(), name, context);
		this.command = command.getUri().toString();
		this.executable = command.getImplementations().get(commandImplementation).getCompiled();
	}
	
	@Override
	public void init() throws Exception {
		logger = new ActionLogger(this);
		String arg = this.getContext().getValue("arg");
		String[] argv;
		if(Helpers.isBlankOrNull(arg)) {
			throw new IllegalArgumentException("executable not specified");
		} else {
			argv =	arg.split("[\\s]+");	// FIXME - find a better regex for shell split
		}
		
		URI command = URI.create(argv[0]);
		if(executable == null)
			initalizeExecutableFromCommandImplementationDefinition(command);
		this.pc = 0;
	}
	
	@Override
	public boolean call() throws IllegalArgumentException, UnexpectedTypeException, NotFoundException, ClassNotFoundException, WaitForSignalRequest { 
		ShellFragment script = this.executable.get(this.executable.size()-1);
		for(int i = this.pc; i < script.children.length; i++) {
			this.pc = i;
			if(this.watchFor != null) {;
			log.info("watchFor "+watchFor);
				throw new ProcessLifecycle.WaitForSignalRequest();
			}

			ShellFragment fragment = this.executable.get(this.pc);
			if(assertDependenciesAvailable(buildDependencySetFor(fragment))) {
				log.info("execute "+i);
				this.execute(script.children[i], null);
			} else {
				log.info("has dependencies");
				return false;
			}
		}
		this.pc = script.children.length;
		int myChildren = CloudProcessResource.dao.countChildren(this.getProcess());
		log.info("waiting for children "+myChildren);
		if(myChildren != 0) {
			//
			//	TODO: Possibility that with eventual consistency, the children are all finished
			//	but not finalized state not in the database.
			//
			throw new ProcessLifecycle.WaitForSignalRequest(Calendar.MINUTE, (myChildren==1)?5 : 60);
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
		log.info("Signal "+kind+":"+assertion);
		boolean isChild = this.watchFor.equals(assertion);
		switch(kind) {
		case Adoption:
			log.info((isChild?"Child ":"Unknown ")+assertion+" adoption");
			URI processURI = URI.create(assertion);
			ProcessLifecycle.mgr().dump(processURI);
			return;
		case Cancel:;
			log.info((isChild?"Child ":"Unknown ")+assertion+" cancelled");
			if(isChild) {
				this.abnormalTermination = assertion;
				this.watchFor = null;
			}
			break;
		case Event:
			log.warning("Ignoring event "+assertion);
			return;
		case Failed:
			log.info((isChild?"Child ":"Unknown ")+assertion+" failed");
			if(isChild) {
				this.abnormalTermination = assertion;
				this.watchFor = null;
			}
			break;
		case Ok:
			log.info((isChild?"Child ":"Unknown ")+assertion+" ok");
			if(isChild) {
				this.watchFor = null;
			}
			break;
		default:
			return;		
		}
		
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
	Variable execute(int pc, String variableName) throws UnexpectedTypeException, IllegalArgumentException, NotFoundException, ClassNotFoundException, WaitForSignalRequest {
		ShellFragment s = executable.get(pc);
		switch (s.kind){
		case createvm:
			return createVMCommand(s, variableName);
		case destroy:
			return destroyCommand(s);
		case on:
			return onCommand(s, variableName);
		case log:
			return logCommand(s, variableName);
		case forCommand:
			return forCommand(s);
		case variableAssign:
			return variableAssignCommand(s);
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
	private Variable createVMCommand(ShellFragment createVMFragment, String specifiedName) throws IllegalArgumentException, UnexpectedTypeException, NotFoundException, ClassNotFoundException {
		Context childContext = new Context();
		childContext.putAll(this.context);
		for(int i : createVMFragment.children){
			ShellFragment option = this.executable.get(i);
			String optionName = option.value;
			if(option.children != null && option.children.length != 0) {
				Variable v = optionParse(this.executable.get(option.children[0]));
				childContext.put(optionName, v);
			} else {
				childContext.putValue(optionName, true);
			}
		}
		boolean isAsync = childContext.getBooleanValue("async");
		return makeChildProcess("CreateVM", childContext, specifiedName, isAsync);

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
	private Variable onCommand(ShellFragment onFragment, String specifiedName) throws IllegalArgumentException, UnexpectedTypeException, NotFoundException, ClassNotFoundException {
		Context childContext = new Context();

		ShellFragment expression = this.executable.get(onFragment.children[0]);
		ExpressionEngine ee = new ExpressionEngine(this.executable, this.context);
		URI uri = URI.create(ee.expression(expression).toString());
		CreateVMAction target =  (CreateVMAction) ActionResource.dao.load(uri);
		
		ShellFragment pieces = this.executable.get(onFragment.children[onFragment.children.length-1]);
		for(int i=1; i < onFragment.children.length-1; i++){
			ShellFragment option = this.executable.get(i);
			String optionName = option.value;
			if(option.children != null && option.children.length != 0) {
				Variable v = optionParse(this.executable.get(option.children[0]));
				childContext.put(optionName, v);
			} else {
				childContext.putValue(optionName, true);
			}
		}
		
		CommandDefinition cmd = loadCommandDefinition(this.getCommand());
		resolveProduces(childContext, cmd);
		resolveNeeds(childContext, cmd);
		
		boolean isAsync = childContext.getBooleanValue("async");
		
		
		childContext.putValue("shellCommand", assemblePieces(pieces));
		childContext.putObjectValue("target", target.getUri());
		
		Map<String,FileTracker> fileTable = buildFileTableFromContext(childContext, target.getFileTable());
		List<URI> inputFileCopy = new ArrayList<URI>();
		Map<String, FileTracker> current = target.getFileTable();
		for(FileTracker i : current.values()) {
			if(fileTable.containsKey(i.getName())) {
				URI producer = i.getProcess();
				if(!i.isXfered()) {
					inputFileCopy.add(producer);
				}
				fileTable.remove(i.getName());
			}
		}
		return null; //FIXME
		
/*		 TODO
		for(Entry<String,FileTracker> e : fileTable.entrySet()) {
			List<URI>inputCopy = new ArrayList<URI>();
			FileTracker f = e.getValue();
			if(!f.isOutput()) {
				if(!current.containsKey(f.getName())) {
					URI copy = spawn();
					inputCopy.add(copy);
			
					f.setProcess(copy);
					current.put(f.getName(), f);
				}
			}
			
		}
		CloudProcessResource.init(copy);
		
		
		
		inputFileCopy.addAll(inputCopy);

		
		CloudProcess on = makeChildProcess("On", childContext, specifiedName, isAsync, inputFileCopy);
		 

		 
			for(Entry<String,FileTracker> e : fileTable.entrySet()) {
				List<URI>inputCopy = new ArrayList<URI>();
				FileTracker f = e.getValue();
				if(f.isOutput()) {
					if(!current.containsKey(f.getName())) {
						URI copy = spawn(   { on });
						
						
						f.setProcess(on);
						current.put(f.getName(), f);
					} else {
						dependency = current.get(f.getName()).getProcess();
						URI copy = spawn(   { dependency });
						
					}
				}
				
			}
			CloudProcessResource.init(copy);

		 */
		 
		/* TODO
		 * Ensure required files are on the vm. If not, launch copy processes to put them there.
		 * If a command is listed as producing a required output file, then create a copy command to transfer
		 * the output to the repo, dependent on that command completing. If vm reuse is a factory function
		 * then the filetable can be kept in the action.
		 */
		
	}
	
	protected void resolveNeeds(Context context, CommandDefinition cmd) {
		boolean needsAll = context.getBooleanValue("needsall");
		boolean needsNone = context.getBooleanValue("needsnone");
		Variable needs = context.get("needs");
		if(needs == null) {
			if(!needsNone) {
				// default is NeedsAll
				Map<String,String> inputs = new HashMap<String,String>();
				for(FileSpecification i : cmd.getInputFiles()) {
					URI source = context.getFileValue(i.getName());
					if(source == null && !i.isOptional()) {
						log.warning("Missing file "+i.getName());
					} else {
						inputs.put(i.getName(), i.getName());
					}
				}
				context.putValue("needs", inputs);
			}
		} 
	}
	
	protected void resolveProduces(Context context, CommandDefinition cmd) {
		boolean producesall = context.getBooleanValue("producesall");
		boolean producesnone = context.getBooleanValue("producesnone");
		Variable produces = context.get("produces");
		
		if(produces == null) {
			if(producesall) {
				Map<String,String> outputs = new HashMap<String,String>();
				for(FileSpecification i : cmd.getOutputFiles()) {
					URI source = context.getFileValue(i.getName());
					outputs.put(i.getName(), i.getName());
				}
				// default is producesNone
				context.putValue("produces", outputs);
			}
		}
		
	}
	
	private Map<String,FileTracker> buildFileTableFromContext(Context context, Map<String,FileTracker>current) {
		/*
		 * copyNeededFiles plan:
		 * 
		 * 	compiled commmand will attach a fileList to each On command with a 
		 * --needs and --produces statement referenceable through the context
		 * 
		 * The copyNeededFiles will go to the VM and look at the attached filetable.
		 * If the needed file is in the filetable, then no action, otherwise
		 * a copy process will be started targeted a file transfer. The filetable
		 * will contain the neededFile entry (commandFilename and localName) and the
		 * name of the process responsible for the copy.
		 * 
		 * 
		 * 
		 */

		@SuppressWarnings("unchecked")
		Map<String,String> inputs = (Map<String, String>) context.getObjectValue("needs");
		@SuppressWarnings("unchecked")
		Map<String,String> outputs = (Map<String, String>) context.getObjectValue("produces");
		Map<String,FileTracker> fileTable = new HashMap<String,FileTracker>();
		Set<URI> preExistingDependencies = new HashSet<URI>();
		buildFileTable(fileTable, inputs, true, current, preExistingDependencies);
		buildFileTable(fileTable, outputs, false, current, preExistingDependencies);
		return fileTable;
	}
	
	private void buildFileTable(Map<String,FileTracker> fileTable, Map<String,String> spec, boolean isInput, 
		                          Map<String, FileTracker>current, Set<URI> preExistingDependency){
		for(Entry<String,String> e: spec.entrySet()) {
			String localName = e.getValue();
			
			if(current.containsKey(localName)) {
				FileTracker preexists = current.get(localName);
				preExistingDependency.add(preexists.getProcess());
				continue;
			} else {
				URI repo = context.getFileValue(e.getKey());
				if(repo == null) {
					/*
					 * File not specified (file is optional)
					 */
				} else {
					FileTracker f = new FileTracker();
					f.setName(e.getKey());
					f.setLocalName(localName);
					f.setOutput(!isInput);
					f.setRepo(repo);
					fileTable.put(f.getLocalName(), f);
				}
			}
			
		}
	}

	private Variable optionParse(ShellFragment option) throws IllegalArgumentException, UnexpectedTypeException {
		Variable result = null;
		String optionName = option.value;
		if(option.children != null && option.children.length == 1) {
			ShellFragment arg = this.executable.get(option.children[0]);
			switch(arg.kind) {
			case literalArg:
				result = new Variable(optionName, arg.value);
				break;
			case fileList:
				Map<String,String> fileList = new HashMap<String,String>();
				if(arg.children != null) {
					for(int i : arg.children){
						String combo = this.executable.get(i).value;
						String[] parts = combo.split(":");
						fileList.put(parts[0], parts[1]);
					}
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
	
	
	
	
	/** FOR shell command
	 * 
	 * @param shellFragment
	 * @return
	 * @throws NotFoundException
	 * @throws IllegalArgumentException
	 * @throws ClassNotFoundException
	 */
	private Variable forCommand(ShellFragment shellFragment) throws NotFoundException, IllegalArgumentException, ClassNotFoundException {
		int children = 0;
		if(shellFragment.children != null && (children = shellFragment.children.length) != 3)
			throw new IllegalArgumentException("For command has "+children+" children");
		ShellFragment expression = this.executable.get(shellFragment.children[1]);
		
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
		
		CloudProcess process = ProcessLifecycle.mgr().spawn(this.getOwner(), name, childContext, dependency, this.getProcess(), "NShell");
		NShellAction action = (NShellAction) ActionResource.dao.load(process.getAction());
		action.setExecutable(this.executable.subList(0, myIndex+1));
		this.context.putValue(name, action);
		ProcessLifecycle.mgr().init(process);
		return this.context.get(name);
		
	}
	

	/** variableAssign shell command
	 * 
	 * < VARIABLE > "=" ( simpleCommand() | expression() )
	 * 
	 * @param varAssign
	 * @return
	 * @throws NotFoundException
	 * @throws IllegalArgumentException
	 * @throws ClassNotFoundException
	 * @throws WaitForSignalRequest 
	 * @throws UnexpectedTypeException 
	 */
	private Variable variableAssignCommand(ShellFragment varAssign) throws NotFoundException, IllegalArgumentException, ClassNotFoundException, UnexpectedTypeException, WaitForSignalRequest {

		String variableName = varAssign.value;
		ShellFragment fragment = this.executable.get(varAssign.children[0]);
		if(fragment.kind == ShellFragmentKind.expression) {
			ExpressionEngine ee = expressionEngineFactory(varAssign.children[0]);
			this.context.putObjectValue(variableName, ee.run());
			return this.context.get(variableName);
		} else {
			return this.execute(varAssign.children[0], variableName);
		}
	}
	
	
	/** LOG shell command
	 * 
	 * LOG:: < LOG > pieces()
	 * PIECES:: ( expression | passThru )+
	 * 
	 * @param logFragment
	 * @return
	 * @throws NotFoundException
	 * @throws IllegalArgumentException
	 * @throws ClassNotFoundException
	 * @throws UnexpectedTypeException 
	 */
	private Variable logCommand(ShellFragment logFragment, String specifiedName) throws NotFoundException, IllegalArgumentException, ClassNotFoundException, UnexpectedTypeException {
		
		Context childContext = new Context();
		childContext.putAll(this.context);
		ShellFragment pieces = this.executable.get(logFragment.children[0]);
		childContext.putValue("arg", assemblePieces(pieces));
		
		return makeChildProcess("Log", childContext, specifiedName, false);
	}
	
	
	/** DESTROY shell command
	 * 
	 * DESTROY:: < DESTROY >	 expression()
	 * 
	 * @param destroy
	 * @return
	 * @throws NotFoundException
	 * @throws IllegalArgumentException
	 * @throws ClassNotFoundException
	 * @throws UnexpectedTypeException 
	 */
	private Variable destroyCommand(ShellFragment destroy) throws NotFoundException, IllegalArgumentException, ClassNotFoundException, UnexpectedTypeException {
		Variable result = null;
		ShellFragment fragment = this.executable.get(destroy.children[0]);
		if(fragment.kind == ShellFragmentKind.expression) {
			ExpressionEngine ee = expressionEngineFactory(destroy.children[0]);
			URI action = URI.create((String)ee.run());
			CreateVMAction createVMAction = (CreateVMAction) ActionResource.dao.load(action, UserResource.dao.load(this.getOwner()));
			createVMAction.killVM();
			result = new Variable(createVMAction.getName(), createVMAction);
		}
		return result;
	}
	

	/*
	 * Helpers
	 * =======
	 */
	
	private Variable lookupAction(String name) {
		int fieldIdx = name.indexOf(".");
		Variable result;
		String primaryName = name;
		if(fieldIdx > 0) {
			primaryName = name.substring(0, fieldIdx);
		}
		result = this.context.get(primaryName);
		return result;
	}
	
	private Set<URI> buildDependencySetFor(ShellFragment s)   {
		Set<String> references = new HashSet<String>();
		Set<URI> dependencies = new HashSet<URI>();
		
		scanExpressionTreeForDependencies(s, references);
		if(!references.isEmpty()) {
			for(String identifier : references) {
				Variable v = lookupAction(identifier);
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
		if(s.children != null) {
			for(int i : s.children) {
				ShellFragment f = this.executable.get(i);
				if(f.kind == ShellFragmentKind.identifier) {
					dependencies.add(f.value);
				}
				if(f.children != null && f.children.length != 0) {
					scanExpressionTreeForDependencies(f, dependencies);
				}
			}
		}
	}
	
	/** sets process to block until a set of dependencies is available
	 * @param dependencies set of dependencies
	 * @return false if process must block, true if dependences are available
	 */
	private boolean assertDependenciesAvailable(Set<URI> dependencies) {
		List<URI> dependency = null;
		if(!dependencies.isEmpty()) {
			dependency = new ArrayList<URI>(dependencies.size());
			for(Action action: ActionResource.dao.load(dependencies)) {
				dependency.add(action.getProcess());
			}
			return !ProcessLifecycle.mgr().setDependentOn(this.getProcess(), dependency);
		}
		return true;
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
		CommandDefinition cmd = loadCommandDefinition(baseURI);
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
	
	private Variable makeChildProcess(String processType, Context childContext, String variableName, boolean isAsync) throws NotFoundException, IllegalArgumentException, ClassNotFoundException {
		
		String optionName = context.getValue("name");
		
		int created = this.context.getIntegerValue(processType+"Created");
		this.context.putValue(processType+"Created", created+1);
		String childName = variableName;
		if(Helpers.isBlankOrNull(childName)) {
			childName = optionName;
			if(Helpers.isBlankOrNull(childName)) {
				String nameSeed = context.getValue(processType+"NameSeed");
				if(nameSeed == null)
					nameSeed = processType;
				childName = nameSeed +"_"+created;
			}
			variableName = childName;
		}
		
		CloudProcess child = ProcessLifecycle.mgr().spawn(this.getOwner(), childName, childContext, null, this.getProcess(), processType);
		if(!isAsync)
			this.setWatchFor(child.getUri());	
		this.context.putActionValue(variableName, child.getAction());
		ProcessLifecycle.mgr().init(child);
		return this.context.get(variableName);
		
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
	 * @param executable the executable to set
	 */
	public void setExecutable(List<ShellFragment> executable) {
		this.executable = executable;
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
	
	/**
	 * @return the abnormally terminating child URI
	 */
	public URI getAbnormallyTermination() {
		return Helpers.stringToURI(abnormalTermination);
	}

	/**
	 * @param child set the abnormally terminating child
	 */
	public void setAbnormallyTermination(URI child) {
		this.abnormalTermination = Helpers.URItoString(child);
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
				.format("NShellActionTest %s [executableName=%s, pc=%s, watchFor=%s, abnormalTermination=%s, command=%s, cloud=%s, start=%s, executable=%s]",
						super.toString(),
						executableName, pc, watchFor, abnormalTermination, command, cloud, start,
						executable);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((abnormalTermination == null) ? 0 : abnormalTermination
						.hashCode());
		result = prime * result + ((cloud == null) ? 0 : cloud.hashCode());
		result = prime * result + ((command == null) ? 0 : command.hashCode());
		result = prime * result
				+ ((executable == null) ? 0 : executable.hashCode());
		result = prime * result
				+ ((executableName == null) ? 0 : executableName.hashCode());
		result = prime * result + pc;
		result = prime * result + start;
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
		if (abnormalTermination == null) {
			if (other.abnormalTermination != null)
				return false;
		} else if (!abnormalTermination.equals(other.abnormalTermination))
			return false;
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
		if (pc != other.pc)
			return false;
		if (start != other.start)
			return false;
		if (watchFor == null) {
			if (other.watchFor != null)
				return false;
		} else if (!watchFor.equals(other.watchFor))
			return false;
		return true;
	}
	

	/*
	 * Testing
	 * =======
	 */	

	/** Fetches the command definition associated with a data store URI.
	 * Override method for unit testing
	 * @param uri
	 * @return
	 * @throws NotFoundException
	 */
	protected CommandDefinition loadCommandDefinition(URI uri) throws NotFoundException {
		CommandDefinition cmd = new CommandDefinition();
		cmd.setUri(uri);
		return cmd;
	}
	
	

	protected ExpressionEngine expressionEngineFactory(int index) {
		return new ExpressionEngine(this.executable.subList(0, index+1), this.context);
	}
}
