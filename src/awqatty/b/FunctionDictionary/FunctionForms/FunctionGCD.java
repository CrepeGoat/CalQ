package awqatty.b.FunctionDictionary.FunctionForms;

import java.util.ArrayList;
import java.util.List;


public class FunctionGCD implements FunctionForm {

	protected Double getGCD(List<Double> vlist) {
		// Avoids "0" corner case
		for (Double d : vlist)
			if (d == 0)
				return Double.valueOf(0);
		
		// Temp variables
		int i;
		Double d;
		final List<Double> list = new ArrayList<Double>(vlist);
		
		// Logic Loop
		while (list.size() > 1) {
			// Find min. element (checks min magnitude, not min value)
			d = list.get(0);
			for (Double d2 : list.subList(1,list.size())) {
				if (Math.abs(d2) < Math.abs(d))
					d = d2;
			}
			// Reduce all other elements by modulo of min. element
			for (i=0; i<list.size(); ++i) {
				if (d != list.get(i)) {
					list.set(i, list.get(i) % d);

					// Remove zero elements
					if (list.get(i) == 0) {
						list.remove(i);
						--i;
					}
				}
			}
		}
		// Returns result
		return list.get(0);
	}

	
	@Override
	public Double calculate(List<Double> vlist) {
		return getGCD(vlist);
	}

}
