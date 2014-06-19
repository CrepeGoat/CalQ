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
import awqatty.b.OpTree.OpTree;

/*
 * TODO
 * Add to app store
 * set up donation system
 * 
 * Parenthesis Logic
 * Fix highlighting, or eliminate highlight tags
 * Trim MathJax package
 * Commutative Function Support
 * Add functionality to delete button (i.e. add "deleteParent" method in ArrayTree)
 * 
 * Cancel key in NumKeys
 * Use drawables for NumKey keys
 * 
 * Make keyboard stretch over full screen width
 * GUI Scheme (colors, custom buttons, TextView borders)
 * Move Num button into main button block (i.e. equals, delete, etc.)
 * 
 * Bugs
 * no auto-render on application startup
 *		(create bound Javascript function to trigger render on completion of setup?)
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
		w.setWebViewClient(new MathmlViewClient(this));
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
		// Loads initial blank element to screen
		refreshScreen(w);
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
	private void refreshScreen(WebView w) {
		loadMathmlToScreen(w, expression.getTextPres());
	}
	private void refreshScreen() {
		// Set local webview object
		WebView w = (WebView) findViewById(R.id.webview);
		refreshScreen(w);
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
	
	private void resetEqualButton() {
		ViewSwitcher panel = (ViewSwitcher) findViewById(R.id.switchEqRes);
		if (panel.getDisplayedChild() != 0) {
			panel.showPrevious();
		}
	}

	//////////////////////////////////////////////////////////////////////
	// ON-CLICK/BUTTON METHODS
	//////////////////////////////////////////////////////////////////////
	// Javascript Function
	public void onMathmlClick(int index) {
		// TODO if clicked element is a child element of the current selection,
		//		set selector to the index of its parent
		expression.unsetHighlight();
		expression.selection = index;
		expression.setHighlight();
		refreshScreen();
	}

	public void onClickEquals(View v) {
		try {
			expression.unsetHighlight();
			
			// Calculate result (throws CalcEx)
			result = expression.getCalculation();

			expression.selection = 0;
			refreshScreen();

			// Set text representation of result to view
			((TextView) findViewById(R.id.textRes))
					.setText(Double.toString(result));
			
			// Set ViewSwitcher from equal button to result text display
			((ViewSwitcher) findViewById(R.id.switchEqRes))
					.showNext();
			}
		catch (CalculationException ce) {
			// Raise toast to alert user that expression is incomplete
			Toast t = Toast.makeText(
					getApplicationContext(),
					"Error: Expression is incomplete",
					Toast.LENGTH_LONG );
			t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0,0);
			t.show();
			
			// set selector to failed index
			expression.selection = (Integer)ce.getCauseObject();
			expression.setHighlight();
			refreshScreen();
		}
	}
	public void onClickResult(View v) {
		// Replaces current expression with calculated result value
		//	(Does not need to unset highlight on mortal objects)
		expression.selection = 0;
		expression.addNumber(result);
		//	(Does not need to set highlight on root)
		refreshScreen();
	}
	
	public void onClickDelete(View v) {
		expression.delete();
		expression.setHighlight();
		refreshScreen();
		// Changes are made -> switch from ans display to equal button
		resetEqualButton();
	}
	
	public void onClickOperator(View v) {
		expression.unsetHighlight();
		expression.addFunction(getFtypeFromViewId(
				v.getId() ));	// sets selector in function
		expression.setHighlight();
		refreshScreen();
		// Changes are made -> switch from ans display to equal button
		resetEqualButton();
	}
	
	public void onClickNumber(View v) {
		/* TODO
		 * bring up keyboard for numeric input
		 * put result in new number object
		 */
		((KeyboardView) findViewById(R.id.keyboardNum)).setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.textNum)).setVisibility(View.VISIBLE);
	}
	public void onNumKeyboardResult(double d) {
		// Hides Keyboard
		((KeyboardView) findViewById(R.id.keyboardNum)).setVisibility(View.GONE);
		((TextView) findViewById(R.id.textNum)).setVisibility(View.GONE);

		//	(Does not need to unset highlight on mortal objects)
		expression.addNumber(d);
		expression.setHighlight();
		refreshScreen();
		// Changes are made -> switch from ans display to equal button
		resetEqualButton();
	}

	//////////////////////////////////////////////////////////////////////
	// TEST FUNCTIONS
	//////////////////////////////////////////////////////////////////////

	/*
	// Testing elements
	Integer temp_count = 0;
	String temp_out = "<mi>a</mi>";
	public void setMathmlExample(View v) {
		// Augment test expression
		++temp_count;
		temp_out += "<mo href=" + IdFormat.encloseIdInTags(0) + ">+</mo>"
				+ "<mn href=" + IdFormat.encloseIdInTags(temp_count) + ">"
				+ temp_count.toString() + "</mn>";
		
		// Set local webview object
		WebView w = (WebView) findViewById(R.id.webview);
		// Load a MathML example on-screen
		loadMathmlToScreen(w, temp_out);
	}
	
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
