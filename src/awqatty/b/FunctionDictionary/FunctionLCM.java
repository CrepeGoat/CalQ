package awqatty.b.FunctionDictionary;

import java.util.List;

public class FunctionLCM extends FunctionGCD implements FunctionForm {
	
	@Override
	public Double calculate(List<Double> vlist) {
		double d = vlist.get(0),
				gcd = getGCD(vlist);
		for (Double d2 : vlist.subList(1, vlist.size())) {
			d *= d2/gcd;
		}
		return d;
	}

}
