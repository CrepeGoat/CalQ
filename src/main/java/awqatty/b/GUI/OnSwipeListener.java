package awqatty.b.GUI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class OnSwipeListener implements 
		OnGestureListener,
		OnTouchListener {
	
	/************************************************************
	 * Static Members
	 ************************************************************/
	protected static float ppi_x = 160;	// must be set by activity
	protected static float ppi_y = 160;	// must be set by activity	
	public static void setDpi(Activity act) {
		final DisplayMetrics metrics = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		ppi_x = metrics.xdpi;
		ppi_y = metrics.ydpi;
	}
	
	public static final float SWIPE_THRESH_DIST = 0.5f;	// in inches
	public static final float SWIPE_THRESH_VEL = 0;	// in inches/sec

	protected static final boolean UP=true, DOWN=false;
	protected static final boolean LEFT=true, RIGHT=false;

	/************************************************************
	 * Private Members
	 ************************************************************/
	private final GestureDetector detector;
	private View view = null;
	private boolean isDragging = false;

	private boolean dragOrientation;
	private static boolean VERTICAL=true, HORIZONTAL=false;
	
	
	/************************************************************
	 * Constructors
	 ************************************************************/
	public OnSwipeListener(Context context) {
		detector = new GestureDetector(context, this);
	}
	
	/************************************************************
	 * OnTouchListener Methods
	 ************************************************************/
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// Set View to be used
		view = v;
		
		if (event.getActionMasked() == MotionEvent.ACTION_UP && isDragging) {
			isDragging = false;
			// Sends event to GestureDetector. If false, then onFling did not occur.
			//		-> perform cancel-fling animation.
			return detector.onTouchEvent(event) || onSwipeFailure(v);
		}
		return detector.onTouchEvent(event);
	}
	
	/************************************************************
	 * OnGestureListener Methods
	 ************************************************************/
	
	@Override
	public boolean onScroll(MotionEvent ev_0, MotionEvent ev, float d_x, float d_y) {
		if (!isDragging) {
			dragOrientation = (Math.abs(ev.getX()-ev_0.getX()) > 
					Math.abs(ev.getY()-ev_0.getY())
					? HORIZONTAL : VERTICAL );
			
			// Executes command at start of swipe
			if (dragOrientation == HORIZONTAL) {
				if (!onStartSwipeHorizontal(view,
						(ev.getX() < ev_0.getX() ? LEFT : RIGHT) ))
					return false;
			}
			else {//dragOrientation == VERTICAL
				if (!onStartSwipeVertical(view,
						(ev.getY() < ev_0.getY() ? UP : DOWN) ))
					return false;
			}
			isDragging = true;
		}
		
		// Executes commands while swiping
		if (dragOrientation == HORIZONTAL)
			return whileSwipingHorizontally(view, ev.getX()-ev_0.getX());
		else //dragOrientation == VERTICAL
			return whileSwipingVertically(view, ev.getY()-ev_0.getY());
	}
	
	@Override
	public boolean onFling(MotionEvent ev_0, MotionEvent ev, float v_x, float v_y) {
		if (dragOrientation == HORIZONTAL) {
			final float d_x = ev.getX()-ev_0.getX();
			// Ensure fling exceeds distance/velocity thresholds
			if (Math.abs(d_x) > SWIPE_THRESH_DIST*ppi_x
					&& Math.abs(v_x) > SWIPE_THRESH_VEL*ppi_x
					&& Math.signum(d_x) == Math.signum(v_x) ) {
				if (d_x < 0)
					return onSwipeLeft(view, v_x);
				else if (d_x > 0)
					return onSwipeRight(view, v_x);
			}
		}
		else {//dragOrientation == VERTICAL
			final float d_y = ev.getY()-ev_0.getY();
			// Ensure fling exceeds distance/velocity thresholds
			if (Math.abs(d_y) > SWIPE_THRESH_DIST*ppi_y
					&& Math.abs(v_y) > SWIPE_THRESH_VEL*ppi_y
					&& Math.signum(d_y) == Math.signum(v_y) ) {
				if (d_y < 0)
					return onSwipeUp(view, v_y);
				else if (d_y > 0)
					return onSwipeDown(view, v_y);
			}
		}
		return false;
	}
	
	// Unused gestures
	@Override
	public void onLongPress(MotionEvent ev) {}
	@Override
	public void onShowPress(MotionEvent ev) {}
	@Override
	public boolean onSingleTapUp(MotionEvent ev) {return false;}
	
	/************************************************************
	 * "Abstract" Methods
	 ************************************************************/
	public boolean onSwipeUp(View v, float v_x) {return false;}
	public boolean onSwipeDown(View v, float v_x) {return false;}
	public boolean onSwipeLeft(View v, float v_y) {return false;}
	public boolean onSwipeRight(View v, float v_y) {return false;}
	public boolean onSwipeFailure(View v) {return false;}
	
	public boolean onStartSwipeHorizontal(View v, boolean direction) {return false;}
	public boolean onStartSwipeVertical(View v, boolean direction) {return false;}
	public boolean whileSwipingHorizontally(View v, float d_x) {return false;}	
	public boolean whileSwipingVertically(View v, float d_y) {return false;}
	
	// Needs to return true in order to run other listener functions
	// UNLESS attached view onDown returns true (i.e. has onClick, onLongClick)
	@Override
	public boolean onDown(MotionEvent ev) {return true;}

}
