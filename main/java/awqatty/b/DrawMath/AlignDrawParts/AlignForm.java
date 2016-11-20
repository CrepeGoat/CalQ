package awqatty.b.DrawMath.AlignDrawParts;

import java.util.List;

import android.graphics.RectF;
import android.util.SparseArray;
import awqatty.b.DrawMath.DrawFormBase;
import awqatty.b.ListTree.ListTree;

/*
 * Provides functionality for use as a alignment subtree within
 * 	the main operation-tree nodes
 */
public interface AlignForm extends DrawFormBase {

	//--- Sizing Loop ---
	public void setSuperLeafSizes(List<RectF> leaf_sizes);
	
	//--- Draw Loop ---
	// Adds original values to array, not copies. Do not edit in use!
	public void getSuperLeafLocations(SparseArray<RectF> leaf_locs);

	//--- Assign Parentheses ---
	public void assignParentheses(int[] branch_ctypes, boolean[] pars_active);
	public <T extends DrawAligned> void subBranchShouldUsePars(
			ListTree<T>.Navigator nav,
			boolean[] pars_active
	);
	public int getClosureFlags();
}
