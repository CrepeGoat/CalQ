package awqatty.b.DrawMath;

import java.util.Arrays;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import awqatty.b.DrawMath.DrawSubTree.AlignAxisBase;
import awqatty.b.DrawMath.DrawSubTree.AlignNumberSegments;
import awqatty.b.DrawMath.DrawSubTree.AlignSuperscript;
import awqatty.b.DrawMath.DrawSubTree.AlignLeaf;
import awqatty.b.DrawMath.DrawSubTree.AlignForm;
import awqatty.b.DrawMath.DrawSubTree.Builders.AlignBorderBuilder;
import awqatty.b.DrawMath.DrawSubTree.Builders.AlignLeafSeriesBuilder;
import awqatty.b.DrawMath.DrawSubTree.Builders.AlignSeriesBuilder;
import awqatty.b.DrawMath.DrawToCanvas.DrawPath;
import awqatty.b.DrawMath.DrawToCanvas.DrawText;
import awqatty.b.DrawMath.DrawToCanvas.DrawTextCap;
import awqatty.b.DrawMath.DrawToCanvas.RawDrawBase;
import awqatty.b.FunctionDictionary.FunctionType;

public class AlignDrawBuilder {
	
	public static final String bracketsOpen = "([{|";
	public static final String bracketsClose = ")]}|";
	public static final String mathOperators = "+-\u2212\u2715";

	
	private static float GROUP_SPACING;
	private static float LETTER_SPACING;
	private static int LINE_THICKNESS;
	
	// Initializes all static values dependent on screen density
	//		(called in constructor of every instance)
	public static final void setDensity(Context context) {
		float density = context.getResources().getDisplayMetrics().density;
		synchronized(AlignDrawBuilder.class) {
			GROUP_SPACING = 15 * density;
			LETTER_SPACING = 4.5f * density;
			LINE_THICKNESS = (int) (3 * density);
		}
		DrawText.setDensity(density);
		AlignNumberSegments.setSpacing(LETTER_SPACING);
		//RawDrawBase.TOUCH_PADDING = LETTER_SPACING;
	}
	
	public AlignDrawBuilder(Context context) {
		setDensity(context);
	}
	
	private double _number;
	public AlignDrawBuilder number(double number) {
		_number = number;
		return this;
	}
	
	
	/*
	 * Build-Number Helper Methods
	 * 
	 */
	/*
	private static final float scale_decay = 0.6f;
	private static final float min_scale = (float) (1/Math.sqrt(2));
											// -> Area2 = (1/2)*Area1	
	private static void getNumberDraw(
			double number, 
			List<DrawText> series_sigFig,
			List<DrawText> series_mag ) {
		final String str_num = NumberStringConverter.toString(number);
		final int str_length = str_num.length();
		final ArrayList<String> list = new ArrayList<String>();
		
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
		getDrawNumText(series_sigFig, list);
		list.clear();
		if (index_E != str_length) {
			list.add("E");
			groupWholeNum(str_num.substring(index_E+1), list);
		}
		getDrawNumText(series_mag, list);
	}
	private static void getDrawNumText(
			List<DrawText> series, 
			List<String> list_num) 
	{
		final int length = list_num.size();
		int i;
		DrawText comp;
		for (i=0; i<length; ++i) {
			comp = new DrawText(list_num.get(i));
			comp.setScale((float)
					(min_scale + (1-min_scale)*Math.pow(scale_decay,i)) );
			series.add(comp);
		}
	}
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
	//*/
	
	/**
	 * Build Method
	 */	
	public AlignForm build(FunctionType ftype) {
		Path path;
		String text=null;
		AlignForm nested_comp=null;
		
		switch (ftype) {
		case BLANK:
			//return new DrawText("\u25FC"); -> square
			// V substitute character V
			//return new DrawText("\u001A");
			return new DrawText("\u25AF");
		case NUMBER:
			// Case for infinite values
			if (_number == Double.POSITIVE_INFINITY) {
				return new DrawTextCap("\u221E");
			}
			else if (_number == Double.NEGATIVE_INFINITY) {
				return new DrawTextCap("-\u221E");
			}
			// Case for finite values
			/*
			List<DrawText> seriesSigFig = new ArrayList<DrawText>();
			List<DrawText> seriesMag = new ArrayList<DrawText>();
			getNumberDraw(_number, seriesSigFig, seriesMag);
			
			if (seriesMag.isEmpty()) {
				if (seriesSigFig.size() == 1)
					return seriesSigFig.get(0);
				else return new AlignSeriesBuilder(seriesSigFig)
				.orientation(AlignSeries.HORIZONTAL)
				.aligned_edge(AlignSeries.EDGE_BOTTOM)
				.whitespace(LETTER_SPACING)
				.build();
			}
			else return new AlignSeriesBuilder(Arrays.asList(
					new AlignSeriesBuilder(seriesSigFig)
					.orientation(AlignSeries.HORIZONTAL)
					.aligned_edge(AlignSeries.EDGE_BOTTOM)
					.whitespace(LETTER_SPACING)
					.build(),
					new AlignSeriesBuilder(seriesMag)
					.orientation(AlignSeries.HORIZONTAL)
					.aligned_edge(AlignSeries.EDGE_TOP)
					.whitespace(LETTER_SPACING)
					.build()
			))
			.orientation(AlignSeries.HORIZONTAL)
			.whitespace(LETTER_SPACING)
			.build();
			/*/
			return AlignNumberSegments.buildDrawNumber(_number);
			//*/

		// Standard Functions
		case ADD:
			return new AlignLeafSeriesBuilder()
			.divider(new DrawTextCap("+"))
			.stretch_divider(AlignAxisBase.STRETCH_NONE)
			.aligned_edge(AlignAxisBase.EDGE_ORIGIN)
			.whitespace(GROUP_SPACING)
			.build();
		case SUBTRACT:
			return new AlignLeafSeriesBuilder()
			.divider(new DrawTextCap("\u2212"))
			.stretch_divider(AlignAxisBase.STRETCH_NONE)
			.aligned_edge(AlignAxisBase.EDGE_ORIGIN)
			.whitespace(GROUP_SPACING)
			.build();
		case MULTIPLY:
			return new AlignLeafSeriesBuilder()
			/* Explicit multiplication symbols (best for simple clicking)
			.divider(new DrawTextCap("\u2715"))
			.stretch_divider(AlignAxisBase.STRETCH_NONE)
			.whitespace(GROUP_SPACING)
			/*/// Implicit adjacency multiplication (works w/ drag select)
			.whitespace(LETTER_SPACING)
			//*/
			.aligned_edge(AlignAxisBase.EDGE_CENTER)
			.build();
		case DIVIDE:
			path = new Path();
			path.addRect(new RectF(0,0,1,LINE_THICKNESS),
					Path.Direction.CCW );
			return new AlignSeriesBuilder(Arrays.asList(
					new AlignLeaf(0), new AlignLeaf(1)))
			.divider(new DrawPath(path,new Paint(Paint.ANTI_ALIAS_FLAG),
					new RectF(0,-GROUP_SPACING/2f,
							1, LINE_THICKNESS+(GROUP_SPACING/2f) )))
			//.divider(new DrawText("."))
			.stretch_divider(AlignAxisBase.STRETCH_GIRTH)
			.orientation(AlignAxisBase.VERTICAL)
			.aligned_edge(AlignAxisBase.EDGE_CENTER)
			.whitespace(GROUP_SPACING/2f)
			.build();
			
		case NEGATIVE:
			return new AlignSeriesBuilder(Arrays.asList(
			new DrawTextCap("-"), new AlignLeaf(0) ))
			.aligned_edge(AlignAxisBase.EDGE_CENTER)
			.whitespace(LETTER_SPACING)
			.build();
		case ABS:
			path = new Path();
			path.addRect(new RectF(0,0,LINE_THICKNESS,LINE_THICKNESS),
					Path.Direction.CCW );
			nested_comp = new DrawPath(path, new Paint(Paint.ANTI_ALIAS_FLAG));
			return new AlignBorderBuilder(new AlignLeaf(0))
			.start_bound(nested_comp)
			.end_bound(nested_comp)
			.bound_stretch(AlignAxisBase.STRETCH_GIRTH)
			.aligned_edge(AlignAxisBase.EDGE_CENTER)
			.whitespace(LETTER_SPACING)
			.build();

		// Power Functions
		case POWER:
		case SQUARE:
		case MULT_INVERSE:
		case EXP_E:
		case EXP_10:
			return new AlignSuperscript(
					new AlignLeaf(0), new AlignLeaf(1));
		case SQRT:
			path = new Path();
			path.addRect(new RectF(0,0,LINE_THICKNESS,LINE_THICKNESS),
					Path.Direction.CCW );
			DrawText draw = new DrawText("\u221A");	//sqrt
			draw.setScale(2);
			return new AlignBorderBuilder(
					new AlignBorderBuilder(new AlignLeaf(0))
					.start_bound(new DrawPath(path,
							new Paint(Paint.ANTI_ALIAS_FLAG) ))
					//.end_bound(new DrawBlank())
					.bound_stretch(AlignAxisBase.STRETCH_GIRTH)
					.orientation(AlignAxisBase.VERTICAL)
					.aligned_edge(AlignAxisBase.EDGE_CENTER)
					.whitespace(GROUP_SPACING)
					.build())
			.start_bound(draw)
			.bound_stretch(AlignAxisBase.STRETCH_GIRTH)
			.orientation(AlignAxisBase.HORIZONTAL)
			.aligned_edge(AlignAxisBase.EDGE_CENTER)
			//.whitespace(-letter_spacing)
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
			return new AlignBorderBuilder(new AlignLeaf(0))
			.end_bound(new DrawTextCap("!"))
			.bound_stretch(AlignAxisBase.STRETCH_NONE)
			.aligned_edge(AlignAxisBase.EDGE_CENTER)
			.whitespace(LETTER_SPACING)
			.build();
		case NCK:
			return buildParentheses(
					new AlignLeafSeriesBuilder()
					.orientation(AlignAxisBase.VERTICAL)
					.aligned_edge(AlignAxisBase.EDGE_CENTER)
					.whitespace(GROUP_SPACING)
					.build() );
		case NPK:
			return new AlignLeafSeriesBuilder()
			.divider(new DrawTextCap("P"))
			.aligned_edge(AlignAxisBase.EDGE_CENTER)
			.whitespace(LETTER_SPACING)
			.build();
		
		// Integer Arithmetic
		case REMAINDER:
			return new AlignLeafSeriesBuilder()
			.divider(new DrawTextCap("mod"))
			.aligned_edge(AlignAxisBase.EDGE_CENTER)
			.whitespace(LETTER_SPACING)
			.build();
		case GCD:
			text = "gcd";
			nested_comp = new AlignLeafSeriesBuilder()
			.divider(new AlignSeriesBuilder(Arrays.asList(
					new DrawTextCap(",") ))
					.orientation(AlignAxisBase.HORIZONTAL)
					.whitespace(GROUP_SPACING)
					.build() )
			.aligned_edge(AlignAxisBase.EDGE_CENTER)
			.whitespace(LETTER_SPACING)
			.build();
			break;
		case LCM:
			text = "lcm";
			nested_comp = new AlignLeafSeriesBuilder()
			.divider(new AlignSeriesBuilder(Arrays.asList(
					new DrawTextCap(",") ))
					.orientation(AlignAxisBase.HORIZONTAL)
					.whitespace(GROUP_SPACING)
					.build() )
			.aligned_edge(AlignAxisBase.EDGE_CENTER)
			.whitespace(LETTER_SPACING)
			.build();
			break;

		// Constants
		case CONST_E:
			return new DrawTextCap("e");
		case CONST_PI:
			return new DrawTextCap("\u03C0");
		default:
			return null;
		}
		
		// Use below logic for common text-parentheses-styled representations
		return new AlignSeriesBuilder(Arrays.asList(
				new DrawTextCap(text), buildParentheses(nested_comp) ))
		.aligned_edge(AlignAxisBase.EDGE_CENTER)
		.whitespace(LETTER_SPACING)
		.build();
	}
	
	// TODO make parentheses not look like garbage
	public static AlignForm buildParentheses(AlignForm core) {
		return new AlignBorderBuilder(core)
		.start_bound(new DrawTextCap("("))
		.end_bound(new DrawTextCap(")"))
		.bound_stretch(AlignAxisBase.STRETCH_GIRTH)
		.orientation(AlignAxisBase.HORIZONTAL)
		.aligned_edge(AlignAxisBase.EDGE_CENTER)
		.whitespace(LETTER_SPACING)
		.build();
	}

}
