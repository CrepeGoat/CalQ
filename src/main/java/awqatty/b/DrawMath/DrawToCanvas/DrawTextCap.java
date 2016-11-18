package awqatty.b.DrawMath.DrawToCanvas;

import android.graphics.Rect;

// TODO Test the class
public class DrawTextCap extends DrawText {

	private Rect rect=null;
	
	public DrawTextCap(String text) {
		super(text);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void loadDrawTools() {
		super.loadDrawTools();
		// (Adjusts size based on sizing style)
		if (rect == null)
			rect = new Rect();
		//Paint.FontMetricsInt metric = paint.getFontMetricsInt();
		//valid_area.top = metric.ascent;
		//valid_area.bottom = metric.descent;
		paint.getTextBounds("X", 0,1, rect);
		valid_area.top = rect.top;
		valid_area.bottom = 0;
	}
	
	@Override
	public void clearCache() {
		super.clearCache();
		rect = null;
	}
	
}
