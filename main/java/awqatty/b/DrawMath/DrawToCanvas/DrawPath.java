package awqatty.b.DrawMath.DrawToCanvas;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.SparseArray;
import awqatty.b.DrawMath.AssignParentheses.ClosureFlags;
import awqatty.b.DrawMath.DrawSubTree.AlignForm;
import awqatty.b.DrawMath.DrawSubTree.DrawAligned;
import awqatty.b.ListTree.ListTree;

public class DrawPath extends RawDrawBase implements AlignForm {

	@Override
	public void clearCache() {}
	
	// Local Members
	private final Path path;
	private final RectF valid_area;
	private final Paint paint;
	
	//--- Constructors ---
	public DrawPath(Path scetch, Paint style) {
		path = scetch;
		paint = style;
		paint.setColor(color);
		valid_area = new RectF();		
		path.computeBounds(valid_area, true);
	}
	public DrawPath(Path scetch, Paint style, RectF src) {
		path = scetch;
		paint = style;
		paint.setColor(color);
		valid_area = new RectF();
		if (src == null || src.width() < 0 || src.height() < 0)
			path.computeBounds(valid_area, true);
		else
			valid_area.set(src);
	}

	//--- Get Methods ---
	@Override
	protected void getRawBounds(RectF dst) {
		dst.set(valid_area);
	}
	//--- Set Methods ---
	@Override
	public void setColor(int color) {
		super.setColor(color);
		paint.setColor(color);
	}
	
	//--- Draw Method ---
	@Override
	protected void drawRaw(Canvas canvas) {
		canvas.drawPath(path, paint);
	}
	
	//--- Loop Methods ---
	@Override
	public void setSuperLeafSizes(List<RectF> leaf_sizes) {}
	@Override
	public void getSuperLeafLocations(SparseArray<RectF> leaf_locs) {}
	
	//--- Manage Parentheses ---
	@Override
	public void assignParentheses(int[] ctypes, boolean[] pars_active) {}
	@Override
	public int getClosureFlags() {return ClosureFlags.NONE;}
	@Override
	public <T extends DrawAligned> void subBranchShouldUsePars(
			ListTree<T>.Navigator nav, boolean[] pars_active) {}


}
