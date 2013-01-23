package n3phele.service.nShell;

import n3phele.service.model.ShellFragmentKind;

public class ExpressionNode extends SelfCompilingNode {

	public ExpressionNode(int i) {
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
		return ExpressionTreeConstants.jjtNodeName[id]+":"+o;
	}

	
	public static Node jjtCreate(int id) {
		return new ExpressionNode(id);
	}


	@Override
	ShellFragmentKind toKind() {
		if(ExpressionTreeConstants.jjtNodeName[id].equals("constant")) {
			if(this.jjtGetValue() instanceof Long) {
				return ShellFragmentKind.constantLong;
			} else if(this.jjtGetValue() instanceof Double) {
				return ShellFragmentKind.constantDouble;
			} else if(this.jjtGetValue() instanceof Boolean) {
				return ShellFragmentKind.constantBoolean;
			}else {
				return ShellFragmentKind.constantString;
			}
		} else {
			return ShellFragmentKind.valueOf(ExpressionTreeConstants.jjtNodeName[id]);
		}
	}

}