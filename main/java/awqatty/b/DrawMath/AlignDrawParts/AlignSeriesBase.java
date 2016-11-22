package awqatty.b.DrawMath.AlignDrawParts;

import java.util.ArrayList;
import java.util.List;

import awqatty.b.DrawMath.AlignDrawParts.Utilities.AlignmentEdge;
import awqatty.b.DrawMath.AlignDrawParts.Utilities.OrientForm;
import awqatty.b.DrawMath.AlignDrawParts.Utilities.StretchType;
import awqatty.b.DrawMath.AssignParentheses.ClosureFlags;

abstract public class AlignSeriesBase extends AlignAxisBase {


	//--- Constructor ---
	public AlignSeriesBase(
			AlignForm divider,
			StretchType stretch_type,
			boolean orientation, 
			float whitespace_series,
			float whitespace_stretch,
			AlignmentEdge aligned_edges) {
		super(orientation, whitespace_series, whitespace_stretch, aligned_edges);
		comps.add(divider);
		stretches.add(stretch_type);
	}

	//--- AlignAxisBase Overrides ---



	//--- AlignBase Overrides ---

	// Manage Parentheses
	/*
	protected boolean decideSingleParentheses(int cflag, int ctype_last) {
		switch(cflag) {
			case ClosureFlags.BOUNDED:
			case ClosureFlags.SCRIPT:
			case ClosureFlags.TEXT_ALPHABETIC:
				return false;
			case ClosureFlags.SERIES_HORIZ:
			case ClosureFlags.SERIES_HORIZ | ClosureFlags.DIVIDER:
				return orient.getOrientation() == OrientForm.HORIZONTAL;
			case ClosureFlags.SERIES_VERT:
			case ClosureFlags.SERIES_VERT | ClosureFlags.DIVIDER:
				return orient.getOrientation() == OrientForm.VERTICAL;
			case ClosureFlags.TEXT_NUMERIC:
				return comps.get(INDEX_DIVIDER)==null &&
						(orient.getOrientation() == OrientForm.HORIZONTAL) &&
						ctype_last!=ClosureFlags.NONE && ClosureFlags.typeIsText(ctype_last);
			case ClosureFlags.TEXT_NUMERIC | ClosureFlags.NEGATIVE:
				return ctype_last!=ClosureFlags.NONE && (orient.getOrientation() == OrientForm.HORIZONTAL)
						&& (comps.get(INDEX_DIVIDER)==null || stretches.get(INDEX_DIVIDER)==StretchType.NONE);
			default:
				return true;
		}
		
	}
	//*/
}
