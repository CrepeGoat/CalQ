package awqatty.b.DrawMath.LinkedParameters;

import java.util.ArrayList;
import java.util.List;

public class OmniParamLinearRelationI
		implements ParamRelationForm<Integer> {

	private final List<LinkedParams<Integer>> params;
	private final List<Float> factors;
	private final float sum;
	
	public OmniParamLinearRelationI(
			List<LinkedParams<Integer>> parameters,
			List<Float> coefficients) {
		final int length = parameters.size();
		if (length == coefficients.size())
			sum = 0;
		else if (length + 1 == coefficients.size())
			sum = coefficients.get(length);
		else
			throw new IllegalArgumentException();
			
		params = new ArrayList<LinkedParams<Integer>>(parameters);
		factors = new ArrayList<Float>(
				coefficients.subList(0, length) );
	}

	
	@Override
	public boolean doesDefine(LinkedParams<Integer> param) {
		if (params != null) {
			for (LinkedParams<Integer> par : params) {
				if (par != param && !par.isDefined())
					return false;
			}
		}
		return true;
	}

	@Override
	public Integer getValueFor(LinkedParams<Integer> param) {
		if (params == null || factors == null)
			return null;
		
		final int index = params.indexOf(param),
				length = params.size();
		final float f = factors.get(index);
		if (index >= length || f == 0)
			return null;
		
		float res = 0;
		for (int i=0; i<length; ++i) {
			if (i == index) continue;
			res += (params.get(i).getValue() * factors.get(i))
					/ (-f);
		}
		res += sum/f;
		return (int)res;
	}

}
