package awqatty.b.DrawMath.DrawSubTree;

import java.util.ArrayList;
import java.util.List;

import android.graphics.RectF;
import awqatty.b.DrawMath.AssignParentheses.ClosureFlags;
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
		public static final byte EDGE_CENTER=1;
		public static final byte EDGE_RIGHT	=2;
		public static final byte EDGE_BOTTOM=2;
		public static final byte EDGE_END	=2;

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
	
	@Override
	public int getClosureFlags() {
		return orient.getOrientation() == HORIZONTAL
				? ClosureFlags.SERIES_HORIZ : ClosureFlags.SERIES_VERT;
	}
	
	//--- AlignBase Overrides ---
	@Override
	public Iterable<RectF> iterLocs() {return locs;}
	
	protected RectF rectf = null;
	protected float max_girth;
	// Use in loop
	private float last_edge;
	@Override
	protected void arrange() {
		// TODO Auto-generated method stub
		loadAlignTools();
		last_edge=0;
				
		// Clear previous lists
		locs.clear();
		// Arrange Components
		addCompsToSeries();
		
		// Set bounds
		valid_area.left = 0;
		valid_area.top = 0;
		orient.setGirthEnd(valid_area, max_girth);
		orient.setLengthEnd(valid_area, last_edge-whtspc);
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
		
		if (rectf == null)
			rectf = new RectF();
		locs.add(rectf);
		
		comp.getSize(rectf);
		rectf.offsetTo(0,0);
				
		switch (stretch_type) {
		case STRETCH_NONE:
			break;
		case STRETCH_FULL:
			orient.setLengthEnd(rectf,
					orient.getLength(rectf)*max_girth/orient.getGirth(rectf) );
		case STRETCH_GIRTH:
			orient.setGirthEnd(rectf, max_girth);
			break;
		default:
			break;
		}
		orient.offsetTo(rectf, last_edge,
				(max_girth-orient.getGirth(rectf)) * align/2f
				);
		last_edge = orient.getLengthEnd(rectf) + whtspc;
		rectf = null;
	}
	abstract protected void addCompsToSeries();

}
