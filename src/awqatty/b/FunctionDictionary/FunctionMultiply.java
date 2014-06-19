package awqatty.b.FunctionDictionary;

import java.util.List;

public class FunctionMultiply implements FunctionForm {

	@Override
	public Double calculate(List<Double> dlist) {
		double product = 1;
		for (double e : dlist)
			product *= e;
		return product;
	}

}
