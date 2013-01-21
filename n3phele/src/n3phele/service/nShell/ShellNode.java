package n3phele.service.nShell;

import n3phele.service.model.ShellFragmentKind;



public class ShellNode extends SelfCompilingNode {

	public ShellNode(int i) {
		super(i, false);
	}


	/* (non-Javadoc)
	 * @see n3phele.service.nShell.SimpleNode#toString()
	 */
	@Override
	public String toString() {
		Object o = this.jjtGetValue();
		if(o instanceof String) {
			o = "\""+((String)o)+"\"";
		}
		return ShellTreeConstants.jjtNodeName[id]+":"+o;
	}

	
	public static Node jjtCreate(int id) {
		return new ShellNode(id);
	}


	@Override
	ShellFragmentKind toKind() {
		return ShellFragmentKind.valueOf(ShellTreeConstants.jjtNodeName[id]);
	}
}
