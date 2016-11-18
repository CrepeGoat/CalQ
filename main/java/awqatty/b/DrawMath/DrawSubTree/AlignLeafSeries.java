package awqatty.b.DrawMath.DrawSubTree;

import java.util.List;

import android.graphics.RectF;
import awqatty.b.DrawMath.AssignParentheses.ClosureFlags;
import awqatty.b.ListTree.ListTree;

public final class AlignLeafSeries extends AlignSeriesBase {
	
	@Override
	public void clearCache() {
		super.clearCache();
		comps.subList(1, comps.size()).clear();
	}
	
	// Constructor
	public AlignLeafSeries(AlignForm divider, byte stretch_type,
			boolean orientation, float whitespace, byte aligned_edges) {
		super(divider, stretch_type, orientation, whitespace, aligned_edges);
	}
	
	//--- AlignBase Override Methods ---
	@Override
	public void setSuperLeafSizes(List<RectF> leaf_sizes) {
		int i;
		final int length = leaf_sizes.size()+1,
				old_length = comps.size();
		
		if (length > old_length) {
			for (i=old_length; i<length; ++i)
				comps.add(new AlignLeaf(i-1));
		}
		else
			comps.subList(length, old_length).clear();
		if (locs != null)
			locs.clear();
		super.setSuperLeafSizes(leaf_sizes);
	}
	@Override
	protected void decideParentheses(int[] cflags, boolean[] pars_active) {
		int cflag_last=ClosureFlags.NONE;
		for (int i=0; i<cflags.length; ++i) {
			pars_active[i] = decideSingleParentheses(cflags[i], cflag_last);
			cflag_last = cflags[i];
		}
	}

	@Override
	public <T extends DrawAligned> void subBranchShouldUsePars(
			ListTree<T>.Navigator nav, boolean[] pars_active) {
		// TODO test!
		if (hasDivider()) {
			for (int i=0; i<pars_active.length; ++i) {
				// Checks for each subnode type
				if (nav.getObject().base_comp instanceof AlignLeafSeries) {
					pars_active[i]=((AlignLeafSeries)
							nav.getObject().base_comp).hasDivider();
				}
				// Increment nav
				nav.toEnd();
			}
		} else {
			for (int i=0; i<pars_active.length; ++i) {
				// Checks for each subnode type
				if (nav.getObject().base_comp instanceof AlignLeafSeries) {
					pars_active[i] = ((AlignLeafSeries) nav.getObject().base_comp)
							.getOrientation() == getOrientation();
				}
				// Increment nav
				nav.toEnd();
			}
		}
	}

}
