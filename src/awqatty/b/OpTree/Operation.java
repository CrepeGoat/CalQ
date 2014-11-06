package awqatty.b.OpTree;

import awqatty.b.DrawMath.AssignParentheses.ClosureType;
import awqatty.b.DrawMath.DrawSubTree.AlignForm;
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
	public Operation(FunctionType function_type,
			FunctionForm function,
			AlignForm component,
			ClosureType ctype) {
		super(component, ctype);
		func = function;
		ftype = function_type;
	}

}
