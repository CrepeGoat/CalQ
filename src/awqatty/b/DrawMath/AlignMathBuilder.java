package awqatty.b.DrawMath;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import awqatty.b.DrawMath.DrawSubTree.AlignAxisBase;
import awqatty.b.DrawMath.DrawSubTree.AlignSuperscript;
import awqatty.b.DrawMath.DrawSubTree.AlignLeaf;
import awqatty.b.DrawMath.DrawSubTree.AlignForm;
import awqatty.b.DrawMath.DrawSubTree.Builders.AlignBorderBuilder;
import awqatty.b.DrawMath.DrawSubTree.Builders.AlignLeafSeriesBuilder;
import awqatty.b.DrawMath.DrawSubTree.Builders.AlignSeriesBuilder;
import awqatty.b.DrawMath.DrawToCanvas.DrawPath;
import awqatty.b.DrawMath.DrawToCanvas.DrawText;
import awqatty.b.DrawMath.DrawToCanvas.DrawTextCap;
import awqatty.b.FunctionDictionary.FunctionType;

public class AlignMathBuilder {
	
	private static float group_spacing;
	private static float letter_spacing;
	private static int line_thickness;
	
	// Initializes all static values dependent on screen density
	//		(called in constructor of every instance)
	public static final void setDensity(Context context) {
		float density = context.getResources().getDisplayMetrics().density;
		DrawText.setDensity(density);
		
		synchronized(AlignMathBuilder.class) {
			group_spacing = 15 * density;
			letter_spacing = 4.5f * density;
			line_thickness = (int) (3 * density);
		}
	}
	
	public AlignMathBuilder(Context context) {
		setDensity(context);
	}
	
	
	private double _number;
	public AlignMathBuilder number(double number) {
		_number = number;
		return this;
	}
	
	/**************************************************
	 * Draw Numbers in Grouped Scale
	 */
	private static final float scale = 0.6f;
	private static final float min_scale = (float) (1/Math.sqrt(2));
											// -> Area2 = (1/2)*Area1
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
					(min_scale + (1-min_scale)*Math.pow(scale,i)) );
			series.add(comp);
		}
	}
	
	private static void getNumberDraw(
			double number, 
			List<DrawText> series_sigFig,
			List<DrawText> series_mag ) 
	{
		final String str_num = Double.toString(number);
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
	private static void groupWholeNum(String str, List<String> list) {
		final int length = str.length();
		
		int index = (!str.startsWith("-") ? 0 : 1);
		index += ((length-index)%3);
		if (index > 0)
			list.add(str.substring(0, index));
		
		index += 3;
		for (; index<=length; index+=3) {
			list.add(str.substring(index-3, index));
		}
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
	
	/****************************************************
	 * Build Method
	 */
	// TODO calculate group_spacing based on pixel density
	
	public AlignForm build(FunctionType ftype) {
		Path path;
		String text=null;
		AlignForm nested_comp=null;
		
		switch (ftype) {
		case BLANK:
			return new DrawText("\u25FC");
		case NUMBER:
			List<DrawText> series_sigFig = new ArrayList<DrawText>();
			List<DrawText> series_mag = new ArrayList<DrawText>();
			getNumberDraw(_number, series_sigFig, series_mag);
			
			if (series_sigFig.size() == 1 && series_mag.isEmpty())
				return series_sigFig.get(0);
			else {
				List<AlignForm> tmp = new ArrayList<AlignForm>();
				tmp.add(new AlignSeriesBuilder(series_sigFig)
						.aligned_edge(AlignAxisBase.EDGE_BOTTOM)
						.whitespace(letter_spacing)
						.build() );
				
				if (series_mag.isEmpty())
					return tmp.get(0);
				else {
					tmp.add(new AlignSeriesBuilder(series_mag)
							.aligned_edge(AlignAxisBase.EDGE_TOP)
							.whitespace(letter_spacing)
							.build() );
					return new AlignSeriesBuilder(tmp)
							.aligned_edge(AlignAxisBase.EDGE_CENTER)
							.whitespace(letter_spacing)
							.build();
				}
			}
			
		// Standard Functions
		case ADD:
			return new AlignLeafSeriesBuilder()
					.divider(new DrawText("+"))
					.stretch_divider(AlignAxisBase.STRETCH_NONE)
					.aligned_edge(AlignAxisBase.EDGE_CENTER)
					.whitespace(group_spacing)
					.build();
		case SUBTRACT:
			return new AlignLeafSeriesBuilder()
					.divider(new DrawText("\u2212"))
					.stretch_divider(AlignAxisBase.STRETCH_NONE)
					.aligned_edge(AlignAxisBase.EDGE_CENTER)
					.whitespace(group_spacing)
					.build();
		case MULTIPLY:
			return new AlignLeafSeriesBuilder()
					.divider(new DrawText("\u2715"))
					.stretch_divider(AlignAxisBase.STRETCH_NONE)
					.aligned_edge(AlignAxisBase.EDGE_CENTER)
					.whitespace(group_spacing)
					.build();
		case DIVIDE:
			path = new Path();
			path.addRect(new RectF(0,0,line_thickness,line_thickness),
					Path.Direction.CCW );
			return new AlignLeafSeriesBuilder()
					.divider(new DrawPath(path, new Paint(Paint.ANTI_ALIAS_FLAG)))
					//.divider(new DrawText("."))
					.stretch_divider(AlignAxisBase.STRETCH_GIRTH)
					.orientation(AlignAxisBase.VERTICAL)
					.aligned_edge(AlignAxisBase.EDGE_CENTER)
					.whitespace(group_spacing)
					.build();
			
		case NEGATIVE:
			return new AlignBorderBuilder(
					new AlignLeaf(0))
					.start_bound(new DrawText("-"))
					.bound_stretch(AlignAxisBase.STRETCH_NONE)
					.aligned_edge(AlignAxisBase.EDGE_CENTER)
					.whitespace(letter_spacing)
					.build();
		case ABS:
			return new AlignBorderBuilder(
					new AlignLeaf(0))
					.start_bound(new DrawText("|"))
					.end_bound(new DrawText("|"))
					.aligned_edge(AlignAxisBase.EDGE_CENTER)
					.whitespace(letter_spacing)
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
			path.addRect(new RectF(0,0,line_thickness,line_thickness),
					Path.Direction.CCW );
			return new AlignBorderBuilder(
					new AlignBorderBuilder(new AlignLeaf(0))
							.start_bound(new DrawPath(path,
									new Paint(Paint.ANTI_ALIAS_FLAG) ))
							.bound_stretch(AlignAxisBase.STRETCH_GIRTH)
							.orientation(AlignAxisBase.VERTICAL)
							.aligned_edge(AlignAxisBase.EDGE_CENTER)
							.whitespace(group_spacing)
							.build())
					.start_bound(new DrawText("\u221A"))
					.bound_stretch(AlignAxisBase.STRETCH_GIRTH)
					.orientation(AlignAxisBase.HORIZONTAL)
					.aligned_edge(AlignAxisBase.EDGE_CENTER)
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
			return new AlignBorderBuilder(
					new AlignLeaf(0))
					.end_bound(new DrawText("!"))
					.bound_stretch(AlignAxisBase.STRETCH_NONE)
					.aligned_edge(AlignAxisBase.EDGE_CENTER)
					.whitespace(letter_spacing)
					.build();
		case NCK:	// TODO change to col.vec. shorthand format
			return new AlignBorderBuilder(
					new AlignLeafSeriesBuilder()
							.orientation(AlignAxisBase.VERTICAL)
							.aligned_edge(AlignAxisBase.EDGE_CENTER)
							.whitespace(group_spacing)
							.build())
					.start_bound(new DrawText("("))
					.end_bound(new DrawText(")"))
					.bound_stretch(AlignAxisBase.STRETCH_FULL)
					.aligned_edge(AlignAxisBase.EDGE_CENTER)
					.whitespace(letter_spacing)
					.build();
		case NPK:
			return new AlignLeafSeriesBuilder()
					.divider(new DrawText("P"))
					.aligned_edge(AlignAxisBase.EDGE_CENTER)
					.whitespace(letter_spacing)
					.build();
		
		// Integer Arithmetic
		case REMAINDER:
			return new AlignLeafSeriesBuilder()
					.divider(new DrawTextCap("mod"))
					.aligned_edge(AlignAxisBase.EDGE_CENTER)
					.whitespace(letter_spacing)
					.build();
		case GCD:
			text = "gcd";
			nested_comp = new AlignLeafSeriesBuilder()
					.divider(new DrawTextCap(","))
					.aligned_edge(AlignAxisBase.EDGE_CENTER)
					.whitespace(group_spacing)
					.build();
			break;
		case LCM:
			text = "lcm";
			nested_comp = new AlignLeafSeriesBuilder()
					.divider(new DrawTextCap(","))
					.aligned_edge(AlignAxisBase.EDGE_CENTER)
					.whitespace(group_spacing)
					.build();
			break;

		// Constants
		case CONST_E:
			return new DrawText("e");
		case CONST_PI:
			return new DrawText("\u03C0");
		//TODO - throw exception?
		default:
			return null;
		}
		
		// Use below logic for common text-parentheses-styled representations
		return new AlignBorderBuilder(
				new AlignBorderBuilder(nested_comp)
						.start_bound(new DrawText("("))
						.end_bound(new DrawText(")"))
						.bound_stretch(AlignAxisBase.STRETCH_FULL)
						.orientation(AlignAxisBase.HORIZONTAL)
						.aligned_edge(AlignAxisBase.EDGE_CENTER)
						.whitespace(letter_spacing)
						.build())
				.start_bound(new DrawTextCap(text))
				.bound_stretch(AlignAxisBase.STRETCH_NONE)
				.aligned_edge(AlignAxisBase.EDGE_CENTER)
				.whitespace(letter_spacing)
				.build();
	}

}
