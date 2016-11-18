package awqatty.b.DrawMath.DrawSubTree.Builders;

import awqatty.b.DrawMath.DrawSubTree.AlignAxisBase;

abstract public class AlignAxisBuilder {

	protected boolean orient = AlignAxisBase.HORIZONTAL;
	protected float whtspc = 0;
	protected byte edge = AlignAxisBase.EDGE_START;
	
	// Constructor
	public AlignAxisBuilder() {}
	
	// Builder methods
	public AlignAxisBuilder orientation(boolean orientation) {
		orient = orientation;
		return this;
	}
	public AlignAxisBuilder whitespace(float whitespace) {
		whtspc = whitespace;
		return this;
	}
	public AlignAxisBuilder aligned_edge(byte aligned_edge) {
		edge = aligned_edge;
		return this;
	}
		
	abstract public AlignAxisBase build();

}
