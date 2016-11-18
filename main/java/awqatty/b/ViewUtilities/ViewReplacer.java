package awqatty.b.ViewUtilities;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;

public class ViewReplacer {

	/**********************************************************
	 * Private Members
	 */
	ViewGroup parent;
	int view_index;
	ViewGroup.LayoutParams params;
	
	public ViewReplacer() {}
	
	/**********************************************************
	 * Private Methods
	 */
	private void setPlaceholderLoc(View placeholder) {
		parent = (ViewGroup) placeholder.getParent();
		view_index = parent.indexOfChild(placeholder);
		params = placeholder.getLayoutParams();
	}
	
	/**********************************************************
	 * Replace View Methods
	 */
	public void replaceView(View placeholder, View insert) {
		setPlaceholderLoc(placeholder);
		parent.removeViewAt(view_index);
		parent.addView(insert, view_index, params);
	}
	
	public void replaceView(View placeholder, List<View> inserts) {
		setPlaceholderLoc(placeholder);
		parent.removeViewAt(view_index);
		for (View insert : inserts) {
			parent.addView(insert, view_index++, params);
		}
	}

	/**********************************************************
	 * Insert View Methods
	 */
	public void insertView(View placeholder, View insert) {
		setPlaceholderLoc(placeholder);
		parent.addView(insert, view_index, params);
	}
	
	public void insertViews(View placeholder, List<View> inserts) {
		setPlaceholderLoc(placeholder);
		for (View insert : inserts) {
			parent.addView(insert, view_index++, params);
		}
	}

	/**********************************************************
	 * Switch View Method
	 */
	public void switchViews(View v1, View v2) {
		setPlaceholderLoc(v2);
		final ViewGroup parent2 = parent;
		final int view_index2 = view_index;
		final ViewGroup.LayoutParams params2 = params;
		parent2.removeViewAt(view_index2);

		setPlaceholderLoc(v1);
		parent.removeViewAt(view_index);
		
		parent.addView(v2, view_index, params);
		parent2.addView(v1, view_index2, params2);
	}
}
