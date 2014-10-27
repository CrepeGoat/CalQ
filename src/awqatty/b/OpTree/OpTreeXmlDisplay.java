package awqatty.b.OpTree;

import java.util.List;

import awqatty.b.ListTree.DataLoopLeafUp;

public class OpTreeXmlDisplay extends DataLoopLeafUp<Operation, String> {

	private byte pres_style;
	
	public OpTreeXmlDisplay(byte i) {
		super();
		pres_style = i;
	}
	

	@Override
	protected String loopAtNode(Operation node, List<String> stack) {
		return node.getTextPres(stack, pres_style);
	}

}
