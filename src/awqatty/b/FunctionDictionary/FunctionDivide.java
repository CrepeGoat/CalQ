package awqatty.b.FunctionDictionary;

import java.util.List;

public class FunctionDivide implements FunctionForm {

	@Override	
	public Double calculate(List<Double> dlist) {
		return dlist.get(0) / dlist.get(1);
	}

}
