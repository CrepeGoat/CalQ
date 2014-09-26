package awqatty.b.MathmlPresentation;

import awqatty.b.FunctionDictionary.FunctionType;
import awqatty.b.GenericTextPresentation.BiTagFill;
import awqatty.b.GenericTextPresentation.NumberStringConverter;
import awqatty.b.GenericTextPresentation.StaticTagFill;
import awqatty.b.GenericTextPresentation.TagFillBase;
import awqatty.b.GenericTextPresentation.TagFlags;
import awqatty.b.GenericTextPresentation.Tags;
import awqatty.b.GenericTextPresentation.TextPresObject;
import awqatty.b.JSInterface.HtmlIdFormat;
import awqatty.b.TextPresentation.TextPresBuilderForm;
import awqatty.b.TextPresentation.TextPresForm;

public class MathmlTextPresBuilder implements TextPresBuilderForm {

	protected double _number;

	@Override
	public TextPresBuilderForm number(double d) {
		_number = d;
		return this;
	}

	@Override
	public TextPresForm build(FunctionType ftype) {
		String[] strlist;
		final TagFillBase[] taglist = new TagFillBase[2];
		final TagFillBase[] childtaglist = new TagFillBase[2];
		
		// Note - ID TagFill MUST be first (see TextPresObject)
		taglist[0] = new BiTagFill(
				TagFlags.NONE, TagFlags.DISABLE_ID, Tags.ID.getTag(),
				// ID value is set dynamically
				"", "" );
		taglist[1] = new BiTagFill(
				TagFlags.HIGHLIGHT, TagFlags.HIGHLIGHT,	Tags.SELECT_L.getTag(),
				" background='#99ddff' style='border: 1pt solid #000; padding: 2pt;'","");
		
		// (Uncomment code when using javascript binding)
		final String href = " href=" + 
				//"'javascript:JSOnClickMathml(&#39;" + 
				HtmlIdFormat.encloseIdInTags(Tags.ID.getTag())
				//+ "&#39;)'"
				,
				out_l = "<mstyle" + href + Tags.SELECT_L.getTag() + ">",
				out_r = "</mstyle>";
		
		switch (ftype) {
		case BLANK:
			strlist = new String[1];
			// TODO use mphantom instead? 
			strlist[0] = out_l + "<mi>&#x025EF;</mi>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "");
			break;
		case NUMBER:
			strlist = new String[1];
			strlist[0] = out_l + "<mn>" + NumberStringConverter.toStringOfLength(_number,8)
					+"</mn>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "");
			break;
			
		// Standard Functions
		case ADD:
			strlist = new String[3];
			strlist[0] = out_l + Tags.PARENTHESIS_L.getTag();
			strlist[1] = "<mo>+</mo>";
			strlist[2] = Tags.PARENTHESIS_R.getTag() + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "<mo>(</mo>");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "<mo>)</mo>");
			break;
		case SUBTRACT:
			strlist = new String[3];
			strlist[0] = out_l + Tags.PARENTHESIS_L.getTag();
			strlist[1] = "<mo>-</mo>";
			strlist[2] = Tags.PARENTHESIS_R.getTag() + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "<mo>(</mo>");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "<mo>)</mo>");
			break;
		case MULTIPLY:
			strlist = new String[3];
			strlist[0] = out_l + "<mo>(</mo>";
			strlist[1] = "<mo>)</mo>" + /*"<mo>&#8901;</mo>" + */ "<mo>(</mo>";
			strlist[2] = "<mo>)</mo>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "");
			break;
		case DIVIDE:
			strlist = new String[3];
			strlist[0] = out_l + "<mfrac><mrow>";
			strlist[1] = "</mrow><mrow>";
			strlist[2] = "</mrow></mfrac>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "");
			break;
		case NEGATIVE:
			strlist = new String[2];
			strlist[0] = out_l + "<mo>-</mo>";
			strlist[1] = out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "<mo>(</mo>");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "<mo>)</mo>");
			break;
		case ABS:
			strlist = new String[2];
			strlist[0] = out_l + "<mo>|</mo>";
			strlist[1] = "<mo>|</mo>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "");
			break;

		// Power Functions
		case POWER:
		case SQUARE:
		case MULT_INVERSE:
			strlist = new String[3];
			strlist[0] = out_l + "<msup><mrow>";
			strlist[1] = "</mrow><mrow>";
			strlist[2] = "</mrow></msup>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "<mo>(</mo>");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "<mo>)</mo>");
			break;
		case SQRT:
			strlist = new String[2];
			strlist[0] = out_l + "<msqrt>";
			strlist[1] = "</msqrt>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "");
			break;
		
		// Log Functions
		case EXP_E:
		case EXP_10:
			strlist = new String[3];
			strlist[0] = out_l + "<msup><mstyle mathsize=0.75>";
			strlist[1] = "</mstyle><mstyle mathsize=1.33>";
			strlist[2] = "</mstyle></msup>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "<mo>(</mo>");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "<mo>)</mo>");
			break;
		case LN:
			strlist = new String[2];
			strlist[0] = out_l + "<mi>ln</mi><mo>(</mo>";
			strlist[1] = "<mo>)</mo>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "");
			break;
		case LOG10:
			strlist = new String[2];
			strlist[0] = out_l + "<msub><mi>log</mi><mn>10</mn></msub> <mo>(</mo>";
			strlist[1] = "<mo>)</mo>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "");
			break;

		// Trig Functions
		case SINE:
			strlist = new String[2];
			strlist[0] = out_l + "<mi>sin</mi><mo>(</mo>";
			strlist[1] = "<mo>)</mo>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "");
			break;
		case COSINE:
			strlist = new String[2];
			strlist[0] = out_l + "<mi>cos</mi><mo>(</mo>";
			strlist[1] = "<mo>)</mo>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "");
			break;
		case TANGENT:
			strlist = new String[2];
			strlist[0] = out_l + "<mi>tan</mi><mo>(</mo>";
			strlist[1] = "<mo>)</mo>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "");
			break;
		case ARCSINE:
			strlist = new String[2];
			strlist[0] = out_l + "<mi>arcsin</mi><mo>(</mo>";
			strlist[1] = "<mo>)</mo>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "");
			break;
		case ARCCOSINE:
			strlist = new String[2];
			strlist[0] = out_l + "<mi>arccos</mi><mo>(</mo>";
			strlist[1] = "<mo>)</mo>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "");
			break;
		case ARCTANGENT:
			strlist = new String[2];
			strlist[0] = out_l + "<mi>arctan</mi><mo>(</mo>";
			strlist[1] = "<mo>)</mo>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "");
			break;

		// Trig Functions
		case HYPSINE:
			strlist = new String[2];
			strlist[0] = out_l + "<mi>sinh</mi><mo>(</mo>";
			strlist[1] = "<mo>)</mo>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "");
			break;
		case HYPCOSINE:
			strlist = new String[2];
			strlist[0] = out_l + "<mi>cosh</mi><mo>(</mo>";
			strlist[1] = "<mo>)</mo>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "");
			break;
		case HYPTANGENT:
			strlist = new String[2];
			strlist[0] = out_l + "<mi>tanh</mi><mo>(</mo>";
			strlist[1] = "<mo>)</mo>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "");
			break;
		case ARHYPSINE:
			strlist = new String[2];
			strlist[0] = out_l + "<mi>arsinh</mi><mo>(</mo>";
			strlist[1] = "<mo>)</mo>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "");
			break;
		case ARHYPCOSINE:
			strlist = new String[2];
			strlist[0] = out_l + "<mi>arcosh</mi><mo>(</mo>";
			strlist[1] = "<mo>)</mo>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "");
			break;
		case ARHYPTANGENT:
			strlist = new String[2];
			strlist[0] = out_l + "<mi>artanh</mi><mo>(</mo>";
			strlist[1] = "<mo>)</mo>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "");
			break;
			
		// Probability
		case FACTORIAL:
			strlist = new String[2];
			strlist[0] = out_l;
			strlist[1] = "<mi>!</mi>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "<mo>(</mo>");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "<mo>)</mo>");
			break;
		case NCK:	// TODO change to col.vec. shorthand format
			strlist = new String[3];
			strlist[0] = out_l;
			strlist[1] = "<mi>C</mi>";
			strlist[2] = out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "<mo>(</mo>");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "<mo>)</mo>");
			break;
		case NPK:
			strlist = new String[3];
			strlist[0] = out_l;
			strlist[1] = "<mi>P</mi>";
			strlist[2] = out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "<mo>(</mo>");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "<mo>)</mo>");
			break;
		
		// Integer Arithmetic
		case REMAINDER:
			strlist = new String[3];
			strlist[0] = out_l + Tags.PARENTHESIS_L.getTag();
			strlist[1] = "<mi>mod</mi>";
			strlist[2] = Tags.PARENTHESIS_R.getTag() + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "<mo>(</mo>");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "<mo>)</mo>");
			break;
		case GCD:
			strlist = new String[3];
			strlist[0] = out_l + "<mi>gcd</mi><mo>(</mo>";
			strlist[1] = "<mo>,</mo>";
			strlist[2] = "<mo>)</mo>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "");
			break;
		case LCM:
			strlist = new String[3];
			strlist[0] = out_l + "<mi>lcm</mi><mo>(</mo>";
			strlist[1] = "<mo>,</mo>";
			strlist[2] = "<mo>)</mo>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "");
			break;

		// Constants
		case CONST_E:
			strlist = new String[1];
			strlist[0] = out_l + "<mi>e</mi>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "");
			break;
		case CONST_PI:
			strlist = new String[1];
			strlist[0] = out_l + "<mi>&#960;</mi>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "");
			break;

		//TODO - throw exception?
		default:
			strlist = null;
			break;
		}
		return new TextPresObject(strlist, taglist, childtaglist);
	}

}
