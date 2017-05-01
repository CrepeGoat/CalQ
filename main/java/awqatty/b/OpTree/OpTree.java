package awqatty.b.OpTree;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import awqatty.b.FunctionDictionary.FunctionType;
import awqatty.b.FunctionDictionary.FunctionForms.CalculationException;
import awqatty.b.GUI.NumberStringConverter;
import awqatty.b.GUI.TouchableMathView;
import awqatty.b.ListTree.BranchCountException;
import awqatty.b.ListTree.ListTree;
import awqatty.b.calq.R;

public class OpTree {

	private static int color_highlightSelect;
	private static int color_highlightEdit;
	private static final float high_scale = 1.125f;
	public static final int null_index = -1;
	private static final int source_index = 0;
	private static final int first_index = 1;


	// Private Members
	private final ListTree<Operation> tree = new ListTree<>();
	private final OperationBuilder node_builder;
	private int index_selection;
	public int getSelectionIndex() {return index_selection;}
	
	// Constructor
	public OpTree(Context context) {
		synchronized(OpTree.class) {
			// TODO
			color_highlightSelect = ContextCompat.getColor(context, R.color.sky_blue);
			color_highlightEdit = ContextCompat.getColor(context, R.color.orange_);
		}
		node_builder = new OperationBuilder(context);
		tree.addBranch(-1, 0, node_builder.build(FunctionType.BLANK));
		node_builder.buildFuncOverSubtree(tree, FunctionType.SOURCE);
		((SourceOperation)tree.get(0)).setResultToIncomplete();
		index_selection = first_index;
	}
		
	//---------------------------------------------------------------
	// Private Methods
	private void unsetHighlight() {
		for (Operation op : tree.subTree(index_selection))
			op.setColor(Color.BLACK);
		tree.get(index_selection).setScale(1);
	}
	private void setHighlight() {
		for (Operation op : tree.subTree(index_selection))
			op.setColor(color_highlightSelect);
		tree.get(index_selection).setScale(high_scale);
	}
	private void assignParentheses(int root_index) {
		// If first element, unset parentheses
		if (root_index < first_index)
			tree.get(first_index).setParentheses(false);
		else if (tree.getBranchCount(root_index) > 0) {
			/*
			int[] branches = tree.getBranchIndices(root_index);
			
			Operation[] ops = new Operation[branches.length];
			for (int i=0; i < branches.length; ++i) {
				ops[i] = tree.get(branches[i]);
			}
			tree.get(root_index).assignBranchParentheses(ops);
			*/
			tree.get(root_index).assignBranchParentheses(tree.new Navigator());

			//final int[] branch_indices = tree.getBranchIndices(root_index);
			//final boolean[] pars_active = tree.get(root_index)
			//		.assignBranchParentheses(tree, branch_indices);
			//for (int i=0; i<branch_indices.length; ++i) {
			//	tree.get(branch_indices[i]).setParentheses(pars_active[i]);
			//}
		}
	}

	private void assignResult() {
		try {
			((SourceOperation)tree.get(0)).setResultToValue(
					index_selection != first_index,
					new OpTreeCalculator().runLoop(tree).get(0)
							//selection > first_index ? tree.subTree(selection):tree
			);
		} catch (CalculationException ce) {
			((SourceOperation)tree.get(0)).setResultToIncomplete();
		}
	}

	//---------------------------------------------------------------
	// Manipulation Methods
	public void setTextAsNumber() {
		if (tree.get(index_selection).ftype == FunctionType.RAW_TEXT)
			addNumber(Double.parseDouble(((RawTextOperation)tree.get(index_selection)).getText()));
	}
	/*
	 * (Used for multi-sub-branch selection)
	public void selectNone() {
		unsetHighlight();
		select_first = 0;
		select_range = 1;
	}
	public void setNewSelection(int index) {
		unsetHighlight();
		// TODO may need to change reset parameters
		if (tree.getBranchCount(index) <= 0) {
			select_first = index;
			select_range = 1;
		} else {
			select_first = tree.getNthBranchIndex(index, 0);
			select_range = 0;
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
			// If selection contains all subbranches of root,
			//	shifts selection to root
			if (select_range == tree.getBranchCount(dcr)) {
				select_first = dcr;
				select_range = 1;



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
	// TODO use to decouple selection setting into distinct "start" and "add" functions
	// TODO implement multi_branch selection
	public void selectNone() {
		setTextAsNumber();
		unsetHighlight();
		index_selection = first_index;
	}
	public void setNewSelection(int... indices) {
		setTextAsNumber();
		unsetHighlight();
		//index_selection = null_index;
		index_selection = tree.getDeepestCommonRoot(indices);
		if (index_selection != source_index)
			setHighlight();
	}
	public void addToSelection(int... indices) {
		// General Case
		unsetHighlight();
		index_selection = tree.getDeepestCommonRoot(
				//(index_selection!=null_index ? index_selection:indices[0]),
				index_selection,
				tree.getDeepestCommonRoot(indices)
		);
		if (index_selection != source_index)
			setHighlight();
	}
	public void finalizeSelection() {
		//if (index_selection == null_index) {
		//	index_selection = first_index;
		//}
		//else
		if (index_selection == source_index) {
			try {
				node_builder.number(new OpTreeCalculator().runLoop(tree).get(0))
						.buildFuncOverSubtree(tree.subTree(first_index), FunctionType.NUMBER);
				index_selection = first_index;
			} catch (CalculationException ce) {
				index_selection = (Integer) ce.getCauseObject();
				setHighlight();
			}
		}
	}

	// TODO delete
	/*
	public void setSelectionIndex(int index) {
		setTextAsNumber();
		unsetHighlight();
		if (index == null_index)
			index_selection = first_index;
		else if (index == source_index) {
			try {
				node_builder.number(new OpTreeCalculator().runLoop(tree).get(0))
						.buildFuncOverSubtree(tree.subTree(first_index), FunctionType.NUMBER);
				index_selection = first_index;
			} catch (CalculationException ce) {
				index_selection = (Integer)ce.getCauseObject();
			}
			setHighlight();
		}
		else {
			index_selection = index;
			setHighlight();
		}
		//assignResult();
	}
	//*/
	/****************************************************************
	 * 
	 * Returns whether or not the new selected object can be shuffled.
	 * Based on function type.
	 * 
	 */
	public void addFunction(FunctionType ftype) {
		setTextAsNumber();
		int i;
		// Condition for commutative operations
		if (ftype.isCommutative()) {
			if (ftype == tree.get(index_selection).ftype) {
				//final boolean can_shuffle = tree.getBranchCount(selection)>1;
				unsetHighlight();
				i = index_selection;
				index_selection = tree.addBranch(
						index_selection,
						tree.getBranchCount(index_selection),
						node_builder.build(FunctionType.BLANK)
				);
				// Set parentheses for all elements in new locations
				setHighlight();
				assignParentheses(i);
				assignResult();
				return;
				//return can_shuffle;
			}
			else {
				final ListTree<Operation>.FindParentAlgorithm alg = 
						tree.new FindParentAlgorithm();
				alg.run(index_selection);
				
				if (alg.getParentIndex() >= 0
						&& ftype == tree.get(alg.getParentIndex()).ftype) {
					unsetHighlight();
					index_selection = tree.addBranch(
							alg.getParentIndex(),
							alg.getBranchNumber()+1,
							node_builder.build(FunctionType.BLANK)
					);
					setHighlight();
					// Set parentheses for all elements in new locations
					assignParentheses(alg.getParentIndex());
					assignResult();
					return;
					//return tree.getBranchCount(alg.getParentIndex())>1;
				}
			}
		}

		unsetHighlight();
		// Set new function elements into place
		// TODO make function return initial branch's new location (relative in subtree)
		//final boolean can_shuffle =
		node_builder.buildFuncOverSubtree(tree.subTree(index_selection), ftype);

		// Set parentheses for all elements in new locations
		assignParentheses(index_selection);
		assignParentheses(tree.getRootIndex(index_selection));
		// Set selection index to new location (i.e. first blank element)
		int[] indices = tree.getBranchIndices(index_selection);
		for (i=0; i<indices.length; ++i) {
			if (tree.get(indices[i]).ftype == FunctionType.BLANK) {
				index_selection = indices[i];
				break;
			}
		}

		setHighlight();
		assignResult();
		// Returns whether shuffle operation is valid or not
		//return can_shuffle;
	}
	
	/*****************************************************************
	 * 
	 */
	public void shuffleOrder() {
		ListTree<Operation>.FindParentAlgorithm alg = tree.new FindParentAlgorithm();
		alg.run(index_selection);

		index_selection = tree.shiftBranchOrder(
				index_selection,
				(alg.getBranchNumber()+1) % tree.getBranchCount(alg.getParentIndex())
		);
		
		// All elements are in new locations -> Set parentheses
		assignParentheses(alg.getParentIndex());
		// No need to reset highlight, selection shifts w/ object movement
		assignResult();
	}

	/*****************************************************************
	 * 
	 */
	public void addNumber(double num) {
		node_builder.number(num)
					.buildFuncOverSubtree(tree.subTree(index_selection), FunctionType.NUMBER);
		
		// Set parentheses for all elements in new locations
		assignParentheses(tree.getRootIndex(index_selection));
		setHighlight();
		assignResult();
	}

	// Allows for text insertion of numbers
	public String getText() {
		if (tree.get(index_selection).ftype != FunctionType.RAW_TEXT) {
			String str_tmp = "";
			if (tree.get(index_selection).ftype == FunctionType.NUMBER) {
				// -> then set temporary text as number's current text
				str_tmp = NumberStringConverter.toReducedString(
						tree.get(index_selection).func.calculate(null)
				);
			}
			node_builder.buildFuncOverSubtree(tree.subTree(index_selection), FunctionType.RAW_TEXT);
			tree.get(index_selection).setColor(color_highlightEdit);
			if (str_tmp != "") {
				((RawTextOperation)tree.get(index_selection)).setText(str_tmp);
			}
		}
		return ((RawTextOperation)tree.get(index_selection)).getText();
	}
	public void setText(String str) {
		((RawTextOperation)tree.get(index_selection)).setText(str);
	}

	/*****************************************************************
	 * 
	 */
	public void delete() {
		if (tree.get(index_selection).ftype != FunctionType.BLANK) {
			// Non-blank branches are replaced with a blank node
			tree.setSubTree(index_selection,
					node_builder.build(FunctionType.BLANK) );
			// Set parentheses for all elements in new locations
			assignParentheses(tree.getRootIndex(index_selection));

			setHighlight();
			assignResult();
		}
		else if (index_selection > first_index) {
		/* Deletes blank node from parent's branch stack
		 * if this puts parent below min. branch count,
		 * 		delete parent, leave non-blank branch in its place
		 */
			int i = index_selection;
			index_selection = tree.getRootIndex(index_selection);
			// TODO replace exception logic with bounds-check
			try {
				tree.deleteSubTree(i);
				// Set parentheses for all elements in new locations
				assignParentheses(index_selection);
			}
			catch (BranchCountException bce) {
				// Replace parent with last non-BLANK branch of parent
				final int[] indices = tree.getBranchIndices(index_selection);
				for (i=indices.length-1; i > 0 ; --i) {
					if (tree.get(indices[i]).ftype != FunctionType.BLANK) {
						break;
					}
				}
				tree.deleteRoot(indices[i]);
				// Set parentheses for all elements in new locations
				assignParentheses(tree.getRootIndex(index_selection));
			}
			setHighlight();
			assignResult();
		}
	}
	
	public void deleteParent() {
		setTextAsNumber();
		if (index_selection > first_index) {
			index_selection = tree.deleteRoot(index_selection);
			// Set parentheses for all elements in new locations
			assignParentheses(tree.getRootIndex(index_selection));
		}
		assignResult();
	}
	
	//-----------------------------------------------------------------
	// Calculation Methods
	//*
	public double getCalculation() throws CalculationException {
		return new OpTreeCalculator().runLoop(tree).get(0);
	}
	public double getSelectionCalculation() throws CalculationException {
		try {
			return new OpTreeCalculator()
					.runLoop(tree.subTree(index_selection)).get(0);
		} catch (CalculationException ce) {
			// Adjust index to be offset from "selection" value
			ce.setCauseObject(((Integer)ce.getCauseObject()) + index_selection);
			throw ce;
		}
	}
	//*/
	// Provides MathView with tree for drawing, w/o exposing tree elsewhere
	public void setMathViewToTree(TouchableMathView mview) {
		mview.setListTree(tree);
	}

}
