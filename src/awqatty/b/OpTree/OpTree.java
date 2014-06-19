package awqatty.b.OpTree;

import awqatty.b.ArrayTree.ArrayTree;
import awqatty.b.CustomExceptions.BranchCountException;
import awqatty.b.CustomExceptions.CalculationException;
import awqatty.b.FunctionDictionary.FunctionType;
import awqatty.b.MathmlPresentation.TagFlags;

public class OpTree {

	private ArrayTree<OpNode> tree = new ArrayTree<OpNode>();
	private OpNodeBuilder node_builder = new OpNodeBuilder();
	public int selection = 0;
	
	public OpTree() {
		tree.addChild(node_builder.build(FunctionType.BLANK), -1);
		tree.get(0).setIdNumber(0);
	}
	
	public void unsetHighlight() {
		tree.get(selection).disableTagFlag(TagFlags.HIGHLIGHT);
	}
	public void setHighlight() {
		if (selection != 0)
			tree.get(selection).enableTagFlag(TagFlags.HIGHLIGHT);
	}
	
	public void addFunction(FunctionType ftype) {
		// TODO add condition for commutative operations
		if (!FunctionType.isFunction(ftype)) {
			// This function should only be called to add functions. Add error cond.?
			throw new RuntimeException();
		}
		
		FunctionType ftype_current = tree.get(selection).ftype;
		// Sets built elements into place in expression
		int i, order=0;
		OpNode[] temp = node_builder.buildExp(ftype);
		tree.addParent(temp[0], selection);
		for (i=1; i<temp.length; ++i) {
			if (order == 0)
				if (temp[i] != null) {
					if (temp[i].ftype == FunctionType.BLANK)
						order = i;
				}
				else {
					if (ftype_current == FunctionType.BLANK)
						order = i;
				}
			if (temp[i] != null)
				tree.addChild(temp[i], selection, i-1);
		}
		
		// Set ID numbers for all elements in new locations
		for (i=selection; i<tree.size(); ++i)
			tree.get(i).setIdNumber(i);

		// Set selection to correct node
		++selection;
		for (i=0; i<order-1; ++i)
			selection = tree.getEndOfBranch(selection);
	}
	
	public void addNumber(double num) {
		boolean has_branch = tree.get(selection).getBranchCount() > 0;
		tree.replaceBranch(
				node_builder.Number(num)
							.build(FunctionType.NUMBER), 
				selection );
		
		// Set ID numbers for all elements in new locations
		if (has_branch)
			for (int i=selection; i<tree.size(); ++i)
				tree.get(i).setIdNumber(i);
		else
			tree.get(selection).setIdNumber(selection);
	}
	
	public void delete() {
		if (tree.get(selection).ftype != FunctionType.BLANK) {
			// Non-blank branches are replaced with a blank node
			boolean has_branch = tree.get(selection).getBranchCount() > 0;
			tree.replaceBranch(
					node_builder.build(FunctionType.BLANK), 
					selection );

			// Set ID numbers for all elements in new locations
			if (has_branch)
				for (int i=selection; i<tree.size(); ++i)
					tree.get(i).setIdNumber(i);
			else
				tree.get(selection).setIdNumber(selection);
		}
		else if (selection != 0){
		/* TODO deletes blank node from parent's branch list
		 * if this puts parent below min. branch count,
		 * 		delete parent, leave non-blank branch in its place
		 */
			try {
				tree.deleteBranch(selection);
				for (int i=selection; i<tree.size(); ++i)
					tree.get(i).setIdNumber(i);
			}
			catch (BranchCountException bce) {
				// TODO replace parent with selected branch
			}
		}
	}
	
	public double getCalculation() throws CalculationException {
		return tree.calculate(new BranchCompute()).get(0);
	}
	public String getTextPres() {
		return tree.calculate(new BranchDisplay()).get(0);
	}

}
