package awqatty.b.GUI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import awqatty.b.DrawMath.DrawToCanvas.DrawForm;
import awqatty.b.GUI.MathTreeLoop.LoopDrawMath;
import awqatty.b.GUI.MathTreeLoop.LoopSizeMath;
import awqatty.b.ListTree.ListTree;

public class MathView extends View {
	
	protected ListTree<? extends DrawForm> tree;
	
	private final LoopSizeMath loop_sizing = new LoopSizeMath();
	private final LoopDrawMath loop_draw = new LoopDrawMath();
	private float minScaleForFit = 1;
	
	protected RectF math_loc;
	
	// Constructors
	public MathView(Context context) {
		super(context);
	}
	public MathView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public MathView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	// ListTree Interface Methods
	public void setListTree(ListTree<? extends DrawForm> list_tree) {
		tree = list_tree;
		refresh();
	}
	public void refresh() {
		requestLayout();
		invalidate();
	}
	
	//--- View Override Methods ---
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		math_loc = loop_sizing.runLoop(tree).get(0);
		
		final int
		wMode = View.MeasureSpec.getMode(widthMeasureSpec),
		hMode = View.MeasureSpec.getMode(heightMeasureSpec);
		
		// Calculate feasible draw dimensions
		float draw_width = Math.min(
				(wMode == View.MeasureSpec.UNSPECIFIED ? Integer.MAX_VALUE :
					// The suggested size
					View.MeasureSpec.getSize(widthMeasureSpec)
							-getPaddingLeft()-getPaddingRight() ),
				(wMode == View.MeasureSpec.EXACTLY ? Integer.MAX_VALUE :
					// The desired size
					math_loc.width() ));
		float draw_height = Math.min(
				(hMode == View.MeasureSpec.UNSPECIFIED ? Integer.MAX_VALUE :
					// The suggested size
					View.MeasureSpec.getSize(heightMeasureSpec)
							-getPaddingTop()-getPaddingBottom() ),
				(hMode == View.MeasureSpec.EXACTLY ? Integer.MAX_VALUE :
					// The desired size
					math_loc.height() ));
		
		// Logic for auto-scaling expression to available dimensions
		// TODO should make a custom attribute to trigger auto-scale
		float scale = Math.min(
				draw_width / math_loc.width(),
				draw_height / math_loc.height() );
		if (scale < 1) {
			// Match scale to constraints (NOTE: minScale MUST be <= 1)
			scale = Math.max(scale, minScaleForFit);
			// Scale down expression size
			math_loc.right = math_loc.left + math_loc.width()*scale;
			math_loc.bottom = math_loc.top + math_loc.height()*scale;
			// Scale down boundary size if necessary
			if (wMode != View.MeasureSpec.EXACTLY) {
				draw_width = math_loc.width();
			}
			if (hMode != View.MeasureSpec.EXACTLY) {
				draw_height = math_loc.height();
			}
		}
		
		// Set dimensions
		setMeasuredDimension(
				(int) draw_width +getPaddingLeft()+getPaddingRight(),
				(int) draw_height +getPaddingTop()+getPaddingBottom() );
	}
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// Centers math in view
		math_loc.offsetTo(
				(getWidth()-math_loc.width())/2f,
				(getHeight()-math_loc.height())/2f );
		// Draws math to canvas
		loop_draw.setCanvas(canvas);
		loop_draw.runLoop(tree, math_loc);
	}
	

}
