package awqatty.b.FunctionDictionary;

public enum FunctionType {
	SOURCE,
	BLANK,
	NUMBER,
	ADD,
	SUBTRACT,
	MULTIPLY,
	DIVIDE,
	POWER,
	SQUARE,
	SQRT;
	
	public static boolean isFunction(FunctionType ftype) {
		switch (ftype) {
		case SOURCE:
		case BLANK:
		case NUMBER:
			return false;
		default:
			return true;
		}
	}
	public static boolean isCommutative(FunctionType ftype) {
		switch (ftype) {
		case ADD:
		case MULTIPLY:
			return true;
		default:
			return false;	
		}
	}
	
	public static int defaultArgCount(FunctionType ftype) {
		switch (ftype) {
		case BLANK:
		case NUMBER:
			return 0;
		case SQRT:
			return 1;
		default:
			return 2;
		}
	}
	
	public static boolean hasDefaultArg(FunctionType ftype) {
		switch (ftype) {
		case SQUARE:
			return true;
		default:
			return false;
		}
	}
	
	public byte getOperatorPrecedence(FunctionType ftype) {
		switch (ftype) {
		case BLANK:
		case NUMBER:
			return 0;
		case POWER:
		case SQUARE:
		case SQRT:
			return 2;
		case MULTIPLY:
		case DIVIDE:
			return 3;
		case ADD:
		case SUBTRACT:
			return 4;
		case SOURCE:
			return Byte.MAX_VALUE;
		default:
			return 1;
		}
	}
}
