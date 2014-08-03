package awqatty.b.ViewManipulation;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;

public class ViewParentFinder extends ViewFinder {

	public List<View> findParentViewsByTag(ViewGroup root, String tag) {
		List<View> views = findViewsByTag(root, tag);
		while (views.isEmpty()) {
			root = (ViewGroup)root.getParent();
			findViewsByTag(root, tag);
		}
		return views;
	}
	
	public List<View> findParentViewsById(ViewGroup root, int id) {
		List<View> views = findViewsById(root, id);
		while (views.isEmpty()) {
			root = (ViewGroup)root.getParent();
			findViewsById(root, id);
		}
		return views;
	}
}
