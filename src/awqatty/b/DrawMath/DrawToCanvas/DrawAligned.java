package awqatty.b.DrawMath.DrawToCanvas;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.SparseArray;
import awqatty.b.DrawMath.DrawSubTree.AlignForm;

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
public class DrawAligned implements DrawForm{
		
	//--- Local Members ---
	private float scale = 1;
	private final AlignForm comp;
	
	//
	private static SparseArray<RectF> leaf_holder = null;

	public DrawAligned(AlignForm component) {
		comp = component;
	}
		
	//--- Set Methods ---
	public void setScale(float scale_factor) {
		scale = scale_factor;
	}
	@Override
	public void setColor(int color) {
		comp.setColor(color);
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
