 package awqatty.b.OpTree;

import awqatty.b.FunctionDictionary.FunctionType;
import awqatty.b.FunctionDictionary.FunctionForms.CalculationException;
import awqatty.b.GenericTextPresentation.Tags;
import awqatty.b.ListTree.BranchCountException;
import awqatty.b.ListTree.ListTree;
import awqatty.b.TextPresentation.TextPresBuilderForm;

public class OpTree {

	// Private Members
	private final ListTree<Operation> tree = new ListTree<Operation>();
	private final OperationBuilder node_builder;
	public int selection = 0;
	
	// Constructor
	public OpTree(TextPresBuilderForm tpb) {
		node_builder = new OperationBuilder(tpb);
		tree.addBranch(-1, 0, node_builder.build(FunctionType.BLANK));
		tree.get(0).setIdNumber(0);
	}
	
	public void setTextPresBuilder(TextPresBuilderForm tpb) {
		node_builder.setTextPresBuilder(tpb);
		// TODO change out all objects to have proper TextPres objects
	}
	
	//---------------------------------------------------------------
	// Private Methods
	private void unsetHighlight() {
		/* TODO uncomment method
		tree.get(selection).disableTagFlag(TagFlags.HIGHLIGHT);
		//*/
	}
	private void setHighlight() {
		/* TODO uncomment method
		if (selection != 0)		// highlighting everything == highlighting nothing
			tree.get(selection).enableTagFlag(TagFlags.HIGHLIGHT); 
		//*/
	}
		
	//---------------------------------------------------------------
	// Manipulation Methods
	public void setSelection(int index) {
		unsetHighlight();
		selection=index;
		setHighlight();
	}
	
	/****************************************************************
	 * 
	 * Returns whether or not the new selected object can be shuffled.
	 * Based on parent's branch count.
	 * 
	 */
	public boolean addFunction(FunctionType ftype) {
		int i;
		// Condition for commutative operations
		if (ftype.isCommutative()) {
			if (ftype == tree.get(selection).ftype) {
				final boolean can_shuffle = tree.getBranchCount(selection)>1;
				i = selection = tree.addBranch(
						selection,
						tree.getBranchCount(selection),
						node_builder.build(FunctionType.BLANK) );
				// Set ID numbers for all elements in new locations
				for (; i<tree.size(); ++i)
					tree.get(i).setIdNumber(i);
				return can_shuffle;
			}
			else {
				final ListTree<Operation>.FindParentAlgorithm alg = 
						tree.new FindParentAlgorithm();
				alg.run(selection);
				
				if (alg.getParentIndex() >= 0
						&& ftype == tree.get(alg.getParentIndex()).ftype) {
					unsetHighlight();
					i = selection = tree.addBranch(
							alg.getParentIndex(),
							alg.getBranchNumber()+1,
							node_builder.build(FunctionType.BLANK) );
					setHighlight();
					// Set ID numbers for all elements in new locations
					for (; i<tree.size(); ++i)
						tree.get(i).setIdNumber(i);
					return tree.getBranchCount(alg.getParentIndex())>1;
				}
			}
		}
		unsetHighlight();
		i = selection;
		// Set new function elements into place
		final boolean can_shuffle = 
				node_builder.buildInSubtree(tree.subTree(selection), ftype);
		
		int last_index = tree.size();
		// Set ID numbers for all elements in new locations
		for (; i<last_index; ++i)
			tree.get(i).setIdNumber(i);
		
		// Set selection index to new location (i.e. first blank element)
		int[] indices = tree.getBranchIndices(selection);
		last_index = tree.getBranchCount(selection);
		for (i=0; i<last_index; ++i) {
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
		
		final int parent_loc = alg.getParentIndex();
		int i = selection;
		selection = tree.shiftBranchOrder(selection,
				(alg.getBranchNumber()+1) % tree.getBranchCount(parent_loc) );
		
		int last_loc = tree.getEndOfBranchIndex(Math.max(i,selection));
		// Set ID numbers for all elements in new locations
		for (i = Math.min(i,selection); i < last_loc; ++i)
			tree.get(i).setIdNumber(i);
		// No need to reset highlight, selection shifts w/ object movement
	}

	/*****************************************************************
	 * 
	 */
	public void addNumber(double num) {
		boolean has_branch = tree.getBranchCount(selection) > 0;
		node_builder.number(num)
					.buildInSubtree(tree.subTree(selection), FunctionType.NUMBER);
		
		// Set ID numbers for all elements in new locations
		if (has_branch)
			for (int i=selection; i<tree.size(); ++i)
				tree.get(i).setIdNumber(i);
		else
			tree.get(selection).setIdNumber(selection);
		setHighlight();
	}
	
	
	/*****************************************************************
	 * 
	 */
	public void delete() {
		if (tree.get(selection).ftype != FunctionType.BLANK) {
			// Non-blank branches are replaced with a blank node
			boolean has_branch = tree.getBranchCount(selection) > 0;
			tree.setSubTree(selection, 
					node_builder.build(FunctionType.BLANK) );
			
			setHighlight();
			// Set ID numbers for all elements in new locations
			if (has_branch)
				for (int i=selection; i<tree.size(); ++i)
					tree.get(i).setIdNumber(i);
			else
				tree.get(selection).setIdNumber(selection);
		}
		else if (selection != 0) {
		/* Deletes blank node from parent's branch stack
		 * if this puts parent below min. branch count,
		 * 		delete parent, leave non-blank branch in its place
		 */
			int i;
			try {
				i = selection;
				selection = tree.getRootIndex(selection);
				tree.deleteSubTree(i);
				
				setHighlight();
				// Set ID numbers for all elements in new locations
				for (i=selection; i<tree.size(); ++i)
					tree.get(i).setIdNumber(i);
			}
			catch (BranchCountException bce) {
				// Replace parent with last non-BLANK branch of parent
				int[] indices = tree.getBranchIndices(selection);
				for (i=indices.length-1; i > 0 ; --i) {
					if (tree.get(indices[i]).ftype != FunctionType.BLANK) {
						break;
					}
				}
				tree.setSubTree(selection,
						new ListTree<Operation>(tree.subTree(indices[i])) );
				
				setHighlight();
				// Set ID numbers for all elements in new locations
				for (i=selection; i<tree.size(); ++i)
					tree.get(i).setIdNumber(i);
			}
		}
	}
	
	public void deleteParent() {
		int i = selection;
		selection = tree.deleteRoot(selection);
		
		// Reset ID tags
		if (i != selection) {
			final int length = tree.size();
			for (i=selection; i<length; ++i)
				tree.get(i).setIdNumber(i);			
		}
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

	public String getTextPres() {
		return new OpTreeXmlDisplay((byte)0).runLoop(tree).get(0)
				.replaceAll(Tags.PARENTHESIS_L.getTag(), "")
				.replaceAll(Tags.PARENTHESIS_R.getTag(), "");
	}

}
