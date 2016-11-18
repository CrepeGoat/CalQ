package awqatty.b.DrawMath.DrawToCanvas;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;

// TODO Remove class altogether
public abstract class RawDrawBase implements DrawForm {
	
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
	public boolean intersectsTouchRegion(RectF dst, float px, float py) {
		// TODO change touch region type
		return dst.contains(px, py);
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
