package awqatty.b.OpTree;

import java.util.List;

import awqatty.b.ArrayTree.BranchFunction;

public class BranchDisplay implements BranchFunction<OpNode, String> {

	public String calculate(OpNode node, List<String> slist) {
		return node.getTextPres(slist);
	}

}
