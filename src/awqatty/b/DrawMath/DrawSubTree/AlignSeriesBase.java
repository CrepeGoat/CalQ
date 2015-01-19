package awqatty.b.DrawMath.DrawSubTree;

import java.util.ArrayList;
import java.util.List;

import awqatty.b.DrawMath.AlignDrawBuilder;
import awqatty.b.DrawMath.DrawToCanvas.DrawText;
import awqatty.b.ListTree.ListTree;

abstract public class AlignSeriesBase extends AlignAxisBase {

	protected final List<AlignForm> comps = new ArrayList<AlignForm>();
	protected static final int INDEX_DIVIDER = 0;
	protected static final int INDEX_FIRST = 1;

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
			comps_ordered = new ArrayList<AlignForm>();
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
		//if (align == EDGE_ORIGIN) {
		if (comps.size() > 1 && comps.get(INDEX_FIRST) != null) {
			comps.get(INDEX_FIRST).getSize(rectf);
			min_girth_start = orient.getGirthStart(rectf);
			max_girth_end = orient.getGirthEnd(rectf);
		}
		for (AlignForm comp : comps.subList(INDEX_FIRST+1,comps.size())) 
				if (comp != null) {
			comp.getSize(rectf);
			min_girth_start = Math.min(min_girth_start, orient.getGirthStart(rectf));
			max_girth_end = Math.max(max_girth_end, orient.getGirthEnd(rectf));
		}
		/* TODO ...what?
		} else {
			max_girth_end = 0;
			for (AlignForm comp : comps.subList(INDEX_FIRST,comps.size())) 
					if (comp != null) {
				comp.getSize(rectf);
				max_girth_end = Math.max(max_girth_end, orient.getGirth(rectf)/2);
			}
			min_girth_start = -max_girth_end;
		}//*/
		
		// Proceed based on whether or not bounds are used
		if (!hasDivider()) {
			// Arrange components in series
			for (AlignForm comp : comps.subList(INDEX_FIRST,comps.size()))
				addCompToSeries(comp, STRETCH_NONE);
		}
		
		else {
			// Sets max_girth based on bound components
			if (stretch_divider == STRETCH_NONE) {
				comps.get(INDEX_DIVIDER).getSize(rectf);
				min_girth_start = Math.min(min_girth_start, orient.getGirthStart(rectf));
				max_girth_end = Math.max(max_girth_end, orient.getGirthEnd(rectf));
			}
			else {/*if (stretch_divider == STRETCH_FULL
				|| stretch_divider == STRETCH_GIRTH) */
				min_girth_start -= 1.5*whtspc;
				max_girth_end += 1.5*whtspc;
			}
			
			// Arrange components in series
			if (comps.size() > 1)
				addCompToSeries(comps.get(INDEX_FIRST), STRETCH_NONE);
			for (AlignForm comp : comps.subList(INDEX_FIRST+1,comps.size())) {
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
	@Override
	public <T extends DrawAligned> void subBranchShouldUsePars(
			ListTree<T> tree, int[] branch_indices, boolean[] pars_active) {
		final int length = comps.size();
		
		AlignForm subcomp;
		int leaf_number;
		if (getOrientation() == AlignAxisBase.VERTICAL) {
			// Vertical series comps need parentheses if:
			//	- has divider, comp is vertical series w/ divider,
			//		& neither divider stretches
			//	- has no divider & comp is vertical series w/o divider
			// (no need for separate cases for numbers, since there is no
			// ambiguity in vertical alignment)
			for (int i=INDEX_FIRST; i<length; ++i) {
				if (comps.get(i) instanceof AlignLeaf) {
					leaf_number = ((AlignLeaf)comps.get(i)).leaf_number;
					subcomp = tree.get(branch_indices[leaf_number]).base_comp;
					pars_active[leaf_number] = 
							subcomp instanceof AlignSeriesBase
							&& (!hasDivider() || ((AlignSeriesBase)subcomp).hasDivider())			
							&& ((AlignSeriesBase)subcomp).getOrientation()
									== getOrientation();
				} else comps.get(i).subBranchShouldUsePars(tree, branch_indices, pars_active);
			}
		}
		else for (int i=INDEX_FIRST; i<length; ++i) {
			// Horizontal series comps need parentheses:
			//	- (same cases for vertical)
			//	- when numbers are adjacent
			//	- when a negative number is preceded by an operator
			if (comps.get(i) instanceof AlignLeaf) {
				leaf_number = ((AlignLeaf)comps.get(i)).leaf_number;
				subcomp = tree.get(branch_indices[leaf_number]).base_comp;
				
				if (subcomp instanceof AlignSeriesBase) {
					pars_active[leaf_number] = (!hasDivider()
							|| ((AlignSeriesBase)subcomp).hasDivider() )
							&& ((AlignSeriesBase)subcomp).getOrientation()
							== getOrientation();
				} else if (i != INDEX_FIRST) {
					final AlignForm comp_prev = comps.get(hasDivider() ? INDEX_DIVIDER:i-1)
							.getLastInSeries(getOrientation(),
							tree.new Navigator(branch_indices[0]).toRoot());
					if (!(comp_prev instanceof DrawText)) continue;
					final AlignForm comp = subcomp
							.getFirstInSeries(getOrientation(),
							tree.new Navigator(branch_indices[0]).toRoot());
					if (!(comp instanceof DrawText)) continue;
					final char
					c1 = ((DrawText)comp_prev).text.charAt(((DrawText)comp_prev).text.length()-1),
					c2 = ((DrawText)comp).text.charAt(0);
					pars_active[leaf_number] = 
							(AlignDrawBuilder.mathOperators.indexOf(c1) != -1
									&& AlignDrawBuilder.mathOperators.indexOf(c2) != -1)
							|| (Character.isDigit(c1) && (Character.isDigit(c2)
									|| AlignDrawBuilder.mathOperators.indexOf(c2) != -1));
				}
			} else comps.get(i).subBranchShouldUsePars(tree, branch_indices, pars_active);
		}	
	}
	
	@Override
	public <T extends DrawAligned> AlignForm getFirstInSeries(
			boolean orientation, ListTree<T>.Navigator nav) {
		if (orientation != getOrientation()) return this;
		return comps.get(INDEX_FIRST).getFirstInSeries(orientation,nav);
	}
	@Override
	public <T extends DrawAligned> AlignForm getLastInSeries(
			boolean orientation, ListTree<T>.Navigator nav) {
		if (orientation != getOrientation()) return this;
		return comps.get(comps.size()-1).getLastInSeries(orientation,nav);
	}
}
