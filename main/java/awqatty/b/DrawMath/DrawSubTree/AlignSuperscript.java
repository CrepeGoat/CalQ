package awqatty.b.DrawMath.DrawSubTree;

import java.util.Arrays;
import java.util.List;

import android.graphics.RectF;
import awqatty.b.ListTree.ListTree;

public final class AlignSuperscript extends AlignBase {

	public static final float EXP_SCALE = 0.75f;
	
	private static final byte INDEX_BASE = 0;
	private static final byte INDEX_EXP = 1;
	private final List<AlignForm> comps;
	private List<RectF> locs=null;
	
	public AlignSuperscript(
			AlignForm component_base,
			AlignForm component_exponent) {
		final AlignForm[] list = new AlignForm[2];
		list[INDEX_BASE] = component_base;
		list[INDEX_EXP] = component_exponent;
		comps = Arrays.asList(list);
	}

	@Override
	protected Iterable<AlignForm> iterComps() {return comps;}
	@Override
	protected Iterable<AlignForm> iterCompsWithLoc() {return comps;}
	@Override
	protected Iterable<RectF> iterLocs() {return locs;}

	@Override
	protected void arrange() {
		if (valid_area == null)
			valid_area = new RectF();
		if (locs == null) {
			final RectF[] list = new RectF[2];
			list[0] = new RectF();
			list[1] = new RectF();
			locs = Arrays.asList(list);
		}
		
		final RectF base = locs.get(INDEX_BASE),
				exp = locs.get(INDEX_EXP);
		comps.get(INDEX_BASE).getSize(base);
		comps.get(INDEX_EXP).getSize(exp);
		
		if (exp.height()*EXP_SCALE > base.height()) {
			// Sets bottom of exp to base centerline (change to 3/4line?)
			exp.set(base.right,
					(base.top+base.bottom)/2f - (exp.height()*EXP_SCALE),
					base.right + (exp.width()*EXP_SCALE),
					(base.top+base.bottom)/2f );
		} else {
			// Centers exp on base.top line
			exp.set(base.right,
					base.top - (exp.height()*EXP_SCALE/2f),
					base.right + (exp.width()*EXP_SCALE),
					base.top + (exp.height()*EXP_SCALE/2f) );
		}
		valid_area.set(base.left, exp.top, exp.right, base.bottom);
	}
	
	//--- Manage Parentheses ---
	@Override
	public <T extends DrawAligned> void subBranchShouldUsePars(
			ListTree<T> tree, int[] branch_indices, boolean[] pars_active) {
		pars_active[INDEX_BASE] = (tree.get(branch_indices[INDEX_BASE])
				.base_comp instanceof AlignSeriesBase);
		pars_active[INDEX_EXP] = (tree.get(branch_indices[INDEX_EXP])
				.base_comp instanceof AlignSuperscript);
	}

	@Override
	public <T extends DrawAligned> AlignForm getFirstInSeries(
			boolean orientation, ListTree<T>.Navigator nav) {
		if (orientation == AlignAxisBase.VERTICAL) return this;
		else return comps.get(INDEX_BASE).getFirstInSeries(orientation,nav);
	}

	@Override
	public <T extends DrawAligned> AlignForm getLastInSeries(
			boolean orientation, ListTree<T>.Navigator nav) {
		if (orientation == AlignAxisBase.HORIZONTAL) return this;
		else return comps.get(INDEX_BASE).getLastInSeries(orientation,nav);
	}

}
