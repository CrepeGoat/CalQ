package awqatty.b.FunctionDictionary;

import java.util.List;

import awqatty.b.CustomExceptions.CalculationException;

public class FunctionException implements FunctionForm {


	@Override
	public Double calculate(List<Double> vlist) throws CalculationException {
		throw new CalculationException();
	}

}
