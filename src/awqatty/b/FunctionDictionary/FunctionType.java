package awqatty.b.FunctionDictionary;

public enum FunctionType {
	SOURCE,		// denotes ListTree container (remove?)
	BLANK,
	NUMBER,
	ADD,
	SUBTRACT,
	MULTIPLY,
	DIVIDE,
	POWER,
	SQUARE,
	SQRT,
	ABS,
	
	PI,
	SINE,
	COSINE,
	TANGENT,
	ARCSINE,
	ARCCOSINE,
	ARCTANGENT,
	;
	
	// Property-Check Methods
	public boolean isFunction() {
		switch (this) {
		case SOURCE:
		case BLANK:
		case NUMBER:
		case PI:
			return false;
		default:
			return true;
		}
	}
	public boolean isCommutative() {
		switch (this) {
		case ADD:
		case MULTIPLY:
			return true;
		default:
			return false;	
		}
	}
	public int defaultArgCount() {
		switch(this) {
			case BLANK:
			case NUMBER:
				return 0;
			case SQUARE:
			case SQRT:
			case ABS:
			case SINE:
			case COSINE:
			case TANGENT:
			case ARCSINE:
			case ARCCOSINE:
			case ARCTANGENT:
			case SOURCE:
				return 1;
			case ADD:
			case SUBTRACT:
			case MULTIPLY:
			case DIVIDE:
			case POWER:
				return 2;
			default:
				return 0;
		}
	}
	
	/*
	public boolean doesEncapsulateBranches() {
		switch (this) {
		case DIVIDE:
		case SQRT:
			return true;
		default:
			return false;
		}
	}
	
	public boolean isEncapsulated() {
		switch(this) {
		case BLANK:
		case NUMBER:
		case DIVIDE:
		case SQRT:
		case POWER:
			return true;
		default:
			return false;
		}
	}
	//*/
}
