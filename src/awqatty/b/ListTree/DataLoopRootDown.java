package awqatty.b.ListTree;

import java.util.ArrayList;
import java.util.List;

abstract public class DataLoopRootDown<E, T> {

	private final List<T> stack = new ArrayList<T>();
	// Allows inherited loop methods access to node indices
	protected int index;
	
	protected static final byte CONTINUE = 0;
	protected static final byte BREAK_BRANCH = 1;
	protected static final byte BREAK_LOOP = 2;
		
	// TODO add "skip branch" functionality
	public void runLoop(ListTree<E> tree) {
		runLoop(tree, null);
	}
	public void runLoop(ListTree<E> tree, T init_data) {
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
		byte break_type;
		for (index=0; index<length;) {
			// Needs to remove element before subList is created
			data = (stack.size() > 0 ? stack.remove(0) : null);
			// Loop function
			break_type = loopAtNode(tree.get(index), data, stack.subList(0, 0));
			
			// Check loop breaks
			if (break_type == BREAK_BRANCH)
				index = tree.getEndOfBranchIndex(index);
			else if (break_type == BREAK_LOOP)
				break;
			else ++index;
		}
	}
	// Return true to skip all elements in the current branch
	abstract protected byte loopAtNode(E node, T data, List<T> sublist);

}
