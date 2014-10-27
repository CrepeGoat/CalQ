package awqatty.b.DrawMath.DrawSubTree;

import java.util.List;

import android.graphics.RectF;

public class AlignLeafSeries extends AlignSeriesBase {
	
	@Override
	public void clearCache() {
		comps.subList(1, comps.size()).clear();
		super.clearCache();
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
		
		super.setSuperLeafSizes(leaf_sizes);
	}

}
