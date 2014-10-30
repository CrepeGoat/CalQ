package awqatty.b.DrawMath;

import java.util.ArrayList;
import java.util.List;

import android.graphics.RectF;
import android.util.Log;
import awqatty.b.DrawMath.DrawToCanvas.DrawForm;
import awqatty.b.ListTree.DataLoopRootDown;
import awqatty.b.ListTree.ListTree;

public class LoopClickMath
		extends DataLoopRootDown<DrawForm, RectF> {

	// Input - Click coordinates
	private float px, py;
	public void setTouchRegion(float x, float y) {
		px = x;
		py = y;
	}
	
	// Output - nodes in touch region
	private final List<Integer> indices = new ArrayList<Integer>();
	public List<Integer> getNodesInTouchRegion() {
		return indices;
	}
	
	
	// Loop Function
	@Override
	public void runLoop(ListTree<? extends DrawForm> tree, RectF init_data) {
		indices.clear();
		super.runLoop(tree, init_data);
	}
	
	private final RectF tmp = new RectF();
	@Override
	protected byte loopAtNode(DrawForm node, RectF data, List<RectF> sublist) {
		// TODO change touch-region type
		/*
		Log.d("LoopClickMath",
				"Region"
				+ ": left=" + Float.toString(data.left)
				+ "; top=" + Float.toString(data.top)
				+ "; right=" + Float.toString(data.right)
				+ "; bottom=" + Float.toString(data.bottom) );
		//*/
		
		// If the clicked region does not intersect this node's area,
		//	skip the rest of its branch
		if (!data.contains(px, py)) {
			Log.d("LoopClickMath", "Check: index=" + Integer.toString(index)
					+ "; return=break branch");
			return BREAK_BRANCH;
		}
		
		tmp.set(data);
		if (node.intersectsTouchRegion(tmp, px, py)) {
			indices.add(index);
			Log.d("LoopClickMath", "Check: index=" + Integer.toString(index)
					+ "; return=break loop");
			return BREAK_LOOP;
		}
		
		Log.d("LoopClickMath", "Check: index=" + Integer.toString(index)
				+ "; return=continue");
		node.getLeafLocations(sublist);
		return CONTINUE;
	}

}
