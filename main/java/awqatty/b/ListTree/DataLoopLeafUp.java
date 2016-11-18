package awqatty.b.ListTree;

import java.util.ArrayList;
import java.util.List;

abstract public class DataLoopLeafUp<E, T>{
	
	private List<T> stack;
	// Allows inherited loop methods access to node indices
	protected int index;
	
	public DataLoopLeafUp() {
		stack = new ArrayList<T>();
	}
	
	// TODO add "skip parent branch" functionality
	public List<T> runLoop(ListTree<? extends E> tree) {
		stack.clear();
		final int count = tree.size();
		int branch_count;
		
		for (index=count-1; index>=0; --index) {
			branch_count = tree.getBranchCount(index);
			// TODO change to make insertions/deletions at end
			if (branch_count > 0) {
				stack.set(branch_count-1,
						loopAtNode(tree.get(index), 
								stack.subList(0, branch_count) ));
				stack.subList(0, branch_count-1).clear();
			}
			else
				stack.add(0, loopAtNode(tree.get(index), stack.subList(0,0)));
		}
		return stack;
	}
	
	abstract protected T loopAtNode(E node, List<T> stack);

}
