package awqatty.b.OpTree;

import java.util.List;

import awqatty.b.ListTree.BranchFunctorBase;

public class BranchCalculator extends BranchFunctorBase<OpNode, Double> {

	@Override
	protected Double calculateNestedResult(OpNode element,
			List<Double> branch_results) {
		return element.calculate(branch_results);
	}

}