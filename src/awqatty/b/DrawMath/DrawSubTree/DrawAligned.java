package awqatty.b.DrawMath.DrawSubTree;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.SparseArray;
import awqatty.b.DrawMath.AlignDrawBuilder;
import awqatty.b.DrawMath.DrawToCanvas.DrawForm;
import awqatty.b.ListTree.ListTree;

/*
 * Two Loops:
 * 
 * Loop 1 - Leaf-to-Root:
 * 	- calculate size of each node
 * 	- pass raw width/height up to root
 * 	- root uses size to arrange components
 * 	- root applies scale factor
 * 
 * Loop 2 - Root-to-Leaf:
 * 	- takes matrix as input from root
 * 	- applies matrix to canvas
 * 	- calls draw method for each non-leaf component
 * 	- scales canvas before drawing non-leaf components
 * 	- passes scale matrix concatenated with input matrix 
 * 		as child output matrix
 * 	- reset canvas matrix
 * 
 */
public class DrawAligned implements DrawForm {
		
	//--- Local Members ---
	private float scale = 1;
	private int color = Color.BLACK;
	
	final AlignForm base_comp;
	private AlignBorder comp_par=null;
	private AlignForm comp;
	
	//
	private static SparseArray<RectF> leaf_holder = null;

	// Constructors
	public DrawAligned(AlignForm component) {
		base_comp = component;
		comp = component;
	}
		
	//--- Set Methods ---
	@Override
	public void setScale(float scale_factor) {
		scale = scale_factor;
	}
	@Override
	public void setColor(int color) {
		comp.setColor(color);
		this.color = color;
	}
	
	
	//--- Manage Parentheses ---
	public void setParentheses(boolean b) {
		if (b) {
			comp_par = (AlignBorder) // TODO implement w/o casting (interface?)
					AlignDrawBuilder.buildParentheses(base_comp);
			comp_par.setColor(color);
			comp = comp_par;
		} else {
			comp_par = null;
			comp = base_comp;
		}
	}
	public <T extends DrawAligned> boolean[] assignBranchParentheses(
			ListTree<T> tree, int[] indices) {
		// decisions for each branch
		boolean[] pars_active = new boolean[indices.length];
		// gets decisions
		base_comp.subBranchShouldUsePars(tree, indices, pars_active);
		// sets decisions
		return pars_active;
	}
	
	//--- Loop Methods ---
	// Loop 1
	@Override
	public void arrange(List<RectF> leaf_dims) {
		comp.setSuperLeafSizes(leaf_dims);
	}
	@Override
	public void getSize(RectF dst) {
		comp.getSize(dst);
		dst.left *= scale;
		dst.top *= scale;
		dst.right *= scale;
		dst.bottom *= scale;
	}

	// Loop 2
	@Override
	public void drawToCanvas(Canvas canvas, RectF dst) {
		comp.drawToCanvas(canvas, dst);
	}
	
	@Override
	public void getLeafLocations(List<RectF> leaf_locs) {
		synchronized(DrawAligned.class) {
			if (leaf_holder == null)
				leaf_holder = new SparseArray<RectF>();	
			else leaf_holder.clear();
			
			comp.getSuperLeafLocations(leaf_holder);
			final int length = leaf_holder.size();
			for (int i=0; i<length; ++i) {
				leaf_locs.add(leaf_holder.get(i));
			}
		}
	}
	
	// On Touch Loop
	@Override
	public boolean intersectsTouchRegion(RectF dst, float px, float py) {
		return comp.intersectsTouchRegion(dst, px, py);
	}
	@Override
	public boolean intersectsTouchRegion(RectF dst, float p1_x, float p1_y,
			float p2_x, float p2_y) {
		return comp.intersectsTouchRegion(dst, p1_x, p1_y, p2_x, p2_y);
	}
	
	// Other Methods
	@Override
	public void clearCache() {
		synchronized(DrawAligned.class) {
			leaf_holder = null;
		}
		if (comp != null)
			comp.clearCache();
	}	
}
