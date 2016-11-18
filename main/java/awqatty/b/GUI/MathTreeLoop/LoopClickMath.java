package awqatty.b.GUI.MathTreeLoop;

import java.util.ArrayList;
import java.util.List;

import android.graphics.RectF;
import awqatty.b.DrawMath.DrawToCanvas.DrawForm;
import awqatty.b.DrawMath.DrawToCanvas.RawDrawBase;
import awqatty.b.ListTree.DataLoopRootDown;

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
	private List<Integer> selected_indices = new ArrayList<Integer>();
	private final List<RectF> touchedAreas = new ArrayList<RectF>();
	
	public int[] getNodeIndices() {
		if (selected_indices.isEmpty()) return null;
		int[] array = new int[selected_indices.size()];
		for (int i=0; i<array.length; ++i) {
			array[i] = selected_indices.get(i);
		}
		selected_indices.clear();
		return array;
	}
	public void clear() {
		selected_indices.clear();
		touchedAreas.clear();
	}
	
	
	// Loop Function
	private final RectF tmp = new RectF();
	@Override
	protected LoopControl loopAtNode(DrawForm node, RectF data, List<RectF> sublist) {
		// If the clicked region does not intersect this node's area,
		//	skip the rest of its branch
		if (!RawDrawBase.containsLineSegment(data, x1,y1, x2,y2)) {
			return LoopControl.BREAK_BRANCH;
		}
		
		tmp.set(data);
		if (node.intersectsTouchRegion(tmp, x1,y1, x2,y2)
				&& !touchedAreas.contains(tmp)) {
			selected_indices.add(index);
			touchedAreas.add(new RectF(tmp));
		}
		node.getLeafLocations(sublist);
		return LoopControl.CONTINUE;
	}

}
