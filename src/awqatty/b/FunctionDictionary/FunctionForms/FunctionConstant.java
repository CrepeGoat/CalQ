package awqatty.b.FunctionDictionary.FunctionForms;

import java.util.List;


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
