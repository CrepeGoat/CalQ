package awqatty.b.ListTree;

import java.util.List;
import java.util.ArrayList;

abstract public class BranchFunctorBase<N extends NodeBase,U> implements BranchFunctorForm<N, U> {

	protected final List<U> result_stack;
	
	public BranchFunctorBase() {
		result_stack = new ArrayList<U>();
	}

	@Override
	public final void excecuteInLoop(N element) {
		// TODO rewrite to push/pop from back -> more efficient
		if (element.getBranchCount() > 0) {
			result_stack.set(0, calculateNestedResult(element, 
					result_stack.subList(0, element.getBranchCount()) ));
			result_stack.subList(1, element.getBranchCount()).clear();
		}
		else {
			result_stack.add(0, calculateNestedResult(element, result_stack.subList(0, 0)));
		}		
	}

	@Override
	public List<U> returnValues() {
		return result_stack;
	}
	
	public List<U> runLoop(ListTree<N> tree) {
		return tree.forAllInReverse(this);
	}
	
	abstract protected U calculateNestedResult(N element, List<U> branch_results);

}
