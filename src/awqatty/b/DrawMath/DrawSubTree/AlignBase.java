package awqatty.b.DrawMath.DrawSubTree;

import java.util.Iterator;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.SparseArray;
import awqatty.b.DrawMath.AssignParentheses.ClosureType;

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
	
	abstract protected Iterable<AlignForm> iterComps();
	abstract protected Iterable<AlignForm> iterCompsWithLoc();
	abstract protected Iterable<RectF> iterLocs();
	
	abstract protected void arrange();
	
	//--- Determine Parentheses ---
	@Override
	public void assignParentheses(ClosureType[] ctypes, boolean[] pars_active) {
		decideParentheses(ctypes, pars_active);
		for (AlignForm comp : iterComps())
			if (comp != null)
				comp.assignParentheses(ctypes, pars_active);
	}
	abstract protected void decideParentheses(
			ClosureType[] ctypes, boolean[] pars_active);
	
	//--- Set Methods ---
	@Override
	public void setColor(int color) {
		for (AlignForm draw : iterComps())
			if (draw != null) draw.setColor(color);
	}
	
	// Loop 1
	@Override
	public void setSuperLeafSizes(List<RectF> leaf_sizes) {
		for (AlignForm draw : iterComps())
			if (draw != null) draw.setSuperLeafSizes(leaf_sizes);
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
		final Iterator<AlignForm> iter_comp = 
				iterCompsWithLoc().iterator();
		final Iterator<RectF> iter_loc = 
				iterLocs().iterator();
		
		if (dst == null || dst.equals(valid_area)) {
			while (iter_comp.hasNext() && iter_loc.hasNext()) {
				iter_comp.next().drawToCanvas(canvas,
						iter_loc.next());
			}
		}
		else if (!dst.isEmpty()) {
			final float
			dx = dst.left - valid_area.left,
			dy = dst.top - valid_area.top,
			sx = dst.width() / valid_area.width(),
			sy = dst.height() / valid_area.height();
			
			//dst = new RectF();
			while (iter_comp.hasNext() && iter_loc.hasNext()) {
				dst.set(iter_loc.next());
				dst.set(
						(dst.left	-valid_area.left)*sx +valid_area.left+dx,
						(dst.top	-valid_area.top)*sy +valid_area.top+dy,
						(dst.right	-valid_area.left)*sx +valid_area.left+dx,
						(dst.bottom	-valid_area.top)*sy +valid_area.top+dy
						);
				iter_comp.next().drawToCanvas(canvas, dst);
			}
		}
	}
	@Override
	public void getSuperLeafLocations(SparseArray<RectF> leaf_locs) {
		for (AlignForm draw : iterComps())
			if (draw != null) draw.getSuperLeafLocations(leaf_locs);
	}
	
	// TODO merge code with drawToCanvas
	// Note - this method overwrites dst value
	@Override
	public boolean intersectsTouchRegion(RectF dst, float px, float py) {
		// TODO change touch region type
		if (!dst.isEmpty()) {
			final Iterator<AlignForm> iter_comp = 
					iterCompsWithLoc().iterator();
			final Iterator<RectF> iter_loc = 
					iterLocs().iterator();
			final float
					dx = dst.left - valid_area.left,
					dy = dst.top - valid_area.top,
					sx = dst.width() / valid_area.width(),
					sy = dst.height() / valid_area.height();
			
			//dst = new RectF();
			while (iter_comp.hasNext() && iter_loc.hasNext()) {
				dst.set(iter_loc.next());
				dst.set(
						(dst.left	-valid_area.left)*sx +valid_area.left+dx,
						(dst.top	-valid_area.top)*sy +valid_area.top+dy,
						(dst.right	-valid_area.left)*sx +valid_area.left+dx,
						(dst.bottom	-valid_area.top)*sy +valid_area.top+dy
						);
				// If any RawDraw objects intersect the touch region,
				//	so does the branch.
				if (iter_comp.next().intersectsTouchRegion(dst, px, py))
					return true;
			}
		}
		return false;
	}
}
