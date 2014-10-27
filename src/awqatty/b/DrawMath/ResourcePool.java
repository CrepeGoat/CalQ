package awqatty.b.DrawMath;

import java.util.Collection;
import java.util.Stack;

abstract public class ResourcePool<T> {

	private final Stack<T> pool = new Stack<T>();
	
	// Resource Management
	//	Needs synchronized; two isEmpty() checks could occur
	//	before one pop() operation
	public synchronized T getResource() {
		return (pool.isEmpty() ? makeNew() : pool.pop());
	}
	abstract protected T makeNew();
	
	// 	Does not need synchronized modifier;
	//	This ensures a null call does not delay other calls
	public void store(T t) {
		if (t != null) pool.addElement(t);
	}
	public void storeAll(Collection<T> col) {
		for (T t : col)
			store(t);
		col.clear();
	}
	
	public void clearResources() {
		pool.clear();
	}
}
