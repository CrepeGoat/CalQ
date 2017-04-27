package awqatty.b.DrawMath.AlignDrawParts.Builders;

import java.util.ArrayList;
import java.util.List;

import awqatty.b.DrawMath.AlignDrawParts.AlignAxisBase;
import awqatty.b.DrawMath.AlignDrawParts.AlignForm;
import awqatty.b.DrawMath.AlignDrawParts.AlignSeries;
import awqatty.b.DrawMath.AlignDrawParts.Utilities.StretchType;

public class AlignSeriesBuilder extends AlignSeriesBaseBuilder {

	private final List<? extends AlignForm> comps;
	private final List<StretchType> stretches;

	public AlignSeriesBuilder(
			List<? extends AlignForm> components
	) {
		comps = components;
		stretches = new ArrayList<>();
		for (AlignForm comp : comps) {
			stretches.add(StretchType.NONE);
		}
	}
	public AlignSeriesBuilder stretchType(int comp_index, StretchType stretch_type) {
		stretches.set(comp_index, stretch_type);
		return this;
	}
	
	// Return Method
	@Override
	public AlignAxisBase build() {
		return new AlignSeries(
				comps, stretches,
				orient,
				whtspc_seriesSeparation, whtspc_stretchPadding,
				edge
		);
	}

}
