package awqatty.b.OpTree;

import java.util.List;
import awqatty.b.ArrayTree.BranchFunction;
import awqatty.b.CustomExceptions.CalculationException;

public class BranchCompute
	implements BranchFunction<OpNode, Double> {
	
	public Double calculate(OpNode node, List<Double> dlist) throws CalculationException {
		return node.calculate(dlist);
	}

}
