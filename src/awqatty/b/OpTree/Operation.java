package awqatty.b.OpTree;

import awqatty.b.DrawMath.DrawSubTree.AlignForm;
import awqatty.b.DrawMath.DrawToCanvas.DrawAligned;
import awqatty.b.FunctionDictionary.FunctionType;
import awqatty.b.FunctionDictionary.FunctionForms.FunctionForm;

public class Operation extends DrawAligned {

	public final FunctionType ftype;
	
	public final FunctionForm func;
	
	public Operation(FunctionType function_type,
			FunctionForm function,
			AlignForm component) {
		super(component);
		func = function;
		ftype = function_type;
	}

}
