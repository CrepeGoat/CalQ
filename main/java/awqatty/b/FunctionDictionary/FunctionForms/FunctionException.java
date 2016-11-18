package awqatty.b.FunctionDictionary.FunctionForms;

import java.util.List;


public class FunctionException implements FunctionForm {


	@Override
	public Double calculate(List<Double> vlist) throws CalculationException {
		throw new CalculationException();
	}

}
