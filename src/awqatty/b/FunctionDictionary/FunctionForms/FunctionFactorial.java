package awqatty.b.FunctionDictionary.FunctionForms;

import java.util.List;


public class FunctionFactorial implements FunctionForm {

	// TODO use an effective algorithm
	protected static double factorialSafe (double n) {
		if (n == 0)
			return 1;
		else
			return n*factorialSafe(n-1);
	}
	public static double factorial(double n) {
		if (Math.round(n) != n)
			return Double.NaN;
		else if (n < 0)
			return Double.NEGATIVE_INFINITY * Math.pow(-1, n);
		else
			return factorialSafe(n);
	}
	
	@Override
	public Double calculate(List<Double> vlist) throws CalculationException {
		// TODO Auto-generated method stub
		return factorial(vlist.get(0));
	}

}
