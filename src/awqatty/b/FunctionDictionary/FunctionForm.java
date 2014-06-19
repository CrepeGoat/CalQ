package awqatty.b.FunctionDictionary;

import java.util.List;

import awqatty.b.CustomExceptions.CalculationException;

public interface FunctionForm {

	public Double calculate(List<Double> vlist) throws CalculationException;
}
