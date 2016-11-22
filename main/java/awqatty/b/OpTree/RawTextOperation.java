package awqatty.b.OpTree;

import awqatty.b.DrawMath.AlignDrawParts.AlignDraw;
import awqatty.b.DrawMath.AlignDrawParts.AlignForm;
import awqatty.b.DrawMath.DrawToCanvas.DrawText;
import awqatty.b.FunctionDictionary.FunctionForms.FunctionForm;
import awqatty.b.FunctionDictionary.FunctionType;

public class RawTextOperation extends Operation {

	public RawTextOperation(FunctionType function_type,
							FunctionForm function,
							AlignForm component
	) {
		super(function_type, function, component);
	}
	public RawTextOperation(FunctionType function_type,
							FunctionForm function,
							AlignForm component,
							int cflags) {
		super(function_type, function, component, cflags);
	}

	private DrawText getDrawText() {return ((DrawText)((AlignDraw)base_comp).getDrawForm());}
	public String getText() {return getDrawText().text;}
	public void setText(String str) {getDrawText().text = str;}
}
