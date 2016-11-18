package awqatty.b.FunctionDictionary.FunctionForms;

import java.util.List;


public class FunctionNPK extends FunctionFactorial implements FunctionForm {

	// TODO make accessible for fractional inputs
	@Override
	public Double calculate(List<Double> vlist) throws CalculationException {
		// Check for integer values
		if (Math.round(vlist.get(0)) != vlist.get(0)
				|| Math.round(vlist.get(1)) != vlist.get(1)
				|| vlist.get(0) < vlist.get(1)
				|| vlist.get(1) < 0)
			return Double.NaN;

		final long n = Math.round(vlist.get(0));
		long k = n-Math.round(vlist.get(1));
		long result;
		for (result=1; k<n; result *= ++k){}
		return (double)result;
	}
}
