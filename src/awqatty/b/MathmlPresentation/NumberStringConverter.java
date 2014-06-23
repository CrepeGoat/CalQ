package awqatty.b.MathmlPresentation;

//import java.text.DecimalFormat;

public final class NumberStringConverter {

	//static final byte max_length = 7;
	//static final byte decimals = 3;
	
	/*
	public static short numOfDigits(double d) {
		return (short)Math.log10(Math.abs(d));
	}
	*/
	
	public static String toString(double d) {
		if (d == (int)d)
			return Integer.toString((int)d);
		else
			return Float.toString((float)d);
	}
	
	/*
	public static String toCompressedString(double d) {
		String tmp;
		// Chooses between integer and decimal representations
		if (d == (int)d)
			tmp = Integer.toString((int)d);
		else
			tmp = Double.toString(d);
		
		// Chooses between previous choice, and abbreviated scientific notation
		if (tmp.length() <= max_length)
			return tmp;
		else
			return new DecimalFormat("0."
					+ new String(new char[decimals]).replace('\0', '#')
					+ "E0").format(d);
	}
	*/

}
