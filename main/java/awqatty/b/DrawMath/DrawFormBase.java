package awqatty.b.DrawMath;

import android.graphics.Canvas;
import android.graphics.RectF;

public interface DrawFormBase {
	//---------------- Arrange Loop ----------------
	// Outputs size of region occupied
	public void getSize(RectF dst);
	//---------------- Draw Loop ----------------
	// Draws patterns to canvas object
	public void drawToCanvas(Canvas canvas, RectF dst);
	//---------------- On Touch Loop ----------------
	// Determines if point intersects the region occupied by the DrawForm objects
	public boolean intersectsTouchRegion(RectF dst, float px, float py);
 	// Determines if line segment intersects the region occupied by the DrawForm objects
	public boolean intersectsTouchRegion(
			RectF dst,
 			float p1_x, float p1_y,
 			float p2_x, float p2_y
	);
	//----------------- Other -----------------
	public void setColor(int color);
	public void clearCache();

	//public int getClosureFlags();
}
