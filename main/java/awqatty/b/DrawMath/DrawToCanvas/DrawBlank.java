package awqatty.b.DrawMath.DrawToCanvas;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.SparseArray;
import awqatty.b.DrawMath.AssignParentheses.ClosureFlags;
import awqatty.b.DrawMath.AlignDrawParts.AlignForm;
import awqatty.b.DrawMath.AlignDrawParts.DrawAligned;
import awqatty.b.ListTree.ListTree;

public final class DrawBlank implements DrawForm {

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
	//@Override
	//public void arrange(List<RectF> branch_sizes) {}
	@Override
	public void drawToCanvas(Canvas canvas, RectF dst) {}
	//@Override
	//public void getLeafLocations(List<RectF> leaf_locs) {}
	@Override
	public boolean intersectsTouchRegion(RectF dst, float px, float py) {return false;}
	@Override
	public boolean intersectsTouchRegion(
			RectF dst, float p1_x, float p1_y, float p2_x, float p2_y
	) {
		return false;
	}

	//--- Manage Parentheses ---
	//@Override
	//public void assignParentheses(int[] ctypes, boolean[] pars_active) {}
	//@Override
	//public int getClosureFlags() {return ClosureFlags.NONE;}

}
