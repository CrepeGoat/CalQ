package awqatty.b.DrawMath.LinkedParameters;

public interface ParamRelationForm<T> {

	public boolean doesDefine(LinkedParams<T> param);
	public T getValueFor(LinkedParams<T> param);
}
