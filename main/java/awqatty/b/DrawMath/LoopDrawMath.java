package awqatty.b.DrawMath;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.SparseArray;

import awqatty.b.DrawMath.AlignDrawParts.AlignForm;
import awqatty.b.DrawMath.DrawToCanvas.DrawForm;
import awqatty.b.ListTree.DataLoopRootDown;

public class LoopDrawMath
		extends DataLoopRootDown<AlignForm, RectF> {

	private Canvas canvas;
	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}
	private SparseArray<RectF> leafLocHolder=new SparseArray<>();

	private final RectF tmp = new RectF();
	@Override
	protected LoopControl loopAtNode(
			AlignForm node,
			RectF data,
			List<RectF> subList
	) {
		/* TODO Test code. Remove
		Log.d("TestDrawMath",
				"Loop 2"
				+ ": left=" + Float.toString(data.left)
				+ "; top=" + Float.toString(data.top)
				+ "; width=" + Float.toString(data.width())
				+ "; height=" + Float.toString(data.height()) );
		//*/
		tmp.set(data);
		node.drawToCanvas(canvas, tmp);

		node.getSubLeafLocations(leafLocHolder);
		final int length = leafLocHolder.size();
		for (int i=0; i<length; ++i) {
			subList.add(leafLocHolder.get(i));
		}
		leafLocHolder.clear();

		return LoopControl.CONTINUE;
	}
}
