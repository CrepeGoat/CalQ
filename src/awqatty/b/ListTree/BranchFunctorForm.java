package awqatty.b.ListTree;

import java.util.List;

public interface BranchFunctorForm<N,U> {
	
	public void excecuteInLoop(N element);
	
	public List<U> returnValues();
	
}
