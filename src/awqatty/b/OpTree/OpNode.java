package awqatty.b.OpTree;

import java.util.List;

import awqatty.b.CustomExceptions.CalculationException;
import awqatty.b.FunctionDictionary.FunctionForm;
import awqatty.b.FunctionDictionary.FunctionType;
import awqatty.b.ListTree.NodeBase;
import awqatty.b.TextPresentation.TextPresForm;


public class OpNode extends NodeBase {
	
	private final FunctionForm function;
	private final TextPresForm presenter;
	
	public final FunctionType ftype;
	
	public OpNode(FunctionType ft, FunctionForm f, TextPresForm p) {
		ftype = ft;
		function = f;
		presenter = p;
	}
	public OpNode(FunctionType ft, FunctionForm f, TextPresForm p,
			int llimit, int ulimit) {
		super(llimit, ulimit);
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
