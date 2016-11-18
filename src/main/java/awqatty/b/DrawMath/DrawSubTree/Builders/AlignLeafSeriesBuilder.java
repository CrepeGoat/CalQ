package awqatty.b.DrawMath.DrawSubTree.Builders;

import awqatty.b.DrawMath.DrawSubTree.AlignAxisBase;
import awqatty.b.DrawMath.DrawSubTree.AlignLeafSeries;

public class AlignLeafSeriesBuilder extends AlignSeriesBaseBuilder {

	@Override
	public AlignAxisBase build() {
		return new AlignLeafSeries(div, stretch_div,
				orient, whtspc, edge );
	}

}
