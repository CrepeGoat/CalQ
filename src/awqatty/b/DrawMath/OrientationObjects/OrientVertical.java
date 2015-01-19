package awqatty.b.DrawMath.OrientationObjects;

import android.graphics.RectF;

public final class OrientVertical implements OrientForm {
	
	@Override
	public boolean getOrientation() {
		return VERTICAL;
	}
	// ------------
	@Override
	public float getLength(RectF rect) {
		return rect.height();
	}
	@Override
	public float getGirth(RectF rect) {
		return rect.width();
	}
	@Override
	public void set(RectF rect, float len1, float gir1, float len2, float gir2) {
		rect.set(gir1, len1, gir2, len2);
	}

	// ------------
	@Override
	public float getLengthStart(RectF rect) {
		return rect.top;
	}
	@Override
	public void setLengthStart(RectF rect, float dim) {
		rect.top = dim;
	}
	
	@Override
	public float getLengthEnd(RectF rect) {
		return rect.bottom;
	}
	@Override
	public void setLengthEnd(RectF rect, float dim) {
		rect.bottom = dim;
	}

	// ------------
	@Override
	public float getGirthStart(RectF rect) {
		return rect.left;
	}
	@Override
	public void setGirthStart(RectF rect, float dim) {
		rect.left = dim;
	}

	@Override
	public float getGirthEnd(RectF rect) {
		return rect.right;
	}
	@Override
	public void setGirthEnd(RectF rect, float dim) {
		rect.right = dim;
	}

	// ------------
	@Override
	public void offset(RectF rect, float dl, float dg) {
		rect.offset(dg, dl);
	}
	@Override
	public void offsetTo(RectF rect, float newLengthStart, float newGirthStart) {
		rect.offsetTo(newGirthStart, newLengthStart);
	}

}
