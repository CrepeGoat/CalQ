package awqatty.b.DrawMath;

import java.util.List;

import android.graphics.RectF;

import awqatty.b.DrawMath.AlignDrawParts.AlignForm;
import awqatty.b.DrawMath.DrawToCanvas.DrawForm;
import awqatty.b.ListTree.DataLoopLeafUp;

public class LoopPreSizingMath
		extends DataLoopLeafUp<AlignForm, RectF> {

	@Override
	protected RectF loopAtNode(AlignForm node, List<RectF> stack) {
		// Use sizes of leaf nodes to arrange elements internally
		node.setSubLeafSizes(stack);
		// Then get size of total arrangement
		final RectF ret = new RectF();
		node.getSize(ret);
		// Return size
		return ret;
	}

}
