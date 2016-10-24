package awqatty.b.DrawMath.DrawToCanvas;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;

// TODO Remove class altogether
public abstract class RawDrawBase implements DrawForm {
	
	//--- Static Methods ---
	static public float TOUCH_PADDING = 0f;
	static public boolean contains(RectF dst, float px, float py, float padding) {
		dst.inset(-padding, -padding);
		final boolean ret_val = dst.contains(px,py);
		dst.inset(padding, padding);
		return ret_val;
	}
	static public boolean containsLineSegment(RectF dst,
			float p1_x, float p1_y,
			float p2_x, float p2_y) {
		// Considers line segment between points p1, p2 as
		//		f(a) = a*p1 + (1-a)p2	for a in [0,1]
		//			 = p2 + a(p1-p2)
		// Checks valid a's for x and y coordinates separately, then takes union
		// return (union is non-empty ? true : false)
		float a_xmin, a_xmax, a_ymin, a_ymax;
		// Check valid 'a' values for x coordinates
		if (p1_x == p2_x) {
			if (p1_x < dst.left || dst.right < p1_x)
				return false;
			a_xmin = 0;
			a_xmax = 1;
		} else {
			final float divisor = p1_x-p2_x;
			// assumes left <= right
			if (divisor >= 0) {
				a_xmin = (dst.left-p2_x)/divisor;
				a_xmax = (dst.right-p2_x)/divisor;
			} else {
				a_xmin = (dst.right-p2_x)/divisor;
				a_xmax = (dst.left-p2_x)/divisor;
			}
		}
		// Check valid 'a' values for y coordinates
		if (p1_y == p2_y) {
			if (p1_y < dst.top || dst.bottom < p1_y)
				return false;
			a_ymin = 0;
			a_ymax = 1;
		} else {
			final float divisor = p1_y-p2_y;
			// assumes top <= bottom
			if (divisor >= 0) {
				a_ymin = (dst.top-p2_y)/divisor;
				a_ymax = (dst.bottom-p2_y)/divisor;
			} else {
				a_ymin = (dst.bottom-p2_y)/divisor;
				a_ymax = (dst.top-p2_y)/divisor;
			}
		}
		// Take union of ranges
		final float a_min = Math.max(Math.max(a_xmin,a_ymin),0);
		final float a_max = Math.min(Math.min(a_xmax,a_ymax),1);
		// Return result
		return (a_min<=a_max);
	}
	static public boolean containsLineSegment(RectF dst,
			float p1_x, float p1_y,
			float p2_x, float p2_y,
			float padding) {
		dst.inset(-padding, -padding);
		final boolean ret_val = containsLineSegment(dst, p1_x, p1_y, p2_x, p2_y);
		dst.inset(padding, padding);
		return ret_val;
	}
	
	
	//--- Local Members ---
	protected int color=Color.BLACK;
	protected float scale=1;
	
	protected RectF valid_area=null;
	@Override
	public void clearCache() {
		valid_area = null;
	}
	
	//--- Arrange Loop Methods ---
	@Override
	public void arrange(List<RectF> branch_sizes) {}
	@Override
	public void getSize(RectF dst) {
		getRawBounds(dst);
		dst.left *= scale;
		dst.top *= scale;
		dst.right *= scale;
		dst.bottom *= scale;
	}
	
	//--- Draw Method ---
	@Override
	public void drawToCanvas(Canvas canvas, RectF dst) {
		if (valid_area == null)
			valid_area = new RectF();
		getRawBounds(valid_area);

		// Set Restore Point
		canvas.save();

		if (dst == null) {
			// Shift canvas to position picture
			canvas.translate(-valid_area.left, -valid_area.top);
			// Draw Picture
			drawRaw(canvas);
		}
		else if (!dst.isEmpty()) {
			// Shift canvas to position picture
			//Matrix mat = new Matrix();
			//mat.setRectToRect(valid_area, dst, Matrix.ScaleToFit.FILL);
			//canvas.setMatrix(mat);
			canvas.translate(dst.left-valid_area.left, dst.top-valid_area.top);
			canvas.scale(
					dst.width()/valid_area.width(),
					dst.height()/valid_area.height(),
					valid_area.left,
					valid_area.top);
			// Draw Picture
			drawRaw(canvas);
		}
		// Undo shift
		canvas.restore();
	}	
	
	@Override
	public void getLeafLocations(List<RectF> leaf_locs) {}
	
	@Override
	public boolean intersectsTouchRegion(RectF dst,
			float p1_x, float p1_y,
			float p2_x, float p2_y) {
		return containsLineSegment(dst, p1_x, p1_y, p2_x, p2_y,
				TOUCH_PADDING);
	}
	@Override
	public boolean intersectsTouchRegion(RectF dst, float px, float py) {
		//return intersectsTouchRegion(dst,px,py,px,py);
		return contains(dst, px,py, TOUCH_PADDING);
	}
	
	// Methods to Override
	//--- Set Methods ---
	@Override
	public void setScale(float scale_factor) {
		scale = scale_factor;
	}
	@Override
	public void setColor(int color) {
		this.color = color;
	}
	//--- Get Methods ---
	abstract protected void getRawBounds(RectF dst);	
	//--- Draw Utilities ---
	abstract protected void drawRaw(Canvas canvas);
}
