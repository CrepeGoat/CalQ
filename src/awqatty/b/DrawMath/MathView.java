package awqatty.b.DrawMath;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import awqatty.b.DrawMath.LoopSizingMath;
import awqatty.b.DrawMath.LoopClickMath;
import awqatty.b.DrawMath.LoopDrawMath;
import awqatty.b.DrawMath.DrawToCanvas.DrawForm;
import awqatty.b.ListTree.ListTree;

public final class MathView extends View implements OnGestureListener{

	private ListTree<DrawForm> tree;
	
	private final LoopSizingMath loop1 = new LoopSizingMath();
	private final LoopDrawMath loop2 = new LoopDrawMath();
	private final LoopClickMath loop3 = new LoopClickMath();
	
	private GestureDetector gesture;
	
	private RectF math_loc;
	private int width, height;
	
	// Constructors
	public MathView(Context context) {
		super(context);
		gesture = new GestureDetector(context, this);
	}
	public MathView(Context context, AttributeSet attrs) {
		super(context, attrs);
		gesture = new GestureDetector(context, this);
	}
	public MathView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		gesture = new GestureDetector(context, this);
	}
	
	// Set
	public void setListTree(ListTree<DrawForm> list_tree) {
		tree = list_tree;
	}
	
	//--- Overriden Methods ---
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		width = w;
		height = h;
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		math_loc = loop1.runLoop(tree).get(0);
		final int
		wMode = View.MeasureSpec.getMode(widthMeasureSpec),
		hMode = View.MeasureSpec.getMode(heightMeasureSpec);
		setMeasuredDimension(
				// Set width
				Math.min(
						(wMode == View.MeasureSpec.UNSPECIFIED ? Integer.MAX_VALUE :
							// The suggested size
							View.MeasureSpec.getSize(widthMeasureSpec) ),
						(wMode == View.MeasureSpec.EXACTLY ? Integer.MAX_VALUE :
							// The desired size
							(int) math_loc.width()
							+ getPaddingLeft() + getPaddingRight() ))
				,
				// Set height
				Math.min(
						(hMode == View.MeasureSpec.UNSPECIFIED ? Integer.MAX_VALUE :
							// The suggested size
							View.MeasureSpec.getSize(heightMeasureSpec) ),
						(hMode == View.MeasureSpec.EXACTLY ? Integer.MAX_VALUE :
							// The desired size
							(int) math_loc.height()
							+ getPaddingTop() + getPaddingBottom() ))
				);
	}
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//math_loc = loop1.runLoop(tree).get(0);
		math_loc.offsetTo(
				(width-math_loc.width())/2f,
				(height-math_loc.height())/2f );
		loop2.setCanvas(canvas);
		loop2.runLoop(tree, math_loc);
	}
	
	// Touch Gestures
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gesture.onTouchEvent(event))
			return true;
		return super.onTouchEvent(event);
	}
	
	@Override
	public boolean onDown(MotionEvent arg0) {
		/* TODO change this to check clicked region:
		 * 
		 * Contains expression -> select-math mode
		 * Does not contain expression:
		 * 		- single click -> deselect all
		 * 		- drag -> scroll mode
		 */
		Log.d("MathView", "Pressed down: x=" + Float.toString(arg0.getX())
				+ "; y=" + Float.toString(arg0.getY())
				);
		
		// Sets touch region clicked for loop to execute later
		loop3.setTouchRegion(arg0.getX(), arg0.getY());
		return true;
	}
	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {return false;}
	@Override
	public void onLongPress(MotionEvent arg0) {}
	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {return false;}
	@Override
	public void onShowPress(MotionEvent arg0) {}
	
	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		Log.d("MathView", "Press released");

		// Undo scale from last click
		if (!loop3.getNodesInTouchRegion().isEmpty())
			tree.get( loop3.getNodesInTouchRegion().get(0) ).setScale(1);
		// Check region
		loop3.runLoop(tree, math_loc);
		// Scale elements in region
		if (!loop3.getNodesInTouchRegion().isEmpty())
			tree.get( loop3.getNodesInTouchRegion().get(0) ).setScale(2);
		requestLayout();
		invalidate();
		return true;
	}
	
}
