package awqatty.b.GUI;


//import java.text.DecimalFormat;

public final class NumberStringConverter {

	static final byte max_length = 13;
	static final byte decimals = 3;
	
	/*
	public static short numOfDigits(double d) {
		return (short)Math.log10(Math.abs(d));
	}

	public static String toString(double d) {
		if (d == (int)d)
			return Integer.toString((int)d);
		else
			return Float.toString((float)d);
	}
	//*/

	public static String toReducedString(double d) {
		if (Math.round(d) == d)
			return Long.toString((long)d);
		else
			return Double.toString(d);
		/*
		String tmp = toString(d);
		
		// Chooses between previous choice, and abbreviated scientific notation
		if (tmp.length() <= length)
			return tmp;
		else
			return new DecimalFormat("0."
					+ new String(new char[Math.max(0, length-4)]).replace('\0', '#')
					+ "E0").format(d);
		//*/
	}

}
