package awqatty.b.OpTree;

import awqatty.b.DrawMath.AlignDrawBuilder;
import awqatty.b.DrawMath.DrawSubTree.AlignForm;
import awqatty.b.FunctionDictionary.FunctionForms.FunctionForm;
import awqatty.b.FunctionDictionary.FunctionType;

public class SourceOperation extends Operation {

	private static AlignDrawBuilder align_builder = null;
	public static void setAlignDrawBuilder(AlignDrawBuilder adb) {
		if (align_builder == null) {
			align_builder = adb;
		}
	}

	public SourceOperation(FunctionType function_type,
					 FunctionForm function,
					 AlignForm component) {
		super(function_type, function, component);
	}

	@Override
	public void setParentheses(boolean b) {}

	public void setResultToValue(boolean selectionIsOn, double value) {
		base_comp = align_builder
				.resultIsValid(true)
				//.selectionIsSubBranch(selectionIsOn)
				.number(value)
				.build(FunctionType.SOURCE);
		comp = base_comp;
	}
	public void setResultToIncomplete() {
		base_comp = align_builder
				.resultIsValid(false)
				.build(FunctionType.SOURCE);
		comp = base_comp;
	}
}
