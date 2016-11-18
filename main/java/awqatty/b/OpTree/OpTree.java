package awqatty.b.OpTree;

import android.content.Context;
import android.graphics.Color;
import awqatty.b.FunctionDictionary.FunctionType;
import awqatty.b.FunctionDictionary.FunctionForms.CalculationException;
import awqatty.b.GUI.MathView;
import awqatty.b.ListTree.BranchCountException;
import awqatty.b.ListTree.ListTree;
import awqatty.b.calq.R;

public class OpTree {

	private static int highlight;
	private static int highlight_edit;
	private static final float high_scale = 1.125f;
	public static final int null_index = -1;
	private static final int source_index = 0;
	private static final int first_index = 1;


	// Private Members
	private final ListTree<Operation> tree = new ListTree<>();
	private final OperationBuilder node_builder;
	private int selection;
	
	// Constructor
	public OpTree(Context context) {
		synchronized(OpTree.class) {
			// TODO
			highlight = context.getResources().getColor(R.color.sky_blue);
			highlight_edit = context.getResources().getColor(R.color.orange_);
		}
		node_builder = new OperationBuilder(context);
		tree.addBranch(-1, 0, node_builder.build(FunctionType.BLANK));
		node_builder.buildFuncOverSubtree(tree, FunctionType.SOURCE);
		((SourceOperation)tree.get(0)).setResultToIncomplete();
		selection = first_index;
	}
		
	//---------------------------------------------------------------
	// Private Methods
	private void unsetHighlight() {
		for (Operation op : tree.subTree(selection))
			op.setColor(Color.BLACK);
		tree.get(selection).setScale(1);
	}
	private void setHighlight() {
		for (Operation op : tree.subTree(selection))
			op.setColor(highlight);
		tree.get(selection).setScale(high_scale);
	}
	private void assignParentheses(int root_index) {
		// If first element, unset parentheses
		if (root_index == null_index)
			tree.get(0).setParentheses(false);
		else {
			/*
			int[] branches = tree.getBranchIndices(root_index);
			
			Operation[] ops = new Operation[branches.length];
			for (int i=0; i < branches.length; ++i) {
				ops[i] = tree.get(branches[i]);
			}
			tree.get(root_index).assignBranchParentheses(ops);
			*/
			tree.get(root_index).assignBranchParentheses(tree.new Navigator(root_index));
		}
	}

	private void assignResult() {
		try {
			((SourceOperation)tree.get(0)).setResultToValue(
					selection != first_index,
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
		if (tree.get(selection).ftype == FunctionType.RAW_TEXT)
			addNumber(Double.parseDouble(((RawTextOperation)tree.get(selection)).getText()));
	}
	public void setSelection(int index) {
		if (index == selection) {
			if (index == first_index)
				setHighlight();
		}
		else {
			setTextAsNumber();
			unsetHighlight();
			if (index == null_index)
				selection = first_index;
			else if (index == source_index) {
				try {
					node_builder.number(new OpTreeCalculator().runLoop(tree).get(0))
							.buildFuncOverSubtree(tree.subTree(first_index), FunctionType.NUMBER);
					selection = first_index;
				} catch (CalculationException ce) {
					selection = (Integer)ce.getCauseObject();
				}
				setHighlight();
			}
			else {
				selection = index;
				setHighlight();
			}
			//assignResult();
		}
	}
	
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
			if (ftype == tree.get(selection).ftype) {
				//final boolean can_shuffle = tree.getBranchCount(selection)>1;
				unsetHighlight();
				i = selection;
				selection = tree.addBranch(
						selection,
						tree.getBranchCount(selection),
						node_builder.build(FunctionType.BLANK) );
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
				alg.run(selection);
				
				if (alg.getParentIndex() >= 0
						&& ftype == tree.get(alg.getParentIndex()).ftype) {
					unsetHighlight();
					selection = tree.addBranch(
							alg.getParentIndex(),
							alg.getBranchNumber()+1,
							node_builder.build(FunctionType.BLANK) );
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
		// TODO remove boolean return, make function return initial branch's
		//		new location (relative in subtree)
		//final boolean can_shuffle =
		node_builder.buildFuncOverSubtree(tree.subTree(selection), ftype);

		// Set parentheses for all elements in new locations
		assignParentheses(selection);
		assignParentheses(tree.getRootIndex(selection));
		// Set selection index to new location (i.e. first blank element)
		int[] indices = tree.getBranchIndices(selection);
		for (i=0; i<indices.length; ++i) {
			if (tree.get(indices[i]).ftype == FunctionType.BLANK) {
				selection = indices[i];
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
		alg.run(selection);
		
		selection = tree.shiftBranchOrder(selection,
				(alg.getBranchNumber()+1) % tree.getBranchCount(alg.getParentIndex()) );
		
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
					.buildFuncOverSubtree(tree.subTree(selection), FunctionType.NUMBER);
		
		// Set parentheses for all elements in new locations
		assignParentheses(tree.getRootIndex(selection));
		setHighlight();
		assignResult();
	}

	// Allows for text insertion of numbers
	public String getText() {
		if ((tree.get(selection).ftype != FunctionType.RAW_TEXT)) {
			node_builder.buildFuncOverSubtree(tree.subTree(selection), FunctionType.RAW_TEXT);
			tree.get(selection).setColor(highlight_edit);
		}
		return ((RawTextOperation)tree.get(selection)).getText();
	}
	public void setText(String str) {
		((RawTextOperation)tree.get(selection)).setText(str);
	}

	/*****************************************************************
	 * 
	 */
	public void delete() {
		if (tree.get(selection).ftype != FunctionType.BLANK) {
			// Non-blank branches are replaced with a blank node
			tree.setSubTree(selection, 
					node_builder.build(FunctionType.BLANK) );
			// Set parentheses for all elements in new locations
			assignParentheses(tree.getRootIndex(selection));

			setHighlight();
			assignResult();
		}
		else if (selection > first_index) {
		/* Deletes blank node from parent's branch stack
		 * if this puts parent below min. branch count,
		 * 		delete parent, leave non-blank branch in its place
		 */
			int i = selection;
			selection = tree.getRootIndex(selection);
			// TODO replace exception logic with bounds-check
			try {
				tree.deleteSubTree(i);
				// Set parentheses for all elements in new locations
				assignParentheses(selection);
			}
			catch (BranchCountException bce) {
				// Replace parent with last non-BLANK branch of parent
				final int[] indices = tree.getBranchIndices(selection);
				for (i=indices.length-1; i > 0 ; --i) {
					if (tree.get(indices[i]).ftype != FunctionType.BLANK) {
						break;
					}
				}
				tree.deleteRoot(indices[i]);
				// Set parentheses for all elements in new locations
				assignParentheses(tree.getRootIndex(selection));
			}
			setHighlight();
			assignResult();
		}
	}
	
	public void deleteParent() {
		setTextAsNumber();
		if (selection > first_index) {
			selection = tree.deleteRoot(selection);
			// Set parentheses for all elements in new locations
			assignParentheses(tree.getRootIndex(selection));
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
					.runLoop(tree.subTree(selection)).get(0);
		} catch (CalculationException ce) {
			// Adjust index to be offset from "selection" value
			ce.setCauseObject(((Integer)ce.getCauseObject()) + selection);
			throw ce;
		}
	}
	//*/
	// Provides MathView with tree for drawing, w/o exposing tree elsewhere
	public void setMathViewToTree(MathView view) {
		view.setListTree(tree);
	}

}
