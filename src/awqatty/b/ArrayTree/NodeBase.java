package awqatty.b.ArrayTree;

import awqatty.b.CustomExceptions.BranchCountException;

public abstract class NodeBase {

	private int branch_count;
	private final int branch_min;
	private final int branch_max;

	public NodeBase(int min, int max) {
		branch_count = 0;
		branch_min = min;
		branch_max = max;
	}
	public NodeBase() {
		this(0, Integer.MAX_VALUE);
	}

	public void incrementCount() throws BranchCountException {
		if (branch_count >= branch_max)
			throw new BranchCountException();
		++branch_count;
	}
	public void decrementCount() throws BranchCountException {
		if (branch_count <= branch_min)
			throw new BranchCountException();
		--branch_count;
	}
	
	public int getBranchCount() {
		return branch_count;
	}

}
