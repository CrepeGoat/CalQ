package awqatty.b.OpTree;

import java.util.List;

import android.content.Context;
import awqatty.b.DrawMath.AlignDrawBuilder;
import awqatty.b.DrawMath.AssignParentheses.ClosureFlags;
import awqatty.b.DrawMath.AssignParentheses.ClosureType;
import awqatty.b.DrawMath.DrawSubTree.AlignForm;
import awqatty.b.FunctionDictionary.*;
import awqatty.b.FunctionDictionary.FunctionForms.FunctionConstant;
import awqatty.b.FunctionDictionary.FunctionForms.FunctionException;
import awqatty.b.FunctionDictionary.FunctionForms.FunctionFactorial;
import awqatty.b.FunctionDictionary.FunctionForms.FunctionForm;
import awqatty.b.FunctionDictionary.FunctionForms.FunctionGCD;
import awqatty.b.FunctionDictionary.FunctionForms.FunctionLCM;
import awqatty.b.FunctionDictionary.FunctionForms.FunctionNCK;
import awqatty.b.FunctionDictionary.FunctionForms.FunctionNPK;
import awqatty.b.ListTree.ListTree;

public final class OperationBuilder {

	double number;
	AlignDrawBuilder align_builder;
	
	public OperationBuilder(Context context) {
		align_builder = new AlignDrawBuilder(context);
		SourceOperation.setAlignDrawBuilder(align_builder);
	}
		
	/**************************************************************
	 *** BUILDER METHODS ***
	 **************************************************************/
	public OperationBuilder number(double n) {
		this.number = n;
		align_builder.number(n);
		return this;
	}
	
	private FunctionForm buildFunction(FunctionType ftype) {
		switch (ftype) {
			case SOURCE:
				return new FunctionForm() {
					@Override
					public Double calculate(List<Double> dlist) {
						return dlist.get(0);
					}
				};
			case BLANK:
			case RAW_TEXT:
				return new FunctionException();
			case NUMBER:
				return new FunctionConstant(number);
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
			case MULT_INVERSE:
			case EXP_E:
			case EXP_10:
				return new FunctionForm() {
					@Override
					public Double calculate(List<Double> dlist) {
						return Math.pow(dlist.get(0), dlist.get(1));
					}
				};

			case NEGATIVE:
				return new FunctionForm() {
					@Override
					public Double calculate(List<Double> dlist) {
						return -dlist.get(0);
					}
				};
			case ABS:
				return new FunctionForm() {
					@Override
					public Double calculate(List<Double> dlist) {
						return Math.abs(dlist.get(0));
					}
				};
			case SQRT:
				return new FunctionForm() {
					@Override
					public Double calculate(List<Double> dlist) {
						return Math.sqrt(dlist.get(0));
					}
				};

			case LN:
				return new FunctionForm() {
					@Override
					public Double calculate(List<Double> dlist) {
						return Math.log(dlist.get(0));
					}
				};
			case LOG10:
				return new FunctionForm() {
					@Override
					public Double calculate(List<Double> dlist) {
						return Math.log10(dlist.get(0));
					}
				};

			// Trig Functions
			case SINE:
				return new FunctionForm() {
					@Override
					public Double calculate(List<Double> dlist) {
						return Math.sin(dlist.get(0));
					}
				};
			case COSINE:
				return new FunctionForm() {
					@Override
					public Double calculate(List<Double> dlist) {
						return Math.cos(dlist.get(0));
					}
				};
			case TANGENT:
				return new FunctionForm() {
					@Override
					public Double calculate(List<Double> dlist) {
						return Math.tan(dlist.get(0));
					}
				};
			case ARCSINE:
				return new FunctionForm() {
					@Override
					public Double calculate(List<Double> dlist) {
						return Math.asin(dlist.get(0));
					}
				};
			case ARCCOSINE:
				return new FunctionForm() {
					@Override
					public Double calculate(List<Double> dlist) {
						return Math.acos(dlist.get(0));
					}
				};
			case ARCTANGENT:
				return new FunctionForm() {
					@Override
					public Double calculate(List<Double> dlist) {
						return Math.atan(dlist.get(0));
					}
				};

			// Hyperbolic Functions
			case HYPSINE:
				return new FunctionForm() {
					@Override
					public Double calculate(List<Double> dlist) {
						return Math.sinh(dlist.get(0));
					}
				};
			case HYPCOSINE:
				return new FunctionForm() {
					@Override
					public Double calculate(List<Double> dlist) {
						return Math.cosh(dlist.get(0));
					}
				};
			case HYPTANGENT:
				return new FunctionForm() {
					@Override
					public Double calculate(List<Double> dlist) {
						return Math.tanh(dlist.get(0));
					}
				};
			case ARHYPSINE:
				return new FunctionForm() {
					@Override
					public Double calculate(List<Double> dlist) {
						final double x = dlist.get(0);
						return Math.log(x + Math.sqrt(Math.pow(x, 2) + 1));
					}
				};
			case ARHYPCOSINE:
				return new FunctionForm() {
					@Override
					public Double calculate(List<Double> dlist) {
						final double x = dlist.get(0);
						return Math.log(x + Math.sqrt(Math.pow(x, 2) - 1));
					}
				};
			case ARHYPTANGENT:
				return new FunctionForm() {
					@Override
					public Double calculate(List<Double> dlist) {
						final double x = dlist.get(0);
						return 0.5*Math.log((1+x)/(1-x));
					}
				};
			// Permutations
			case FACTORIAL:
				return new FunctionFactorial();
			case NPK:
				return new FunctionNPK();
			case NCK:
				return new FunctionNCK();

			// Integer Arithmetic
			case REMAINDER:
				return new FunctionForm() {
					@Override
					public Double calculate(List<Double> dlist) {
						return dlist.get(0) % dlist.get(1);
					}
				};
			case GCD:
				return new FunctionGCD();
			case LCM:
				return new FunctionLCM();

			case CONST_E:
				return new FunctionConstant(Math.E);
			case CONST_PI:
				return new FunctionConstant(Math.PI);
			//TODO - throw exception?
			default:
				return null;
		}
	}
	
	private AlignForm buildDisplay(FunctionType ftype) {
		return align_builder.build(ftype);
	}
	public ClosureType buildClosureType(FunctionType ftype) {
		return (ftype != FunctionType.NUMBER ? null :
			(number < 0 ? ClosureType.TEXT_NUMERIC_NEG
					: ClosureType.TEXT_NUMERIC_POS ));
		/*
		switch (ftype) {
		case BLANK:
			return ClosureType.OTHER;
		case NUMBER:
			return (number < 0 ? ClosureType.TEXT_NUMERIC_NEG
					: ClosureType.TEXT_NUMERIC_POS );
		default:
			return null;
		}
		*/
	}
	public int buildClosureFlags(FunctionType ftype) {
		//return (ftype != FunctionType.NUMBER ? 0 : ClosureFlags.NUMERIC |
		//	(number < 0 ? ClosureFlags.NEGATIVE : 0) );
		if (ftype == FunctionType.NUMBER) {
			return ClosureFlags.TEXT_NUMERIC |
					(number <0 ? ClosureFlags.NEGATIVE : ClosureFlags.NONE);
		}
		return ClosureFlags.NONE;
	}
	
	private static int getMinLeaves(FunctionType ftype) {
		switch (ftype) {
			case BLANK:
			case NUMBER:
			case RAW_TEXT:
			case CONST_PI:
			case CONST_E:
				return 0;
			case ADD:
			case MULTIPLY:
			case SUBTRACT:
			case DIVIDE:
			case POWER:
			case SQUARE:
			case MULT_INVERSE:
			case EXP_E:
			case EXP_10:
			case NCK:
			case NPK:
			case REMAINDER:
			case GCD:
			case LCM:
				return 2;
			case SOURCE:
			case NEGATIVE:
			case ABS:
			case SQRT:
			case LN:
			case LOG10:
			case SINE:
			case COSINE:
			case TANGENT:
			case ARCSINE:
			case ARCCOSINE:
			case ARCTANGENT:
			case HYPSINE:
			case HYPCOSINE:
			case HYPTANGENT:
			case ARHYPSINE:
			case ARHYPCOSINE:
			case ARHYPTANGENT:
			case FACTORIAL:
				return 1;
				//TODO - throw exception?
			default:
				return 0;
		}
	}
	private static int getMaxLeaves(FunctionType ftype) {
		switch (ftype) {
			case BLANK:
			case NUMBER:
			case RAW_TEXT:
			case CONST_PI:
			case CONST_E:
				return 0;
			case ADD:
			case MULTIPLY:
			case GCD:
			case LCM:
				return Integer.MAX_VALUE;
			case SUBTRACT:
			case DIVIDE:
			case POWER:
			case SQUARE:
			case MULT_INVERSE:
			case EXP_E:
			case EXP_10:
			case NCK:
			case NPK:
			case REMAINDER:
				return 2;
			case SOURCE:
			case NEGATIVE:
			case ABS:
			case SQRT:
			case LN:
			case LOG10:
			case SINE:
			case COSINE:
			case TANGENT:
			case ARCSINE:
			case ARCCOSINE:
			case ARCTANGENT:
			case HYPSINE:
			case HYPCOSINE:
			case HYPTANGENT:
			case ARHYPSINE:
			case ARHYPCOSINE:
			case ARHYPTANGENT:
			case FACTORIAL:
				return 1;
				//TODO - throw exception?
			default:
				return 0;
		}
	}
	
	public Operation build(FunctionType ftype) {
		if (ftype == FunctionType.SOURCE) {
			return new SourceOperation(
					ftype,
					buildFunction(ftype),
					buildDisplay(ftype)
			);
		} else if (ftype == FunctionType.RAW_TEXT) {
			return new RawTextOperation(
					ftype,
					buildFunction(ftype),
					buildDisplay(ftype),
					buildClosureFlags(ftype)
			);
		}
		else {
			return new Operation(
					ftype,
					buildFunction(ftype),
					buildDisplay(ftype),
					buildClosureFlags(ftype)
			);
		}
		//else throw new RuntimeException("Cannot create source_obj node instance");
	}
	
	// Returns whether the args can be shuffled or not
	public void buildFuncOverSubtree(ListTree<Operation> subtree, FunctionType ftype) {
		final double tmp = number;
		switch(ftype){
		//getMinBranchCount(ftype),
		//getMaxBranchCount(ftype)
		
			// 0-arg
			case NUMBER:
			case BLANK:
			case RAW_TEXT:
			case CONST_PI:
			case CONST_E:
				subtree.setSubTree(0, build(ftype));
				return;
				//return false;
			// 2+ args
			case ADD:
			case SUBTRACT:
			case MULTIPLY:
			case DIVIDE:
			case POWER:
			case NCK:
			case NPK:
			case REMAINDER:
			case GCD:
			case LCM:
				subtree.addRoot(0, build(ftype),
						getMinLeaves(ftype), getMaxLeaves(ftype));
				subtree.addBranch(0, 1, build(FunctionType.BLANK),
						getMinLeaves(FunctionType.BLANK), getMaxLeaves(FunctionType.BLANK));
				return;
				//return true;
			// 1-arg
			case SOURCE:
			case NEGATIVE:
			case ABS:
			case SQRT:
			case LN:
			case LOG10:
			case SINE:
			case COSINE:
			case TANGENT:
			case ARCSINE:
			case ARCCOSINE:
			case ARCTANGENT:
			case HYPSINE:
			case HYPCOSINE:
			case HYPTANGENT:
			case ARHYPSINE:
			case ARHYPCOSINE:
			case ARHYPTANGENT:
			case FACTORIAL:
				subtree.addRoot(0, build(ftype),
						getMinLeaves(ftype), getMaxLeaves(ftype) );
				return;
				//return false;
			// 2-arg w/ default value
			case SQUARE:
				subtree.addRoot(0, build(FunctionType.POWER),
						getMinLeaves(FunctionType.POWER),
						getMaxLeaves(FunctionType.POWER) );
				number(2);
				subtree.addBranch(0, 1, build(FunctionType.NUMBER),
						getMinLeaves(FunctionType.NUMBER),
						getMaxLeaves(FunctionType.NUMBER) );
				number(tmp);
				return;
				//return false;
			case MULT_INVERSE:
				subtree.addRoot(0, build(FunctionType.POWER),
						getMinLeaves(FunctionType.POWER),
						getMaxLeaves(FunctionType.POWER) );
				number(-1);
				subtree.addBranch(0, 1, build(FunctionType.NUMBER),
						getMinLeaves(FunctionType.NUMBER),
						getMaxLeaves(FunctionType.NUMBER) );
				number(tmp);
				return;
				//return false;
			case EXP_E:
				subtree.addRoot(0, build(FunctionType.POWER),
						getMinLeaves(FunctionType.POWER),
						getMaxLeaves(FunctionType.POWER) );
				subtree.addBranch(0, 0, build(FunctionType.CONST_E),
						getMinLeaves(FunctionType.CONST_E),
						getMaxLeaves(FunctionType.CONST_E) );
				return;
				//return false;
			case EXP_10:
				subtree.addRoot(0, build(FunctionType.POWER),
						getMinLeaves(FunctionType.POWER),
						getMaxLeaves(FunctionType.POWER) );
				number(10);
				subtree.addBranch(0, 0, build(FunctionType.NUMBER),
						getMinLeaves(FunctionType.NUMBER),
						getMaxLeaves(FunctionType.NUMBER) );
				number(tmp);
				return;
				//return false;

			default:
				throw new RuntimeException("Invalid function-type code");
		}
	}

	
}
