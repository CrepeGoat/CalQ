package awqatty.b.DrawMath.AlignDrawParts.Builders;

import awqatty.b.DrawMath.AlignDrawParts.AlignAxisBase;
import awqatty.b.DrawMath.AlignDrawParts.AlignForm;
import awqatty.b.DrawMath.AlignDrawParts.Utilities.StretchType;

public abstract class AlignSeriesBaseBuilder extends AlignAxisBuilder {

	protected AlignForm div = null;
	protected StretchType stretch_div = StretchType.NONE;
	
	public AlignSeriesBaseBuilder() {}
	
	// Builder Methods
	public AlignSeriesBaseBuilder divider(AlignForm divider) {
		div = divider;
		return this;
	}
	public AlignSeriesBaseBuilder stretch_divider(StretchType stretch_type) {
		stretch_div = stretch_type;
		return this;
	}
	
}
