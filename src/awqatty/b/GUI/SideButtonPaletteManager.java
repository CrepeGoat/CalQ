package awqatty.b.GUI;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import awqatty.b.ViewUtilities.ViewFinder;
import awqatty.b.calq.R;

public final class SideButtonPaletteManager extends PaletteManager {

	private final OnClickListener
			swap_listener,
			del_listener;
	
	public SideButtonPaletteManager(Activity activity, int palettebox_layout_id,
			OnClickListener swap, OnClickListener del ) {
		super(activity, palettebox_layout_id);
		swap_listener = swap;
		del_listener = del;
	}
	
	@Override
	protected void setPaletteListeners(View root, ViewFinder finder) {
		List<View> views;
		// Operation Buttons
		views = finder.findViewsByTag(root,	activity.getString(R.string.tag_op));
		for (View button_op : views) {
			// (This removes any listeners set from OnSwipeManager) (I think?)
			button_op.setOnTouchListener(null);
		}
		
		// Swap Buttons
		views = finder.findViewsById(root, R.id.buttonSwapPalette);
		for (View button_swap : views)
			button_swap.setOnClickListener(swap_listener);

		// Delete Buttons
		views = finder.findViewsById(root, R.id.buttonRemovePalette);
		for (View button_del : views)
			button_del.setOnClickListener(del_listener);
		
		// Palettes (sets openContextMenu to be valid for all contained buttons)
		views = finder.findViewsByTag(root, activity.getString(R.string.tag_plt));
		for (View palette : views)
			activity.registerForContextMenu(palette);
	}
}
