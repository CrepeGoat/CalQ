package awqatty.b.DrawMath.AlignDrawParts;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.SparseArray;

import awqatty.b.ListTree.ListTree;

abstract public class AlignBase implements AlignForm {

	protected RectF valid_area=null;
	@Override
	public void clearCache() {
		//super.clearCache();
		valid_area = null;
		for (AlignForm draw : iterComps()) {
			if (draw != null)
				draw.clearCache();
		}
	}

	abstract protected boolean hasNthEntry(int index);
	abstract protected AlignForm getNthComp(int index);
	abstract protected RectF getNthLoc(int index);

	abstract protected Iterable<AlignForm> iterComps();

	abstract protected void arrange();

	///////////////////////////////// v REMOVE? v /////////////////////////////////////
	//--- Determine Parentheses for subnodes ---
	//@Override
	//public void assignParentheses(int[] cflags, boolean[] pars_active) {
	//	decideParentheses(cflags, pars_active);
	//	for (AlignForm comp : iterComps())
	//		if (comp != null)
	//			comp.assignParentheses(cflags, pars_active);
	//}
	//abstract protected void decideParentheses(int[] cflags, boolean[] pars_active);
	///////////////////////////////// ^ REMOVE? ^ /////////////////////////////////////

	//--- Set Methods ---
	@Override
	public void setColor(int color) {
		for (AlignForm comp : iterComps())
			if (comp != null) comp.setColor(color);
	}
	
	// Loop 1
	@Override
	public void setSubLeafSizes(List<RectF> leaf_sizes) {
		for (AlignForm comp : iterComps())
			if (comp != null) comp.setSubLeafSizes(leaf_sizes);
		arrange();
	}
	@Override
	public void getSize(RectF dst) {
		dst.set(valid_area);
	}

	// Loop 2
	// Note - this function edits dst as a loop variable,
	//		and will overwrite the current data.
	@Override
	public void drawToCanvas(Canvas canvas, RectF dst) {
		//final Iterator<AlignForm> iter_comp =
		//		iterOrderedComps().iterator();
		//final Iterator<RectF> iter_loc =
		//		iterOrderedLocs().iterator();
		int i=0;
		if (dst == null || dst.equals(valid_area)) {
			while (hasNthEntry(i)) {
				getNthComp(i).drawToCanvas(canvas, getNthLoc(i));
				++i;
			}
		}
		else if (!dst.isEmpty()) {
			final float
			dx = dst.left - valid_area.left,
			dy = dst.top - valid_area.top,
			sx = dst.width() / valid_area.width(),
			sy = dst.height() / valid_area.height();
			
			//dst = new RectF();
			while (hasNthEntry(i)) {
				dst.set(getNthLoc(i));
				dst.set(
						(dst.left	-valid_area.left)*sx +valid_area.left+dx,
						(dst.top	-valid_area.top)*sy +valid_area.top+dy,
						(dst.right	-valid_area.left)*sx +valid_area.left+dx,
						(dst.bottom	-valid_area.top)*sy +valid_area.top+dy
						);
				getNthComp(i).drawToCanvas(canvas, dst);
				++i;
			}
		}
	}
	@Override
	public void getSubLeafLocations(SparseArray<RectF> leaf_locs) {
		for (AlignForm comp : iterComps())
			if (comp != null) comp.getSubLeafLocations(leaf_locs);
	}
	
	// TODO merge code with drawToCanvas
	// Note - this method overwrites dst value
	@Override
	public boolean intersectsTouchRegion(RectF dst, float px, float py) {
		if (!dst.isEmpty()) {
			//final Iterator<AlignForm> iter_comp =
			//		iterOrderedComps().iterator();
			//final Iterator<RectF> iter_loc =
			//		iterOrderedLocs().iterator();
			int i=0;
			final float
					dx = dst.left - valid_area.left,
					dy = dst.top - valid_area.top,
					sx = dst.width() / valid_area.width(),
					sy = dst.height() / valid_area.height();
			
			//dst = new RectF();
			while (hasNthEntry(i)) {
				dst.set(getNthLoc(i));
				dst.set(
						(dst.left	-valid_area.left)*sx +valid_area.left+dx,
						(dst.top	-valid_area.top)*sy +valid_area.top+dy,
						(dst.right	-valid_area.left)*sx +valid_area.left+dx,
						(dst.bottom	-valid_area.top)*sy +valid_area.top+dy
						);
				// If any RawDraw objects intersect the touch region,
				//	so does the branch.
				if (getNthComp(i).intersectsTouchRegion(dst, px, py))
					return true;
			}
		}
		return false;
	}
 	@Override
 	public boolean intersectsTouchRegion(
			RectF dst,
			float p1_x, float p1_y,
			float p2_x, float p2_y
	) {
 		if (!dst.isEmpty()) {
 			//final Iterator<AlignForm> iter_comp =
 			//		iterOrderedComps().iterator();
 			//final Iterator<RectF> iter_loc =
 			//		iterOrderedLocs().iterator();
			int i=0;
			final float
 					dx = dst.left - valid_area.left,
 					dy = dst.top - valid_area.top,
 					sx = dst.width() / valid_area.width(),
 					sy = dst.height() / valid_area.height();

 			//dst = new RectF();
 			while (hasNthEntry(i)) {
 				dst.set(getNthLoc(i));
 				dst.set(
 						(dst.left	-valid_area.left)*sx +valid_area.left+dx,
 						(dst.top	-valid_area.top)*sy +valid_area.top+dy,
 						(dst.right	-valid_area.left)*sx +valid_area.left+dx,
 						(dst.bottom	-valid_area.top)*sy +valid_area.top+dy
 						);
 				// If any RawDraw objects intersect the touch region,
 				//	so does the branch.
 				if (getNthComp(i).intersectsTouchRegion(dst, p1_x,p1_y, p2_x,p2_y))
 					return true;
				++i;
 			}
 		}
 		return false;
 	}

	//TODO stubbed to always add parentheses to expressions; needs actual logic
	public <T extends DrawAligned> void subBranchShouldUsePars(
			ListTree<T>.Navigator nav,
			boolean[] pars_active
	) {
		for (int i=0; i<pars_active.length; ++i)
			pars_active[i] = true;
	}
}
