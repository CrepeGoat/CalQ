package awqatty.b.DrawMath.DrawSubTree;

import java.util.Arrays;
import java.util.List;

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
	
	//--- AlignBase Overrides ---
	@Override
	protected Iterable<AlignForm> iterComps()	{return comps;}
	@Override
	protected Iterable<AlignForm> iterLocComps(){return comps;}

	//--- AlignAxisBase Overrides ---
	@Override
	protected void addCompsToSeries() {
		// Gets girth of component
		comps.get(INDEX_COMP).getSize(rectf);
		max_girth = orient.getGirth(rectf);
		// Sets max_girth based on bound components
		if (stretch_bounds == STRETCH_NONE) {
			if (comps.get(INDEX_BOUND1) != null)
				comps.get(INDEX_BOUND1).getSize(rectf);
			max_girth = Math.max(max_girth, orient.getGirth(rectf));
			if (comps.get(INDEX_BOUND2) != null)
				comps.get(INDEX_BOUND2).getSize(rectf);
			max_girth = Math.max(max_girth, orient.getGirth(rectf));
		}
		else /*if (stretch_bounds == STRETCH_FULL
				|| stretch_bounds == STRETCH_GIRTH) */ {
			if (comps.get(INDEX_BOUND1) != null
					|| comps.get(INDEX_BOUND2) != null) {
				max_girth += 2*whtspc;
			}
		}
		
		// Arrange Components
		addCompToSeries(comps.get(INDEX_BOUND1), stretch_bounds);
		addCompToSeries(comps.get(INDEX_COMP), STRETCH_NONE);
		addCompToSeries(comps.get(INDEX_BOUND2), stretch_bounds);
	}	
}
