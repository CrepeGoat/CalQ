package awqatty.b.OpButtons;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import awqatty.b.FunctionDictionary.FunctionType;
import awqatty.b.calq.R;

public class OperationButton extends Button {
		
	public static final FunctionType getFtypeFromId(int id) {
		switch (id) {
		// Basic Palette
		case R.id.buttonSum:
			return FunctionType.ADD;
		case R.id.buttonDiff:
			return FunctionType.SUBTRACT;
		case R.id.buttonProd:
			return FunctionType.MULTIPLY;
		case R.id.buttonQuot:
			return FunctionType.DIVIDE;
		case R.id.buttonNeg:
			return FunctionType.NEGATIVE;
		case R.id.buttonAbs:
			return FunctionType.ABS;
		case R.id.buttonSqr:
			return FunctionType.SQUARE;
		case R.id.buttonMultInv:
			return FunctionType.MULT_INVERSE;
		
		// Power/Log Palette
		case R.id.buttonPow:
			return FunctionType.POWER;
		case R.id.buttonSqrt:
			return FunctionType.SQRT;
		case R.id.buttonExpE:
			return FunctionType.EXP_E;
		case R.id.buttonExp10:
			return FunctionType.EXP_10;
		case R.id.buttonLogE:
			return FunctionType.LN;
		case R.id.buttonLog10:
			return FunctionType.LOG10;
		case R.id.buttonConstE:
			return FunctionType.CONST_E;
		
		// Trig. Palette
		case R.id.buttonSin:
			return FunctionType.SINE;
		case R.id.buttonCos:
			return FunctionType.COSINE;
		case R.id.buttonTan:
			return FunctionType.TANGENT;
		case R.id.buttonAsin:
			return FunctionType.ARCSINE;
		case R.id.buttonAcos:
			return FunctionType.ARCCOSINE;
		case R.id.buttonAtan:
			return FunctionType.ARCTANGENT;
		case R.id.buttonPi:
			return FunctionType.CONST_PI;
		
		// Trig. Palette
		case R.id.buttonHypSin:
			return FunctionType.HYPSINE;
		case R.id.buttonHypCos:
			return FunctionType.HYPCOSINE;
		case R.id.buttonHypTan:
			return FunctionType.HYPTANGENT;
		case R.id.buttonAHypSin:
			return FunctionType.ARHYPSINE;
		case R.id.buttonAHypCos:
			return FunctionType.ARHYPCOSINE;
		case R.id.buttonAHypTan:
			return FunctionType.ARHYPTANGENT;
		
		// Permutation Palette
		case R.id.buttonFact:
			return FunctionType.FACTORIAL;
		case R.id.buttonNPK:
			return FunctionType.NPK;
		case R.id.buttonNCK:
			return FunctionType.NCK;
			
		// Arithmetic Palette
		case R.id.buttonRmdr:
			return FunctionType.REMAINDER;
		case R.id.buttonGCD:
			return FunctionType.GCD;
		case R.id.buttonLCM:
			return FunctionType.LCM;
		
		// vvv Occurs only under improper use vvv
		default:
			return null;
		}
	}

	
	private FunctionType ftype;
	public FunctionType getFtype() {return ftype;}
	public void setFtype(FunctionType ft) {ftype = ft;}


	public OperationButton(Context context) {
		super(context);
	}

	public OperationButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO get Ftype another way, this is sloppy
		//		& requires more labor to maintain
		ftype = getFtypeFromId(getId());
	}

	public OperationButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO get Ftype another way, this is sloppy
		//		& requires more labor to maintain
		ftype = getFtypeFromId(getId());
	}

}
