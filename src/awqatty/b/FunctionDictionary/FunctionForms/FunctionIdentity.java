package awqatty.b.FunctionDictionary.FunctionForms;

import java.util.List;


public class FunctionIdentity implements FunctionForm {

	@Override
	public Double calculate(List<Double> dlist) {
		return dlist.get(0);
	}

}
