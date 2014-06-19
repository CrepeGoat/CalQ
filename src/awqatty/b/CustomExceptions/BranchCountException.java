package awqatty.b.CustomExceptions;

/*****************************************
 * Thrown for errors relating to a NodeBase derived object
 * having too few/many branches.
 */
public class BranchCountException extends RuntimeException {

	static final long serialVersionUID = 0;

	public BranchCountException() {
		// TODO Auto-generated constructor stub
	}

	public BranchCountException(String detailMessage) {
		super(detailMessage);
		// TODO Auto-generated constructor stub
	}

	public BranchCountException(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}

	public BranchCountException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}

}
