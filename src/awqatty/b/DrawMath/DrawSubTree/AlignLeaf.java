package awqatty.b.DrawMath.DrawSubTree;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.SparseArray;

public class AlignLeaf implements AlignForm {

	// Cached Members
	private RectF dim=null;
	@Override
	public void clearCache() {
		dim = null;
	}
	
	// Local Members
	private int leaf_num;
	private float scale=1;
	
	// Constructors
	public AlignLeaf(int index) {
		leaf_num = index;
	}
	public AlignLeaf(int index, float scale_factor) {
		leaf_num = index;
		scale = scale_factor;
	}
	
	@Override
	public void setColor(int color) {}
	
	//--- Loop 1 ---
	@Override
	public void setSuperLeafSizes(List<RectF> leaf_sizes) {
		if (dim == null)
			dim = new RectF(leaf_sizes.get(leaf_num));
		else dim.set(leaf_sizes.get(leaf_num));
		dim.left *= scale;
		dim.top *= scale;
		dim.right *= scale;
		dim.bottom *= scale;
	}
	@Override
	public void getSize(RectF dst) {
		dst.set(dim);
	}

	//--- Loop 2 ---
	@Override
	public void drawToCanvas(Canvas canvas, RectF dst) {
		dim.set(dst);
	}
	@Override
	public void getSuperLeafLocations(SparseArray<RectF> leaf_locs) {
		leaf_locs.put(leaf_num, dim);
	}
	@Override
	public boolean intersectsTouchRegion(RectF dst, float px, float py) {
		// Intersection of a leaf's region is tested when the loop reaches it.
		//	Returns false to allow for the loop to proceed.
		return false;
	}

}
