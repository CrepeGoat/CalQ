package awqatty.b.ArrayTree;

import java.util.List;
import java.util.ArrayList;

import awqatty.b.CustomExceptions.BranchCountException;
import awqatty.b.CustomExceptions.CalculationException;

public class ArrayTree<T extends NodeBase> {

	protected ArrayList<T> array = new ArrayList<T>();
		
	//--------------------------------------------------------------------
	// Access Methods
	public T get(int index) {
		return array.get(index);
	}
	public int size() {
		return array.size();
	}
	
	//--------------------------------------------------------------------
	//Navigation Methods
	
	/*********************************************************************
	 * METHOD - getRoot
	 * Returns ref. index of parent node to the given "index"
	 * 
	 */
	public int getRoot(int index) {
		int tmp=0; --index;
		for (; index >= 0; --index) {
			tmp += 1-array.get(index).getBranchCount();
			if (tmp <= 0)
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
	public int getEndOfBranch(int index) {
		int tmp=array.get(index++).getBranchCount();
		while (tmp > 0) {
			tmp += array.get(index++).getBranchCount() - 1;
		}
		return index;
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
	public int addChild(T branch, int parent_loc, int order) throws BranchCountException {		
		if (parent_loc >= 0) {
			array.get(parent_loc).incrementCount();
			order = Math.min(order, array.get(parent_loc).getBranchCount());
		}
		else
			order = 0;
		
		int branch_loc = parent_loc+1;

		for (int index=0; index < order; ++index) {
			if (branch_loc >= array.size())
				break;
			else
				branch_loc = getEndOfBranch(branch_loc);
		}
		
		array.add(branch_loc, branch);
		return branch_loc;
	}
	// same as previous method, with order==0 as default
	// (ensures fastest insertion, best if order does not matter)
	public int addChild(T branch, int parent_loc) throws BranchCountException {		
		if (parent_loc >= 0)
			array.get(parent_loc).incrementCount();
				
		array.add(parent_loc+1, branch);
		return parent_loc+1;
	}
	
	/*********************************************************************
	 * FUNCTION - addParent
	 * 
	 */
	public void addParent(T branch, int child_loc) throws BranchCountException {
		branch.incrementCount();
		array.add(child_loc, branch);
	}
	
	/*********************************************************************
	 * FUNCTION - swapBranches
	 * 
	 */
	public void swapBranches(int parent_loc, int child1, int child2) {
		List<T> temp1 = array.subList(child1, getEndOfBranch(child1));
		List<T> temp2 = array.subList(child2, getEndOfBranch(child2));
		List<T> temp3 = new ArrayList<T>(temp1);
		
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
		if (getRoot(branch_loc) != -1)
			array.get(getRoot(branch_loc)).decrementCount();
		array.subList(branch_loc, getEndOfBranch(branch_loc)).clear();
	}
	
	/*********************************************************************
	 * FUNCTION - replaceBranch
	 * 
	 */
	public void replaceBranch(T branch, int branch_loc) {
		// TODO (?) make method for adding entire branch, instead of single node
		array.subList(branch_loc, getEndOfBranch(branch_loc)).clear();
		array.add(branch_loc, branch);
	}
	
	//--------------------------------------------------------------------
	//Calculation Methods
	
	public <U> ArrayList<U> calculate(BranchFunction<T,U> func) throws CalculationException {
		// TODO rewrite to push/pop from back -> more efficient
		ArrayList<U> stack = new ArrayList<U>();
		for (int i = array.size()-1; i >= 0; --i) {
			try {
				if (array.get(i).getBranchCount() == 0)
					stack.add(0, func.calculate(array.get(i),
							stack.subList(0, array.get(i).getBranchCount()) ));
				else {
					stack.set(0, func.calculate(array.get(i), 
							stack.subList(0, array.get(i).getBranchCount()) ));
					stack.subList(1, array.get(i).getBranchCount()).clear();
				}	
			}
			catch (CalculationException ce) {
				ce.setCauseObject(Integer.valueOf(i));
				throw ce;
			}
		}
		
		return stack;

	}

}
