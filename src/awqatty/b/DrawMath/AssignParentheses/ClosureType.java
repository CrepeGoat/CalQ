package awqatty.b.DrawMath.AssignParentheses;

public enum ClosureType {
	BOUNDED,
	SUBSUPERSCRIPT,
	SERIES_HORIZ,
	SERIES_VERT,
	TEXT_ALPHA,
	TEXT_NUMERIC_POS,
	TEXT_NUMERIC_NEG,
	OTHER;
	
	public boolean isText() {
		switch (this) {
		case TEXT_ALPHA:
		case TEXT_NUMERIC_POS:
		case TEXT_NUMERIC_NEG:
			return true;
		default:
			return false;
		}
	}
	public boolean isNumber() {
		switch (this) {
		case TEXT_NUMERIC_POS:
		case TEXT_NUMERIC_NEG:
			return true;
		default:
			return false;
		}
	}

}
