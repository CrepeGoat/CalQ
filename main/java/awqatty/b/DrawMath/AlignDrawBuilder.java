package awqatty.b.DrawMath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import awqatty.b.DrawMath.AlignDrawParts.AlignDraw;
import awqatty.b.DrawMath.AlignDrawParts.AlignSuperscript;
import awqatty.b.DrawMath.AlignDrawParts.AlignLeaf;
import awqatty.b.DrawMath.AlignDrawParts.AlignForm;
import awqatty.b.DrawMath.AlignDrawParts.Builders.AlignLeafSeriesBuilder;
import awqatty.b.DrawMath.AlignDrawParts.Builders.AlignSeriesBuilder;
import awqatty.b.DrawMath.AlignDrawParts.Utilities.AlignmentEdge;
import awqatty.b.DrawMath.AlignDrawParts.Utilities.OrientForm;
import awqatty.b.DrawMath.AlignDrawParts.Utilities.StretchType;
import awqatty.b.DrawMath.DrawToCanvas.DrawBlank;
import awqatty.b.DrawMath.DrawToCanvas.DrawPath;
import awqatty.b.DrawMath.DrawToCanvas.DrawText;
import awqatty.b.DrawMath.DrawToCanvas.DrawTextCap;
import awqatty.b.FunctionDictionary.FunctionType;
import awqatty.b.GUI.NumberStringConverter;
import awqatty.b.calq.R;

public final class AlignDrawBuilder {

	private static float GROUP_SPACING;
	private static float LETTER_SPACING;
	private static int LINE_THICKNESS;
	private static int COLOR_LOWLIGHT;
	
	// Initializes all static values dependent on context
	//		(called in constructor of every instance)
	public static final void setDensity(Context context) {
		float density = context.getResources().getDisplayMetrics().density;
		DrawText.setDensity(density);
		COLOR_LOWLIGHT = context.getResources().getColor(R.color.light_gray);
		synchronized(AlignDrawBuilder.class) {
			GROUP_SPACING = 15 * density;
			LETTER_SPACING = 4.5f * density;
			LINE_THICKNESS = (int) (3 * density);
		}
	}
	
	public AlignDrawBuilder(Context context) {
		setDensity(context);
	}
	
	private double number;
	public AlignDrawBuilder number(double number) {
		this.number = number;
		return this;
	}

	private boolean resultIsValid;
	public AlignDrawBuilder resultIsValid(boolean resultIsValid) {
		this.resultIsValid = resultIsValid;
		return this;
	}

	/*
	private boolean selectionIsSubBranch;
	public AlignDrawBuilder selectionIsSubBranch(boolean selectionIsSubBranch) {
		this.selectionIsSubBranch = selectionIsSubBranch;
		return this;
	}
	//*/

	/**************************************************
	 * Draw Numbers in Grouped Scale
	 */
	private static final float scale = 0.6f;
	private static final float min_scale = (float) (1/Math.sqrt(2));
											// -> Area2 = (1/2)*Area1
	private static void getDrawSeriesFromNumTextSeries(
			List<AlignForm> series,
			List<String> list_num) 
	{
		final int length = list_num.size();
		int i;
		DrawText comp;
		for (i=0; i<length; ++i) {
			comp = new DrawText(list_num.get(i));
			comp.setScale((float)
					(min_scale + (1-min_scale)*Math.pow(scale,i)) );
			series.add(new AlignDraw(comp));
		}
	}
	
	private static void getNumDraw(
			double number, 
			List<AlignForm> series_sigFig,
			List<AlignForm> series_mag
	) {
		final String str_num = NumberStringConverter.toReducedString(number);
		final int str_length = str_num.length();
		final ArrayList<String> list = new ArrayList<>();
		
		int index_dec = str_num.indexOf(".");
		int index_E = str_num.indexOf("E", index_dec);
		if (index_E == -1) index_E = str_length;
		
		if (index_dec == -1) {
			// index.e. non-decimal number
			groupWholeNum(str_num.substring(0,index_E), list);
		}
		else {
			groupWholeNum(str_num.substring(0,index_dec), list);
			final int index = list.size();
			groupDecimalNum(str_num.substring(index_dec+1, index_E),
					list.subList(list.size(), list.size()) );
			list.set(index, "."+list.get(index));
		}
		getDrawSeriesFromNumTextSeries(series_sigFig, list);
		list.clear();
		if (index_E != str_length) {
			list.add("E");
			groupWholeNum(str_num.substring(index_E+1), list);
		}
		getDrawSeriesFromNumTextSeries(series_mag, list);
	}
	// TODO change to parse based on regional separators (e.g., ',' '.' ' ' etc.)
	private static void groupWholeNum(String str, List<String> list) {
		final int length = str.length();
		
		final byte start = (byte) (str.startsWith("-") ? 1 : 0);
		
		int index = ((length-start)%3) + start;
		if (index > start)
			list.add(str.substring(start, index));
		index += 3;
		for (; index<=length; index+=3) {
			list.add(str.substring(index-3, index));
		}
		
		if (start == 1)
			list.set(0, "-" + list.get(0));
	}
	private static void groupDecimalNum(String str, List<String> list) {
		final int length = str.length();
		
		int index = length - ( 2 + ((length+1)%3) );
		list.add(0, str.substring(Math.max(0, index), length));
		index-=3;
		for (; index>=0; index-=3) {
			list.add(0, str.substring(index, index+3));
		}
	}
	
	/**
	 * Build Method
	 */	
	@SuppressWarnings("unchecked")
	public AlignForm build(FunctionType ftype) {
		Path path;
		AlignForm nested_comp;
		String text;
		switch (ftype) {
			case SOURCE:
				if (resultIsValid) {
					List<AlignForm> list_align = new ArrayList(Arrays.asList(
							new AlignDraw(new DrawTextCap("=")),
							build(FunctionType.NUMBER)
					));
					//if (selectionIsSubBranch) {
					//	list_align.add(0,new DrawTextCap("selection"));
					//}
					nested_comp = new AlignSeriesBuilder(list_align)
							.orientation(OrientForm.HORIZONTAL)
							.aligned_edge(AlignmentEdge.CENTER)
							.whitespaceBetweenSeries(GROUP_SPACING)
							.build();
				} else {
					final DrawText draw_text = new DrawText("(expression incomplete)");
					// Set text to be less significant (smaller, gray)
					draw_text.setScale(0.75f);
					draw_text.setColor(COLOR_LOWLIGHT);
					nested_comp = new AlignDraw(draw_text);
				}
				return new AlignSeriesBuilder(
						Arrays.asList(
								new AlignLeaf(0),
								nested_comp
						))
						.orientation(OrientForm.VERTICAL)
						.aligned_edge(AlignmentEdge.CENTER)
						.whitespaceBetweenSeries(2*GROUP_SPACING)
						.build();
			case BLANK:
				//return new DrawText("\u25FC"); -> square
				// V substitute character V
				//return new DrawText("\u001A");
				return new AlignDraw(new DrawText("\u25AF"));
			case RAW_TEXT:
				return new AlignDraw(new DrawText("0"));
			case NUMBER:
				// Case for infinite values
				if (number == Double.POSITIVE_INFINITY) {
					return new AlignDraw(new DrawText("\u221E"));
				}
				else if (number == Double.NEGATIVE_INFINITY) {
					return new AlignDraw(new DrawText("-\u221E"));
				}
				// Case for finite values
				List<AlignForm> series_sigFig = new ArrayList<>();
				List<AlignForm> series_mag = new ArrayList<>();
				getNumDraw(number, series_sigFig, series_mag);

				if (series_sigFig.size() == 1 && series_mag.isEmpty())
					return series_sigFig.get(0);
				else {
					List<AlignForm> tmp = new ArrayList<>();
					tmp.add(new AlignSeriesBuilder(series_sigFig)
							.aligned_edge(AlignmentEdge.BOTTOM_RIGHT)
							.whitespaceBetweenSeries(LETTER_SPACING)
							.build() );

					if (series_mag.isEmpty())
						return tmp.get(0);
					else {
						tmp.add(new AlignSeriesBuilder(series_mag)
								.aligned_edge(AlignmentEdge.TOP_LEFT)
								.whitespaceBetweenSeries(LETTER_SPACING)
								.build() );
						return new AlignSeriesBuilder(tmp)
								.aligned_edge(AlignmentEdge.CENTER)
								.whitespaceBetweenSeries(LETTER_SPACING)
								.build();
					}
				}

			// Standard Functions
			case ADD:
				return new AlignLeafSeriesBuilder()
						.divider(new AlignDraw(new DrawTextCap("+")))
						.stretch_divider(StretchType.NONE)
						.aligned_edge(AlignmentEdge.CENTER)
						.whitespaceBetweenSeries(GROUP_SPACING)
						.build();
			case SUBTRACT:
				return new AlignLeafSeriesBuilder()
						.divider(new AlignDraw(new DrawTextCap("\u2212")))
						.stretch_divider(StretchType.NONE)
						.aligned_edge(AlignmentEdge.CENTER)
						.whitespaceBetweenSeries(GROUP_SPACING)
						.build();
			case MULTIPLY:
				return new AlignLeafSeriesBuilder()
						/* Explicit multiplication symbols (best for simple clicking)
						.divider(new DrawTextCap("\u2715"))
						.stretch_divider(AlignAxisBase.STRETCH_NONE)
						.whitespaceBetweenSeries(GROUP_SPACING)
						/*/// Implicit adjacency multiplication (works w/ drag select)
						.whitespaceBetweenSeries(LETTER_SPACING)
						//*/
						.aligned_edge(AlignmentEdge.CENTER)
						.build();
			case DIVIDE:
				path = new Path();
				path.addRect(new RectF(0,0,1,LINE_THICKNESS),
						Path.Direction.CCW );
				return new AlignLeafSeriesBuilder()
						.divider(new AlignDraw(new DrawPath(
								path,
								new Paint(Paint.ANTI_ALIAS_FLAG),
								new RectF(
										0,-GROUP_SPACING/2f,
										1, LINE_THICKNESS+(GROUP_SPACING/2f)
								)
						)))
						//.divider(new DrawText("."))
						.stretch_divider(StretchType.GIRTH)
						.orientation(OrientForm.VERTICAL)
						.aligned_edge(AlignmentEdge.CENTER)
						.whitespaceBetweenSeries(GROUP_SPACING/3f)
						.whitespacePaddingStretch(2*LETTER_SPACING)
						.build();

			case NEGATIVE:
				return new AlignSeriesBuilder(Arrays.asList(
						new AlignDraw(new DrawTextCap("-")),
						new AlignLeaf(0)
						))
						.aligned_edge(AlignmentEdge.CENTER)
						.whitespaceBetweenSeries(LETTER_SPACING)
						.build();
			case ABS:
				path = new Path();
				path.addRect(new RectF(0,0,LINE_THICKNESS,LINE_THICKNESS),
						Path.Direction.CCW
				);
				nested_comp = new AlignDraw(new DrawPath(path, new Paint(Paint.ANTI_ALIAS_FLAG)));
				return new AlignSeriesBuilder(Arrays.asList(
						nested_comp,
						new AlignLeaf(0),
						nested_comp
						))
						.stretchType(0,StretchType.GIRTH)
						.stretchType(2,StretchType.GIRTH)
						.aligned_edge(AlignmentEdge.CENTER)
						.whitespaceBetweenSeries(LETTER_SPACING)
						.whitespacePaddingStretch(LETTER_SPACING)
						.build();

			// Power Functions
			case POWER:
			case SQUARE:
			case MULT_INVERSE:
			case EXP_E:
			case EXP_10:
				return new AlignSuperscript(new AlignLeaf(0), new AlignLeaf(1));
			case SQRT:
				path = new Path();
				path.addRect(
						new RectF(0,0,LINE_THICKNESS,LINE_THICKNESS),
						Path.Direction.CCW
				);
				final DrawText draw_sqrt = new DrawText("\u221A");	//sqrt symbol
				draw_sqrt.setScale(2);
				return new AlignSeriesBuilder(Arrays.asList(
						new AlignDraw(draw_sqrt),
						new AlignSeriesBuilder(Arrays.asList(
								new AlignDraw(new DrawPath(
										path,
										new Paint(Paint.ANTI_ALIAS_FLAG)
								)),
								new AlignLeaf(0)
								))
								//.end_bound(new DrawBlank())
								.stretchType(0,StretchType.GIRTH)
								.orientation(OrientForm.VERTICAL)
								.aligned_edge(AlignmentEdge.CENTER)
								.whitespaceBetweenSeries(GROUP_SPACING)
								.whitespacePaddingStretch(2*LETTER_SPACING)
								.build()
						))
						.stretchType(0,StretchType.GIRTH)
						.orientation(OrientForm.HORIZONTAL)
						.aligned_edge(AlignmentEdge.CENTER)
						.whitespaceBetweenSeries(-LETTER_SPACING/2)
						.whitespacePaddingStretch(0)
						.build();

			// Text-Represented Functions
			// Log Functions
			case LN:
				text = "ln";
				nested_comp = new AlignLeaf(0);
				break;
			case LOG10:
				text = "log";
				nested_comp = new AlignLeaf(0);
				break;
			// Trig Functions
			case SINE:
				text = "sin";
				nested_comp = new AlignLeaf(0);
				break;
			case COSINE:
				text = "cos";
				nested_comp = new AlignLeaf(0);
				break;
			case TANGENT:
				text = "tan";
				nested_comp = new AlignLeaf(0);
				break;
			case ARCSINE:
				text = "arcsin";
				nested_comp = new AlignLeaf(0);
				break;
			case ARCCOSINE:
				text = "arccos";
				nested_comp = new AlignLeaf(0);
				break;
			case ARCTANGENT:
				text = "arctan";
				nested_comp = new AlignLeaf(0);
				break;
			// Hyperbolic Functions
			case HYPSINE:
				text = "sinh";
				nested_comp = new AlignLeaf(0);
				break;
			case HYPCOSINE:
				text = "cosh";
				nested_comp = new AlignLeaf(0);
				break;
			case HYPTANGENT:
				text = "tanh";
				nested_comp = new AlignLeaf(0);
				break;
			case ARHYPSINE:
				text = "arcsinh";
				nested_comp = new AlignLeaf(0);
				break;
			case ARHYPCOSINE:
				text = "arccosh";
				nested_comp = new AlignLeaf(0);
				break;
			case ARHYPTANGENT:
				text = "arctanh";
				nested_comp = new AlignLeaf(0);
				break;

			// Probability
			case FACTORIAL:
				return new AlignSeriesBuilder(Arrays.asList(
						new AlignLeaf(0),
						new AlignDraw(new DrawText("!"))
						))
						.orientation(OrientForm.HORIZONTAL)
						.aligned_edge(AlignmentEdge.CENTER)
						.whitespaceBetweenSeries(LETTER_SPACING)
						.whitespacePaddingStretch(-LETTER_SPACING)
						.build();
			case NCK:
				return buildParentheses(
						new AlignLeafSeriesBuilder()
								.orientation(OrientForm.VERTICAL)
								.aligned_edge(AlignmentEdge.CENTER)
								.whitespaceBetweenSeries(GROUP_SPACING)
								.build() );
			case NPK:
				return new AlignLeafSeriesBuilder()
						.divider(new AlignDraw(new DrawText("P")))
						.aligned_edge(AlignmentEdge.CENTER)
						.whitespaceBetweenSeries(LETTER_SPACING)
						.build();

			// Integer Arithmetic
			case REMAINDER:
				return new AlignLeafSeriesBuilder()
						.divider(new AlignDraw(new DrawTextCap("mod")))
						.aligned_edge(AlignmentEdge.CENTER)
						.whitespaceBetweenSeries(LETTER_SPACING)
						.build();
			case GCD:
				text = "gcd";
				nested_comp = new AlignLeafSeriesBuilder()
						.divider(new AlignSeriesBuilder(Arrays.asList(
								new AlignDraw(new DrawTextCap(",")),
								new AlignDraw(new DrawBlank())
								))
								.orientation(OrientForm.HORIZONTAL)
								.whitespaceBetweenSeries(LETTER_SPACING)
								.build()
						)
						.aligned_edge(AlignmentEdge.CENTER)
						.whitespaceBetweenSeries(LETTER_SPACING)
						.build();
				break;
			case LCM:
				text = "lcm";
				nested_comp = new AlignLeafSeriesBuilder()
						.divider(new AlignSeriesBuilder(Arrays.asList(
								new AlignDraw(new DrawTextCap(",")),
								new AlignDraw(new DrawBlank())
								))
								.orientation(OrientForm.HORIZONTAL)
								.whitespaceBetweenSeries(LETTER_SPACING)
								.build()
						)
						.aligned_edge(AlignmentEdge.CENTER)
						.whitespaceBetweenSeries(LETTER_SPACING)
						.build();
				break;

			// Constants
			case CONST_E:
				return new AlignDraw(new DrawTextCap("e"));
			case CONST_PI:
				return new AlignDraw(new DrawTextCap("\u03C0"));
			default:
				return null;
		}
		
		// Use below logic for common text-parentheses styled representations
		// i.e., cos(x), gcd(x,y), etc.
		return new AlignSeriesBuilder(Arrays.asList(
				new AlignDraw(new DrawTextCap(text)),
				new AlignDraw(new DrawText("(")),
				nested_comp,
				new AlignDraw(new DrawText(")"))
				))
				.stretchType(1,StretchType.GIRTH)
				.stretchType(3,StretchType.GIRTH)
				.aligned_edge(AlignmentEdge.CENTER)
				.whitespaceBetweenSeries(LETTER_SPACING)
				.whitespacePaddingStretch(LETTER_SPACING)
				.build();
	}

	//*
	// TODO make parentheses not look like garbage
	public static AlignForm buildParentheses(AlignForm core) {
		return new AlignSeriesBuilder(Arrays.asList(
				new AlignDraw(new DrawText("(")),
				core,
				new AlignDraw(new DrawText(")"))
				))
				.stretchType(0,StretchType.GIRTH)
				.stretchType(2,StretchType.GIRTH)
				.orientation(OrientForm.HORIZONTAL)
				.aligned_edge(AlignmentEdge.CENTER)
				.whitespaceBetweenSeries(LETTER_SPACING)
				.build();
	}
	//*/
}
