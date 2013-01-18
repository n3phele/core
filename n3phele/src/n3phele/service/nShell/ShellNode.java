package n3phele.service.nShell;

public class ShellNode extends SimpleNode {

	public ShellNode(int i) {
		super(i);
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

}
