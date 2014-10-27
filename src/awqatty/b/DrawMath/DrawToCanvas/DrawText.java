package awqatty.b.DrawMath.DrawToCanvas;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.SparseArray;
import awqatty.b.DrawMath.DrawSubTree.AlignForm;

public class DrawText implements DrawForm, AlignForm {
	
	//--- Static Members ---
	private static final Typeface font = Typeface.SERIF;
						//Typeface.create("CAMBRIA",Typeface.ITALIC);
	private static final byte fontSize = 32;
	
	// (Set during runtime)
	private static float textSize = fontSize;
	public static float letter_spacing = 0;
	public static void setDensity(float density) {
		textSize = fontSize * density;
		// Thanks, android v
		//return paint.getLetterSpacing();
		/*
		final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextSize(textSize);
		paint.setTypeface(font);
		paint.setLinearText(true);
		final Rect rect1 = new Rect();
		paint.getTextBounds("||", 0,2, rect1);
		synchronized(DrawText.class) {
			letter_spacing = rect1.width();
			paint.getTextBounds("|", 0,1, rect1);
			letter_spacing -= 2*rect1.width();
		}
		Log.d("DrawText", "Letter Spacing = " + Float.toString(letter_spacing));
		//*/
	}
	
	//--- Local Private Members ---
	private final String text;
	
	protected Paint paint=null;
	protected Rect valid_area=null;
	private int color=Color.BLACK;
	private float scale=1;
	
	//--- Constructor ---
	public DrawText(String text) {
		this.text = text;
	}
	
	//--- Get Methods ---
	@Override
	public void getSize(RectF dst) {
		loadDrawTools();
		dst.set(valid_area);
	}
		
	//--- Overrides ---
	@Override
	public void setColor(int color) {
		this.color = color;
		if (paint != null)
			paint.setColor(color);
	}
	@Override
	public void setScale(float scale_factor) {
		scale = scale_factor;
		if (paint != null)
			paint.setTextSize(textSize*scale);
	}
	@Override
	public void drawToCanvas(Canvas canvas, RectF dst) {
		loadDrawTools();
		if (dst == null)
			canvas.drawText(text, -valid_area.left, -valid_area.top, paint);
		else {
			// Scales up text to match height
			paint.setTextSize(textSize*scale
					* (dst.height()/valid_area.height()) );
			// Scales width back to original, then to target width
			paint.setTextScaleX(
					(dst.width()*valid_area.height()) /
					(dst.height()*valid_area.width()) );
			// Draws Text
			loadDrawTools();
			canvas.drawText(text,
					dst.left-valid_area.left,
					dst.top-valid_area.top, paint);
			// Removes scaling
			paint.setTextSize(textSize*scale);
			paint.setTextScaleX(1);
		}
	}
	
	//--- Load/Clear Cache ---
	protected void loadDrawTools() {
		// Creates paint object
		loadPaint();
		// Initializes size
		if (valid_area == null)
			valid_area = new Rect();
		paint.getTextBounds(text, 0, text.length(), valid_area);
	}
	private void loadPaint() {
		// Creates paint object
		if (paint == null) {
			paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setColor(color);
			paint.setTextSize(textSize*scale);
			paint.setTypeface(font);
			paint.setLinearText(true);
		}
	}
	@Override
	public void clearCache() {
		paint = null;
		valid_area = null;
	}
	
	//--- Loop Methods ---
	@Override
	public void setSuperLeafSizes(List<RectF> leaf_sizes) {}
	@Override
	public void arrange(List<RectF> branch_sizes) {}
	@Override
	public void getLeafLocations(List<RectF> leaf_locs) {}
	@Override
	public void getSuperLeafLocations(SparseArray<RectF> leaf_locs) {}
	@Override
	public boolean intersectsTouchRegion(RectF dst, float px, float py) {
		// TODO change touch region type
		return dst.contains(px, py);
	}

}
