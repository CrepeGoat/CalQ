package awqatty.b.OpTree;

import awqatty.b.DrawMath.DrawSubTree.AlignForm;
import awqatty.b.DrawMath.DrawSubTree.DrawAligned;
import awqatty.b.FunctionDictionary.FunctionType;
import awqatty.b.FunctionDictionary.FunctionForms.FunctionForm;

public class Operation extends DrawAligned {

	public final FunctionType ftype; // stores enum identifying the operation
	public final FunctionForm func; // stores the object that performs the operation calculations
	
	public Operation(FunctionType function_type,
			FunctionForm function,
			AlignForm component) {
		super(component);
		func = function;
		ftype = function_type;
	}
	//*
	public Operation(FunctionType function_type,
			FunctionForm function,
			AlignForm component,
			int cflags) {
		super(component, cflags);
		func = function;
		ftype = function_type;
	}
	//*/

}
