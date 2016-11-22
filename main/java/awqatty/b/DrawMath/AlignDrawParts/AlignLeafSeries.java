package awqatty.b.DrawMath.AlignDrawParts;

import java.util.List;

import android.graphics.RectF;

import awqatty.b.DrawMath.AlignDrawParts.Utilities.AlignmentEdge;
import awqatty.b.DrawMath.AlignDrawParts.Utilities.StretchType;
import awqatty.b.ListTree.ListTree;

public final class AlignLeafSeries extends AlignAxisBase {

	//protected List<AlignForm> comps_ordered = null;
	//@Override
	//public Iterable<AlignForm> iterOrderedComps() {return comps_ordered;}

	@Override
	public void clearCache() {
		super.clearCache();
		comps.subList(1,comps.size()).clear();
	}
	
	// Constructor
	public AlignLeafSeries(
			AlignForm divider,
			StretchType stretch_type,
			boolean orientation,
			float whitespace_series,
			float whitespace_stretch,
			AlignmentEdge aligned_edges
	) {
		super(orientation, whitespace_series,
				whitespace_stretch, aligned_edges);
		comps.add(divider);
		stretches.add(stretch_type);
	}
	private boolean hasDivider() {
		return comps.get(INDEX_DIVIDER)==null;
	}

	@Override
	protected int getCompIndex(int index) {
		if (hasDivider())
			return index+1;
		else return (index%2==0 ? index/2+1 : INDEX_DIVIDER);
	}
	@Override
	protected boolean hasNthEntry(int index) {
		if (hasDivider())
			return index+1 < comps.size();
		else return index<(2*comps.size()-3);
	}
	protected final int INDEX_DIVIDER = 0;

	//--- AlignBase Override Methods ---
	@Override
	public void setSubLeafSizes(List<RectF> leaf_sizes) {
		int i;
		final int length = leaf_sizes.size()+1,
				old_length = comps.size();
		
		if (length > old_length) {
			for (i=old_length; i<length; ++i) {
				comps.add(new AlignLeaf(i-1));
				stretches.add(StretchType.NONE);
			}
		}
		else {
			comps.subList(length, old_length).clear();
			stretches.subList(length, old_length).clear();
		}
		if (locs_ordered != null)
			locs_ordered.clear();
		super.setSubLeafSizes(leaf_sizes);
	}
	//@Override
	//protected void decideParentheses(int[] cflags, boolean[] pars_active) {
	//	int cflag_last=ClosureFlags.NONE;
	//	for (int i=0; i<cflags.length; ++i) {
	//		pars_active[i] = decideSingleParentheses(cflags[i], cflag_last);
	//		cflag_last = cflags[i];
	//	}
	//}

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
				nav.moveToEnd();
			}
		} else {
			for (int i=0; i<pars_active.length; ++i) {
				// Checks for each subnode type
				if (nav.getObject().base_comp instanceof AlignLeafSeries) {
					pars_active[i] = ((AlignLeafSeries) nav.getObject().base_comp)
							.getOrientation() == getOrientation();
				}
				// Increment nav
				nav.moveToEnd();
			}
		}
	}
}
