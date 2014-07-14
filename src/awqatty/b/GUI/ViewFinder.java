package awqatty.b.GUI;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;

public class ViewFinder {
	
	private final List<View> views;
	private final List<ViewGroup> view_groups;
	private Object tag;
	
	// Used locally in loop
	private ViewGroup temp_group;
	private View temp_child;
	private Object temp_tag;

	public ViewFinder() {
		views = new ArrayList<View>();
		view_groups = new ArrayList<ViewGroup>();
	}
		
	private void recursiveFindViewsByTag() {
		temp_group = view_groups.remove(0);
		final int child_count = temp_group.getChildCount();
		for (int i=0; i < child_count; ++i) {
			temp_child = temp_group.getChildAt(i);
			if (temp_child instanceof ViewGroup)
				view_groups.add((ViewGroup)temp_child);
			temp_tag = temp_child.getTag();
			if (temp_tag != null && tag.equals(temp_tag))
				views.add(temp_child);
		}
	}
	
	public List<View> findViewsByTag(ViewGroup root, String view_tag) {
		views.clear();
		
		tag = view_tag;
		view_groups.add(root);
		while (!view_groups.isEmpty()) {
			recursiveFindViewsByTag();			
		}
		return views;
	}

}
