package awqatty.b.DrawMath.AlignDrawParts;

import java.util.ArrayList;
import java.util.List;

import android.graphics.RectF;

import awqatty.b.DrawMath.AlignDrawParts.Utilities.AlignmentEdge;
import awqatty.b.DrawMath.AlignDrawParts.Utilities.StretchType;
import awqatty.b.DrawMath.AssignParentheses.ClosureFlags;
import awqatty.b.DrawMath.AlignDrawParts.Utilities.OrientForm;

public abstract class AlignAxisBase extends AlignBase {

	// Local Members
	protected List<RectF> locs_ordered = null;
	protected final List<AlignForm> comps = new ArrayList<>();
	protected final List<StretchType> stretches = new ArrayList<>();
	@Override
	public void clearCache() {
		super.clearCache();
		locs_ordered = null;
	}

	protected final OrientForm orient;
	protected final float whtspc_seriesSeparation;	// separates each component, including divider
	protected final float whtspc_stretchPadding; // girth added to ends of stretched components
	protected final AlignmentEdge align;	// how to align components in opposing axis

	public AlignAxisBase(
			boolean orientation,
			float whitespace_series,
			float whitespace_stretch,
			AlignmentEdge aligned_edges
	) {
		orient = (orientation == OrientForm.HORIZONTAL
				? OrientForm.horiz : OrientForm.vert);
		whtspc_seriesSeparation = whitespace_series;
		whtspc_stretchPadding = whitespace_stretch;
		align = aligned_edges;
	}
	
	//--- Get Methods ---
	public boolean getOrientation() {
		return orient.getOrientation();
	}

	//@Override
	//public int getClosureFlags() {
	//	return orient.getOrientation() == OrientForm.HORIZONTAL
	//			? ClosureFlags.SERIES_HORIZ : ClosureFlags.SERIES_VERT;
	//}

	// Abstract Methods
	abstract protected int getCompIndex(int index);

	//--- AlignBase Overrides ---
	@Override
	public Iterable<AlignForm> iterComps() {return comps;}
	@Override
	protected AlignForm getNthComp(int index) {
		return comps.get(getCompIndex(index));
	}
	@Override
	protected RectF getNthLoc(int index) {
		return locs_ordered.get(index);
	}


	protected RectF rectf = null;
	protected float max_girth;
	// Use in loop
	private float last_edge;
	@Override
	protected void arrange() {
		loadAlignTools();
		last_edge=0;
				
		// Clear previous lists
		locs_ordered.clear();
		// Arrange Components
		addCompsToSeries();
		// (removes buffer whitespaceBetweenSeries from last comp
		// added to space individual comps)
		last_edge -= whtspc_seriesSeparation;

		// Set bounds
		valid_area.left = 0;
		valid_area.top = 0;
		orient.setGirthEnd(valid_area, max_girth);
		orient.setLengthEnd(valid_area, last_edge);
	}
	
	protected void loadAlignTools() {
		if (valid_area == null)
			valid_area = new RectF();
		if (locs_ordered == null)
			locs_ordered = new ArrayList<>();
		else locs_ordered.clear();
		if (rectf == null)
			rectf = new RectF();
	}
	
	private void addCompToSeries(AlignForm comp, StretchType stretch_type) {
		if (comp == null) return;

		if (rectf == null)
			rectf = new RectF();
		locs_ordered.add(rectf);
		
		comp.getSize(rectf);
		rectf.offsetTo(0,0);
				
		switch (stretch_type) {
		case NONE:
			break;
		case FULL:
			orient.setLengthEnd(rectf,
					orient.getLength(rectf)*max_girth/orient.getGirth(rectf) );
		case GIRTH:
			orient.setGirthEnd(rectf, max_girth);
			break;
		default:
			break;
		}
		orient.offsetTo(rectf, last_edge,
				(align==AlignmentEdge.TOP_LEFT) ? 0 :
				(max_girth-orient.getGirth(rectf))*(align==AlignmentEdge.CENTER ? 0.5f:1)
		);
		last_edge = orient.getLengthEnd(rectf) + whtspc_seriesSeparation;
		rectf = null;
	}
	private void addCompsToSeries() {
		// Gets max girth of components
		max_girth = 0;
		boolean hasStretchComp = false;
		for (int i=0;i<comps.size();++i) {
			if (comps.get(i)!=null){
				if (stretches.get(i)==StretchType.NONE) {
					comps.get(i).getSize(rectf);
					max_girth = Math.max(max_girth, orient.getGirth(rectf));
				} else {
					hasStretchComp = true;
				}
			}
		}
		if (hasStretchComp) max_girth += 2*whtspc_stretchPadding;

		// Add each component in order
		for (int i=0; hasNthEntry(i); ++i) {
			addCompToSeries(
					comps.get(getCompIndex(i)),
					stretches.get(getCompIndex(i))
			);
		}
	}
}
