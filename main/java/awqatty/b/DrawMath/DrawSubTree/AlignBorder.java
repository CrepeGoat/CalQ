package awqatty.b.DrawMath.DrawSubTree;

import java.util.Arrays;
import java.util.List;

import awqatty.b.ListTree.ListTree;

public final class AlignBorder extends AlignAxisBase {

	// Contains only three elements: component, start bound, end bound
	private final List<AlignForm> comps;
		private static final int INDEX_BOUND1	=0;
		private static final int INDEX_COMP		=1;
		private static final int INDEX_BOUND2	=2;
	
	private final byte stretch_bounds;
	
	public AlignBorder(
			AlignForm component,
			AlignForm start_bound,
			AlignForm end_bound,
			byte stretch_type,
			boolean orientation,
			float whitespace,
			byte aligned_edges) {
		super(orientation, whitespace, aligned_edges);
		final AlignForm[] list = new AlignForm[3];
		list[INDEX_BOUND1] = start_bound;
		list[INDEX_COMP] = component;
		list[INDEX_BOUND2] = end_bound;
		comps = Arrays.asList(list);
		stretch_bounds = stretch_type;
	}
	
	//--- Get/Set Method ---
	public AlignForm getComponent() {
		return comps.get(INDEX_COMP);
	}
	public void setComponent(AlignForm comp) {
		comps.set(INDEX_COMP, comp);
	}
		
	//--- AlignBase Overrides ---
	@Override
	protected Iterable<AlignForm> iterComps()	{return comps;}
	@Override
	protected Iterable<AlignForm> iterCompsWithLoc(){return comps;}

	//--- AlignAxisBase Overrides ---
	@Override
	protected void addCompsToSeries() {
		// Gets girth of components
		//if (align == EDGE_ORIGIN) {
		comps.get(INDEX_COMP).getSize(rectf);
		min_girth_start = orient.getGirthStart(rectf);
		max_girth_end = orient.getGirthEnd(rectf);
		
		// Expand girth for bounds
		if (stretch_bounds == STRETCH_NONE) {
			if (comps.get(INDEX_BOUND1) != null) {
				comps.get(INDEX_BOUND1).getSize(rectf);
				min_girth_start = Math.min(min_girth_start, orient.getGirthStart(rectf));
				max_girth_end = Math.max(max_girth_end, orient.getGirthEnd(rectf));
			}
			if (comps.get(INDEX_BOUND2) != null) {
				comps.get(INDEX_BOUND2).getSize(rectf);
				min_girth_start = Math.min(min_girth_start, orient.getGirthStart(rectf));
				max_girth_end = Math.max(max_girth_end, orient.getGirthEnd(rectf));
			}				
		} else if (comps.get(INDEX_BOUND1) != null
				|| comps.get(INDEX_BOUND2) != null) {
			min_girth_start -= 1.5*whtspc;
			max_girth_end += 1.5*whtspc;				
		}
		/* TODO ...what?
		} else {
		
			if (stretch_bounds == STRETCH_NONE) {
				max_girth_end = 0;
				for (AlignForm comp : comps) if (comp != null) {
					comp.getSize(rectf);
					max_girth_end = Math.max(max_girth_end, orient.getGirth(rectf)/2);
				}
			} else {
				comps.get(INDEX_COMP).getSize(rectf);
				max_girth_end = orient.getGirth(rectf)/2;
				if (comps.get(INDEX_BOUND1) != null
						|| comps.get(INDEX_BOUND2) != null) {
					max_girth_end += 1.5*whtspc;
				}
			}
			min_girth_start = -max_girth_end;
		}//*/
				
		// Arrange Components
		addCompToSeries(comps.get(INDEX_BOUND1), stretch_bounds);
		addCompToSeries(comps.get(INDEX_COMP), STRETCH_NONE);
		addCompToSeries(comps.get(INDEX_BOUND2), stretch_bounds);
	}

	@Override
	public <T extends DrawAligned> void subBranchShouldUsePars(
			ListTree<T> tree, int[] branch_indices, boolean[] pars_active) {
		comps.get(INDEX_COMP).subBranchShouldUsePars(tree, branch_indices, pars_active);
	}
	@Override
	public <T extends DrawAligned> AlignForm getFirstInSeries(
			boolean orientation, ListTree<T>.Navigator nav) {
		if (orientation != getOrientation()) return this;
		return comps.get(INDEX_BOUND1).getFirstInSeries(orientation, nav);
	}	
	@Override
	public <T extends DrawAligned> AlignForm getLastInSeries(
			boolean orientation, ListTree<T>.Navigator nav) {
		if (orientation != getOrientation()) return this;
		return comps.get(INDEX_BOUND2).getLastInSeries(orientation, nav);
	}	
}
