package awqatty.b.GUI;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import awqatty.b.ViewUtilities.ViewParentFinder;
import awqatty.b.calq.R;


public class PaletteSwipeListener extends OnSwipeListener implements
		PaletteboxAnimator {

	private OnClickListener action_left=null,
							action_right=null;
	protected boolean dragDirection;
	
	private View leftCurtain,
				rightCurtain,
				currentCurtain,
				palette;
	private LayoutParams params;
	
	private static int CURTAIN_MIN_WIDTH;
	private static final float MIN_ALPHA = 0.25f;
	private static final float ALPHA_RATE = (1-MIN_ALPHA)/(SWIPE_THRESH_DIST*ppi_x);


	public PaletteSwipeListener(Context context) {
		super(context);
		CURTAIN_MIN_WIDTH = (int) context.getResources()
				.getDimension(R.dimen.paletteboxCurtain_minWidth);
	}

	public void setOnSwipeLeftListener(OnClickListener left_listener) {
		action_left = left_listener;
	}
	public void setOnSwipeRightListener(OnClickListener right_listener) {
		action_right = right_listener;
	}
		
	private void setCurtainWidth(int width) {
		params.width = width;
		currentCurtain.setLayoutParams(params);				
	}

	
	@Override
	public boolean onStartSwipeHorizontal(View v, boolean direction) {
		final ViewParentFinder finder = new ViewParentFinder();
		leftCurtain = finder.findViewsById(v, 
				R.id.palettebox_leftCurtain ).get(0);
		rightCurtain = finder.findViewsById(v, 
				R.id.palettebox_rightCurtain ).get(0);
		currentCurtain = (direction == LEFT ? rightCurtain : leftCurtain);
		params = currentCurtain.getLayoutParams();
		palette = finder.findViewsByTag(v,
				v.getContext().getString(R.string.tag_plt) ).get(0);

		if (v instanceof Button)
			((Button)v).setPressed(false);
		dragDirection=direction;
		return true;
	}

	@Override
	public boolean whileSwipingHorizontally(View v, float d_x) {
		// If drag direction has changed:
		if ((d_x < 0) != (dragDirection == LEFT)) {
			setCurtainWidth(CURTAIN_MIN_WIDTH);
			dragDirection = !dragDirection;
			currentCurtain = (dragDirection == LEFT ? rightCurtain : leftCurtain);
			params = currentCurtain.getLayoutParams();
		}
		setCurtainWidth(Math.abs(Math.round(d_x)) + CURTAIN_MIN_WIDTH);
		palette.setAlpha(Math.max(1-Math.abs(d_x)*ALPHA_RATE, MIN_ALPHA));
		return true;
	}

	@Override
	public boolean onSwipeLeft(View v, float v_x) {
		// TODO Should animate transition
		setCurtainWidth(-2);
		palette.setAlpha(MIN_ALPHA);
		if (action_left != null) {
			action_left.onClick(v);
			return true;
		}
		else return false;
	}

	@Override
	public boolean onSwipeRight(View v, float v_x) {
		// TODO Should animate transition
		setCurtainWidth(-2);
		palette.setAlpha(MIN_ALPHA);
		if (action_right != null) {
			action_right.onClick(v);
			return true;
		}
		else return false;
	}

	@Override
	public boolean onSwipeFailure(View v) {
		// TODO Should animate transition
		setCurtainWidth(CURTAIN_MIN_WIDTH);
		palette.setAlpha(1);
		return true;
	}

	@Override
	public boolean onDown(MotionEvent ev) {
		// Needed to allow natural onClick to operate
		return false;
	}
	
	/*******************************************************
	 * PaletteboxAnimator Methods
	 *******************************************************/


	@Override
	public void animateAfterSwap() {
		setCurtainWidth(CURTAIN_MIN_WIDTH);
		palette.setAlpha(1);
	}
}
