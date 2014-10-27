package awqatty.b.FunctionDictionary.FunctionForms;

import java.util.List;


public class FunctionNPK extends FunctionFactorial implements FunctionForm {

	// TODO use an efficient algorithm
	@Override
	public Double calculate(List<Double> vlist) throws CalculationException {
		// TODO Auto-generated method stub
		return factorial(vlist.get(0)) / factorial(vlist.get(1));
	}
}
