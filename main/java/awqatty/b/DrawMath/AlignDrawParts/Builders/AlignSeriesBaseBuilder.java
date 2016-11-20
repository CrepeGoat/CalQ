package awqatty.b.DrawMath.AlignDrawParts.Builders;

import awqatty.b.DrawMath.AlignDrawParts.AlignAxisBase;
import awqatty.b.DrawMath.AlignDrawParts.AlignForm;

public abstract class AlignSeriesBaseBuilder extends AlignAxisBuilder {

	protected AlignForm div = null;
	protected byte stretch_div = AlignAxisBase.STRETCH_NONE;
	
	public AlignSeriesBaseBuilder() {}
	
	// Builder Methods
	public AlignSeriesBaseBuilder divider(AlignForm divider) {
		div = divider;
		return this;
	}
	public AlignSeriesBaseBuilder stretch_divider(byte stretch_type) {
		stretch_div = stretch_type;
		return this;
	}
	
}
