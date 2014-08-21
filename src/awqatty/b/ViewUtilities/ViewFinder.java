package awqatty.b.ViewUtilities;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;

public class ViewFinder {
	
	/*****************************************************************
	 * Private Classes/Interfaces
	 */
	public static interface ViewMatcher {
		public boolean viewIsAMatch(View v);
	}
	protected static class IdMatcher implements ViewMatcher {
		private final int match_id;
		
		public IdMatcher(int id) {
			match_id = id;
		}
		@Override
		public boolean viewIsAMatch(View v) {
			return v.getId() == match_id;
		}
	}
	protected static class TagMatcher implements ViewMatcher {
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
		
	protected List<View> findViews(ViewMatcher match, View root) {
		// Clears results from last search
		views.clear();
		
		// Sets matching condition
		matcher = match;
		// Checks root view
		if (matcher.viewIsAMatch(root))
			views.add(root);
		// Adds root to checking list, & starts loop
		if (isAValidViewGroup(root))
			view_groups.add((ViewGroup)root);
		
		while (!view_groups.isEmpty()) {
			// Assigns group to local variable & removes it from future loops
			temp_group = view_groups.remove(0);
			
			// Checks every child of group
			temp_childCount = temp_group.getChildCount();
			for (int i=0; i < temp_childCount; ++i) {
				// Assigns child to local variable
				temp_child = temp_group.getChildAt(i);
				
				// Adds valid views to results list
				if (matcher.viewIsAMatch(temp_child))
					views.add(temp_child);
				// Adds any groups to future loops
				if (isAValidViewGroup(temp_child))
					view_groups.add((ViewGroup)temp_child);
			}
		}
		// Returns results
		return views;
	}
	// Methods that can be overriden in inherited class
	protected boolean isAValidViewGroup(View v) {
		return v instanceof ViewGroup;
	}
	
	public final List<View> findViewsByTag(View root, String view_tag) {
		return findViews(new TagMatcher(view_tag), root);
	}
	
	public final List<View> findViewsById(View root, int view_id) {
		return findViews(new IdMatcher(view_id), root);
	}
	

}
