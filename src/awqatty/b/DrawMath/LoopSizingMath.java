package awqatty.b.DrawMath;

import java.util.List;

import android.graphics.RectF;
import awqatty.b.DrawMath.DrawToCanvas.DrawForm;
import awqatty.b.ListTree.DataLoopLeafUp;

public class LoopSizingMath
		extends DataLoopLeafUp<DrawForm, RectF> {

	@Override
	protected RectF loopAtNode(DrawForm node, List<RectF> stack) {
		node.arrange(stack);
		
		final RectF ret = new RectF();
		node.getSize(ret);
		
		return ret;
	}

}
