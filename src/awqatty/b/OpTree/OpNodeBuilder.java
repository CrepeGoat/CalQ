package awqatty.b.OpTree;

import awqatty.b.FunctionDictionary.*;
import awqatty.b.JSInterface.HtmlIdFormat;
import awqatty.b.ListTree.ListTree;
import awqatty.b.MathmlPresentation.*;

public final class OpNodeBuilder {

	double number;
	
	/**************************************************************
	 *** BUILDER METHODS ***
	 **************************************************************/
	public OpNodeBuilder Number(double n) {
		number = n;
		return this;
	}
		
	private FunctionForm buildFunction(FunctionType ftype) {
		switch (ftype) {
		case BLANK:
			return new FunctionException();
		case NUMBER:
			return new FunctionConstant(number);
		case ADD:
			return new FunctionAdd();
		case SUBTRACT:
			return new FunctionSubtract();
		case MULTIPLY:
			return new FunctionMultiply();
		case DIVIDE:
			return new FunctionDivide();
		case POWER:
		case SQUARE:
			return new FunctionPower();
		case SQRT:
			return new FunctionSqrt();
			//TODO - throw exception?
		default:
			return null;
		}
	}
	
	private TextPresObject buildTextPres(FunctionType ftype) {		
		String[] strlist;
		TagFillBase[] taglist = new TagFillBase[4];
		
		// Note - ID TagFill MUST be first (see TextPresObject)
		taglist[0] = new BiTagFill(
				TagFlags.NONE, TagFlags.DISABLE_ID, Tags.ID.getTag(),
				// ID value is set dynamically
				"", "" );
		taglist[1] = new BiTagFill(
				TagFlags.HIGHLIGHT, TagFlags.HIGHLIGHT,	Tags.SELECT_L.getTag(),
				" background='#99ddff' style='border: 1pt solid #000; padding: 2pt;'","");
		taglist[2] = new BiTagFill(
				TagFlags.PARENTHESES, TagFlags.PARENTHESES,	Tags.PARENTHESIS_L.getTag(),
				"<mo>(</mo>","");
		taglist[3] = new BiTagFill(
				TagFlags.PARENTHESES, TagFlags.PARENTHESES,	Tags.PARENTHESIS_R.getTag(),
				"<mo>)</mo>","");
		
		String href = " href=" + HtmlIdFormat.encloseIdInTags(Tags.ID.getTag()),
				out_l = "<mstyle" + href + Tags.SELECT_L.getTag() + ">"
						+ Tags.PARENTHESIS_L.getTag(),
				out_r = Tags.PARENTHESIS_R.getTag() + "</mstyle>";
		
		switch (ftype) {
		case BLANK:
			strlist = new String[1];
			// TODO use mphantom instead? 
			strlist[0] = out_l + "<mi>&#x025EF;</mi>" + out_r;
			break;
		case NUMBER:
			strlist = new String[1];
			strlist[0] = out_l + "<mn>" + NumberStringConverter.toString(number)
					+"</mn>" + out_r;
			break;
		case ADD:
			strlist = new String[3];
			strlist[0] = out_l;
			strlist[1] = "<mo>+</mo>";
			strlist[2] = out_r;
			break;
		case SUBTRACT:
			strlist = new String[3];
			strlist[0] = out_l;
			strlist[1] = "<mo>-</mo>";
			strlist[2] = out_r;
			break;
		case MULTIPLY:
			strlist = new String[3];
			strlist[0] = out_l;
			strlist[1] = "<mo>&times;</mo>";
			strlist[2] = out_r;
			break;
		case DIVIDE:
			strlist = new String[3];
			strlist[0] = out_l + "<mfrac><mrow>";
			strlist[1] = "</mrow><mrow>";
			strlist[2] = "</mrow></mfrac>" + out_r;
			break;
		case POWER:
		case SQUARE:
			strlist = new String[3];
			strlist[0] = out_l + "<msup><mrow>";
			strlist[1] = "</mrow><mrow>";
			strlist[2] = "</mrow></msup>" + out_r;
			break;
		case SQRT:
			strlist = new String[2];
			strlist[0] = out_l + "<msqrt>";
			strlist[1] = "</msqrt>" + out_r;
			break;
			//TODO - throw exception?
		default:
			strlist = null;
			break;
		}
		return new TextPresObject(strlist, taglist);
	}
	
	private int getMinBranchCount(FunctionType ftype) {
		switch (ftype) {
		case BLANK:
		case NUMBER:
			return 0;
		case ADD:
		case SUBTRACT:
		case MULTIPLY:
		case DIVIDE:
		case POWER:
		case SQUARE:
			return 2;
		case SQRT:
			return 1;
			//TODO - throw exception?
		default:
			return 0;
		}
	}
	private int getMaxBranchCount(FunctionType ftype) {
		switch (ftype) {
		case BLANK:
		case NUMBER:
			return 0;
		case ADD:
		case MULTIPLY:
			return Integer.MAX_VALUE;
		case SUBTRACT:
		case DIVIDE:
		case POWER:
		case SQUARE:
			return 2;
		case SQRT:
			return 1;
			//TODO - throw exception?
		default:
			return 0;
		}
	}

	public OpNode build(FunctionType ftype) {
		if (ftype != FunctionType.SOURCE) {
			return new OpNode(
					(ftype == FunctionType.SQUARE ? FunctionType.POWER : ftype),
					buildFunction(ftype),
					buildTextPres(ftype),
					getMinBranchCount(ftype),
					getMaxBranchCount(ftype) );
		}
		// Unexpected case
		else
			throw new RuntimeException("Cannot create source node instance");
	}
	
	// The null entries are placeholders for the selected node
	public ListTree<OpNode> buildExp(FunctionType ftype) {
		ListTree<OpNode> tmp = new ListTree<OpNode>();
		switch(ftype){
		// 2+ args
		case ADD:
		case SUBTRACT:
		case MULTIPLY:
		case DIVIDE:
		case POWER:
			tmp.addChild(tmp.addChild(-1, 0, build(ftype)),
					0, build(FunctionType.BLANK) );
			break;
		// Others (1-arg, 2-arg w/ default)
		case SQUARE:
			tmp.addChild(tmp.addChild(-1, 0, build(ftype)),
					0, Number(2).build(FunctionType.NUMBER) );
			break;
		case SQRT:
		case NUMBER:
		case BLANK:
			tmp.addChild(-1, 0, build(ftype));
			break;
		default:
			throw new RuntimeException("Invalid function-type code");
		}
		return tmp;
	}
	
	public int buildExpLoc(FunctionType ftype) {
		return 0;
	}

	
}
