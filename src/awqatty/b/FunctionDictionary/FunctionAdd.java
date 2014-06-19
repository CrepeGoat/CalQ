package awqatty.b.FunctionDictionary;

import java.util.List;

public class FunctionAdd implements FunctionForm {

	@Override
	public Double calculate(List<Double> dlist) {
		double sum = 0;
		for (double e : dlist)
			sum += e;
		return sum;
	}

}
