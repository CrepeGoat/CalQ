package awqatty.b.OpTree;

import java.util.List;

import awqatty.b.CustomExceptions.CalculationException;
import awqatty.b.FunctionDictionary.FunctionForm;
import awqatty.b.FunctionDictionary.FunctionType;
import awqatty.b.ListTree.NodeBase;
import awqatty.b.MathmlPresentation.TextPresObject;


public class OpNode extends NodeBase {
	
	protected final FunctionForm function;
	protected final TextPresObject presentation;
	
	public final FunctionType ftype;
	
	public OpNode(FunctionType ft, FunctionForm f, TextPresObject p) {
		ftype = ft;
		function = f;
		presentation = p;
	}
	public OpNode(FunctionType ft, FunctionForm f, TextPresObject p,
			int llimit, int ulimit) {
		super(llimit, ulimit);
		ftype = ft;
		function = f;
		presentation = p;
	}
	
	// Set display parameters
	public void setIdNumber(int index) {
		presentation.setIdValue(index);
	}
	public void enableTagFlag(int f) {
		presentation.enableTagFlag(f);
	}
	public void disableTagFlag(int f) {
		presentation.disableTagFlag(f);
	}
	
	// Main Calculation Methods
	public Double calculate(List<Double> dlist) throws CalculationException {
		return function.calculate(dlist);
	}
	public String getTextPres(List<String> slist) {
		return presentation.getTextPres(slist);
	}

}
