package awqatty.b.DrawMath.AlignDrawParts.Builders;

import java.util.List;

import awqatty.b.DrawMath.AlignDrawParts.AlignAxisBase;
import awqatty.b.DrawMath.AlignDrawParts.AlignForm;
import awqatty.b.DrawMath.AlignDrawParts.AlignSeries;

public class AlignSeriesBuilder extends AlignSeriesBaseBuilder {

	private final List<? extends AlignForm> comps;
	
	public AlignSeriesBuilder(List<? extends AlignForm> components) {
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
