package awqatty.b.DrawMath.DrawSubTree;

import java.util.List;

import android.graphics.RectF;
import awqatty.b.DrawMath.AssignParentheses.ClosureType;

public class AlignLeafSeries extends AlignSeriesBase {
	
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
	protected void decideParentheses(ClosureType[] ctypes, boolean[] pars_active) {
		ClosureType ctype_last=null;
		for (int i=0; i<ctypes.length; ++i) {
			pars_active[i] = decideSingleParentheses(ctypes[i],	ctype_last);
			ctype_last = ctypes[i];
		}
	}

}
