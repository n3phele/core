package n3phele.service.nShell;

import java.util.List;

import n3phele.service.model.ShellFragment;
import n3phele.service.model.ShellFragmentKind;

public abstract class SelfCompilingNode extends SimpleNode {
	protected int compiledIndex = -1;
	protected boolean isNoop;

	public SelfCompilingNode(int i, boolean defaultNoop) {
		super(i);
		this.isNoop = defaultNoop;
	}
	public void compile(List<ShellFragment> result) {
	    ShellFragment me = new ShellFragment();
	    me.kind = toKind();
	    me.value = this.value==null? null : this.value.toString();
	    if(children == null || children.length == 0) {
	    	result.add(me);
	    	this.compiledIndex = result.size()-1;
	    } else if(children.length == 1) {
	    	SelfCompilingNode n = (SelfCompilingNode)children[0];
    		n.compile(result);
	    	if(isNoop && me.value == null) {
	    		this.compiledIndex = n.compiledIndex;
	    	} else {
	    		me.children.add(n.compiledIndex);
	    		result.add(me);
		        this.compiledIndex = result.size()-1;
	    	}
	    } else {
	    	for (int i = 0; i < children.length; ++i) {
		        SelfCompilingNode n = (SelfCompilingNode)children[i];
		        if (n != null) {
		           n.compile(result);
		           me.children.add(n.compiledIndex);
		        }
		        result.add(me);
		        this.compiledIndex = result.size()-1;
		    }
	    }
	   
	}
	
	public void notNoop() {
		this.isNoop = false;
	}
	
	public void isNoop() {
		this.isNoop = true;
	}
	
	abstract ShellFragmentKind toKind();
}
