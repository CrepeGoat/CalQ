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
			strlist[0] = out_l + Tags.PARENTHESIS_L.getTag();
			strlist[1] = "<mo>&times;</mo>";
			strlist[2] = Tags.PARENTHESIS_R.getTag() + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "<mo>(</mo>");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "<mo>)</mo>");
			break;
		case DIVIDE:
			strlist = new String[3];
			strlist[0] = out_l + "<mfrac><mrow>";
			strlist[1] = "</mrow><mrow>";
			strlist[2] = "</mrow></mfrac>" + out_r;
			childtaglist[0] = new StaticTagFill(Tags.PARENTHESIS_L.getTag(), "");
			childtaglist[1] = new StaticTagFill(Tags.PARENTHESIS_R.getTag(), "");
			break;
		case POWER:
		case SQUARE:
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
		//TODO - throw exception?
		default:
			strlist = null;
			break;
		}
		return new TextPresObject(strlist, taglist, childtaglist);
	}

}
