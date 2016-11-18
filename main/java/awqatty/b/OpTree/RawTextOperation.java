package awqatty.b.OpTree;

import awqatty.b.DrawMath.DrawSubTree.AlignForm;
import awqatty.b.DrawMath.DrawToCanvas.DrawText;
import awqatty.b.FunctionDictionary.FunctionForms.FunctionForm;
import awqatty.b.FunctionDictionary.FunctionType;

public class RawTextOperation extends Operation {

	public RawTextOperation(FunctionType function_type,
							FunctionForm function,
							AlignForm component) {
		super(function_type, function, component);
	}
	public RawTextOperation(FunctionType function_type,
							FunctionForm function,
							AlignForm component,
							int cflags) {
		super(function_type, function, component, cflags);
	}

	public String getText() {
		return ((DrawText)base_comp).text;
	}
	public void setText(String str) {
		((DrawText)base_comp).text = str;
	}
}
