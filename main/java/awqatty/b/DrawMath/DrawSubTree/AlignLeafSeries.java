package awqatty.b.DrawMath.DrawSubTree;

import java.util.List;

import android.graphics.RectF;
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
	
	//--- Private Methods ---
	private void refreshLeaves(int leaf_count) {
		final int old_leaf_count = comps.size()-1;
		if (leaf_count > old_leaf_count) {
			for (int i=old_leaf_count; i<leaf_count; ++i)
				comps.add(new AlignLeaf(i));
		} else
			comps.subList(
					leaf_count+INDEX_FIRST,
					old_leaf_count+INDEX_FIRST ).clear();
	}

	//--- AlignBase Override Methods ---
	@Override
	public void setSuperLeafSizes(List<RectF> leaf_sizes) {
		refreshLeaves(leaf_sizes.size());		
		if (locs != null)
			locs.clear();
		super.setSuperLeafSizes(leaf_sizes);
	}
	@Override
	public <T extends DrawAligned> void subBranchShouldUsePars(
			ListTree<T> tree, int[] branch_indices, boolean[] pars_active) {
		refreshLeaves(branch_indices.length);
		super.subBranchShouldUsePars(tree, branch_indices, pars_active);
	}


}
