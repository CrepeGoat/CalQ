package awqatty.b.OpTree;

import java.util.List;

import awqatty.b.FunctionDictionary.FunctionForms.CalculationException;
import awqatty.b.ListTree.DataLoopLeafUp;

public class OpTreeCalculator extends DataLoopLeafUp<Operation, Double> {

	@Override
	protected Double loopAtNode(Operation node, List<Double> stack) {
		try {
			return node.func.calculate(stack);
		}
		catch (CalculationException e) {
			e.setCauseObject(Integer.valueOf(index));
			throw e;
		}
	}


}
