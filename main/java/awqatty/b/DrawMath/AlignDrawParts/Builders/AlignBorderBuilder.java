package awqatty.b.DrawMath.AlignDrawParts.Builders;

import awqatty.b.DrawMath.AlignDrawParts.AlignAxisBase;
import awqatty.b.DrawMath.AlignDrawParts.AlignBorder;
import awqatty.b.DrawMath.AlignDrawParts.AlignForm;

public class AlignBorderBuilder extends AlignAxisBuilder {
	
	private final AlignForm comp;
	private AlignForm bound1 = null;
	private AlignForm bound2 = null;

	private byte stretch_bounds = AlignAxisBase.STRETCH_FULL;
	
	
	// Constructor (for non-optional arguments)
	public AlignBorderBuilder(AlignForm component) {
		comp = component;
	}
	
	// Builder Methods
	public AlignBorderBuilder start_bound(AlignForm start_bound) {
		bound1 = start_bound;
		return this;
	}
	public AlignBorderBuilder end_bound(AlignForm end_bound) {
		bound2 = end_bound;
		return this;
	}
	
	public AlignBorderBuilder bound_stretch(byte stretch_type) {
		stretch_bounds = stretch_type;
		return this;
	}
		
	// Build Method
	@Override
	public AlignBorder build() {
		return new AlignBorder(
				comp, bound1, bound2, 
				stretch_bounds, 
				orient, whtspc, edge );
	}

}
