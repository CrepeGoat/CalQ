package awqatty.b.ListTree;

import java.util.List;

import awqatty.b.CustomExceptions.CalculationException;

// T - object (i.e. nodes)
// U - object method return type
public interface BranchFunction<T,U> {
	
	public U calculate(T object, List<U> list) throws CalculationException;

}
