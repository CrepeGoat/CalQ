package awqatty.b.TextPresentation;

import awqatty.b.FunctionDictionary.FunctionType;

public interface TextPresBuilderForm {
	
	public TextPresBuilderForm number(double d);
	
	public TextPresForm build(FunctionType ftype);

}
