package awqatty.b.OpTree;

import java.util.List;

import awqatty.b.FunctionDictionary.*;
import awqatty.b.ListTree.ListTree;
import awqatty.b.TextPresentation.TextPresBuilderForm;
import awqatty.b.TextPresentation.TextPresForm;

public final class OpNodeBuilder {

	double _number;
	TextPresBuilderForm textbuilder;
	
	public OpNodeBuilder(TextPresBuilderForm tpb) {
		textbuilder = tpb;
	}
	
	public void setTextPresBuilder(TextPresBuilderForm tpb) {
		textbuilder = tpb;
		tpb.number(_number);
	}
	
	/**************************************************************
	 *** BUILDER METHODS ***
	 **************************************************************/
	public OpNodeBuilder number(double n) {
		_number = n;
		textbuilder.number(n);
		return this;
	}
	
	private FunctionForm buildFunction(FunctionType ftype) {
		switch (ftype) {
		case BLANK:
			return new FunctionException();
		case NUMBER:
			return new FunctionConstant(_number);
		case ADD:
			return new FunctionForm() {
				@Override
				public Double calculate(List<Double> dlist) {
					double sum = 0;
					for (double e : dlist)
						sum += e;
					return sum;
				}
			};
		case SUBTRACT:
			return new FunctionForm() {
				@Override
				public Double calculate(List<Double> dlist) {
					return dlist.get(0)-dlist.get(1);
				}
			};
		case MULTIPLY:
			return new FunctionForm() {
				@Override
				public Double calculate(List<Double> dlist) {
					double product = 1;
					for (double e : dlist)
						product *= e;
					return product;
				}
			};
		case DIVIDE:
			return new FunctionForm() {
				@Override	
				public Double calculate(List<Double> dlist) {
					return dlist.get(0) / dlist.get(1);
				}
			};
		case POWER:
		case SQUARE:
			return new FunctionForm() {
				@Override
				public Double calculate(List<Double> dlist) {
					return Math.pow(dlist.get(0), dlist.get(1));
				}
			};
		case SQRT:
			return new FunctionForm() {
				@Override
				public Double calculate(List<Double> dlist) {
					return Math.sqrt(dlist.get(0));
				}
			};
		//TODO - throw exception?
		default:
			return null;
		}
	}
	
	private TextPresForm buildTextPres(FunctionType ftype) {
		return textbuilder.build(ftype);
	}
	
	private static int getMinBranchCount(FunctionType ftype) {
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
	private static int getMaxBranchCount(FunctionType ftype) {
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
	
	// Used to ensure functions have appropriate ftypes
	//	i.e. SQUARE differs from POWER only by default values,
	//		should be treated as POWER function (esp. when args change from defaults)
	private static FunctionType getImplementingType(FunctionType ftype) {
		switch(ftype){
		case SQUARE:
			return FunctionType.POWER;
		default:
			return ftype;
		}
	}

	public OpNode build(FunctionType ftype) {
		if (ftype != FunctionType.SOURCE) {
			return new OpNode(
					getImplementingType(ftype),
					buildFunction(ftype),
					buildTextPres(ftype),
					getMinBranchCount(ftype),
					getMaxBranchCount(ftype) );
		}
		// Unexpected case
		else
			throw new RuntimeException("Cannot create source node instance");
	}
		
	public void buildInSubtree(ListTree<OpNode> subtree, FunctionType ftype) {
		switch(ftype){
		// 0-arg
		case NUMBER:
		case BLANK:
			subtree.setBranch(0, build(ftype));
			break;
		// 2+ args
		case ADD:
		case SUBTRACT:
		case MULTIPLY:
		case DIVIDE:
		case POWER:
			subtree.addParent(0, build(ftype));
			subtree.addChild(0, 1, build(FunctionType.BLANK));
			break;
		// 1-arg
		case SQRT:
			subtree.addParent(0, build(ftype));
			break;
		// 2-arg w/ default value
		case SQUARE:
			double tmp = _number;
			number(2);
			subtree.addParent(0, build(ftype));
			subtree.addChild(0, 1, build(FunctionType.NUMBER));
			number(tmp);
			break;
		default:
			throw new RuntimeException("Invalid function-type code");
		}	
	}

	
}
