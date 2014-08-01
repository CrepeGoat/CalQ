package awqatty.b.ViewManipulation;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;

public class ViewFinder {
	
	/*****************************************************************
	 * Private Classes/Interfaces
	 */
	private static interface ViewMatcher {
		public boolean viewIsAMatch(View v);
	}
	private static class IdMatcher implements ViewMatcher {
		private final int match_id;
		
		public IdMatcher(int id) {
			match_id = id;
		}
		@Override
		public boolean viewIsAMatch(View v) {
			return v.getId() == match_id;
		}
	}
	private static class TagMatcher implements ViewMatcher {
		private final String match_tag;
		
		public TagMatcher(String tag) {
			match_tag = tag;
		}
		@Override
		public boolean viewIsAMatch(View v) {
			return match_tag.equals(v.getTag());
		}
	}
	
	/****************************************************************
	 * Private Members
	 */
	private final List<View> views;
	private final List<ViewGroup> view_groups;
	
	// Used locally in loop
	private ViewMatcher matcher;
	private ViewGroup temp_group;
	private int temp_childCount;
	private View temp_child;

	/****************************************************************
	 * Public Methods
	 */
	public ViewFinder() {
		views = new ArrayList<View>();
		view_groups = new ArrayList<ViewGroup>();
	}
		
	private List<View> findViews(ViewMatcher match, ViewGroup root) {
		// Clears results from last search
		views.clear();
		
		// Sets matching condition
		matcher = match;
		// Checks root view
		if (matcher.viewIsAMatch(root))
			views.add(root);
		
		// Adds root to checking list, & starts loop
		view_groups.add(root);
		while (!view_groups.isEmpty()) {
			// Assigns group to local variable & removes it from future loops
			temp_group = view_groups.remove(0);
			
			// Checks every child of group
			temp_childCount = temp_group.getChildCount();
			for (int i=0; i < temp_childCount; ++i) {
				// Assigns child to local variable
				temp_child = temp_group.getChildAt(i);
				
				// Adds any groups to future loops
				if (temp_child instanceof ViewGroup)
					view_groups.add((ViewGroup)temp_child);
				// Adds valid views to results list
				if (matcher.viewIsAMatch(temp_child))
					views.add(temp_child);
			}
		}
		// Returns results
		return views;
	}
	
	public List<View> findViewsByTag(ViewGroup root, String view_tag) {
		return findViews(new TagMatcher(view_tag), root);
	}
	
	public List<View> findViewsById(ViewGroup root, int view_id) {
		return findViews(new IdMatcher(view_id), root);
	}

}
