package awqatty.b.GUI;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import awqatty.b.ViewUtilities.ViewFinder;
import awqatty.b.calq.R;

public final class SwipePaletteManager extends PaletteManager {

	private final PaletteSwipeListener swipe_listener;
	
	public SwipePaletteManager(Activity activity, int palettebox_layout_id,
			OnClickListener swap, OnClickListener del ) {
		super(activity, palettebox_layout_id);
		
		PaletteSwipeListener.setDpi(activity);
		swipe_listener = new PaletteSwipeListener(activity);
		if (palettebox_layout_id == 
				R.layout.palettebox_swipe_swapleftright) {
			swipe_listener.setOnSwipeLeftListener(swap);
			swipe_listener.setOnSwipeRightListener(swap);
		}
		else if (palettebox_layout_id == 
				R.layout.palettebox_swipe_swapleft_delright) {
			swipe_listener.setOnSwipeLeftListener(del);
			swipe_listener.setOnSwipeRightListener(swap);
		}
	}
	
	@Override
	protected void setPaletteListeners(View root, ViewFinder finder) {
		// Operation Buttons
		for (View button_op : finder.findViewsByTag(root, 
				activity.getString(R.string.tag_op) )) {
			button_op.setOnTouchListener(swipe_listener);
		}
		
		// Palettes (sets openContextMenu to be valid for all contained buttons)
		for (View palette : finder.findViewsByTag(root, 
				activity.getString(R.string.tag_plt) ))
			activity.registerForContextMenu(palette);
	}
	
	@Override
	public PaletteboxAnimator getPaletteboxAnimator() {
		return swipe_listener;
	}
}
