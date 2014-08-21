package awqatty.b.ViewUtilities;

import java.util.List;

import android.view.View;

public class ViewParentFinder extends ViewFinder {
	
	private View prev_root=null;
	
	@Override
	protected List<View> findViews(ViewMatcher match, View root) {
		final List<View> views = super.findViews(match, root);
		prev_root = root;
		while (views.isEmpty()) {
			if (!(root.getParent() instanceof View))
				break;
			super.findViews(match, (root=(View)root.getParent()));
			prev_root = root;
		}
		return views;
	}
	
	@Override
	protected boolean isAValidViewGroup(View v) {
		if (!super.isAValidViewGroup(v) || v == prev_root) return false;
		else return true;
	}
}
