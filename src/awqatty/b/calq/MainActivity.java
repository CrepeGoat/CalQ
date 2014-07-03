package awqatty.b.calq;

import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import android.widget.Toast;

import awqatty.b.CustomExceptions.CalculationException;
import awqatty.b.FunctionDictionary.FunctionType;
import awqatty.b.GUI.CompositeOnClickListener;
import awqatty.b.GUI.NumberKeyboardListener;
import awqatty.b.JSInterface.*;
import awqatty.b.MathmlPresentation.NumberStringConverter;
import awqatty.b.OpTree.OpTree;



/***************************************************************************************
 * Author - Becker Awqatty
 * 
 * References:
 * StackOverflow Community
 * MathJax in Android Sample Code (https://github.com/leathrum/android-apps/blob/master
 * 		/MathJaxApp/mml-full/MainActivity.java)
 * 
 * TODO
 * 
 * Fix highlighting, or eliminate highlight tags
 * 
 * Use drawables for NumKey keys
 * 
 * Add ftype values to buttons (to avoid switch table for OnPressOperator & addFunction)
 * 
 * GUI Scheme (colors, custom buttons, TextView borders)
 * 
 * Add more operators (tabbed view?)
 * 
 * Add code to allow for a "blank-click" on-screen, & allow for SVG output
 * 		(i.e. at each link, add "stopPropogation" functionality)
 * 
 * 
 *****************************************************************************************/


public final class MainActivity extends Activity {
	
	/*********************************************************
	 * Private Fields
	 *********************************************************/
	private final OpTree expression = new OpTree();
	private double result;
	private CompositeOnClickListener.ListenerSwitch
			trigger_resetEqualButton,
			trigger_resetOpButton;

	private View button_shuffle;
	private View button_temp;
	
	private WebView webview;		// local reference
	
	//---------------------------------------------------
	// Init Functions
	@Override
	@SuppressLint("SetJavaScriptEnabled")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		webview = (WebView) findViewById(R.id.webview);

		/*********************************************************
		 * Set Button Listeners
		 *********************************************************/
		CompositeOnClickListener
				op_listener = new CompositeOnClickListener(3),
				num_listener = new CompositeOnClickListener(3),
				del_listener = new CompositeOnClickListener(3),
				web_listener = new CompositeOnClickListener(1);
		
		trigger_resetOpButton = 
				op_listener.addSwitchListener(
				num_listener.addSwitchListener(
				del_listener.addSwitchListener(
				web_listener.addSwitchListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								((MainActivity)v.getContext()).resetOpButton();
							}
						}
				))));
		trigger_resetOpButton.deactivateListener();
		
		trigger_resetEqualButton = 
				op_listener.addSwitchListener(
				num_listener.addSwitchListener(
				del_listener.addSwitchListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								((MainActivity)v.getContext()).resetEqualButton();
							}
						}
				)));
		trigger_resetEqualButton.deactivateListener();
		
		op_listener.addListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity)v.getContext()).onClickOperator(v);
			}
		});
		num_listener.addListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity)v.getContext()).onClickNumber(v);
			}
		});
		del_listener.addListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity)v.getContext()).onClickDelete(v);
			}
		});
				
		// Sets listeners to respective views
		findViewById(R.id.buttonNum).setOnClickListener(num_listener);
		findViewById(R.id.buttonDel).setOnClickListener(del_listener);
		
		View[] op_buttons = {
		findViewById(R.id.buttonSum),
		findViewById(R.id.buttonDiff),
		findViewById(R.id.buttonProd),
		findViewById(R.id.buttonQuot),
		findViewById(R.id.buttonSqr),
		findViewById(R.id.buttonPow),
		findViewById(R.id.buttonSqrt),
		};
		for (View v : op_buttons) {
			v.setOnClickListener(op_listener);
		}
		
		button_shuffle = View.inflate(this, R.layout.button_shuffle, null);

		/*********************************************************
		 * Set local WebView object
		 *********************************************************/
		// Enable Javascript in Webview (warning suppressed)
		webview.getSettings().setJavaScriptEnabled(true);

		// Reroute "html-links" to MainActivity
		//*
		MathmlLinksViewClient client = new MathmlLinksViewClient();
		client.setOnClickListener(web_listener);
		webview.setWebViewClient(client);
		//*/

		// vvv Used for Javascript onclick methods vvv
		/*
		JSBinder binder = new JSBinder(webview);
		binder.setOnClickListener(web_listener);
		webview.addJavascriptInterface(binder, "Android");
		//*/
		
		// Loads initial MathJax configuration
		//	(https://github.com/leathrum/android-apps/blob/master
		//		/MathJaxApp/mml-full/MainActivity.java)
		String base_url = "<script type='text/x-mathjax-config'>"
					+"MathJax.Hub.Config({ "
						+"showMathMenu: false, "
						+"jax: ['input/MathML','output/'], "
						+"extensions: ['mml2jax.js'], "
						+"TeX: { extensions: ['noErrors.js','noUndefined.js'] }, "
						+"OUTPUT: { scale: 175 }, "
						+"});</script>"
					+"<script type='text/javascript' "
						+"src='file:///android_asset/MathJax_2_3_custom/MathJax.js'>"
						+"</script>"
					/* Used for JavaScript binding
					+"<script>function JSOnClickMathml(id_tag){"
						+"Android.onClickMathml(id_tag);"
						+"return false;"
						+"}</script>"
					//*/
					+"<span id='math'></span>";
		// Chooses HTML-CSS vs. SVG, based on android version
		//		(disabled SVG, causes links to nest improperly)
		/*
		if (android.os.Build.VERSION.SDK_INT
				>= android.os.Build.VERSION_CODES.HONEYCOMB ) {
			base_url = base_url
					.replaceFirst("output/", "output/SVG")
					.replaceFirst("OUTPUT", "SVG");
			Log.d(this.toString(), "SVG USED!!!!!!!!!!");
		}
		else //*/
			base_url = base_url
					.replaceFirst("output/", "output/HTML-CSS")
					.replaceFirst("OUTPUT", "\"HTML-CSS\"");
		
		webview.loadDataWithBaseURL(
				"http://bar", base_url, "text/html","utf-8","" );
		
		/*********************************************************
		 * Set Number Keyboard
		 *********************************************************/
		KeyboardView k = (KeyboardView)findViewById(R.id.keyboardNum);
		k.setKeyboard(new Keyboard(this, R.xml.num_keys));
		k.setOnKeyboardActionListener(new NumberKeyboardListener(
				this, (TextView)findViewById(R.id.textNum) ));
		k.setEnabled(false);
		
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
	private void loadMathmlToScreen(String mathml_exp) {
		// Load a MathML example on-screen
		//	(https://github.com/leathrum/android-apps/blob/master
		//		/MathJaxApp/mml-full/MainActivity.java)
		/* 								  vv green
		 * TODO add color ( mathcolor='#000000')
		 * 							red	^^	^^ blue
		 */
		webview.loadUrl("javascript:document.getElementById('math').innerHTML='"
				+"<math xmlns=\"http://www.w3.org/1998/Math/MathML\" display=\"block\">"
				+"<mstyle displaystyle=\"true\""
				//+" mathcolor='#000000'"
				//+ " mathsize=1.2em"
				+">"
				// Insert MathML code here
				+ mathml_exp
				//
				+"</mstyle></math>';"
				+"\njavascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);"
				);
	}
	public void refreshMathmlScreen() {
		loadMathmlToScreen(expression.getTextPres());
	}
	
	private void hideKeyboard() {
		// Shows button panel
		findViewById(R.id.buttonPanel).setVisibility(View.VISIBLE);
		// Aligns bottom of WebView back to button panel
		((RelativeLayout.LayoutParams)webview.getLayoutParams())
				.addRule(RelativeLayout.ABOVE, R.id.buttonPanel);
		// Hides Keyboard
		findViewById(R.id.textNum).setVisibility(View.GONE);
		KeyboardView keyboard = (KeyboardView)findViewById(R.id.keyboardNum);
		keyboard.setVisibility(View.GONE);
		keyboard.setEnabled(false);
	}
	private void showKeyboard() {
		// Shows keyboard
		KeyboardView keyboard = (KeyboardView)findViewById(R.id.keyboardNum);
		keyboard.setVisibility(View.VISIBLE);
		keyboard.setEnabled(true);
		findViewById(R.id.textNum).setVisibility(View.VISIBLE);
		// Aligns bottom of WebView to top of keyboard
		((RelativeLayout.LayoutParams)webview.getLayoutParams())
				.addRule(RelativeLayout.ABOVE, R.id.textNum);
		// Hides button panel
		findViewById(R.id.buttonPanel).setVisibility(View.GONE);
	}
	
	public void raiseToast(String str) {
		Toast t = Toast.makeText(
				getApplicationContext(), str, Toast.LENGTH_SHORT );
		// Sets toast position to bottom of WebView
		t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0,
				webview.getBottom() - 10 );
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
	public void onClickMathml(int index) {
		// TODO (?) if clicked element is a child element of the current selection,
		//		set selector to the index of its parent
		expression.setSelection(index);
		refreshMathmlScreen();
	}
	
	// Called when the equals button is clicked
	public void onClickEquals(View v) {
		try {			
			// Calculate result (throws CalcEx)
			result = expression.getCalculation();
			
			expression.setSelection(0);
			refreshMathmlScreen();

			// Set text representation of result to view
			((TextView) findViewById(R.id.textRes))
					.setText(NumberStringConverter.toString(result));
			
			// Set ViewSwitcher from equal button to result text display
			((ViewSwitcher) findViewById(R.id.switchEqRes))
					.showNext();
			// Set onClick response to reset eq button
			trigger_resetEqualButton.activateListener();
		}
		catch (CalculationException ce) {
			// Raise toast to alert user that expression is incomplete
			raiseToast("Error: Expression is incomplete");
			
			// set selector to failed index
			expression.setSelection((Integer)ce.getCauseObject());
			refreshMathmlScreen();
		}
	}
	// Called when the result text box is clicked
	public void onClickResult(View v) {
		// Replaces current expression with calculated result value
		expression.setSelection(0);
		expression.addNumber(result);
		resetEqualButton();
		refreshMathmlScreen();
	}
	// Called on button click after Equals has been clicked
	public void resetEqualButton() {
		ViewSwitcher panel = (ViewSwitcher) findViewById(R.id.switchEqRes);
		panel.showPrevious();
		trigger_resetEqualButton.deactivateListener();
	}
	
	// Called when the user clicks the delete button
	public void onClickDelete(View v) {
		expression.delete();
		refreshMathmlScreen();
	}
	
	// Called when the user clicks an operator button
	public void onClickOperator(View v) {
		// Adds function to expression
		FunctionType ftype = getFtypeFromViewId(v.getId());
		expression.addFunction(ftype);	// sets selector in function
		refreshMathmlScreen();
		
		// Replaces op-button with shuffle button
		// TODO replace assertion with something with better style
		if (ftype.defaultArgCount() > 1) {
			ViewGroup parent = (ViewGroup) v.getParent();
			parent.addView(button_shuffle, parent.indexOfChild(v), v.getLayoutParams());
			parent.removeView(v);
			button_temp = v;
			trigger_resetOpButton.activateListener();
		}
	}
	public void onClickShuffle(View v) {
		//TODO remove
		Log.d(this.toString(), "Test: Shuffle button clicked!!!");
		expression.shuffleOrder();
		refreshMathmlScreen();
	}
	public void resetOpButton() {
		// Replaces shuffle button w/ op-button
		ViewGroup parent = (ViewGroup) button_shuffle.getParent();
		parent.addView(button_temp, parent.indexOfChild(button_shuffle), button_shuffle.getLayoutParams());
		parent.removeView(button_shuffle);
		trigger_resetOpButton.deactivateListener();
	}
	
	// Called when the user clicks the number insertion button
	public void onClickNumber(View v) {
		showKeyboard();
	}
	public void onNumKeyboardResult(double d) {
		hideKeyboard();
		expression.addNumber(d);
		refreshMathmlScreen();
	}
	public void onNumKeyboardCancel() {
		hideKeyboard();
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
		
		// Load a MathML example on-screen
		loadMathmlToScreen(temp_out);
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
