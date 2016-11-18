package awqatty.b.DrawMath;

import android.graphics.Canvas;
import android.graphics.RectF;

public interface DrawFormBase {
	// Arrange Loop
	public void getSize(RectF dst);
	// Draw Loop
	public void drawToCanvas(Canvas canvas, RectF dst);
	// On Touch Loop
	public boolean intersectsTouchRegion(RectF dst, float px, float py);
 	public boolean intersectsTouchRegion(RectF dst,
 			float p1_x, float p1_y,
 			float p2_x, float p2_y
	);

	// Other
	public void setColor(int color);
	public void clearCache();

}
