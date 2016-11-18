package awqatty.b.FunctionDictionary.FunctionForms;

/***********************************************
 * Thrown for when "blank" objects are used in mathematical calculations
 */

public final class CalculationException extends RuntimeException {

	static final long serialVersionUID = 0;
	private Object cause;
	
	public CalculationException() {
		super("");
		cause = null;
	}
	public CalculationException(
			String message, 
			Object c) {
		super(message);
		cause = c;
	}
	
	public Object getCauseObject()
	{
		return cause;
	}
	public void setCauseObject(Object obj)
	{
		cause = obj;
	}
}
