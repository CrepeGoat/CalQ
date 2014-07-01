package awqatty.b.FunctionDictionary;

import java.util.List;

import awqatty.b.CustomExceptions.CalculationException;

public class FunctionConstant implements FunctionForm {

	private final double value;
	public FunctionConstant(double v) {
		value = v;
	}

	@Override
	public Double calculate(List<Double> vlist) throws CalculationException {
		return value;
	}

}
