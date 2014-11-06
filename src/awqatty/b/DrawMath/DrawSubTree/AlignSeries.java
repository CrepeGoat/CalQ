package awqatty.b.DrawMath.DrawSubTree;

import java.util.List;

import awqatty.b.DrawMath.AssignParentheses.ClosureType;

public class AlignSeries extends AlignSeriesBase {
	
	// New Methods
	public AlignSeries(
			List<? extends AlignForm> components,
			AlignForm divider, byte stretch_type,
			boolean orientation, float whitespace, byte aligned_edges) {
		super(divider, stretch_type, orientation, whitespace, aligned_edges);
		comps.addAll(components);
	}
	
	public void addComponent(AlignForm draw) {
		comps.add(draw);
	}
	public void clearComponents() {
		comps.subList(1,comps.size()).clear();
	}
	
	@Override
	protected void decideParentheses(ClosureType[] ctypes, boolean[] pars_active) {
		ClosureType ctype_last=null;
		for (AlignForm comp : comps.subList(1,comps.size())) {
			if (comp instanceof AlignLeaf) {
				pars_active[((AlignLeaf)comp).leaf_number] = decideSingleParentheses(
						ctypes[((AlignLeaf)comp).leaf_number], ctype_last );
				ctype_last = ctypes[((AlignLeaf)comp).leaf_number];
			}
			else
				ctype_last = comp.getClosureType();
		}
		
	}

}
