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
	
	//*
	private TextPresObject buildTextPres(FunctionType ftype) {		
		String[] strList = new String[3];
		//TODO Add more tags
		TagFillBase[] tagList = new TagFillBase[5];

		tagList[0] = new BiTagFill(
				TagFlags.NONE, TagFlags.DISABLE_ID, Tags.ID.getTag(),
				"", "" );
		//TODO use this in selection tags
		/*
		"<mstyle background='#99ddff' style='border: 1pt solid #000; padding: 2pt;'>",
		"</mstyle>",
		//*/
		tagList[1] = new BiTagFill(
				TagFlags.HIGHLIGHT, TagFlags.HIGHLIGHT,	Tags.SELECT_L.getTag(),
				//TODO fill with actual values (add space to front of value)
				"","");
		tagList[2] = new BiTagFill(
				TagFlags.HIGHLIGHT, TagFlags.HIGHLIGHT,	Tags.SELECT_R.getTag(),
				//TODO fill with actual values
				"","");
		tagList[3] = new BiTagFill(
				TagFlags.PARENTHESES, TagFlags.PARENTHESES,	Tags.PARENTHESIS_L.getTag(),
				"<mo>(</mo>","");
		tagList[4] = new BiTagFill(
				TagFlags.PARENTHESES, TagFlags.PARENTHESES,	Tags.PARENTHESIS_R.getTag(),
				"<mo>)</mo>","");
		
		String href = " href=" + HtmlIdFormat.encloseIdInTags(Tags.ID.getTag()),
				out_l = "<mstyle" + href + Tags.SELECT_L.getTag() + ">"
						+ Tags.PARENTHESIS_L.getTag(),
				out_r = Tags.PARENTHESIS_R.getTag() + "</mstyle>";
		
		switch (ftype) {
		case BLANK:
			/* How it should be (TODO figure this out)
			strList[0] = "<mphantom style='border: 5px solid #000; padding: 5px;' "
				+ href + ">" + "<mn>0</mn></mphantom>";
			*/
			// Temp job
			strList[0] = out_l + "<mi>&#x025EF;</mi>" + out_r;
			strList[1] = "";
			strList[2] = "";
			break;
		case NUMBER:
			strList[0] = out_l + "<mn>" + NumberStringConverter.toString(number)
					+"</mn>" + out_r;
			strList[1] = "";
			strList[2] = "";
			break;
		case ADD:
			strList[0] = out_l;
			strList[1] = "<mo>+</mo>";
			strList[2] = out_r;
			break;
		case SUBTRACT:
			strList[0] = out_l;
			strList[1] = "<mo>-</mo>";
			strList[2] = out_r;
			break;
		case MULTIPLY:
			strList[0] = out_l;
			strList[1] = "<mo>&times;</mo>";
			strList[2] = out_r;
			break;
		case DIVIDE:
			strList[0] = out_l + "<mfrac><mrow>";
			strList[1] = "</mrow><mrow>";
			strList[2] = "</mrow></mfrac>" + out_r;
			break;
		case POWER:
		case SQUARE:
			strList[0] = out_l + "<msup><mrow>";
			strList[1] = "</mrow><mrow>";
			strList[2] = "</mrow></msup>" + out_r;
			break;
		case SQRT:
			strList[0] = out_l + "<msqrt>";
			strList[1] = "";
			strList[2] = "</msqrt>" + out_r;
			break;
			//TODO - throw exception?
		default:
			break;
		}
		return new TextPresObject(strList, tagList);
	}
	//*/
	
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
