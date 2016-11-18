package awqatty.b.DrawMath.DrawSubTree;

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
	public <T extends DrawAligned> void subBranchShouldUsePars(
			ListTree<T> tree, int[] branch_indices, boolean[] pars_active);
	public <T extends DrawAligned> AlignForm getFirstInSeries(
			boolean orientation, ListTree<T>.Navigator nav);
	public <T extends DrawAligned> AlignForm getLastInSeries(
			boolean orientation, ListTree<T>.Navigator nav);
}
