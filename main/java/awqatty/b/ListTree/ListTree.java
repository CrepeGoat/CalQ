package awqatty.b.ListTree;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;


/**
 * CLASS - ListTree
 * A tree-organized collection of elements, stored in a stack. The stack
 * is ordered depth-wise, such that one can find each branch node of an 
 * element from the number of branches each root node has.
 */
public class ListTree<E> implements Collection<E> {
	
	
	/**
	 * CLASS - Node
	 * Wraps each inserted element to provide branch-counting methods.
	 */
	protected static final class Node<E> {

		// Local Members
		private final E obj;
		
		private int branch_count;
		public final int branch_min;
		public final int branch_max;

		// Constructors
		public Node(E object, int min, int max) {
			obj = object;
			branch_count = 0;
			branch_min = min;
			branch_max = max;
		}
		public Node(E object) {
			this(object, 0, Integer.MAX_VALUE);
		}
		
		// Get Methods
		public E getObject() {
			return obj;
		}
		
		public int getBranchCount() {
			return branch_count;
		}
		// Increment/Decrement Operations
		//		throws Exception on illegal child counts
		public void incrementCount() throws BranchCountException {
			if (branch_count >= branch_max)
				throw new BranchCountException();
			++branch_count;
		}
		public void decrementCount() throws BranchCountException {
			if (branch_count <= branch_min)
				throw new BranchCountException();
			--branch_count;
		}
		// Checks count for validity
		//		(When building a branch, no exception will be thrown
		//		 while incrementing. Use this at end of build process
		//		 to ensure end count complies with bounds.)
		public void checkCount() throws BranchCountException {
			if (branch_count <= branch_min || branch_count >= branch_max)
				throw new BranchCountException();
		}
	}
	
	public final class Navigator {
		private int index;
		
		public Navigator(int start) {
			index = start;
		}
		public Navigator new_copy() {
			return new Navigator(index);
		}

		public int getIndex() {
			return index;
		}
		public int getNumberOfBranches() {
			return getBranchCount(index);
		}
		public E getObject() {
			return get(index);
		}
		
		public void toRoot() {
			index = getRootIndex(index);
		}
		public void toNthBranch(int branch_order) {
			index = getNthBranchIndex(index, branch_order);
		}
		public void toEnd() {
			index = getEndOfBranchIndex(index);
		}
	}
	
	/**
	 * CLASS - FindParentAlgorithm
	 * Used to run the parent-finder algorithm, and recover both the 
	 * parent index and the child number concurrently.
	 */
	public final class FindParentAlgorithm {
		private int index, count;
		
		public void run(int child_loc) {
			count = 0;
			index = child_loc-1;
			for (; index >= 0; --index) {
				count += list.get(index).getBranchCount()-1;
				if (count >= 0)
					break;
			}
		}
		public int getParentIndex() {
			return index;
		}
		public int getBranchNumber() {
			return list.get(index).getBranchCount()-1 - count;
		}
	}
	
	protected final List<Node<E>> list;
	
	public ListTree() {
		list = new ArrayList<Node<E>>();
	}
	protected ListTree(List<Node<E>> in) {
		list = in;
	}
	public ListTree(ListTree<E> tree) {
		list = new ArrayList<Node<E>>(tree.list);
	}
		
	/**********************************************************
	 * Access Methods
	 **********************************************************/
	public E get(int index) {
		return list.get(index).getObject();
	}
	public int getBranchCount(int index) {
		return list.get(index).getBranchCount();		
	}
	
	/**
	 * METHOD - subTree
	 * Returns a new tree object, with subList as base stack
	 */
	public ListTree<E> subTree(int index) {
		return new ListTree<E>( list.subList(index, getEndOfBranchIndex(index)) );
	}
	
	/**********************************************************
	 * Navigation Methods
	 **********************************************************/
	
	/**
	 * METHOD - getRootIndex
	 * Returns ref. index of parent node to the given "index"
	 * 
	 */
	public int getRootIndex(int index) {
		int tmp=0; --index;
		for (; index >= 0; --index) {
			tmp += list.get(index).getBranchCount()-1;
			if (tmp >= 0)
				break;
		}
		return index;
	}
	/**
	 * METHOD - getEndLeafIndex
	 *  Returns the index of next child node to "index"'s parent
	 *	If "index" is the rightmost node in level, it returns the
	 *	index at which the next child would be located
	 *	(such logic is used for inserting new child nodes)
	 * 
	 */
	public int getEndOfBranchIndex(int index) {
		if (index < list.size()) {
			int tmp=list.get(index++).getBranchCount();
			while (tmp > 0) {
				tmp += list.get(index++).getBranchCount() - 1;
			}
		}
		return index;
	}
	
	/**
	 * METHOD - getNthBranchIndex
	 *  Returns the index of the n-th child node of the given parent. 
	 *  If the parent has less than n children, it returns EndOfBranchIndex
	 * 
	 */
	public int getNthBranchIndex(int parent_loc, int order) {
		if (parent_loc != -1)
			order = Math.min(order, list.get(parent_loc).getBranchCount());
		
		int branch_loc = parent_loc+1;
		final int length = list.size();
		for (int index=0; index<order && branch_loc<length; ++index)
			branch_loc = getEndOfBranchIndex(branch_loc);
			
		return branch_loc;
	}
	/**
	 * METHOD - getBranchNumber
	 *  Returns the child number of this branch in its parent.
	 *  (Algorithm is identical to that of FindParentAlgorithm.)
	 * 
	 */
	public int getBranchNumber(int index) {
		int tmp=0; --index;
		for (; index >= 0; --index) {
			tmp += list.get(index).getBranchCount()-1;
			if (tmp >= 0)
				break;
		}
		return list.get(index).getBranchCount()-1 - tmp;
	}
	
	/**
	 * METHOD - getBranchIndices
	 *  Returns a stack of the indices for each child of the designated branch.
	 * 
	 */
	public int[] getBranchIndices(int parent_loc) {
		int[] indices = new int[list.get(parent_loc).getBranchCount()];
		if (indices.length > 0) {
			indices[0] = parent_loc+1;
			for (int i=1; i < indices.length; ++i) {
				indices[i] = getEndOfBranchIndex(indices[i-1]);
			}
		}
		return indices;
	}
	
	/**
	 * METHOD - getNodeDepth
	 *  Returns the number of branches separating the current node from 
	 *  the root node. (index.e. for index=0, return=0)
	 * 
	 */
	public int getNodeDepth(int index) {
		int depth = -1;
		while (index >= 0) {
			index = getRootIndex(index);
			++depth;
		}
		return depth;
	}
	
	/**********************************************************
	 * Basic Insertion Methods
	 * 
	 * NOTE - in all insertion/deletion methods, branch counts must be altered first,
	 *	before any other significant changes are made. This makes recovery
	 *	from branch count errors manageable.
	 **********************************************************/

	/**
	 * FUNCTION - addBranch
	 *	branch - object to insert
 	 *	parent_loc - the index of the parent under which to insert the new object
 	 *		domain values: 0 - array.size()-1
 	 *	order - the position among the current child objects in which
 	 *		to insert the "branch" object (0 is first, & fastest)
 	 *		domain values: 0 - infty
 	 *	return - the new index of the inserted element
 	 *		range: 0 - array.size()-1
	 * Set (parent_loc==-1) to create a new separate tree
	 * 
	 */
	public int addBranch(int parent_loc, int order,
			E object, int min_leaves, int max_leaves)
			throws BranchCountException {
		return addBranch(parent_loc, order,
				new Node<E>(object, min_leaves, max_leaves));
	}
	public int addBranch(int parent_loc, int order, E object)
			throws BranchCountException {
		return addBranch(parent_loc, order, new Node<E>(object));
	}
	private int addBranch(int parent_loc, int order, Node<E> node) 
			throws BranchCountException {		
		if (parent_loc != -1)
			list.get(parent_loc).incrementCount();
		
		final int branch_loc = getNthBranchIndex(parent_loc, order);
		
		list.add(branch_loc, node);
		return branch_loc;
	}
	
	public int addBranch(int parent_loc, int order, ListTree<E> branch) {
		if (parent_loc != -1)
			list.get(parent_loc).incrementCount();
		
		final int branch_loc = getNthBranchIndex(parent_loc, order);
		
		list.addAll(branch_loc, branch.list);
		return branch_loc;
	}
	
	/**
	 * FUNCTION - addRoot
	 * 
	 * child_loc - the index used for insertion
	 * branch - the object to use as the new parent
	 * order - the location in the new parent in which to add the existing object
	 * Return - the new index of the object originally at the child_loc index
	 * 
	 * Inserts the provided object into the tree location of an existing object,
	 * and sets the existing object as its child
	 * 
	 */
	public int addRoot(int child_loc, E object)
			throws BranchCountException {
		return addRoot(child_loc, new Node<E>(object));
	}
	public int addRoot(int child_loc, E object, int min_leaves, int max_leaves)
			throws BranchCountException {
		return addRoot(child_loc, new Node<E>(object, min_leaves, max_leaves));
	}
	private int addRoot(int child_loc, Node<E> node) throws BranchCountException {
		node.incrementCount();
		list.add(child_loc, node);
		return child_loc+1;
	}
	
	public int addRoot(int child_loc, int order, ListTree<E> branch)
			throws BranchCountException  {
		final int tmp = branch.addBranch(0, order, this.subTree(child_loc));
		setSubTree(child_loc, branch.subTree(0));
		return child_loc+tmp;
	}
	
	/**
	 * FUNCTION - swapBranches
	 * 
	 * Swaps the locations of two children under the same parent node.
	 * 
	 */
	public void swapBranches(int child1, int child2) {
		final List<Node<E>> temp1 = list.subList(child1, getEndOfBranchIndex(child1));
		final List<Node<E>> temp2 = list.subList(child2, getEndOfBranchIndex(child2));
		final List<Node<E>> temp3 = new ArrayList<Node<E>>(temp1);
		
		temp1.clear();
		temp1.addAll(temp2);
		temp2.clear();
		temp2.addAll(temp3);
	}
	
	/**
	 * FUNCTION - shiftBranchOrder
	 * 
	 * Moves a child node from its current ordered location to a new order.
	 * All other nodes will retain their relative ordering.
	 * 
	 * branch_loc - the index location of the branch to move
	 * new_order - the new order location of the indicated branch
	 * 
	 */
	public int shiftBranchOrder(int branch_loc, int new_order) {
		final FindParentAlgorithm alg = new FindParentAlgorithm();
		alg.run(branch_loc);
		
		final int parent_loc = alg.getParentIndex();
		if (parent_loc != -1) {
			new_order = Math.min(new_order,	list.get(parent_loc).getBranchCount()-1);
		}
		
		// Avoids redundant actions
		if (new_order != alg.getBranchNumber()) {
			final List<Node<E>>
					sublist = list.subList(branch_loc, getEndOfBranchIndex(branch_loc)),
					tmplist = new ArrayList<Node<E>>(sublist);
			sublist.clear();
			branch_loc = getNthBranchIndex(parent_loc, new_order);
			list.addAll(branch_loc, tmplist);
		}
		return branch_loc;
	}
	/**
	 * FUNCTION - deleteBranch
	 * 
	 * Removes branch from tree. If the parent branch cannot have so few children,
	 * an exception is thrown from the parent node. Is effectively the inverse
	 * operation to "addBranch".
	 * 
	 * branch_loc - the index of the branch to be deleted.
	 * 
	 */
	public void deleteSubTree(int branch_loc) throws BranchCountException {
		if (getRootIndex(branch_loc) != -1)
			list.get(getRootIndex(branch_loc)).decrementCount();
		list.subList(branch_loc, getEndOfBranchIndex(branch_loc)).clear();
	}
	
	/**
	 * FUNCTION - deleteRoot
	 * 
	 * Deletes the entire parent branch of the designated node, replacing it with
	 * the designated branch itself. The inverse operation of "addRoot".
	 * 
	 * branch_loc - the index of the designated branch
	 * 
	 */
	public int deleteRoot(int branch_loc) {
		int parent_loc = getRootIndex(branch_loc);
		int parent_end;
		if (parent_loc == -1) {
			++parent_loc;
			parent_end = list.size();
		}
		else {
			parent_end = getEndOfBranchIndex(parent_loc);
		}
		
		list.subList(getEndOfBranchIndex(branch_loc), parent_end).clear();
		list.subList(parent_loc, branch_loc).clear();
		
		return parent_loc;
	}
	
	/**
	 * FUNCTION - setSubTree
	 * 
	 * Replaces a branch with a new node object, or an existing Tree.
	 * Avoids child-count increments/decrements.
	 * 
	 */
	public void setSubTree(int branch_loc, E object) {
		setSubTree(branch_loc, new Node<E>(object));
	}
	public void setSubTree(int branch_loc, E object, int min_leaves, int max_leaves) {
		setSubTree(branch_loc, new Node<E>(object, min_leaves, max_leaves));
	}
	private void setSubTree(int branch_loc, Node<E> node) {
		list.subList(branch_loc, getEndOfBranchIndex(branch_loc)).clear();
		list.add(branch_loc, node);
	}

	public void setSubTree(int branch_loc, ListTree<E> branch) {
		list.subList(branch_loc, getEndOfBranchIndex(branch_loc)).clear();
		list.addAll(branch_loc, branch.list);
	}
	
	/**********************************************************
	 * Collection-Inherited Methods
	 **********************************************************/
	@Override
	public int size() {
		return list.size();
	}
	@Override
	public boolean add(E arg0) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean addAll(Collection<? extends E> arg0) {
		throw new UnsupportedOperationException();
	}
	@Override
	public void clear() {
		list.clear();
	}
	@Override
	public boolean contains(Object obj) {
		if (obj == null) {
			for (Node<E> node : list) {
				if (node.getObject() == null)
					return true;
			}
		}
		else {
			for (Node<E> node : list) {
				if (obj.equals(node.getObject()))
					return true;
			}
		}
		return false;
	}
	@Override
	public boolean containsAll(Collection<?> collection) {
		for (Object obj : collection) {
			if (!contains(obj))
				return false;
		}
		return true;
	}
	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	private class TreeIterator implements Iterator<E> {
		private final Iterator<Node<E>> iter;
		public TreeIterator(Iterator<Node<E>> it) {
			iter = it;
		}
		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}

		@Override
		public E next() {
			return iter.next().getObject();
		}

		@Override
		public void remove() {
			iter.remove();
		}
		
	}
	@Override
	public Iterator<E> iterator() {
		return new TreeIterator(list.iterator());
	}
	@Override
	public boolean remove(Object arg0) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean removeAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean retainAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}
	@Override
	public Object[] toArray() {
		Object[] array = new Object[list.size()];
		for (int i=0; i<array.length; ++i) {
			array[i] = get(i);
		}
		return array;
	}
	@Override
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] array) {
		final int length = list.size();
		if (array.length < length) {
			array = (T[]) Array.newInstance(
					array.getClass().getComponentType(),
					array.length );
		}
		int i=0;
		for (; i<length; ++i) {
			array[i] = (T)get(i);
		}
		if (i<array.length)
			array[i] = null;
		return array;
	}

}
