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
	private static final float high_scale = 1.25f;
	
	// Private Members
	private final ListTree<Operation> tree = new ListTree<Operation>();
	private final OperationBuilder node_builder;
	public int selection = 0;
	
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
		if (root_index == -1)
			tree.get(0).setParentheses(false);
		else {
			int[] branches = tree.getBranchIndices(root_index);
			
			Operation[] ops = new Operation[branches.length];
			for (int i=0; i < branches.length; ++i) {
				ops[i] = tree.get(branches[i]);
			}
			tree.get(root_index).assignBranchParentheses(ops);
		}
	}
	
	//---------------------------------------------------------------
	// Manipulation Methods
	public static final int null_index = -1;
	public void setSelection(int index) {
		if (index == selection) {
			if (selection == 0)
				setHighlight();
		}
		else {
			unsetHighlight();
			if (index == null_index)
				selection = 0;
			else {
				selection = index;
				setHighlight();
			}
		}
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
			if (ftype == tree.get(selection).ftype) {
				final boolean can_shuffle = tree.getBranchCount(selection)>1;
				i = selection;
				selection = tree.addBranch(
						selection,
						tree.getBranchCount(selection),
						node_builder.build(FunctionType.BLANK) );
				// Set parentheses for all elements in new locations
				assignParentheses(i);
				return can_shuffle;
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
					
					return tree.getBranchCount(alg.getParentIndex())>1;
				}
			}
		}
		unsetHighlight();
		// Set new function elements into place
		// TODO remove boolean return, make function return initial branch's
		//		new location (relative in subtree)
		final boolean can_shuffle = 
				node_builder.buildInSubtree(tree.subTree(selection), ftype);
		
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
		
		// Returns whether shuffle operation is valid or not
		return can_shuffle;
	}
	
	/*****************************************************************
	 * 
	 */
	public void shuffleOrder() {
		ListTree<Operation>.FindParentAlgorithm alg = tree.new FindParentAlgorithm();
		alg.run(selection);
		
		selection = tree.shiftBranchOrder(selection,
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
					.buildInSubtree(tree.subTree(selection), FunctionType.NUMBER);
		
		// Set ID numbers for all elements in new locations
		assignParentheses(tree.getRootIndex(selection));
		setHighlight();
	}
	
	
	/*****************************************************************
	 * 
	 */
	public void delete() {
		if (tree.get(selection).ftype != FunctionType.BLANK) {
			// Non-blank branches are replaced with a blank node
			tree.setSubTree(selection, 
					node_builder.build(FunctionType.BLANK) );
			
			setHighlight();
			// Set parentheses for all elements in new locations
			assignParentheses(tree.getRootIndex(selection));
		}
		else if (selection != 0) {
		/* Deletes blank node from parent's branch stack
		 * if this puts parent below min. branch count,
		 * 		delete parent, leave non-blank branch in its place
		 */
			int i = selection;
			selection = tree.getRootIndex(selection);
			// TODO replace exception logic with bounds-check
			try {
				tree.deleteSubTree(i);				
				setHighlight();
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
				
				setHighlight();
				// Set parentheses for all elements in new locations
				assignParentheses(tree.getRootIndex(selection));
			}
		}
	}
	
	public void deleteParent() {
		selection = tree.deleteRoot(selection);
		
		// Set parentheses for all elements in new locations
		assignParentheses(tree.getRootIndex(selection));
	}
	
	//-----------------------------------------------------------------
	// Calculation Methods
	public double getCalculation() throws CalculationException {
		return new OpTreeCalculator().runLoop(tree).get(0);
	}
	public double getSelectionCalculation() throws CalculationException {
		try {
			return new OpTreeCalculator()
					.runLoop(tree.subTree(selection)).get(0);
		} catch (CalculationException ce) {
			ce.setCauseObject(((Integer)ce.getCauseObject()) + selection);
			throw ce;
		}
	}
	
	// Provides MathView with tree for drawing, w/o exposing tree elsewhere
	public void setMathViewToTree(MathView view) {
		view.setListTree(tree);
	}

}
