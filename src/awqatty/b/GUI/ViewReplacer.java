package awqatty.b.GUI;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import awqatty.b.calq.R;

public class ViewReplacer {

	ViewGroup parent;
	int view_index;
	ViewGroup.LayoutParams params;
	
	public ViewReplacer() {
		// TODO Auto-generated constructor stub
	}
	
	private void setPlaceholderLoc(View placeholder) {
		parent = (ViewGroup) placeholder.getParent();
		view_index = parent.indexOfChild(placeholder);
		params = placeholder.getLayoutParams();
		
		parent.removeViewAt(view_index);
	}
	
	public void replaceView(View placeholder, View insert) {
		setPlaceholderLoc(placeholder);
		parent.addView(insert, view_index, params);
	}
	
	public void replaceView(View placeholder, List<View> inserts) {
		setPlaceholderLoc(placeholder);
		for (View insert : inserts) {
			parent.addView(insert, view_index++, params);
		}
	}
	
	public void switchViews(View v1, View v2) {
		setPlaceholderLoc(v2);
		final ViewGroup parent2 = parent;
		final int view_index2 = view_index;
		final ViewGroup.LayoutParams params2 = params;
		setPlaceholderLoc(v1);		
		
		parent.addView(v2, view_index, params);
		parent2.addView(v1, view_index2, params2);
	}
}
