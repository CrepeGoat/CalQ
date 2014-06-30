package awqatty.b.ListTree;

import java.util.List;
import java.util.ArrayList;

import awqatty.b.CustomExceptions.BranchCountException;
import awqatty.b.CustomExceptions.CalculationException;

public class ListTree<N extends NodeBase> {

	protected List<N> list;
	
	public ListTree() {
		list = new ArrayList<N>();
	}
	public ListTree(List<N> in) {
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
	 * 
	 */
	public ListTree<N> subTree(int index) {
		return new ListTree<N>( list.subList(index, getEndOfBranchIndex(index)) );
	}
	
	//--------------------------------------------------------------------
	//Navigation Methods
	
	/*********************************************************************
	 * METHOD - getRoot
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
	 * METHOD - getNextInLevel
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
	
	public int getNthBranchIndex(int parent_loc, int order) {
		if (parent_loc != -1)
			order = Math.min(order, list.get(parent_loc).getBranchCount());
		
		int branch_loc = parent_loc+1;

		for (int index=0; index < order && branch_loc < list.size(); ++index) {
			branch_loc = getEndOfBranchIndex(branch_loc);
		}
		
		return branch_loc;
	}
	public int getBranchNumber(int index) {
		int tmp=0; --index;
		for (; index >= 0; --index) {
			tmp += list.get(index).getBranchCount()-1;
			if (tmp >= 0)
				break;
		}
		return list.get(index).getBranchCount()-1 - tmp;
	}
	
	public int[] getBranchIndices(int parent_loc) {
		int[] indices = new int[list.get(parent_loc).getBranchCount()];
		indices[0] = parent_loc+1;
		for (int i=1; i < indices.length; ++i) {
			indices[i] = getEndOfBranchIndex(indices[i-1]);
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
 	 *	parent_loc - the index of the parent under which to insert
 	 *		the new object
 	 *		domain values: 0 - array.size()-1
 	 *	order - the position among the current child objects in which
 	 *		to insert the "branch" object (0 is first, & fastest)
 	 *		domain values: 0 - infty
 	 *	return - the new index of the inserted element
 	 *		range: 0 - array.size()-1
	 * Set (parent_loc==-1) to create a new separate tree
	 * 
	 */
	public int addChild(int parent_loc, int order, N branch) throws BranchCountException {		
		if (parent_loc != -1)
			list.get(parent_loc).incrementCount();
		
		int branch_loc = getNthBranchIndex(parent_loc, order);
		
		list.add(branch_loc, branch);
		return branch_loc;
	}
	
	public int addChild(int parent_loc, int order, ListTree<N> branch) {
		if (parent_loc != -1)
			list.get(parent_loc).incrementCount();
		
		int branch_loc = getNthBranchIndex(parent_loc, order);
		
		list.addAll(branch_loc, branch.list);
		return branch_loc;
	}
	
	/*********************************************************************
	 * FUNCTION - addParent
	 * 
	 */
	public int addParent(int child_loc, N branch) throws BranchCountException {
		branch.incrementCount();
		list.add(child_loc, branch);
		return child_loc+1;
	}
	
	public int addParent(int child_loc, int order, ListTree<N> branch) throws BranchCountException  {
		int tmp = branch.addChild(0, order, this.subTree(child_loc));
		setBranch(child_loc, branch.subTree(0));
		return child_loc+tmp;
	}
	
	/*********************************************************************
	 * FUNCTION - swapBranches
	 * 
	 */
	public void swapBranches(int parent_loc, int child1, int child2) {
		List<N> temp1 = list.subList(child1, getEndOfBranchIndex(child1));
		List<N> temp2 = list.subList(child2, getEndOfBranchIndex(child2));
		List<N> temp3 = new ArrayList<N>(temp1);
		
		temp1.clear();
		temp1.addAll(temp2);
		temp2.clear();
		temp2.addAll(temp3);
	}
	
	/*********************************************************************
	 * FUNCTION - deleteBranch
	 * 
	 */
	public void deleteBranch(int branch_loc) throws BranchCountException {
		if (getParentIndex(branch_loc) != -1)
			list.get(getParentIndex(branch_loc)).decrementCount();
		list.subList(branch_loc, getEndOfBranchIndex(branch_loc)).clear();
	}
	
	/*********************************************************************
	 * FUNCTION - setBranch
	 * 
	 */
	public void setBranch(int branch_loc, N branch) {
		// TODO (?) make method for adding entire branch, instead of single node
		list.subList(branch_loc, getEndOfBranchIndex(branch_loc)).clear();
		list.add(branch_loc, branch);
	}

	public void setBranch(int branch_loc, ListTree<N> branch) {
		// TODO (?) make method for adding entire branch, instead of single node
		list.subList(branch_loc, getEndOfBranchIndex(branch_loc)).clear();
		list.addAll(branch_loc, branch.list);
	}

	//--------------------------------------------------------------------
	//Calculation Methods	
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
