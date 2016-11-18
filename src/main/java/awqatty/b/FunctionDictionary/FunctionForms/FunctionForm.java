package awqatty.b.FunctionDictionary.FunctionForms;

import java.util.List;


public interface FunctionForm {

	public Double calculate(List<Double> vlist) throws CalculationException;
}
