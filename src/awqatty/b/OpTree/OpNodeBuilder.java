package awqatty.b.OpTree;

import awqatty.b.FunctionDictionary.*;
import awqatty.b.JSInterface.IdFormat;
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
			return new FunctionPower();
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
		//TODO Add more tags
		String[] strList = new String[3];
		TagBox[] tagList = new TagBox[3];

		tagList[0] = new IdTagBox(Tags.ID.getTag());
		tagList[1] = new TagBox(
				TagFlags.HIGHLIGHT,
				true,false,
				Tags.SELECT_L.getTag(),
				"" );
		tagList[2] = new TagBox(
				TagFlags.HIGHLIGHT,
				true,false,
				Tags.SELECT_R.getTag(),
				"" );
		
		String href = "href=" + IdFormat.encloseIdInTags(Tags.ID.getTag());

		switch (ftype) {
		case BLANK:
			/* How it should be (TODO figure this out)
			strList[0] = "<mphantom style='border: 5px solid #000; padding: 5px;' "
				+ href + ">" + "<mn>0</mn></mphantom>";
			*/
			// Temp job
			strList[0] = Tags.SELECT_L.getTag() + "<mi " + href + ">&#x025EF;</mi>";
			strList[1] = "";
			strList[2] = Tags.SELECT_R.getTag();
			break;
		case NUMBER:
			strList[0] = Tags.SELECT_L.getTag() + "<mn " + href + ">";
			strList[1] = "";
			strList[2] = (number != (long)number
					? Double.toString(number) : Long.toString((long)number) )
					+"</mn>" + Tags.SELECT_R.getTag();
			break;
		case ADD:
			strList[0] = Tags.SELECT_L.getTag();
			strList[1] = "<mo " + href + ">+</mo>";
			strList[2] = Tags.SELECT_R.getTag();
			break;
		case SUBTRACT:
			strList[0] = Tags.SELECT_L.getTag();
			strList[1] = "<mo " + href + ">-</mo>";
			strList[2] = Tags.SELECT_R.getTag();
			break;
		case MULTIPLY:
			strList[0] = Tags.SELECT_L.getTag();
			strList[1] = "<mo " + href + ">&times;</mo>";
			strList[2] = Tags.SELECT_R.getTag();
			break;
		case DIVIDE:
			strList[0] = Tags.SELECT_L.getTag() + "<mfrac " + href + "><mrow>";
			strList[1] = "</mrow><mrow>";
			strList[2] = "</mrow></mfrac>" + Tags.SELECT_R.getTag();
			break;
		case POWER:
		case SQUARE:
			strList[0] = Tags.SELECT_L.getTag() + "<msup " + href + "><mrow>";
			strList[1] = "</mrow><mrow>";
			strList[2] = "</mrow></msup>" + Tags.SELECT_R.getTag();
			break;
		case SQRT:
			strList[0] = Tags.SELECT_L.getTag() + "<msqrt " + href + ">";
			strList[1] = "";
			strList[2] = "</msqrt>" + Tags.SELECT_R.getTag();
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
			FunctionForm func = buildFunction(ftype);
			TextPresObject pres = buildTextPres(ftype);
			return new OpNode(ftype, func, pres,
					getMinBranchCount(ftype),
					getMaxBranchCount(ftype) );
		}
		// Unexpected case
		else
			throw new RuntimeException("Cannot create source node instance");
	}
	
	// The null entries are placeholders for the selected node
	public OpNode[] buildExp(FunctionType ftype) {
		switch(ftype){
		// 2+ args
		case ADD:
		case SUBTRACT:
		case MULTIPLY:
		case DIVIDE:
		case POWER:
			return new OpNode[] {
					build(ftype),
					null,
					build(FunctionType.BLANK)
				};
		// Others (1-arg, 2-arg w/ default)
		case SQUARE:
			return new OpNode[] {
					build(ftype),
					null,
					Number(2).build(FunctionType.NUMBER)
				};
		case SQRT:
			return new OpNode[] {
					build(ftype),
					null
				};
		case NUMBER:
		case BLANK:
			return new OpNode[] {
					build(ftype)
				};
		case SOURCE:
			throw new RuntimeException("Cannot create node instance of this type");
		default:
			throw new RuntimeException("Invalid function-type code");
		}
	}

	
}
