package awqatty.b.DrawMath.AlignDrawParts.Builders;

import awqatty.b.DrawMath.AlignDrawParts.AlignAxisBase;
import awqatty.b.DrawMath.AlignDrawParts.Utilities.AlignmentEdge;
import awqatty.b.DrawMath.AlignDrawParts.Utilities.OrientForm;

import static awqatty.b.DrawMath.AlignDrawParts.Utilities.AlignmentEdge.CENTER;

abstract public class AlignAxisBuilder {

	protected boolean orient = OrientForm.HORIZONTAL;
	protected float whtspc_seriesSeparation=0;
	protected float whtspc_stretchPadding=0;
	protected AlignmentEdge edge = CENTER;
	
	// Constructor
	public AlignAxisBuilder() {}
	
	// Builder methods
	public AlignAxisBuilder orientation(boolean orientation) {
		orient = orientation;
		return this;
	}
	public AlignAxisBuilder whitespaceBetweenSeries(float whitespace) {
		whtspc_seriesSeparation = whitespace;
		return this;
	}
	public AlignAxisBuilder whitespacePaddingStretch(float whitespace) {
		whtspc_stretchPadding = whitespace;
		return this;
	}
	public AlignAxisBuilder aligned_edge(AlignmentEdge aligned_edge) {
		edge = aligned_edge;
		return this;
	}
		
	abstract public AlignAxisBase build();

}
