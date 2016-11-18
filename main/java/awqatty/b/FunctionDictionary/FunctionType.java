package awqatty.b.FunctionDictionary;

public enum FunctionType {
	SOURCE,		// denotes ListTree container (remove?)
	BLANK,
	RAW_TEXT,
	NUMBER,

	CONST_E,
	CONST_PI,
	
	ADD,
	SUBTRACT,
	MULTIPLY,
	DIVIDE,
	NEGATIVE,
	ABS,
	SQUARE,
	MULT_INVERSE,
	
	POWER,
	SQRT,
	EXP_E,
	EXP_10,
	LN,
	LOG10,
	
	SINE,
	COSINE,
	TANGENT,
	ARCSINE,
	ARCCOSINE,
	ARCTANGENT,

	HYPSINE,
	HYPCOSINE,
	HYPTANGENT,
	ARHYPSINE,
	ARHYPCOSINE,
	ARHYPTANGENT,
	
	FACTORIAL,
	NCK,
	NPK,
	
	REMAINDER,
	GCD,
	LCM,
	;
	
	// Property-Check Methods
	public boolean isFunction() {
		switch (this) {
		case SOURCE:
		case BLANK:
		case NUMBER:
		case CONST_PI:
		case CONST_E:
			return false;
			
			default:
				return true;
		}
	}
	public boolean isCommutative() {
		switch (this) {
		case ADD:
		case MULTIPLY:
		case GCD:
		case LCM:
			return true;

			default:
				return false;	
		}
	}
	
	public boolean isCoreType() {
		switch (this) {
		case SQUARE:
		case MULT_INVERSE:
		case EXP_E:
		case EXP_10:
			return false;
			
			default:
				return true;
		}
	}
	
	/*
	public int defaultArgCount() {
		switch(this) {
			case BLANK:
			case NUMBER:
			case CONST_PI:
			case CONST_E:
				return 0;
			case SQUARE:
			case SQRT:
			case NEGATIVE:
			case ABS:
			case MULT_INVERSE:
			case EXP_E:
			case EXP_10:
			case LN:
			case LOG10:
			case SINE:
			case COSINE:
			case TANGENT:
			case ARCSINE:
			case ARCCOSINE:
			case ARCTANGENT:
			case HYPSINE:
			case HYPCOSINE:
			case HYPTANGENT:
			case ARHYPSINE:
			case ARHYPCOSINE:
			case ARHYPTANGENT:
			case FACTORIAL:
			case SOURCE:
				return 1;
			case ADD:
			case SUBTRACT:
			case MULTIPLY:
			case DIVIDE:
			case POWER:
			case NCK:
			case NPK:
				return 2;
			default:
				throw new RuntimeException();
		}
	}
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
