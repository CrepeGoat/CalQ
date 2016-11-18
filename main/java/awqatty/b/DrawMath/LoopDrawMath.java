package awqatty.b.DrawMath;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.RectF;
import awqatty.b.DrawMath.DrawToCanvas.DrawForm;
import awqatty.b.ListTree.DataLoopRootDown;

public class LoopDrawMath
		extends DataLoopRootDown<DrawForm, RectF> {

	private Canvas canvas;
	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}
	
	private final RectF tmp = new RectF();
	@Override
	protected byte loopAtNode(DrawForm node, RectF data,
			List<RectF> sublist) {
		
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
		node.getLeafLocations(sublist);
		return CONTINUE;
	}


}
