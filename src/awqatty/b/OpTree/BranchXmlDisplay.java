package awqatty.b.OpTree;

import java.util.List;

import awqatty.b.ListTree.BranchFunctorBase;

public class BranchXmlDisplay extends BranchFunctorBase<OpNode, String> {

	@Override
	protected String calculateNestedResult(OpNode element,
			List<String> branch_results) {
		return element.getTextPres(branch_results);
	}

}
