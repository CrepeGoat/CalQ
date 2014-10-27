package awqatty.b.DrawMath.DrawSubTree;

import java.util.ArrayList;
import java.util.List;

abstract public class AlignSeriesBase extends AlignAxisBase {

	protected final List<AlignForm> comps = new ArrayList<AlignForm>();

	private final byte stretch_divider;
		
	//--- Constructor ---
	public AlignSeriesBase(
			AlignForm divider,
			byte stretch_type,
			boolean orientation, 
			float whitespace,
			byte aligned_edges) {
		super(orientation, whitespace, aligned_edges);
		comps.add(divider);
		stretch_divider = stretch_type;
	}
	
	//--- AlignAxisBase Overrides ---
	private List<AlignForm> comps_ordered=null;
	@Override
	protected void loadAlignTools() {
		super.loadAlignTools();
		if (comps_ordered == null)
			comps_ordered = new ArrayList<AlignForm>();
	}
	@Override
	protected void addCompToSeries(AlignForm comp, byte stretch_type) {
		if (comp != null) comps_ordered.add(comp);
		super.addCompToSeries(comp, stretch_type);
	}
	
	@Override
	protected void addCompsToSeries() {
		max_girth = 0;
		// Gets max girth of components
		for (AlignForm draw : comps.subList(1,comps.size())) {
			if (draw != null) {
				draw.getSize(rectf);
				max_girth = Math.max(max_girth,
						orient.getGirth(rectf) );
			}
		}
		// Sets max_girth based on bound components
		if (comps.get(0) != null) {
			if (stretch_divider == STRETCH_NONE) {
					comps.get(0).getSize(rectf);
					max_girth = Math.max(max_girth, orient.getGirth(rectf));
				}
			else /*if (stretch_divider == STRETCH_FULL
				|| stretch_divider == STRETCH_GIRTH) */
				max_girth += 3*whtspc;
			}
		
		// Arrange components in series
		if (comps.get(0) == null) {
			for (AlignForm comp : comps.subList(1,comps.size()))
				addCompToSeries(comp, STRETCH_NONE);
		}
		else {
			if (comps.size() > 1)
				addCompToSeries(comps.get(1), STRETCH_NONE);
			for (AlignForm comp : comps.subList(2,comps.size())) {
				addCompToSeries(comps.get(0), stretch_divider);
				addCompToSeries(comp, STRETCH_NONE);
			}
		}
	}
	
	//--- AlignBase Overrides ---
	@Override
	public Iterable<AlignForm> iterComps() {return comps;}
	@Override
	public Iterable<AlignForm> iterLocComps() {return comps_ordered;}
	
}
