package n3phele.service.nShell;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import n3phele.service.actions.tasks.CreateVMAction;
import n3phele.service.core.NotFoundException;
import n3phele.service.model.CloudProcess;
import n3phele.service.model.Context;
import n3phele.service.model.ShellFragment;
import n3phele.service.model.ShellFragmentKind;
import n3phele.service.model.Variable;
import n3phele.service.model.VariableType;
import n3phele.service.model.core.Helpers;
import n3phele.service.rest.impl.ActionResource;
import n3phele.service.rest.impl.CloudProcessResource;

public class Engine {
	private static Logger log = Logger.getLogger(Engine.class.getName()); 
	private List<ShellFragment> executable;
	private Context context;
	private URI owner;
	private URI parent;
	private URI previous = null;
	
	public Engine(List<ShellFragment> executable, Context context, URI owner, URI parent) {
		this.executable = executable;
		this.context = context;
		this.owner = owner;
		this.parent = parent;
	}

	Object eval(int pc) throws UnexpectedTypeException, IllegalArgumentException {
		ShellFragment s = executable.get(pc);
		switch (s.kind){
		case block:
			return block(s);
		case script:
			return script(s);
		case createvm:
			return createvm(s);
		case destroy:
			return destory(s);
		case expression:
			return expression(s);
		case fileList:
			return fileList(s);
		case forCommand:
			return forCommand(s);
		case literalArg:
			return literalArg(s);
		case log:
			return log(s);
		case on:
			return on(s);
		case option:
			return option(s);
		case passThru:
			return passThru(s);
		case remoteShell:
			return remoteShell(s);
		case variable:
			return variable(s);
		case variableAssign:
			return variableAssign(s);
		default:
			break;
		}
		return s;
		
	}
	
	/*
	 * Expression processing
	 * =====================
	 */




	
	/*
	 * nShell processing
	 * =================
	 */
	
	private Object expression(ShellFragment s) throws IllegalArgumentException, UnexpectedTypeException  {
		Set<String> references = new HashSet<String>(); 
		
		scanExpressionTreeForDependencies(s, references);
		if(!references.isEmpty()) {
			List<URI> dependencies = new ArrayList<URI>();
			for(String identifier : references) {
				Variable v = lookup(identifier);
				if(v.getType() == VariableType.Action) {
					dependencies.add(URI.create(v.getValue()));
				}
			}
			if(!dependencies.isEmpty())
				return dependencies;
		}
		ExpressionEngine expr = new ExpressionEngine(this.executable, this.context);
		Variable v = (Variable) expr.eval(this.executable.indexOf(s));
		return v.getValue();
	}
	
	private List<URI> getDependencies(ShellFragment s)   {
		Set<String> references = new HashSet<String>();
		List<URI> dependencies = new ArrayList<URI>();
		
		scanExpressionTreeForDependencies(s, references);
		if(!references.isEmpty()) {
			for(String identifier : references) {
				Variable v = lookup(identifier);
				if(v.getType() == VariableType.Action) {
					dependencies.add(URI.create(v.getValue()));
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
	
	private Variable variableAssign(ShellFragment s) throws IllegalArgumentException, UnexpectedTypeException {
		Object[] stack = oneChild(s);
		Variable v = lookup(s.value);
		if(v == null) {
			v = new Variable();
			v.setName(s.value);
		}
		// FIXME based on object type update type
		v.setType(VariableType.Object);
		// FIXME this needs to cope with object.field
		v.setValue(stack[0].toString());
		// FIXME It isnt all about context update
		this.context.put(v.getName(), v);
		return v;
	}

	private Variable variable(ShellFragment s) {
		Variable v = lookup(s.value);
		return v;
	}
	
	private Variable createvm(ShellFragment s) {
		/*
		 * < CREATEVM > ( option() )+
		 */
		List<URI> dependency = getDependencies(s);
		//
		// search for the noWait option
		//
		boolean noWait = false;
		for(int i :Helpers.safeIterator(s.children)) {
			ShellFragment f = this.executable.get(i);
			if("noWait".equals(f.value)) {
				noWait = true;
				break;
			}
		}
		if(!noWait && this.previous != null) {
			dependency.add(previous);
		}
		int vmCreated = this.context.getIntegerValue("vmCreated");
		this.context.putValue("vmCreated", vmCreated+1);
		
		String vmNameSeed = this.context.getValue("vmNameSeed");
		if(vmNameSeed == null)
			vmNameSeed = "vm";
		String name = vmNameSeed +"_"+vmCreated;
		
		/*
		 * FIXME: Load cloud and user defaults into context path
		 * 
		 * FIXME: Need to store the executable and a callback for processing prior to init() invocation
		 * FIXME: need to push the options etc into the context. Process has a copy of the executable and the pc of
		 * the action. Something like a "ChildReadtoRun" callback.
		 */
		
		
		
		
		CloudProcess process = null;
		try {
			process = CloudProcessResource.spawn(this.owner, name, this.context, dependency, this.parent, "CreateVM");
			CreateVMAction action = (CreateVMAction) ActionResource.dao.load(process.getTask());
			this.previous = process.getUri();
			this.context.putValue(name, action);
			return this.context.get(name);
		} catch (NotFoundException e) {
			log.log(Level.SEVERE, "Internal error fetching process task", e);
			throw new IllegalArgumentException("Internal error fetching process task", e);
		} catch (IllegalArgumentException e) {
			log.log(Level.SEVERE, "Internal error spawning process", e);
			throw new IllegalArgumentException("Internal error spawning process", e);
		} catch (ClassNotFoundException e) {
			log.log(Level.SEVERE, "Internal error spawning CreateVM", e);
			throw new IllegalArgumentException("Internal error spawning CreateVM", e);
		}

	}
	
	private Object fileList(ShellFragment s) {
		// TODO Auto-generated method stub
		return null;
	}

	private Object block(ShellFragment s) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private Object forCommand(ShellFragment s) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String passThru(ShellFragment s) {
		return s.value;
	}

	private NameValue option(ShellFragment s) throws IllegalArgumentException, UnexpectedTypeException {
		Object[] stack;
		if((stack = oneChild(s))!=null) {
			if(stack[0] instanceof String) {
				return new NameValue((String)stack[0], "true");
			}
			throw new UnexpectedTypeException(stack[0], "String");
		} else if((stack = twoChildren(s))!=null) {
			if(stack[0] instanceof String) {
				if(stack[1] instanceof String) {
					return new NameValue((String)stack[0], (String)stack[1]);
				} else if(stack[1] instanceof Variable) {
					return new NameValue((String)stack[0], ((Variable)stack[1]).getValue());
				} 
					throw new UnexpectedTypeException(stack[1], "String");
			} else
				throw new UnexpectedTypeException(stack[0], "String");
		}
		throw new IllegalArgumentException();
	}

	private Object on(ShellFragment s) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private Object remoteShell(ShellFragment s) {
		// TODO Auto-generated method stub
		return null;
	}

	private String literalArg(ShellFragment s) {
		return s.value;
	}
	
	private Object log(ShellFragment s) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private Object destory(ShellFragment s) {
		// TODO Auto-generated method stub
		return null;
	}

	private Object script(ShellFragment s) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * Helpers
	 * =======
	 */
	private Object[] oneChild(ShellFragment s) throws IllegalArgumentException, UnexpectedTypeException {
		if(s.children.size() == 1) {
			return new Object[] { eval(s.children.get(0)) };
		}
		return null;
	}
	
	private Object[] twoChildren(ShellFragment s) throws IllegalArgumentException, UnexpectedTypeException {
		if(s.children.size() == 2) {
			return new Object[] { eval(s.children.get(0)), eval(s.children.get(1)) };
		}
		return null;
	}
	
	private Object[] threeChildren(ShellFragment s) throws IllegalArgumentException, UnexpectedTypeException {
		if(s.children.size() == 3) {
			return new Object[] { eval(s.children.get(0)), eval(s.children.get(1)), eval(s.children.get(2)) };
		}
		return null;
	}
	
	private boolean isClass(Object o, String expected, Class<?> clazz ) throws UnexpectedTypeException {
		if(o.getClass() == clazz)
			return true;
		throw new UnexpectedTypeException(o, expected);
	}
	
	private Variable lookup(String name) {
		Variable result = this.context.get(name);
		return result;
	}
	
	public static class NameValue {
		public String name;
		public String value;
		public NameValue() {};
		public NameValue(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}
}
