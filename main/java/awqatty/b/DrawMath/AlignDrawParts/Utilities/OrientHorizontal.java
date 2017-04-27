package awqatty.b.DrawMath.AlignDrawParts.Utilities;

import android.graphics.RectF;

public final class OrientHorizontal implements OrientForm {

	@Override
	public boolean getOrientation() {
		return HORIZONTAL;
	}

	// ------------
	@Override
	public float getLength(RectF rectf) {
		return rectf.width();
	}
	@Override
	public float getGirth(RectF rectf) {
		return rectf.height();
	}
	@Override
	public void set(RectF rectf, float len1, float gir1, float len2, float gir2) {
		rectf.set(len1, gir1, len2, gir2);
	}

	// ------------
	@Override
	public float getLengthStart(RectF rectf) {
		return rectf.left;
	}
	@Override
	public void setLengthStart(RectF rectf, float dim) {
		rectf.left = dim;
	}

	@Override
	public float getLengthEnd(RectF rectf) {
		return rectf.right;
	}
	@Override
	public void setLengthEnd(RectF rectf, float dim) {
		rectf.right = dim;
	}

	// ------------
	@Override
	public float getGirthStart(RectF rectf) {
		return rectf.top;
	}
	@Override
	public void setGirthStart(RectF rectf, float dim) {
		rectf.top = dim;
	}

	@Override
	public float getGirthEnd(RectF rectf) {
		return rectf.bottom;
	}
	@Override
	public void setGirthEnd(RectF rectf, float dim) {
		rectf.bottom = dim;
	}

	@Override
	public void offset(RectF rectf, float dl, float dg) {
		rectf.offset(dl, dg);
	}
	@Override
	public void offsetTo(RectF rectf, float newLengthStart, float newGirthStart) {
		rectf.offsetTo(newLengthStart, newGirthStart);
	}
}
