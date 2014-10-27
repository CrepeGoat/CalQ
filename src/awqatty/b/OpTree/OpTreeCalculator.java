package awqatty.b.OpTree;

import java.util.List;

import awqatty.b.ListTree.DataLoopLeafUp;

public class OpTreeCalculator extends DataLoopLeafUp<Operation, Double> {

	@Override
	protected Double loopAtNode(Operation node, List<Double> stack) {
		return node.calculate(stack);
	}


}
