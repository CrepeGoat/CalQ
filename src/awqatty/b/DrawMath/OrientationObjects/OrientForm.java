package awqatty.b.DrawMath.OrientationObjects;

import android.graphics.RectF;

public interface OrientForm {

	public static final OrientHorizontal horiz = new OrientHorizontal();
	public static final OrientVertical vert = new OrientVertical();	
	
	public boolean getOrientation();
	public static final boolean HORIZONTAL=false;
	public static final boolean VERTICAL=true;
	
	
	public float getLength(RectF rect);
	public float getGirth(RectF rect);
	public void set(RectF rect, float len1, float gir1, float len2, float gir2);

	public float getLengthStart(RectF rect);
	public void	 setLengthStart(RectF rect, float dim);
	public float getLengthEnd(RectF rect);
	public void	 setLengthEnd(RectF rect, float dim);

	public float getGirthStart(RectF rect);
	public void	 setGirthStart(RectF rect, float dim);
	public float getGirthEnd(RectF rect);
	public void	 setGirthEnd(RectF rect, float dim);
	
	public void offset(RectF rect, float dl, float dg);
	public void offsetTo(RectF rect, float newLengthStart, float newGirthStart);
}
