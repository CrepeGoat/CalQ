package awqatty.b.DrawMath.DrawSubTree;

import java.util.List;

public class AlignSeries extends AlignSeriesBase {
	
	// New Methods
	public AlignSeries(
			List<? extends AlignForm> components,
			AlignForm divider, byte stretch_type,
			boolean orientation, float whitespace, byte aligned_edges) {
		super(divider, stretch_type, orientation, whitespace, aligned_edges);
		comps.addAll(components);
	}
	
	public void addComponent(AlignForm draw) {
		comps.add(draw);
	}
	public void clearComponents() {
		comps.subList(1,comps.size()).clear();
	}
}
