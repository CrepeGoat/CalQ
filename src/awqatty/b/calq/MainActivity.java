package awqatty.b.calq;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import awqatty.b.CompositeSwitchEventListener.CompositeOnClickListener;
import awqatty.b.CompositeSwitchEventListener.CompositeOnLongClickListener;
import awqatty.b.CompositeSwitchEventListener.CompositeSwitchEventListenerBase.ListenerBoxSwitch;
import awqatty.b.CompositeSwitchEventListener.OnViewEventListener;
import awqatty.b.CustomExceptions.CalculationException;
import awqatty.b.FunctionDictionary.FunctionType;
import awqatty.b.GUI.NumberKeyboardListener;
import awqatty.b.GenericTextPresentation.NumberStringConverter;
import awqatty.b.JSInterface.MathmlLinksViewClient;
import awqatty.b.MathmlPresentation.MathmlTextPresBuilder;
import awqatty.b.OpTree.OpTree;
import awqatty.b.ViewManipulation.ViewFinder;
import awqatty.b.ViewManipulation.ViewParentFinder;
import awqatty.b.ViewManipulation.ViewReplacer;

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
 * Fix highlighting, or shift to pure html display
 * 
 * Fix number-to-text conversion
 * 
 * Add _ftype values to buttons (to avoid switch table for OnPressOperator & addFunction)
 * 
 * GUI Scheme (colors, custom buttons, TextView borders)
 * 
 * Add code to allow for a "blank-click" on-screen, & allow for SVG output
 * 		(i.e. at each link, add "stopPropogation" functionality)
 * 
 * Add larger device support
 * (also change drawables to be API specific)
 * 
 * (?) Shift textNum TextView to be constantly in view,
 * 		remove equals button
 * 
 * NEED TO TEST:
 * 
 * tablet views
 * 
 * 
 *****************************************************************************************/


public final class MainActivity extends Activity {
	
	/*********************************************************
	 * Private Fields
	 *********************************************************/
	private OpTree expression;
	private double result;
	private int blank_index;
	
	private CompositeOnClickListener op_listener;
	private CompositeOnLongClickListener
			delplt_listener,
			swapplt_listener;
	
	private ListenerBoxSwitch
			trigger_resetOpButton,
			trigger_setEqualToText,
			trigger_setTextToEqual,
			trigger_showNumKeys,
			trigger_hideNumKeys;

	private View button_shuffle;
	private View button_temp;
	private View button_newpalette;
	private WebView webview;		// local references
	private TextView number_text;
	
	//---------------------------------------------------
	// Init Functions
	@Override
	@SuppressLint("SetJavaScriptEnabled")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		expression = new OpTree(new MathmlTextPresBuilder());
		
		webview = (WebView) findViewById(R.id.webview);
		number_text = (TextView) findViewById(R.id.textNum);
		
		//refreshNumberText();
		
		final ViewFinder finder = new ViewFinder();
		final ViewReplacer replacer = new ViewReplacer();
		final ViewGroup op_panel = (ViewGroup)findViewById(R.id.panelOps);

		/********************************************************
		 * Fill Incomplete Views
		 ********************************************************/
		
		// Inflate Shuffle Button (substituted in dynamically for op buttons)
		button_shuffle = View.inflate(this, R.layout.button_shuffle, null);
		
		// Get old palette id's from preferences
		final SharedPreferences pref = getPreferences(MODE_PRIVATE);
		final String keybase = getString(R.string.prefkey_palette_basestr);
		final int max = getResources().getInteger(R.integer.maxPaletteQuantity);
		
		// Collect old palette ids in list (use only basic palette if none exist)
		final List<Integer> ids = new ArrayList<Integer>(max);
		for (int i=0; pref.contains(keybase+Integer.toString(i)) && i<max; ++i)
			ids.add(pref.getInt(keybase+Integer.toString(i), 0));
		if (ids.isEmpty())
			ids.add(R.id.palette_basic);
		
		// Create list of inflated palettes respective to id list
		final List<View> palette_list = new ArrayList<View>();
		// (Loop variables)
		ViewGroup container;
		View placeholder;
		View palette;
		for (int id : ids) {
			// Inflate palette-container layouts
			container = (ViewGroup)View.inflate(this, R.layout.palette_container, null);
			// Insert respective palette into container
			placeholder
				= finder.findViewsByTag(container, getString(R.string.tag_plt)).get(0);
			palette = View.inflate(this, getXmlLayoutFromId(id), null);
			replacer.replaceView(placeholder, palette);
			// Add palette container to list
			palette_list.add(container);
		}
		
		// Replace palette-container placeholder with palette containers
		placeholder	= finder.findViewsById(op_panel, R.id.tmpPaletteHolderLoc).get(0);
		replacer.replaceView(placeholder, palette_list);

		/*********************************************************
		 * Set Button Listeners
		 *********************************************************/
		final CompositeOnClickListener
				eql_listener = new CompositeOnClickListener(2),
				del_listener = new CompositeOnClickListener(3),
				delpar_listener = new CompositeOnClickListener(3),
				web_listener = new CompositeOnClickListener(3),
				txt_listener = new CompositeOnClickListener(2),
				keys_listener = new CompositeOnClickListener(1);
		op_listener = new CompositeOnClickListener(3);
		delplt_listener = new CompositeOnLongClickListener(2);
		swapplt_listener = new CompositeOnLongClickListener(2);
		

		trigger_resetOpButton = 
				op_listener.addSwitchListener(
				eql_listener.addSwitchListener(
				del_listener.addSwitchListener(
				delpar_listener.addSwitchListener(
				web_listener.addSwitchListener(
				txt_listener.addSwitchListener(
				delplt_listener.addSwitchListener(
				swapplt_listener.addSwitchListener(
						new OnViewEventListener() {
							@Override
							public void onViewEvent(View v) {
								((MainActivity)v.getContext()).resetOpButton();
							}
						}
				))))))));
		trigger_setEqualToText = 
				eql_listener.addSwitchListener(
						new OnViewEventListener() {
							@Override
							public void onViewEvent(View v) {
								((MainActivity)v.getContext()).setEqualToText();
							}
						}
				);
		trigger_setTextToEqual = 
				op_listener.addSwitchListener(
				del_listener.addSwitchListener(
				delpar_listener.addSwitchListener(
				keys_listener.addSwitchListener(
				web_listener.addSwitchListener(
						new OnViewEventListener() {
							@Override
							public void onViewEvent(View v) {
								((MainActivity)v.getContext()).setTextToEqual();
							}
						}
				)))));
		
		trigger_resetOpButton.disableListener();
		trigger_setEqualToText.enableListener();
		trigger_setTextToEqual.disableListener();
		
		op_listener.addListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					((MainActivity)v.getContext()).onClickOperator(v);
				}
		});
		eql_listener.addListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity)v.getContext()).onClickEquals(v);
			}
		});
		del_listener.addListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					((MainActivity)v.getContext()).onClickDelete(v);
				}
		});
		delpar_listener.addListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity)v.getContext()).onClickDeleteParent(v);
			}
		});
		// web_listener has no default onClickListener
		txt_listener.addListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					((MainActivity)v.getContext()).onClickNumberText(v);
				}
		});
		swapplt_listener.addListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				((MainActivity)v.getContext()).openContextMenu(v);
				return true;
			}
		});
		delplt_listener.addListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				((MainActivity)v.getContext()).onClickDeletePalette(v);
				return true;
			}
		});

		
		// Accommodates large screens that do not hide the number keyboard
		final View button_num = findViewById(R.id.buttonNum);
		if (button_num != null) {
			// Set listeners to _number-keyboard button
			final CompositeOnClickListener
					num_listener = new CompositeOnClickListener(3);
			num_listener.addSwitchListener(trigger_resetOpButton);
			num_listener.addSwitchListener(trigger_setEqualToText);
			num_listener.addListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						((MainActivity)v.getContext()).onClickNumbersButton(v);
					}
			});
			button_num.setOnClickListener(num_listener);

			//Set trigger to show keyboard
			trigger_showNumKeys = 
					txt_listener.addSwitchListener(
							new OnViewEventListener() {
								@Override
								public void onViewEvent(View v) {
									((MainActivity)v.getContext()).showNumKeys();
								}
							}
					);
			// Set trigger to hide keyboard
			trigger_hideNumKeys = 
					web_listener.addSwitchListener(
					keys_listener.addSwitchListener(
							new OnViewEventListener() {
								@Override
								public void onViewEvent(View v) {
									((MainActivity)v.getContext()).hideNumKeys();
								}
							}
					));
			trigger_showNumKeys.enableListener();
			trigger_hideNumKeys.disableListener();
		}
		else {
			keys_listener.addSwitchListener(trigger_resetOpButton);
			trigger_hideNumKeys = null;
			trigger_showNumKeys = null;
		}
		
		// Sets listeners to respective views
		//		Unique Views
		findViewById(R.id.buttonEqual).setOnClickListener(eql_listener);
		findViewById(R.id.buttonDel).setOnClickListener(del_listener);
		findViewById(R.id.buttonDelParent).setOnClickListener(delpar_listener);
		number_text.setOnClickListener(txt_listener);
		
		setPaletteButtonListeners(op_panel, finder);
		
		final View button_addPalette = findViewById(R.id.buttonNewPalette);
		if (button_addPalette != null) {
			registerForContextMenu(button_addPalette);
			button_addPalette.setOnLongClickListener(swapplt_listener);
		}
		
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
						+"src='file:///android_asset/MathJax-2.3-trim/MathJax.js'>"
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
		NumberKeyboardListener num_keyslistener = new NumberKeyboardListener(number_text);
		num_keyslistener.setOnClickListener(keys_listener);
		
		KeyboardView k = (KeyboardView)findViewById(R.id.keyboardNum);
		k.setKeyboard(new Keyboard(this, R.xml.num_keys));
		k.setOnKeyboardActionListener(num_keyslistener);
		k.setEnabled(false);
		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		/******************************************************
		 * Save Palette Choices To Preferences
		 ******************************************************/
		final SharedPreferences pref = getPreferences(MODE_PRIVATE);
		final SharedPreferences.Editor pref_edit = pref.edit();
		final String keybase = getString(R.string.prefkey_palette_basestr);
		// Gets current ids
		List<Integer> ids = new ArrayList<Integer>();
		for (View palette : (new ViewFinder()).findViewsByTag(
				(ViewGroup)findViewById(R.id.panelOps),
				getString(R.string.tag_plt) )) {
			ids.add(palette.getId());
		}
		
		int i;
		// Adds/Replaces with current keys
		for (i=0; i<ids.size(); ++i)
			pref_edit.putInt(keybase+Integer.toString(i), ids.get(i));
		// Deletes excess keys
		for (; pref.contains(keybase+Integer.toString(i)); ++i) {
			pref_edit.remove(keybase+Integer.toString(i));
		}
		pref_edit.apply();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// Creates the popup menu for switching out op-palettes
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menu_info) {
		super.onCreateContextMenu(menu, v, menu_info);
		getMenuInflater().inflate(R.menu.menu_palettes, menu);
		if (v.getId() == R.id.buttonNewPalette) {
			button_newpalette = v;
			// Get ID's of existing palettes
			final ViewGroup panel_ops = (ViewGroup)findViewById(R.id.panelOps);
			final List<View> palettes = (new ViewFinder()).findViewsByTag(
					panel_ops, getString(R.string.tag_plt) );
			int[] ids = new int[palettes.size()];
			for (int i=0; i<palettes.size(); ++i)
				ids[0] = palettes.get(0).getId();
			
			// Disable choices for existing palettes
			for (int id : ids)
				menu.findItem(id).setEnabled(false);
		}
		else {
			button_newpalette = getPaletteFromSwapButton(v);
			// Disable choices for existing palettes
			menu.findItem(button_newpalette.getId()).setEnabled(false);
		}
	}
	
	// Assumes palette is a child of palette-swap's parent
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final int id2 = item.getItemId();
		final ViewReplacer replacer = new ViewReplacer();
		
		if (button_newpalette.getId() == R.id.buttonNewPalette) {
			final ViewFinder finder = new ViewFinder();
			
			// Inflate palette-container layouts
			final ViewGroup container = (ViewGroup)View.inflate(
					this, R.layout.palette_container, null );
			// Insert respective palette into container
			final View placeholder = finder.findViewsByTag(
					container, getString(R.string.tag_plt) ).get(0);
			replacer.replaceView(placeholder, 
					View.inflate(this, getXmlLayoutFromId(id2), null) );
			
			// Add new palette behind "add" button
			replacer.insertView(button_newpalette, container);
			
			// Set Button Listeners to new palette
			setPaletteButtonListeners(container, finder);
		}
		else if (getString(R.string.tag_plt).equals(button_newpalette.getTag())) {
			View palette2 = findViewById(id2);
			
			// Condition: palette is already on-screen
			if (palette2 != null) {
				// Switch selected palettes
				replacer.switchViews(button_newpalette, palette2);
			}
			// Condition: palette has to be inflated from layout xml
			else {
				// Inflate new palette from XML
				palette2 = View.inflate(this, getXmlLayoutFromId(id2), null);
				replacer.replaceView(button_newpalette, palette2);
				// Set button listeners for buttons in inflated palette
				for (View op_button : (new ViewFinder())
						.findViewsByTag((ViewGroup)palette2, getString(R.string.tag_op)) )
					op_button.setOnClickListener(op_listener);
			}
		}
		// Cleanup
		button_newpalette = null;
		
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

	/*
	private void refreshNumberText() {
		number_text.setText("");
		// Sets result of selection to textNum hint
		try {			
			// Calculate result (throws CalcEx)
			result = expression.getSelectionCalculation();
			// Set text representation of result to view
			number_text.setHint(NumberStringConverter.toCompressedString(result,13));
		}
		catch (CalculationException ce) {
			// Set failed index
			blank_index = (Integer)ce.getCauseObject();
			number_text.setHint(getString(R.string.textNum_blank));
		}
	}
	//*/
	
	// Called on button click after Equals has been clicked
	public void setEqualToText() {
		((ViewSwitcher) findViewById(R.id.switchEqText)).showNext();
		trigger_setTextToEqual.enableListener();
		trigger_setEqualToText.disableListener();
	}
	public void setTextToEqual() {
		((ViewSwitcher) findViewById(R.id.switchEqText)).showPrevious();
		// TODO this shouldn't have to be here
		number_text.setText("");
		trigger_setTextToEqual.disableListener();
		trigger_setEqualToText.enableListener();
	}

	public void hideNumKeys() {
		// Shows button panel
		findViewById(R.id.panelOps).setVisibility(View.VISIBLE);
		/*** Use only if root view is a RelativeLayout ***
		// Aligns bottom of WebView back to button panel
		((RelativeLayout.LayoutParams)number_text.getLayoutParams())
				.addRule(RelativeLayout.ABOVE, R.id.opPanel);
		//*/
		// Hides Keyboard
		KeyboardView keyboard = (KeyboardView)findViewById(R.id.keyboardNum);
		keyboard.setVisibility(View.GONE);
		keyboard.setEnabled(false);
		// Set triggers
		trigger_showNumKeys.enableListener();
		trigger_hideNumKeys.disableListener();
	}
	public void showNumKeys() {
		// Shows keyboard
		KeyboardView keyboard = (KeyboardView)findViewById(R.id.keyboardNum);
		keyboard.setVisibility(View.VISIBLE);
		keyboard.setEnabled(true);
		/*** Use only if root view is a RelativeLayout ***
		// Aligns bottom of WebView to top of keyboard
		((RelativeLayout.LayoutParams)number_text.getLayoutParams())
				.addRule(RelativeLayout.ABOVE, R.id.keyboardNum);
		//*/
		// Hides button panel
		findViewById(R.id.panelOps).setVisibility(View.GONE);
		// Set triggers
		trigger_showNumKeys.disableListener();
		trigger_hideNumKeys.enableListener();
	}
	
	public void setPaletteButtonListeners(ViewGroup container, ViewFinder finder) {
		List<View> buttons;
		//		Operation Buttons
		buttons = finder.findViewsByTag(
				container, getString(R.string.tag_op) );
		for (View button_operator : buttons)
			button_operator.setOnClickListener(op_listener);
		//		Add/Swap-Palette Buttons		
		buttons = finder.findViewsById(
				container, R.id.buttonSwapPalette );
		for (View button_swapPalette : buttons) {
			registerForContextMenu(button_swapPalette);
			button_swapPalette.setOnLongClickListener(swapplt_listener);
		}
		//		Delete-Palette Buttons
		buttons = finder.findViewsById(
				container, R.id.buttonRemovePalette );
		for (View button_removePalette : buttons)
			button_removePalette.setOnLongClickListener(delplt_listener);
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
		case R.id.buttonAbs:
			return FunctionType.ABS;
			
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
			return FunctionType.PI;
		// vvv Occurs only under improper use vvv
		default:
			return null;
		}
	}
	
	private int getXmlLayoutFromId(int id) {
		switch (id) {
		case R.id.palette_basic:
			return R.layout.palette_basic;
		case R.id.palette_trig:
			return R.layout.palette_trig;
		// vvv Occurs only under improper use vvv
		default:
			return 0;
		}
	}
	
	private View getPaletteFromSwapButton(View swap_button) {
		final List<View> views = (new ViewParentFinder()).findParentViewsByTag(
				(ViewGroup) swap_button.getParent(),
				getString(R.string.tag_plt) );
		return (views.size() == 1 ? views.get(0) : null);
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
		//refreshNumberText();
	}
	
	public void onClickEquals(View v) {
		try {			
			// Calculate result (throws CalcEx)
			result = expression.getCalculation();

			expression.setSelection(0);
			refreshMathmlScreen();

			// Set text representation of result to view
			((TextView) findViewById(R.id.textNum))
					.setHint(NumberStringConverter.toString(result));
		}
		catch (CalculationException ce) {
			// Set text representation of result to view
			((TextView) findViewById(R.id.textNum))
					.setHint(getString(R.string.textNum_blank));

			// set failed index
			blank_index = (Integer)ce.getCauseObject();
		}
	}
	// Called when the result text box is clicked
	public void onClickNumberText(View v) {		
		if (number_text.getHint() == getString(R.string.textNum_blank)) {
			expression.setSelection(blank_index);
			number_text.setText(getString(R.string.textNum_default));
		}
		else {
			number_text.setText(number_text.getHint());
		}
	}
		
	// Called when the user clicks the delete button
	public void onClickDelete(View v) {
		expression.delete();
		refreshMathmlScreen();
		//refreshNumberText();
	}

	// Called when the user clicks the delete-parent button
	public void onClickDeleteParent(View v) {
		expression.deleteParent();
		refreshMathmlScreen();
		//refreshNumberText();
	}

	// Called when the user clicks an operator button
	public void onClickOperator(View v) {
		// Adds function to expression
		FunctionType ftype = getFtypeFromViewId(v.getId());
		expression.addFunction(ftype);	// sets selector in function
		refreshMathmlScreen();
		//refreshNumberText();
		
		// Replaces op-button with shuffle button
		// TODO replace decision condition with something with better style
		if (ftype.defaultArgCount() > 1) {
			(new ViewReplacer()).replaceView(v, button_shuffle);
			button_temp = v;
			trigger_resetOpButton.enableListener();
		}
	}
	public void onClickShuffle(View v) {
		expression.shuffleOrder();
		refreshMathmlScreen();
		// Selection remains the same in shuffle, no refreshNumberText() necessary
	}
	public void resetOpButton() {
		// Replaces shuffle button w/ op-button
		(new ViewReplacer()).replaceView(button_shuffle, button_temp);
		button_temp = null;
		trigger_resetOpButton.disableListener();
	}
	
	// Called when the user clicks the _number insertion button
	public void onClickNumbersButton(View v) {
		showNumKeys();
		if (number_text.getText() == "")
			number_text.setText(getString(R.string.textNum_default));
	}
	public void onNumKeyboardResult(double d) {
		hideNumKeys();
		expression.addNumber(d);
		refreshMathmlScreen();
		//refreshNumberText();
	}
	public void onNumKeyboardCancel() {
		hideNumKeys();
	}
	
	public void onClickDeletePalette(View v) {
		v = (new ViewParentFinder()).findParentViewsById(
				(ViewGroup) v.getParent(),
				R.id.panel_paletteContainer ).get(0);
		((ViewGroup)v.getParent()).removeView(v);
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
