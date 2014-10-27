package awqatty.b.DrawMath.DrawSubTree;

import java.util.List;

import android.graphics.RectF;
import android.util.SparseArray;
import awqatty.b.DrawMath.DrawFormBase;

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


}
