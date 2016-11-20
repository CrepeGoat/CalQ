package awqatty.b.DrawMath.AlignDrawParts;

import java.util.ArrayList;
import java.util.List;

import awqatty.b.DrawMath.AssignParentheses.ClosureFlags;

abstract public class AlignSeriesBase extends AlignAxisBase {

	protected final List<AlignForm> comps = new ArrayList<>();
	protected final int INDEX_DIVIDER = 0;

	protected final byte stretch_divider;
		
	//--- Constructor ---
	public AlignSeriesBase(
			AlignForm divider,
			byte stretch_type,
			boolean orientation, 
			float whitespace,
			byte aligned_edges) {
		super(orientation, whitespace, aligned_edges);
		comps.add(divider);
		stretch_divider = stretch_type;
	}
	
	//--- Access Functions ---
	public boolean hasDivider() {
		return comps.get(INDEX_DIVIDER) != null;
	}
	
	//--- AlignAxisBase Overrides ---
	private List<AlignForm> comps_ordered=null;
	@Override
	protected void loadAlignTools() {
		super.loadAlignTools();
		if (comps_ordered == null)
			comps_ordered = new ArrayList<>();
		else comps_ordered.clear();
	}
	@Override
	protected void addCompToSeries(AlignForm comp, byte stretch_type) {
		if (comp != null) comps_ordered.add(comp);
		super.addCompToSeries(comp, stretch_type);
	}
	
	@Override
	protected void addCompsToSeries() {
		// Gets max girth of components
		max_girth = 0;
		for (AlignForm draw : comps.subList(1,comps.size())) {
			if (draw != null) {
				draw.getSize(rectf);
				max_girth = Math.max(max_girth, orient.getGirth(rectf));
			}
		}
		// Proceed based on whether or not bounds are used
		if (comps.get(INDEX_DIVIDER) == null) {
			// Arrange components in series
			for (AlignForm comp : comps.subList(1,comps.size()))
				addCompToSeries(comp, STRETCH_NONE);
		}
		else {
			// Sets max_girth based on bound components
			if (stretch_divider == STRETCH_NONE) {
				comps.get(INDEX_DIVIDER).getSize(rectf);
				max_girth = Math.max(max_girth, orient.getGirth(rectf));
			}
			//else max_girth += 3*whtspc;
				/*if (stretch_divider == STRETCH_FULL
				|| stretch_divider == STRETCH_GIRTH) */
			
			// Arrange components in series
			if (comps.size() > 1)
				addCompToSeries(comps.get(1), STRETCH_NONE);
			for (AlignForm comp : comps.subList(2,comps.size())) {
				addCompToSeries(comps.get(INDEX_DIVIDER), stretch_divider);
				addCompToSeries(comp, STRETCH_NONE);
			}
		}
	}
	
	//--- AlignBase Overrides ---
	@Override
	public Iterable<AlignForm> iterComps() {return comps;}
	@Override
	public Iterable<AlignForm> iterCompsWithLoc() {return comps_ordered;}
	
	// Manage Parentheses
	protected boolean decideSingleParentheses(int cflag, int ctype_last) {
		switch(cflag) {
			case ClosureFlags.BOUNDED:
			case ClosureFlags.SCRIPT:
			case ClosureFlags.TEXT_ALPHABETIC:
				return false;
			case ClosureFlags.SERIES_HORIZ:
			case ClosureFlags.SERIES_HORIZ | ClosureFlags.DIVIDER:
				return orient.getOrientation() == HORIZONTAL;
			case ClosureFlags.SERIES_VERT:
			case ClosureFlags.SERIES_VERT | ClosureFlags.DIVIDER:
				return orient.getOrientation() == VERTICAL;
			case ClosureFlags.TEXT_NUMERIC:
				return !(comps.get(INDEX_DIVIDER)!=null) &&
						(orient.getOrientation() == HORIZONTAL) &&
						ctype_last!=ClosureFlags.NONE && ClosureFlags.typeIsText(ctype_last);
			case ClosureFlags.TEXT_NUMERIC | ClosureFlags.NEGATIVE:
				return ctype_last!=ClosureFlags.NONE && (orient.getOrientation() == HORIZONTAL)
						&& (!(comps.get(INDEX_DIVIDER)!=null) || stretch_divider==STRETCH_NONE);
			default:
				return true;
		}
		
	}
	
}
