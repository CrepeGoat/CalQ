package awqatty.b.DrawMath;

import awqatty.b.DrawMath.DrawSubTree.AlignForm;
import awqatty.b.DrawMath.DrawToCanvas.DrawAligned;
import awqatty.b.FunctionDictionary.FunctionForms.FunctionForm;

public class MathNode extends DrawAligned {

	public final FunctionForm func;
	
	public MathNode(AlignForm component, FunctionForm function) {
		super(component);
		func = function;
	}

}
