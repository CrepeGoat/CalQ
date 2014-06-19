package awqatty.b.FunctionDictionary;
import java.lang.Math;
import java.util.List;

public class FunctionPower implements FunctionForm {

	@Override
	public Double calculate(List<Double> dlist) {
		return Math.pow(dlist.get(0), dlist.get(1));
	}

}
