package awqatty.b.FunctionDictionary;

import java.util.List;

public class FunctionIdentity implements FunctionForm {

	public Double calculate(List<Double> dlist) {
		return dlist.get(0);
	}

}
