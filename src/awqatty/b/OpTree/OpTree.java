package awqatty.b.OpTree;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import awqatty.b.FunctionDictionary.FunctionType;
import awqatty.b.FunctionDictionary.FunctionForms.CalculationException;
import awqatty.b.GUI.TouchMathView;
import awqatty.b.ListTree.BranchCountException;
import awqatty.b.ListTree.ListTree;
import awqatty.b.calq.R;

public class OpTree {

	private static int highlight;
	private static final float high_scale = 1.25f;
	
	// Private Members
	private final ListTree<Operation> tree = new ListTree<Operation>();
	private final OperationBuilder node_builder;
	
	private int select_index=0;
	public int getSelectionIndex() {return select_index;}
	
	// Constructor
	public OpTree(Context context) {
		synchronized(OpTree.class) {
			highlight = context.getResources().getColor(R.color.sky_blue);
		}
		node_builder = new OperationBuilder(context);
		tree.addBranch(-1, 0, node_builder.build(FunctionType.BLANK));
	}
		
	//---------------------------------------------------------------
	// Private Methods
	private void assignParentheses(int root_index) {
		// If first element, unset parentheses
		if (root_index == -1)
			tree.get(0).setParentheses(false);
		else if (tree.getBranchCount(root_index) > 0) {
			/*
			int[] branches = tree.getBranchIndices(root_index);
			
			Operation[] ops = new Operation[branches.length];
			for (int i=0; i < branches.length; ++i) {
				ops[i] = tree.get(branches[i]);
			}
			tree.get(root_index).assignBranchParentheses(ops);
			/*/
			final int[] branch_indices = tree.getBranchIndices(root_index);
			final boolean[] pars_active = tree.get(root_index)
					.assignBranchParentheses(tree, branch_indices);
			for (int i=0; i<branch_indices.length; ++i) {
				Log.d("AlignSeriesBase::subBranchShouldUsePars",
						"Decision " + Integer.toString(branch_indices[i]) + ": "
						+ Boolean.toString(pars_active[i]));
				tree.get(branch_indices[i]).setParentheses(pars_active[i]);
			}
			//*/
		}
	}
	
	private void unsetHighlight() {
		tree.get(select_index).setScale(1);
		for (int i=tree.getEndOfBranchIndex(select_index)-1;
				i>=select_index; --i) {
			tree.get(i).setColor(Color.BLACK);
		}
	}
	private void setHighlight() {
		tree.get(select_index).setScale(high_scale);
		for (int i=tree.getEndOfBranchIndex(select_index)-1;
				i>=select_index; --i) {
			tree.get(i).setColor(highlight);
		}
	}
	//---------------------------------------------------------------
	// Manipulation Methods
	//public static final int null_index = -1;
	/*
	 * (Used for multi-sub-branch selection)
	public void selectNone() {
		unsetHighlight();
		select_first = 0;
		select_range = 1;
	}
	public void resetSelection(int index) {
		unsetHighlight();
		// TODO may need to change reset parameters
		if (tree.getBranchCount(index) <= 0) {
			select_first = index;
			select_range = 1;
		} else {
			select_first = tree.getNthBranchIndex(index, 0);
			select_range = 0;
		}
	}
	public void addToSelection(int index) {
		// TODO finish logic to accommodate multi_select
		// if root - needs to highlight (since blank click does not)
		// General Case
		final int dcr = tree.getDeepestCommonRoot(select_first, index);
		
		if (dcr == select_first) {
			// Specialty Case: Re-clicking selected element
			if (select_first != 0)
				// if not root - do nothing
				return;
			// else - highlight selection
		} else {
			// Sets selection/index to its highest root underneath dcr
			boolean selectIsSet = false;
			boolean indexIsSet = (index==dcr);
			int select_counter=0;
			int index_counter=0;
			
			int start_index;
			int end_index = tree.getNthBranchIndex(dcr, 0);
			int counter = 0;
			
			while(true) {
				// Uses no bounds test; both index & select_first should be
				//	within root branch
				
				// Increments range
				start_index = end_index;
				end_index = tree.getEndOfBranchIndex(end_index);
				// Checks select_first
				if (!selectIsSet && select_first < end_index) {
					if (select_first > start_index) {
						// sets selection to deeper node
						// -> resets root selection & selection length
						select_first = start_index;
						select_range = 1;
					}
					select_counter = counter;
					
					selectIsSet = true;
					if (indexIsSet) break;
				}
				// Checks index
					// (Note - uses else, since both cannot be within same sub-branch
					//	by definition of dcr)
				else if (!indexIsSet && index < end_index) {
					index = start_index;
					index_counter = counter;
					
					indexIsSet = true;
					if (selectIsSet) break;
				}
				++counter;
			}
			// Sets selection according to head nodes
			if (dcr != index) {
				// -> new selection does not encloses old
				// Expands range to include index branch
				if (select_range == 0) {
					// -> original selection
					select_first = index;
					select_range = 1;
				} else if (select_first <= index) {
					select_range = Math.max(select_range, index_counter-select_counter+1);
				} else {
					select_first = index;
					select_range += select_counter-index_counter;
				}
			}
			// If selection contains all subbranches of root,
			//	shifts selection to root
			if (select_range == tree.getBranchCount(dcr)) {
				select_first = dcr;
				select_range = 1;
			}
		}
	}
	public void finalizeSelection() {
		// takes care of case issue w/ split root/branches logic
		// TODO
		if (select_range == 0) {
			select_first = tree.getRootIndex(select_first);
			select_range = 1;
		}
	}
	*/
	// (Used for single-branch selection)
	public void selectNone() {
		unsetHighlight();
		select_index = 0;
	}
	public void resetSelection(int... indices) {
		unsetHighlight();
		select_index = tree.getDeepestCommonRoot(indices);
	}
	public void addToSelection(int... indices) {
		// General Case
		int[] tmp_array = new int[indices.length+1];
		for (int i=0; i<indices.length; ++i)
			tmp_array[i] = indices[i];
		tmp_array[indices.length] = select_index;
		select_index = tree.getDeepestCommonRoot(tmp_array);
	}
	public void finalizeSelection() {
		setHighlight();
	}

	
	/****************************************************************
	 * 
	 * Returns whether or not the new selected object can be shuffled.
	 * Based on function type.
	 * 
	 */
	public boolean addFunction(FunctionType ftype) {
		int i;
		// Condition for commutative operations
		if (ftype.isCommutative()) {
			if (ftype == tree.get(select_index).ftype) {
				final boolean can_shuffle = tree.getBranchCount(select_index)>1;
				i = select_index;
				select_index = tree.addBranch(
						select_index,
						tree.getBranchCount(select_index),
						node_builder.build(FunctionType.BLANK) );
				// Set parentheses for all elements in new locations
				assignParentheses(i);
				return can_shuffle;
			}
			else {
				final ListTree<Operation>.FindParentAlgorithm alg = 
						tree.new FindParentAlgorithm();
				alg.run(select_index);
				
				if (alg.getParentIndex() >= 0
						&& ftype == tree.get(alg.getParentIndex()).ftype) {
					unsetHighlight();
					select_index = tree.addBranch(
							alg.getParentIndex(),
							alg.getBranchNumber()+1,
							node_builder.build(FunctionType.BLANK) );
					setHighlight();
					// Set parentheses for all elements in new locations
					assignParentheses(alg.getParentIndex());
					
					return tree.getBranchCount(alg.getParentIndex())>1;
				}
			}
		}
		unsetHighlight();
		// Set new function elements into place
		final boolean can_shuffle = 
				node_builder.buildInSubtree(tree.subTree(select_index), ftype);
		
		// Set parentheses for all elements in new locations
		assignParentheses(select_index);
		assignParentheses(tree.getRootIndex(select_index));
		// Set selection index to new location (i.e. first blank element)
		int[] indices = tree.getBranchIndices(select_index);
		for (i=0; i<indices.length; ++i)
				if (tree.get(indices[i]).ftype == FunctionType.BLANK) {
			select_index = indices[i];
			break;
		}
		setHighlight();
		
		// Returns whether shuffle operation is valid or not
		return can_shuffle;
	}
	
	/*****************************************************************
	 * 
	 */
	public void shuffleOrder() {
		ListTree<Operation>.FindParentAlgorithm alg = tree.new FindParentAlgorithm();
		alg.run(select_index);
		
		select_index = tree.shiftBranchOrder(select_index,
				(alg.getBranchNumber()+1) % tree.getBranchCount(alg.getParentIndex()) );
		
		// Set parentheses for all elements in new locations
		assignParentheses(alg.getParentIndex());
		// No need to reset highlight, selection shifts w/ object movement
	}

	/*****************************************************************
	 * 
	 */
	public void addNumber(double num) {
		node_builder.number(num)
					.buildInSubtree(tree.subTree(select_index), FunctionType.NUMBER);
		
		// Set ID numbers for all elements in new locations
		assignParentheses(tree.getRootIndex(select_index));
		setHighlight();
	}
	
	
	/*****************************************************************
	 * 
	 */
	public void delete() {
		if (tree.get(select_index).ftype != FunctionType.BLANK) {
			// Non-blank branches are replaced with a blank node
			tree.setSubTree(select_index, 
					node_builder.build(FunctionType.BLANK) );
			
			setHighlight();
			// Set parentheses for all elements in new locations
			assignParentheses(tree.getRootIndex(select_index));
		}
		else if (select_index != 0) {
		/* Deletes blank node from parent's branch stack
		 * if this puts parent below min. branch count,
		 * 		delete parent, leave non-blank branch in its place
		 */
			int i = select_index;
			select_index = tree.getRootIndex(select_index);
			// TODO replace exception logic with bounds-check
			try {
				tree.deleteSubTree(i);				
				setHighlight();
				// Set parentheses for all elements in new locations
				assignParentheses(select_index);
			}
			catch (BranchCountException bce) {
				// Replace parent with last non-BLANK branch of parent
				final int[] indices = tree.getBranchIndices(select_index);
				for (i=indices.length-1; i > 0 ; --i) {
					if (tree.get(indices[i]).ftype != FunctionType.BLANK) {
						break;
					}
				}
				tree.deleteRoot(indices[i]);
				
				setHighlight();
				// Set parentheses for all elements in new locations
				assignParentheses(tree.getRootIndex(select_index));
			}
		}
	}
	
	public void deleteParent() {
		select_index = tree.deleteRoot(select_index);
		
		// Set parentheses for all elements in new locations
		assignParentheses(tree.getRootIndex(select_index));
	}
	
	//-----------------------------------------------------------------
	// Calculation Methods
	public double getCalculation() throws CalculationException {
		return new OpTreeCalculator().runLoop(tree).get(0);
	}
	public double getSelectionCalculation() throws CalculationException {
		try {
			return new OpTreeCalculator()
					.runLoop(tree.subTree(select_index)).get(0);
		} catch (CalculationException ce) {
			ce.setCauseObject(((Integer)ce.getCauseObject()) + select_index);
			throw ce;
		}
	}
	
	// Provides MathView with tree for drawing, w/o exposing tree elsewhere
	public void setMathViewToTree(TouchMathView view) {
		view.setListTree(tree);
	}

}
