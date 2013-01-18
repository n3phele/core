package n3phele.service.nShell;

public class ExpressionNode extends SimpleNode {

	public ExpressionNode(int i) {
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
		return ExpressionTreeConstants.jjtNodeName[id]+":"+o;
	}

	
	public static Node jjtCreate(int id) {
		return new ExpressionNode(id);
	}

}