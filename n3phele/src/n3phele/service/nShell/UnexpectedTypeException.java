package n3phele.service.nShell;

public class UnexpectedTypeException extends Exception {
	private static final long serialVersionUID = 1L;
	public UnexpectedTypeException(Object o, String expected) {
		super("Encountered "+o.getClass().getSimpleName()+" Expected "+expected);
	}

}
