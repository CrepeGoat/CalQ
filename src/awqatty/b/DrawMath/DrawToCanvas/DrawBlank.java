package awqatty.b.DrawMath.DrawToCanvas;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.SparseArray;
import awqatty.b.DrawMath.AssignParentheses.ClosureType;
import awqatty.b.DrawMath.DrawSubTree.AlignForm;

public final class DrawBlank implements AlignForm, DrawForm {

	private float w,h;
	private float scale=1;
	
	public DrawBlank() {
		w=0; h=0;
	}
	public DrawBlank(float width, float height) {
		w = width;
		h = height;
	}
	public DrawBlank(RectF src) {
		set(src);
	}
	public void set(RectF src) {
		w = src.width();
		h = src.height();
	}
	public void setWidth(float width) {
		w = width;
	}
	public void setHeight(float height) {
		h = height;
	}
	@Override
	public void setScale(float scale_factor) {
		scale = scale_factor;
	}

	@Override
	public void getSize(RectF dst) {
		dst.right = dst.left + (w*scale);
		dst.bottom = dst.top + (h*scale);
	}
	
	@Override
	public void clearCache() {}
	@Override
	public void setColor(int color) {}
	@Override
	public void setSuperLeafSizes(List<RectF> leaf_sizes) {}
	@Override
	public void arrange(List<RectF> branch_sizes) {}
	@Override
	public void drawToCanvas(Canvas canvas, RectF dst) {}
	@Override
	public void getLeafLocations(List<RectF> leaf_locs) {}
	@Override
	public void getSuperLeafLocations(SparseArray<RectF> leaf_locs) {}
	@Override
	public boolean intersectsTouchRegion(RectF dst, float px, float py) {return false;}
	
	//--- Manage Parentheses ---
	@Override
	public void assignParentheses(ClosureType[] ctypes, boolean[] pars_active) {}
	@Override
	public ClosureType getClosureType() {return ClosureType.OTHER;}

}
