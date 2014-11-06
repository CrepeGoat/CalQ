package awqatty.b.DrawMath.DrawSubTree;

import java.util.Arrays;
import java.util.List;

import android.graphics.RectF;
import awqatty.b.DrawMath.AssignParentheses.ClosureType;

public class AlignSuperscript extends AlignBase {

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
		
		exp.set(base.width(), 0,
				exp.width()*EXP_SCALE + base.width(),
				exp.height()*EXP_SCALE );
		base.offsetTo(0, Math.max(
				exp.height()-(base.height()/4),
				exp.height()/2 ));
		valid_area.set(0,0, exp.right, base.bottom);
	}
	
	//--- Manage Parentheses ---
	@Override
	public ClosureType getClosureType() {
		return ClosureType.SUBSUPERSCRIPT;
	}
	@Override
	protected void decideParentheses(ClosureType[] ctypes, boolean[] pars_active) {
		if (comps.get(INDEX_BASE) instanceof AlignLeaf) {
			int leaf_num = ((AlignLeaf)comps.get(INDEX_BASE)).leaf_number;
			pars_active[leaf_num] =
					!(ctypes[leaf_num] == ClosureType.TEXT_ALPHA ||
					ctypes[leaf_num] == ClosureType.TEXT_NUMERIC_POS ||
					ctypes[leaf_num] == ClosureType.BOUNDED );
		}
		if (comps.get(INDEX_EXP) instanceof AlignLeaf) {
			pars_active[((AlignLeaf)comps.get(INDEX_EXP)).leaf_number] = false;
		}
	}

}
