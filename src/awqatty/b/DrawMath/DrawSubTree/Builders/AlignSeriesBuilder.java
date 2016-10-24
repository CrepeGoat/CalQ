package awqatty.b.DrawMath.DrawSubTree.Builders;

import java.util.List;

import awqatty.b.DrawMath.DrawSubTree.AlignAxisBase;
import awqatty.b.DrawMath.DrawSubTree.AlignForm;
import awqatty.b.DrawMath.DrawSubTree.AlignSeries;

public class AlignSeriesBuilder extends AlignSeriesBaseBuilder {

	private final List<? extends AlignForm> comps;
	
	public AlignSeriesBuilder(List<? extends AlignForm> components) {
		super();
		comps = components;
	}
	
	// Return Method
	@Override
	public AlignAxisBase build() {
		return new AlignSeries(
				comps, div, stretch_div, 
				orient, whtspc, edge );
	}

}
