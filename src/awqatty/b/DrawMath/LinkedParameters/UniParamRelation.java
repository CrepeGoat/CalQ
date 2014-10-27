package awqatty.b.DrawMath.LinkedParameters;

import java.util.ArrayList;
import java.util.List;


public abstract class UniParamRelation<T> implements ParamRelationForm<T> {
	
	//private final LinkedParams<T> target;
	protected final List<LinkedParams<T>> sources;
	
	public UniParamRelation(
			//LinkedParams<T> root,
			List<LinkedParams<T>> dependencies) {
		//target = root;
		sources = new ArrayList<LinkedParams<T>>(dependencies);
	}
	
	// Methods to Override
	@Override
	public boolean doesDefine(LinkedParams<T> param) {
		if (sources != null) {
			for (LinkedParams<T> source : sources) {
				if (!source.isDefined())
					return false;
			}
		}
		return true;
	}
	@Override
	abstract public T getValueFor(LinkedParams<T> param);
}
