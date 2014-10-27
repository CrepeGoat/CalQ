package awqatty.b.DrawMath.LinkedParameters;

import java.util.List;


public class LinkedParams<T> {

	private List<ParamRelationForm<T>> links;
		
	public void addRelation(ParamRelationForm<T> relation) {
		links.add(relation);
	}
	
	public boolean isDefined() {
		if (links != null) {
			for (ParamRelationForm<T> rel : links) {
				if (rel.doesDefine(this))
					return true;
			}
		}
		return false;
	}
		
	public T getValue() {
		for (ParamRelationForm<T> rel : links) {
			if (rel.doesDefine(this))
				return rel.getValueFor(this);
		}
		return null;
	}
}
