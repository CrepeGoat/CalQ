package awqatty.b.DrawMath.DrawSubTree;

import java.util.ArrayList;
import java.util.List;

import android.graphics.RectF;
import awqatty.b.DrawMath.OrientationObjects.OrientForm;

public abstract class AlignAxisBase extends AlignBase {

	// Local Members
	protected List<RectF> locs=null;
	@Override
	public void clearCache() {
		super.clearCache();
		locs = null;
	}
	
	// Stretch types (for bounds/divider)
		public static final byte STRETCH_NONE	=0;
		public static final byte STRETCH_GIRTH	=1;
		public static final byte STRETCH_FULL	=2;

	protected final OrientForm orient;
		public static final boolean HORIZONTAL	=OrientForm.HORIZONTAL;
		public static final boolean VERTICAL	=OrientForm.VERTICAL;
	
	protected final float whtspc;	// separates each component, including divider
	
	protected final byte align;	// how to align components in opposing axis
		public static final byte EDGE_LEFT	=0;
		public static final byte EDGE_TOP	=0;
		public static final byte EDGE_START	=0;
		
		public static final byte EDGE_RIGHT	=2;
		public static final byte EDGE_BOTTOM=2;
		public static final byte EDGE_END	=2;
		
		public static final byte EDGE_CENTER=1;
		public static final byte EDGE_ORIGIN=3;
			// when unspecified, make identical to edge_center

	public AlignAxisBase(
			boolean orientation,
			float whitespace,
			byte aligned_edges) {
		orient = (orientation == HORIZONTAL
				? OrientForm.horiz : OrientForm.vert);
		whtspc = whitespace;
		align = aligned_edges;
	}
	
	//--- Get Methods ---
	public boolean getOrientation() {
		return orient.getOrientation();
	}
	
	//--- AlignBase Overrides ---
	@Override
	public void getSize(RectF dst) {
		dst.set(valid_area);
		orient.offsetTo(dst,
				-orient.getLength(dst)/2,
				orient.getGirthStart(dst));
	}
	
	@Override
	public Iterable<RectF> iterLocs() {return locs;}
	
	protected RectF rectf = null;
	protected float min_girth_start, max_girth_end;
	// Use in loop
	private float last_edge;
	@Override
	protected void arrange() {
		loadAlignTools();
		last_edge=0;
				
		// Clear previous lists
		locs.clear();
		// Arrange Components
		addCompsToSeries();
		
		orient.set(valid_area, 
				0, 					min_girth_start,
				last_edge-whtspc, 	max_girth_end );
	}
	
	protected void loadAlignTools() {
		if (valid_area == null)
			valid_area = new RectF();
		if (locs == null)
			locs = new ArrayList<RectF>();
		else locs.clear();
		if (rectf == null)
			rectf = new RectF();
	}
	
	protected void addCompToSeries(AlignForm comp, byte stretch_type) {
		if (comp == null) return;
		// Init local variable
		if (rectf == null)
			rectf = new RectF();
		// Retrieve component size
		comp.getSize(rectf);
		// Adjust size based on stretch type
		final float girth_factor = (max_girth_end-min_girth_start)/orient.getGirth(rectf);
		switch (stretch_type) {
		case STRETCH_FULL:
			orient.setLengthStart(rectf, orient.getLengthStart(rectf)*girth_factor);
			orient.setLengthEnd(rectf, orient.getLengthEnd(rectf)*girth_factor);
		case STRETCH_GIRTH:
			orient.setGirthStart(rectf, orient.getGirthStart(rectf)*girth_factor);
			orient.setGirthEnd(rectf, orient.getGirthEnd(rectf)*girth_factor);
			break;
		case STRETCH_NONE:
			break;
			default:
				break;
		}
		// Move location based on alignment
		float new_girth_start;
		if (align==EDGE_ORIGIN) {
			new_girth_start = (stretch_type==STRETCH_NONE)
					? orient.getGirthStart(rectf)
					: min_girth_start;//(max_girth_end+min_girth_start -orient.getGirth(rectf)) /2f;
						// if stretching, treats divider as if under EDGE_CENTER
		} else new_girth_start = min_girth_start + ((max_girth_end-min_girth_start)
				-orient.getGirth(rectf) ) * (align/2f);
		orient.offsetTo(rectf, last_edge, new_girth_start);
		// Increment position element
		last_edge = orient.getLengthEnd(rectf) + whtspc;
		// Set location
		locs.add(rectf);
		rectf = null;
	}
	abstract protected void addCompsToSeries();

}
