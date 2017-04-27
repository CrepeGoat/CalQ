package awqatty.b.GUI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import awqatty.b.DrawMath.AlignDrawParts.AlignForm;
import awqatty.b.DrawMath.LoopPreSizingMath;
import awqatty.b.DrawMath.LoopDrawMath;
import awqatty.b.DrawMath.DrawToCanvas.DrawForm;
import awqatty.b.ListTree.ListTree;

public class MathView
		extends View
		//implements OnGestureListener, OnChangeListener
{
	protected ListTree<? extends AlignForm> tree;
	
	private final LoopPreSizingMath loop_sizing = new LoopPreSizingMath();
	private final LoopDrawMath loop_draw = new LoopDrawMath();
	private float minScaleForFit = (float)Math.sqrt(0.5); // -> area = 1/2 of original

	//private GestureDetector gesture;
	
	protected RectF math_loc;
	//private boolean should_scale_down = true;
	
	// Constructors
	public MathView(Context context) {
		super(context);
		//gesture = new GestureDetector(context, this);
	}
	public MathView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//gesture = new GestureDetector(context, this);
	}
	public MathView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		//gesture = new GestureDetector(context, this);
	}
	
	// ListTree Interface Methods
	public void setListTree(ListTree<? extends AlignForm> list_tree) {
		tree = list_tree;
		refresh();
	}

	public void refresh() {
		requestLayout();
		invalidate();
	}
	//@Override
	//public void onChange(ChangeEvent event) {
		// TODO
	//	if (event.source_obj instanceof ObservedOpTree &&
	//			event.timing_code == ObservedOpTree.POST_EVENT) {
	//		requestLayout();
	//		invalidate();
	//	}
	//}
	
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
				(wMode == View.MeasureSpec.UNSPECIFIED ? Float.MAX_VALUE :
					// The suggested size
					View.MeasureSpec.getSize(widthMeasureSpec)
							-getPaddingLeft()-getPaddingRight() ),
				(wMode == View.MeasureSpec.EXACTLY ? Float.MAX_VALUE :
					// The desired size
					math_loc.width() ));
		float draw_height = Math.min(
				(hMode == View.MeasureSpec.UNSPECIFIED ? Float.MAX_VALUE :
					// The suggested size
					View.MeasureSpec.getSize(heightMeasureSpec)
							-getPaddingTop()-getPaddingBottom() ),
				(hMode == View.MeasureSpec.EXACTLY ? Float.MAX_VALUE :
					// The desired size
					math_loc.height() ));
		
		// Logic for auto-scaling expression to available dimensions
		// TODO should make a custom attribute to trigger auto-scale (s.t. can be set from xml)
		//*
		float scale = Math.min(
				draw_width / math_loc.width(),
				draw_height / math_loc.height()
		);
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
		/*/
		if (should_scale_down) {
			float scale = Math.min(
					draw_width / math_loc.width(),
					draw_height / math_loc.height() );
			if (scale < 1) {
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
		}
		//*/
		// Set dimensions
		setMeasuredDimension(
				(int) draw_width +getPaddingLeft()+getPaddingRight(),
				(int) draw_height +getPaddingTop()+getPaddingBottom()
		);
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

	/*
	// OnTouchListener Methods	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gesture.onTouchEvent(event))
			return true;
		return super.onTouchEvent(event);
	}
	
	@Override
	public boolean onDown(MotionEvent arg0) {
		Log.d("MathView", "Pressed down: x=" + Float.toString(arg0.getX())
				+ "; y=" + Float.toString(arg0.getY())
				);
		// Sets touch region clicked
		loop_click.setTouchRegion(arg0.getX(), arg0.getY());
		// Checks region (result saved for later reference, based on touch action)
		loop_click.runLoop(tree, math_loc);
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

		// Send result to OpTree
		((MainActivity) this.getContext()).onClickMathml(
				(loop_click.getNodesInTouchRegion().isEmpty() ? OpTree.null_index
				: loop_click.getNodesInTouchRegion().get(0)) );
		return true;
	}
	//*/
	
}
