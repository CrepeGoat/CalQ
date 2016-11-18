package awqatty.b.GUI;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import awqatty.b.ViewUtilities.ViewFinder;
import awqatty.b.ViewUtilities.ViewReplacer;
import awqatty.b.calq.R;

abstract public class PaletteManager {
	
	private static int getLayoutIdFromViewId(int id) {
		switch (id) {
		case R.id.palette_basic:
			return R.layout.palette_basic;
		case R.id.palette_log:
			return R.layout.palette_log;
		case R.id.palette_trig:
			return R.layout.palette_trig;
		case R.id.palette_hyp:
			return R.layout.palette_hyp;
		case R.id.palette_perm:
			return R.layout.palette_perm;
		case R.id.palette_arith:
			return R.layout.palette_arith;
		// vvv Occurs only under improper use vvv
		default:
			return 0;
		}
	}
	
	private final String tag_plt;
	
	protected final Activity activity;
	protected final View root;
	protected final int palettebox_layout_id;
			
	public PaletteManager(Activity activity, int palettebox_layout_id) {
		this.activity = activity;
		this.root = activity.getWindow().getDecorView().getRootView();
		this.palettebox_layout_id = palettebox_layout_id;		
		this.tag_plt = activity.getString(R.string.tag_plt);
	}
	
	/****************************************************
	 * Add Method
	 */
	public View addPalette(int palette_id, View insert_loc) {
		palette_id = getLayoutIdFromViewId(palette_id);
		if (palette_id == 0) return null;

		// Load View Utilities
		final ViewFinder finder = new ViewFinder();
		final ViewReplacer replacer = new ViewReplacer();
		
		// Inflate palette-container layouts
		final View container = View.inflate(
				activity, palettebox_layout_id, null );
		// (Decides whether to keep or remove the placeholder view)
		if (insert_loc.getId() == R.id.tmp_palette)
			replacer.replaceView(insert_loc, container);
		else
			replacer.insertView(insert_loc, container);

		// Insert respective palette into container
		replacer.replaceView(
				finder.findViewsByTag(container, tag_plt).get(0),
				View.inflate(activity, palette_id, null) );
		
		// Set Button Listeners to new palette
		setPaletteListeners(container, finder);
		
		return container;
	}
	
	/****************************************************
	 * Mass-Add Method
	 */
	public List<View> addPalettes(List<Integer> palette_ids, View insert_loc) {
		if (palette_ids.isEmpty()) return null;
		// Load View Utilities
		final ViewFinder finder = new ViewFinder();
		final ViewReplacer replacer = new ViewReplacer();
		
		// Loop variables
		final List<View> paletteboxes = new ArrayList<View>();
		View container;

		for (Integer palette_id : palette_ids) {
			palette_id = getLayoutIdFromViewId(palette_id);
			if (palette_id == 0) return null;

			container = View.inflate(activity, palettebox_layout_id, null);
			replacer.replaceView(
					finder.findViewsByTag(container, tag_plt).get(0),
					View.inflate(activity, palette_id, null) );
			paletteboxes.add(container);
		}
		container = (View)insert_loc.getParent();
		
		// (Decides whether to keep or remove the placeholder view)
		if (insert_loc.getId() == R.id.tmp_palette)
			replacer.replaceView(insert_loc, paletteboxes);
		else
			replacer.insertViews(insert_loc, paletteboxes);
		
		// Set Button Listeners to new palette
		setPaletteListeners(container, finder);

		return paletteboxes;
	}


	/****************************************************
	 * Swap Method
	 */
	public void swapPalette(View palette1, int palette_id2) {
		final ViewReplacer replacer = new ViewReplacer();
		View palette2 = activity.findViewById(palette_id2);
		
		// Condition: palette is already on-screen
		if (palette2 != null) {
			// Switch selected palettes
			replacer.switchViews(palette1, palette2);
		}
		// Condition: palette has to be inflated from layout xml
		else {
			// Inflate new palette from XML
			palette2 = View.inflate(activity,
					getLayoutIdFromViewId(palette_id2), null);
			replacer.replaceView(palette1, palette2);
			// Set button listeners for buttons in inflated palette
			setPaletteListeners(palette2);
		}
		
	}

	/****************************************************
	 * Remove Method
	 */
	public void removePalette(View palettebox) {
		((ViewGroup) palettebox.getParent()).removeView(palettebox);
	}
	
	/****************************************************
	 * Refresh Palette Boxes Method
	 */
	public void refreshPaletteBoxes() {
		View new_palettebox;
		final ViewFinder finder = new ViewFinder();
		final ViewReplacer replacer = new ViewReplacer();
		final List<View> paletteboxes = finder.findViewsById(root,R.id.palettebox);
		for (View palettebox : paletteboxes) {
			new_palettebox = View.inflate(activity, palettebox_layout_id, null);
			replacer.switchViews(
					finder.findViewsByTag(palettebox, tag_plt).get(0),
					finder.findViewsByTag(new_palettebox, tag_plt).get(0) );
			replacer.replaceView(palettebox, new_palettebox);
			setPaletteListeners(new_palettebox, finder);
		}
	}
	
	/****************************************************
	 * "Abstract" Methods
	 */
	protected void setPaletteListeners(View root, ViewFinder finder) {}
	private void setPaletteListeners(View root) {
		setPaletteListeners(root, new ViewFinder());
	}
	
	public PaletteboxAnimator getPaletteboxAnimator() {return null;}
}
