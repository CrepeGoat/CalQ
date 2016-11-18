package awqatty.b.DrawMath.AssignParentheses;

public class ClosureFlags {
	
	private static final int BASE_MAX	=1<<3;
	
	public static final int NONE=0;
	
	public static final int BOUNDED	=1;
	public static final int SCRIPT	=2;
	
	public static final int SERIES_HORIZ	=3;
	public static final int SERIES_VERT		=4;
		public static final int DIVIDER		=1*BASE_MAX;
	
	public static final int TEXT_ALPHABETIC	=5;
	public static final int TEXT_NUMERIC	=6;
		public static final int NEGATIVE	=1*BASE_MAX;
	

			
			
	public static int baseType(int flags) {
		return flags & (BASE_MAX-1);
	}
	
	public static boolean typeIsSeries(int flags) {
		return baseType(flags) == SERIES_HORIZ ||
				baseType(flags) == SERIES_VERT;
	}
	public static boolean seriesHasDivider(int flags) {
		return (flags & DIVIDER) != 0;
	}
	
	public static boolean typeIsText(int flags) {
		return baseType(flags) == TEXT_ALPHABETIC ||
				baseType(flags) == TEXT_NUMERIC;
	}
	public static boolean numericIsNegative(int flags) {
		return (flags & NEGATIVE) == NEGATIVE;
	}
	
}
