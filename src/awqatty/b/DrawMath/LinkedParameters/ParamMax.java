package awqatty.b.DrawMath.LinkedParameters;

import java.util.List;

public class ParamMax<T extends Comparable<T>> extends UniParamRelation<T> {
	
	public ParamMax(List<LinkedParams<T>> dependencies) {
		super(dependencies);
		// TODO Auto-generated constructor stub
	}

	@Override
	public T getValueFor(LinkedParams<T> param) {
		if (sources == null || sources.size() <= 0)
			return null;
		
		T res = sources.get(0).getValue();
		for (LinkedParams<T> source : sources) {
			if (res.compareTo(source.getValue()) < 0)
				res = source.getValue();
		}
		return res;
	}

}
