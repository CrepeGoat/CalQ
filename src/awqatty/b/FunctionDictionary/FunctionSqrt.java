package awqatty.b.FunctionDictionary;
import java.lang.Math;
import java.util.List;

public class FunctionSqrt implements FunctionForm {

	@Override
	public Double calculate(List<Double> dlist) {
		return Math.sqrt(dlist.get(0));
	}

}
