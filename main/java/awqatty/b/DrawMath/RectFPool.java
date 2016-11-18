package awqatty.b.DrawMath;

import android.graphics.RectF;

public final class RectFPool extends ResourcePool<RectF> {

	private RectFPool() {}
	@Override
	protected RectF makeNew() {
		return new RectF();
	}
	
	// Unique pool for all RectF objects
	public static final RectFPool pool = new RectFPool();
}
