package awqatty.b.calq;

import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
//import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import android.widget.Toast;

import awqatty.b.CustomExceptions.CalculationException;
import awqatty.b.FunctionDictionary.FunctionType;
import awqatty.b.JSInterface.*;
import awqatty.b.MathmlPresentation.NumberStringConverter;
import awqatty.b.OpTree.OpTree;

/*
 * TODO
 * Add to app store
 * set up donation system
 * 
 * Fix highlighting, or eliminate highlight tags
 * 
 * Use drawables for NumKey keys
 * 
 * Add ftype values to buttons (to avoid switch table for OnPressOperator & addFunction)
 * Make MultiOnClickListener class, with ability to disable listeners w/o removal
 * 		(make listenerSwitch class, which can switch on/off listener w/o explicitly
 * 			granting access to listener, which should remain private)
 * 
 * GUI Scheme (colors, custom buttons, TextView borders)
 * Move Num button into main button block (i.e. equals, delete, etc.)
 * 
 * Add double-click actions to operator/delete buttons
 * 
 * Add more operators (tabbed view?)
 * 
 * Add code to allow for a "blank-click" on-screen, & allow for SVG output
 * 		(i.e. )
 * 
 * Bugs
 * x/0, sqrt(negatives) errors
 * 
 * 
 */


public class MainActivity extends Activity {
	
	private OpTree expression = new OpTree();
	// TODO necessary? vvv
	private double result;
	
	//---------------------------------------------------
	// Init Functions
	@Override
	@SuppressLint("SetJavaScriptEnabled")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Set local webview object
		WebView w = (WebView) findViewById(R.id.webview);
		// Enable Javascript in Webview (warning suppressed)
		w.getSettings().setJavaScriptEnabled(true);
		// Reroute "links" to MainActivity
		w.setWebViewClient(new MathmlLinksViewClient(this));
		// vvv Used for Javascript onclick methods vvv
		//		w.addJavascriptInterface(new JSObject(this), "onClick");
		
		// Loads initial MathJax configuration
		//	(https://github.com/leathrum/android-apps/blob/master
		//		/MathJaxApp/mml-full/MainActivity.java)
		String base_url = "<script type='text/x-mathjax-config'>"
					+"MathJax.Hub.Config({ "
						+"showMathMenu: false, "
						+"jax: ['input/MathML','output/'], "
						+"extensions: ['mml2jax.js'], "
						+"TeX: { extensions: ['noErrors.js','noUndefined.js'] }, "
						+"OUTPUT: { scale: 150 }, "
						+"});</script>"
					+"<script type='text/javascript' "
						+"src='file:///android_asset/MathJax_2_3_custom/MathJax.js'"
						+"></script><span id='math'></span>";
		// Chooses HTML-CSS vs. SVG, based on android version
		//		(disabled SVG, causes links to nest improperly)
		/*
		if (android.os.Build.VERSION.SDK_INT
				< android.os.Build.VERSION_CODES.HONEYCOMB )
			base_url = base_url
					.replaceFirst("output/", "output/SVG")
					.replaceFirst("OUTPUT", "SVG");
		else //*/
			base_url = base_url
					.replaceFirst("output/", "output/HTML-CSS")
					.replaceFirst("OUTPUT", "\"HTML-CSS\"");
		
		w.loadDataWithBaseURL(
				"http://bar", base_url, "text/html","utf-8","" );
		
		// (?) Disables animation for equals/result panel
		//((ViewSwitcher) findViewById(R.id.switchEqRes)).setAnimateFirstView(false);

		// Set up Number Keyboard
		//*
		KeyboardView k = (KeyboardView) findViewById(R.id.keyboardNum);
		k.setKeyboard(new Keyboard(this, R.xml.num_keys));
		k.setOnKeyboardActionListener(new NumberKeyboardListener(
				this, (TextView) findViewById(R.id.textNum) ));
		//*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	

	//////////////////////////////////////////////////////////////////////
	// INTERNAL FUNCTIONS
	//////////////////////////////////////////////////////////////////////
	private void loadMathmlToScreen(WebView w, String mathml_exp) {
		// Load a MathML example on-screen
		/* 								  vv green
		 * TODO add color ( mathcolor='#000000')
		 * 							red	^^	^^ blue
		 */
		w.loadUrl("javascript:document.getElementById('math').innerHTML='"
				+"<math xmlns=\"http://www.w3.org/1998/Math/MathML\" display=\"block\">"
				+"<mstyle displaystyle=\"true\""
				//+" mathcolor='#000000'"
				+">"
				// Insert MathML code here
				+ mathml_exp
				//
				+"</mstyle></math>';"
				+"\njavascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);"
				);
	}
	public void refreshScreen(WebView w) {
		loadMathmlToScreen(w, expression.getTextPres());
	}
	private void refreshScreen() {
		// Set local webview object
		WebView w = (WebView) findViewById(R.id.webview);
		refreshScreen(w);
	}

	private void resetEqualButton() {
		ViewSwitcher panel = (ViewSwitcher) findViewById(R.id.switchEqRes);
		if (panel.getDisplayedChild() != 0) {
			panel.showPrevious();
		}
	}
	
	public void raiseToast(String str) {
		Toast t = Toast.makeText(
				getApplicationContext(), str, Toast.LENGTH_SHORT );
		// Sets toast position to bottom of WebView
		t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0,
				findViewById(R.id.webview).getBottom() - 10 );
		t.show();
	}
	
	private FunctionType getFtypeFromViewId(int id) {
		switch (id) {
		case R.id.buttonSum:
			return FunctionType.ADD;
		case R.id.buttonDiff:
			return FunctionType.SUBTRACT;
		case R.id.buttonProd:
			return FunctionType.MULTIPLY;
		case R.id.buttonQuot:
			return FunctionType.DIVIDE;
		case R.id.buttonPow:
			return FunctionType.POWER;
		case R.id.buttonSqr:
			return FunctionType.SQUARE;
		case R.id.buttonSqrt:
			return FunctionType.SQRT;
		// vvv Occurs only under improper use vvv
		default:
			return null;
		}
	}
	

	//////////////////////////////////////////////////////////////////////
	// ON-CLICK/BUTTON METHODS
	//////////////////////////////////////////////////////////////////////
	// Called when a MathML element is clicked in the WebView
	public void onMathmlClick(int index) {
		// TODO (?) if clicked element is a child element of the current selection,
		//		set selector to the index of its parent
		expression.setSelection(index);
		refreshScreen();
	}
	
	// Called when the equals button is clicked
	public void onClickEquals(View v) {
		try {			
			// Calculate result (throws CalcEx)
			result = expression.getCalculation();
			
			expression.setSelection(0);
			refreshScreen();

			// Set text representation of result to view
			((TextView) findViewById(R.id.textRes))
					.setText(NumberStringConverter.toString(result));
			
			// Set ViewSwitcher from equal button to result text display
			((ViewSwitcher) findViewById(R.id.switchEqRes))
					.showNext();
			}
		catch (CalculationException ce) {
			// Raise toast to alert user that expression is incomplete
			raiseToast("Error: Expression is incomplete");
			
			// set selector to failed index
			expression.setSelection((Integer)ce.getCauseObject());
			refreshScreen();
		}
	}
	// Called when the result text box is clicked
	public void onClickResult(View v) {
		// Replaces current expression with calculated result value
		expression.setSelection(0);
		expression.addNumber(result);
		resetEqualButton();
		refreshScreen();
	}
	
	// Called when the user clicks the delete button
	public void onClickDelete(View v) {
		expression.delete();
		refreshScreen();
		// Changes are made -> switch from ans display to equal button
		resetEqualButton();
	}
	
	// Called when the user clicks an operator button
	// TODO add multiListener to operator buttons,
	//		for reactivating "=" button w/o performing a view lookup
	public void onClickOperator(View v) {
		expression.addFunction(getFtypeFromViewId(
				v.getId() ));	// sets selector in function
		refreshScreen();
		// Changes are made -> switch from ans display to equal button
		resetEqualButton();
	}
	
	// Called when the user clicks the number insertion button
	public void onClickNumber(View v) {
		((KeyboardView) findViewById(R.id.keyboardNum)).setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.textNum)).setVisibility(View.VISIBLE);
	}
	public void onNumKeyboardResult(double d) {
		// Hides Keyboard
		((KeyboardView) findViewById(R.id.keyboardNum)).setVisibility(View.GONE);
		((TextView) findViewById(R.id.textNum)).setVisibility(View.GONE);

		//	(Does not need to unset highlight on mortal objects)
		expression.addNumber(d);
		refreshScreen();
		// Changes are made -> switch from ans display to equal button
		resetEqualButton();
	}
	public void onNumKeyboardCancel() {
		// Hides Keyboard
		((KeyboardView) findViewById(R.id.keyboardNum)).setVisibility(View.GONE);
		((TextView) findViewById(R.id.textNum)).setVisibility(View.GONE);
	}

	//////////////////////////////////////////////////////////////////////
	// TEST FUNCTIONS
	//////////////////////////////////////////////////////////////////////

	//*
	// Testing elements
	Integer temp_count = 0;
	String temp_out;
	public void setMathmlExample(View v) {
		// Augment test expression
		++temp_count;
		temp_out = "<mstyle  background='#9df' style='border: 1pt solid #000; padding: 2pt;'>"
				+ "<mn>1</mn><mo>+</mo><mn>" + temp_count.toString() + "</mn></mstyle>";
		//temp_out += "<mo href=" + HtmlIdFormat.encloseIdInTags(0) + ">+</mo>"
				//+ "<mn href=" + HtmlIdFormat.encloseIdInTags(temp_count) + ">"
				//+ temp_count.toString() + "</mn>";
		
		// Set local webview object
		WebView w = (WebView) findViewById(R.id.webview);
		// Load a MathML example on-screen
		loadMathmlToScreen(w, temp_out);
	}
	//*/
	/*
	private int build_state = 0;
	public void setMathmlExample(View v) {
		resetEqualButton();
		// build simple example
		switch (build_state++) {
		case 1:
			expression.selection =0;
			expression.addFunction(FunctionType.ADD);
			expression.selection = 1;
			expression.addNumber(7);
			break;
		case 2:
			expression.selection = 2;
			expression.addNumber(2);
			break;
		case 3:
			expression.addFunction(FunctionType.DIVIDE);
			expression.selection = 4;
			expression.addNumber(3);
			break;
		case 0:
		default:
			break;
		}
		refreshScreen();
	}
	//*/


	/*
	 * // Test Code (TODO remove)
	 * Log.d(this.toString(), "Loaded MathML: " + mathml_exp);
	 */
}
