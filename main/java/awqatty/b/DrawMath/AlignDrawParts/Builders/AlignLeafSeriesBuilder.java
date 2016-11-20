package awqatty.b.DrawMath.AlignDrawParts.Builders;

import awqatty.b.DrawMath.AlignDrawParts.AlignAxisBase;
import awqatty.b.DrawMath.AlignDrawParts.AlignLeafSeries;

public class AlignLeafSeriesBuilder extends AlignSeriesBaseBuilder {

	@Override
	public AlignAxisBase build() {
		return new AlignLeafSeries(div, stretch_div,
				orient, whtspc, edge );
	}

}
