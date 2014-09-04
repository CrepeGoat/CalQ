package awqatty.b.ListTree;

import java.util.List;
import java.util.ArrayList;

import awqatty.b.CustomExceptions.BranchCountException;
import awqatty.b.CustomExceptions.CalculationException;

/********************************************************************
 * CLASS - ListTree
 * A tree-organized collection of elements, stored in a list. The list
 * is ordered depth-wise, such that one can find each child node of an 
 * element from the number of children each node has.
 */
public class ListTree<N extends NodeBase> {

	protected final List<N> list;
	
	/********************************************************************
	 * CLASS - FindParentAlgorithm
	 * Used to run the parent-finder algorithm, and recover both the 
	 * parent index and the child number concurrently.
	 */
	public class FindParentAlgorithm {
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
	
	
	public ListTree() {
		list = new ArrayList<N>();
	}
	protected ListTree(List<N> in) {
		list = in;
	}
	public ListTree(ListTree<N> tree) {
		list = new ArrayList<N>(tree.list);
	}
		
	//--------------------------------------------------------------------
	// Access Methods
	public N get(int index) {
		return list.get(index);
	}
	public int size() {
		return list.size();
	}
	
	/********************************************************************
	 * METHOD - subTree
	 * Returns a new tree object, with subList as base list
	 */
	public ListTree<N> subTree(int index) {
		return new ListTree<N>( list.subList(index, getEndOfBranchIndex(index)) );
	}
	
	//--------------------------------------------------------------------
	//Navigation Methods
	
	/*********************************************************************
	 * METHOD - getParentIndex
	 * Returns ref. index of parent node to the given "index"
	 * 
	 */
	public int getParentIndex(int index) {
		int tmp=0; --index;
		for (; index >= 0; --index) {
			tmp += list.get(index).getBranchCount()-1;
			if (tmp >= 0)
				break;
		}
		return index;
	}
	/*********************************************************************
	 * METHOD - getEndOfBranchIndex
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
	
	/*********************************************************************
	 * METHOD - getNthBranchIndex
	 *  Returns the index of the n-th child node of the given parent. 
	 *  If the parent has less than n children, it returns the list size.
	 * 
	 */
	public int getNthBranchIndex(int parent_loc, int order) {
		if (parent_loc != -1)
			order = Math.min(order, list.get(parent_loc).getBranchCount());
		int branch_loc = parent_loc+1;

		for (int index=0; index < order && branch_loc < list.size(); ++index)
			branch_loc = getEndOfBranchIndex(branch_loc);
			
		return branch_loc;
	}
	/*********************************************************************
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
	
	/*********************************************************************
	 * METHOD - getBranchIndices
	 *  Returns a list of the indices for each child of the designated branch.
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
	
	//--------------------------------------------------------------------
	// Basic Insertion Methods
	// NOTE - in all insertion/deletion methods, branch counts must be altered first,
	//	before any other significant changes are made. This makes recovery
	//	from branch count errors manageable.
	
	/*********************************************************************
	 * FUNCTION - addChild
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
	public int addChild(int parent_loc, int order, N branch)
			throws BranchCountException {		
		if (parent_loc != -1)
			list.get(parent_loc).incrementCount();
		
		final int branch_loc = getNthBranchIndex(parent_loc, order);
		
		list.add(branch_loc, branch);
		return branch_loc;
	}
	
	public int addChild(int parent_loc, int order, ListTree<N> branch) {
		if (parent_loc != -1)
			list.get(parent_loc).incrementCount();
		
		final int branch_loc = getNthBranchIndex(parent_loc, order);
		
		list.addAll(branch_loc, branch.list);
		return branch_loc;
	}
	
	/*********************************************************************
	 * FUNCTION - addParent
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
	public int addParent(int child_loc, N branch) throws BranchCountException {
		branch.incrementCount();
		list.add(child_loc, branch);
		return child_loc+1;
	}
	
	public int addParent(int child_loc, int order, ListTree<N> branch)
			throws BranchCountException  {
		final int tmp = branch.addChild(0, order, this.subTree(child_loc));
		setBranch(child_loc, branch.subTree(0));
		return child_loc+tmp;
	}
	
	/*********************************************************************
	 * FUNCTION - swapBranches
	 * 
	 * Swaps the locations of two children under the same parent node.
	 * 
	 */
	public void swapBranches(int child1, int child2) {
		final List<N> temp1 = list.subList(child1, getEndOfBranchIndex(child1));
		final List<N> temp2 = list.subList(child2, getEndOfBranchIndex(child2));
		final List<N> temp3 = new ArrayList<N>(temp1);
		
		temp1.clear();
		temp1.addAll(temp2);
		temp2.clear();
		temp2.addAll(temp3);
	}
	
	/*********************************************************************
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
			final List<N>
					sublist = list.subList(branch_loc, getEndOfBranchIndex(branch_loc)),
					tmplist = new ArrayList<N>(sublist);
			sublist.clear();
			branch_loc = getNthBranchIndex(parent_loc, new_order);
			list.addAll(branch_loc, tmplist);
		}
		return branch_loc;
	}
	/*********************************************************************
	 * FUNCTION - deleteBranch
	 * 
	 * Removes branch from tree. If the parent branch cannot have so few children,
	 * an exception is thrown from the parent node. Is effectively the inverse
	 * operation to "addChild".
	 * 
	 * branch_loc - the index of the branch to be deleted.
	 * 
	 */
	public void deleteBranch(int branch_loc) throws BranchCountException {
		if (getParentIndex(branch_loc) != -1)
			list.get(getParentIndex(branch_loc)).decrementCount();
		list.subList(branch_loc, getEndOfBranchIndex(branch_loc)).clear();
	}
	
	/*********************************************************************
	 * FUNCTION - deleteParent
	 * 
	 * Deletes the entire parent branch of the designated node, replacing it with
	 * the designated branch itself. The inverse operation of "addParent".
	 * 
	 * branch_loc - the index of the designated branch
	 * 
	 */
	public int deleteParent(int branch_loc) {
		int parent_loc = getParentIndex(branch_loc);
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
	
	/*********************************************************************
	 * FUNCTION - setBranch
	 * 
	 * Replaces a branch with a new node object, or an existing Tree.
	 * Avoids child-count increments/decrements.
	 * 
	 */
	public void setBranch(int branch_loc, N branch) {
		list.subList(branch_loc, getEndOfBranchIndex(branch_loc)).clear();
		list.add(branch_loc, branch);
	}

	public void setBranch(int branch_loc, ListTree<N> branch) {
		list.subList(branch_loc, getEndOfBranchIndex(branch_loc)).clear();
		list.addAll(branch_loc, branch.list);
	}

	//--------------------------------------------------------------------
	//Calculation Methods
	// TODO replace functionality by returning a reverse iterator
	public <U> List<U> forAllInReverse(BranchFunctorForm<N,U> func) {
		int i = list.size()-1;
		try {
			for (; i >= 0; --i)
				func.excecuteInLoop(list.get(i));
		} catch (CalculationException ce) {
			ce.setCauseObject(Integer.valueOf(i));
			throw ce;
		}
		return func.returnValues();
	}

}
