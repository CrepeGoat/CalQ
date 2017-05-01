package awqatty.b.DrawMath.AlignDrawParts;

import java.util.List;

import awqatty.b.DrawMath.AlignDrawParts.Utilities.AlignmentEdge;
import awqatty.b.DrawMath.AlignDrawParts.Utilities.StretchType;
import awqatty.b.DrawMath.AssignParentheses.ClosureFlags;
import awqatty.b.DrawMath.DrawToCanvas.DrawText;
import awqatty.b.ListTree.ListTree;

public final class AlignSeries extends AlignAxisBase {
	
	// New Methods
	public AlignSeries(
			List<? extends AlignForm> components,
			List<StretchType> componentStretches,
			boolean orientation,
			float whitespace_series,
			float whitespace_stretch,
			AlignmentEdge aligned_edges
	) {
		super(orientation, whitespace_series, whitespace_stretch, aligned_edges);
		comps.addAll(components);
		stretches.addAll(componentStretches);
	}

	@Override
	protected int getCompIndex(int index) {
		return index;
	}
	@Override
	protected boolean hasNthEntry(int index) {
		return index < comps.size();
	}

	/*
	@Override
	protected void decideParentheses(int[] cflags, boolean[] pars_active) {
		int cflag_last=ClosureFlags.NONE;
		for (AlignForm comp : comps.subList(1,comps.size())) {
			if (comp instanceof AlignLeaf) {
				pars_active[((AlignLeaf)comp).leaf_number] = decideSingleParentheses(
						cflags[((AlignLeaf)comp).leaf_number], cflag_last );
				cflag_last = cflags[((AlignLeaf)comp).leaf_number];
			}
			else
				cflag_last = comp.getClosureFlags();
		}
		
	}
	//*/
	/*
	@Override
	public <T extends DrawAligned> void subBranchShouldUsePars(
			ListTree<T>.Navigator nav, boolean[] pars_active) {
		if (comps.size() > 2
				&& comps.get(1) instanceof DrawText
				&& ((DrawText)comps.get(1)).text == "-"
					// ^ -> this function is the negative function
				&& ((nav.getObject().base_comp instanceof DrawText
					&& ((DrawText)nav.getObject().base_comp).text == "-")
				|| (nav.getObject().base_comp instanceof AlignSeries
					&& ((AlignSeries)nav.getObject().base_comp).comps.get(1) instanceof DrawText
					&& ((DrawText)((AlignSeries)nav.getObject().base_comp).comps.get(1))
							.text.startsWith("-") ))) {
				pars_active[0]=true;
		}
		// else all = false
	}
	//*/

}
