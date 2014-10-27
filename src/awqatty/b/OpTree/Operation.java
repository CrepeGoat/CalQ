package awqatty.b.OpTree;

import java.util.List;

import awqatty.b.FunctionDictionary.FunctionType;
import awqatty.b.FunctionDictionary.FunctionForms.CalculationException;
import awqatty.b.FunctionDictionary.FunctionForms.FunctionForm;
import awqatty.b.TextPresentation.TextPresForm;


public class Operation {
	
	private final FunctionForm function;
	private final TextPresForm presenter;
	
	public final FunctionType ftype;
	
	public Operation(FunctionType ft, FunctionForm f, TextPresForm p) {
		ftype = ft;
		function = f;
		presenter = p;
	}
	
	// Set display parameters
	public void setIdNumber(int index) {
		presenter.setIdValue(index);
	}
	public void enableTagFlag(int f) {
		presenter.enableTagFlag(f);
	}
	public void disableTagFlag(int f) {
		presenter.disableTagFlag(f);
	}
	
	// Main Calculation Methods
	public Double calculate(List<Double> dlist) throws CalculationException {
		return function.calculate(dlist);
	}
	public String getTextPres(List<String> slist, byte index) {
		return presenter.getTextPres(slist);
	}

}
