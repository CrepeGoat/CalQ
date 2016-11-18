package awqatty.b.ListTree;

import java.util.ArrayList;
import java.util.List;

abstract public class DataLoopRootDown<E, T> {

	private final List<T> stack = new ArrayList<T>();
	// Allows inherited loop methods access to node indices
	protected int index;
	
	protected static enum LoopControl {
		CONTINUE,
		BREAK_BRANCH,
		BREAK_LOOP
	}
	
	public void runLoop(ListTree<E> tree) {
		runLoop(tree, null);
	}
	public void runLoop(ListTree<? extends E> tree, T init_data) {
		stack.clear();
		index=0;
		final int length=tree.size();
		// Initializing loop array
		// TODO change to be more flexible (abstract method?)
		if (init_data != null) {
			while (index<length) {
				index = tree.getEndOfBranchIndex(index);
				stack.add(init_data);
			}
		}
		// TODO change to make insertions/deletions at end
		T data;
		LoopControl break_type;
		for (index=0; index<length;) {
			// Needs to remove element before subList is created
			data = (stack.size() > 0 ? stack.remove(0) : null);
			// Loop function
			break_type = loopAtNode(tree.get(index), data, stack.subList(0, 0));
			
			// Check loop breaks
			if (break_type == LoopControl.BREAK_BRANCH)
				index = tree.getEndOfBranchIndex(index);
			else if (break_type == LoopControl.BREAK_LOOP)
				break;
			else ++index;
		}
	}
	// Return true to skip all elements in the current branch
	abstract protected LoopControl loopAtNode(E node, T data, List<T> sublist);

}
