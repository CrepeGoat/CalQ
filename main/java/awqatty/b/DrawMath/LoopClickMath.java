package awqatty.b.DrawMath;

import java.util.ArrayList;
import java.util.List;

import android.graphics.RectF;
import android.util.Log;
import awqatty.b.DrawMath.DrawToCanvas.DrawForm;
import awqatty.b.DrawMath.DrawToCanvas.RawDrawBase;
import awqatty.b.ListTree.DataLoopRootDown;
import awqatty.b.ListTree.ListTree;

public class LoopClickMath
		extends DataLoopRootDown<DrawForm, RectF> {

	// Input - Click coordinates
	private float x1,y1, x2,y2;
	public void setTouchRegion(float x, float y) {
		setTouchRegion(x,y,x,y);
	}
	public void setTouchRegion(float p1_x,float p1_y, float p2_x,float p2_y) {
		x1 = p1_x;
		y1 = p1_y;
		x2 = p2_x;
		y2 = p2_y;
	}

	// Output - nodes in touch region
	private final List<Integer> indices = new ArrayList<>();
	//private final List<RectF> touchedAreas = new ArrayList<>();

	public int[] getNodeIndices() {
		if (indices.isEmpty()) return null;
		int[] array = new int[indices.size()];
		for (int i=0; i<array.length; ++i) {
			array[i] = indices.get(i);
		}
		indices.clear();
		return array;
	}
	public void clear() {
		indices.clear();
		//touchedAreas.clear();
	}
	
	
	// Loop Function
	@Override
	public void runLoop(ListTree<? extends DrawForm> tree, RectF init_data) {
		indices.clear();
		//touchedAreas.clear();
		super.runLoop(tree, init_data);
	}
	
	private final RectF rectf_tmp = new RectF();
	@Override
	protected LoopControl loopAtNode(DrawForm node, RectF data, List<RectF> sublist) {
		// If the clicked region does not intersect this node's area,
		//	skip the rest of its branch
		if (!RawDrawBase.containsLineSegment(data, x1,y1, x2,y2)) {
			Log.d("LoopClickMath", "Check: index=" + Integer.toString(index)
					+ "; return=break branch");
			return LoopControl.BREAK_BRANCH;
		}
		
		rectf_tmp.set(data);
		if (node.intersectsTouchRegion(rectf_tmp, x1,y1, x2,y2)
				//&& !touchedAreas.contains(rectf_tmp)
				) {
			indices.add(index);
			//touchedAreas.add(new RectF(rectf_tmp));
		}
		
		node.getLeafLocations(sublist);
		return LoopControl.CONTINUE;
	}

}
