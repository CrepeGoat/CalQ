package awqatty.b.GUI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import awqatty.b.CustomEventListeners.ChangeEvent;
import awqatty.b.CustomEventListeners.ObservedOpTree;
import awqatty.b.CustomEventListeners.OnChangeListener;
import awqatty.b.GUI.MathTreeLoop.LoopClickMath;
import awqatty.b.OpTree.OpTree;

public class TouchMathView
		extends MathView implements OnChangeListener {

	private final LoopClickMath loop_click = new LoopClickMath();
	private OpTree optree;
		
	// Constructors
	public TouchMathView(Context context) {
		super(context);
	}
	public TouchMathView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public TouchMathView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	// Set Method
	public void setOpTree(OpTree ot) {
		optree = ot;
		optree.setMathViewToTree(this);
	}
	
	// OnChange Method
	@Override
	public void onChange(ChangeEvent event) {
		// TODO make sure events correspond to screen refreshes
		if (event.source_obj instanceof ObservedOpTree
				&& event.timing_code == ObservedOpTree.POST_EVENT) {
			refresh();
		}
	}
	
	// OnTouchListener Methods
	private boolean isTrackingScroll;
	private int[] node_indices;
	private int prev_selection;
	private float x_prev=0,y_prev=0;
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		/* Contains expression -> select-math mode
		 * Does not contain expression:
		 * 		- single click -> deselect all
		 * 		- drag -> scroll mode
		 */
		switch (e.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			// Reset touch actions
			loop_click.clear();
			// Sets touch region clicked
			loop_click.setTouchRegion(e.getX(),e.getY());
			// Checks region
			loop_click.runLoop(tree, math_loc);
			// Send result to OpTree
			node_indices = loop_click.getNodeIndices();
			if (isTrackingScroll = (node_indices != null)) {
				prev_selection = optree.getSelectionIndex();
				x_prev = e.getX();
				y_prev = e.getY();
				optree.resetSelection(node_indices);
				getParent().requestDisallowInterceptTouchEvent(true);
			}
			return true;			
		
		case MotionEvent.ACTION_MOVE:
			if (isTrackingScroll) {
				loop_click.setTouchRegion(e.getX(),e.getY(), x_prev,y_prev);
				x_prev = e.getX();
				y_prev = e.getY();
				loop_click.runLoop(tree, math_loc);
				node_indices = loop_click.getNodeIndices();
				if (node_indices != null) {
					optree.addToSelection(node_indices);
				}
			}
			return isTrackingScroll;
		
		case MotionEvent.ACTION_UP:
			if (isTrackingScroll) {
				optree.finalizeSelection();
				loop_click.clear();
				getParent().requestDisallowInterceptTouchEvent(false);
			}
			else
				optree.selectNone();
			return true;
			
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_OUTSIDE:
			if (isTrackingScroll) {
				optree.resetSelection(prev_selection);
				optree.finalizeSelection();
				loop_click.clear();
				getParent().requestDisallowInterceptTouchEvent(false);
			}
			return isTrackingScroll;
			
		default:
			return super.onTouchEvent(e);
		}
	}
	
}
