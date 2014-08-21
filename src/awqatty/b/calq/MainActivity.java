package awqatty.b.calq;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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
import awqatty.b.CompositeSwitchEventListener.CompositeSwitchEventListenerBase.ListenerBoxSwitch;
import awqatty.b.CompositeSwitchEventListener.OnViewEventListener;
import awqatty.b.CustomExceptions.CalculationException;
import awqatty.b.FunctionDictionary.FunctionType;
import awqatty.b.GUI.NumberKeyboardListener;
import awqatty.b.GUI.PaletteManager;
import awqatty.b.GUI.PaletteboxAnimator;
import awqatty.b.GUI.SideButtonPaletteManager;
import awqatty.b.GUI.SwipePaletteManager;
import awqatty.b.GenericTextPresentation.NumberStringConverter;
import awqatty.b.JSInterface.MathmlLinksViewClient;
import awqatty.b.MathmlPresentation.MathmlTextPresBuilder;
import awqatty.b.OpTree.OpTree;
import awqatty.b.OpButtons.OperationButton;
import awqatty.b.ViewUtilities.ViewFinder;
import awqatty.b.ViewUtilities.ViewParentFinder;
import awqatty.b.ViewUtilities.ViewReplacer;

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
 * Shift textNum TextView to be constantly in view,
 * 		remove equals button
 * 
 * improve parentheses
 * 
 * make sure numText contains result in one line
 * 
 * convert single-palette view to be swipe up/down (not scroll) list
 * 
 * divide panels into fragments
 * 
 * TODO Bugs
 * 
 *****************************************************************************************/


public final class MainActivity extends Activity  implements
	SharedPreferences.OnSharedPreferenceChangeListener
	{
	
	/*********************************************************
	 * Private Fields
	 *********************************************************/
	private OpTree expression = null;
	private double result;
	private int blank_index;	// stores node loc. from calc.exception
	
	// Click Listeners stored for inflation of new palettes
	private PaletteManager pltmanager;
	private CompositeOnClickListener op_listener, swapplt_listener, delplt_listener;
	
	// Listener-Box Switches to (de)activate on-view-event listeners
	private ListenerBoxSwitch
			trigger_resetOpButton,
			trigger_setEqualToText,
			trigger_setTextToEqual,
			trigger_showNumKeys,
			trigger_hideNumKeys;

	// used to swap operators with shuffle button
	private View button_shuffle;
	private View button_temp;
	
	private View view_newpalette;	// local storage for context-menu operation
	
	// trigger for storage function in onDestroy()
	private RetainDataFragment<OpTree> fragment_retainOpTree;
	
	// Local References (used for faster access of commonly-accessed views)
	private WebView webview;
	private TextView number_text;
	
	//---------------------------------------------------
	// Init Functions
	@Override
	@SuppressLint("SetJavaScriptEnabled")
	@SuppressWarnings("unchecked")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		webview = (WebView) findViewById(R.id.webview);
		number_text = (TextView) findViewById(R.id.textNum);

		/*********************************************************
		 * Fragment Management
		 *********************************************************/
		
		// If activity was restarted, get old OpTree object
		fragment_retainOpTree = (RetainDataFragment<OpTree>)
				getFragmentManager().findFragmentByTag(
				getString(R.string.fragtag_retainOpTree) );
		
		if (fragment_retainOpTree == null) {
			fragment_retainOpTree = new RetainDataFragment<OpTree>();
			getFragmentManager().beginTransaction()
					.add(fragment_retainOpTree,
							getString(R.string.fragtag_retainOpTree) )
					.commit();
		}
		if (fragment_retainOpTree.getData() == null) {
			expression = new OpTree(new MathmlTextPresBuilder());
			fragment_retainOpTree.setData(expression);
		}
		else
			expression = fragment_retainOpTree.getData();
		
		fragment_retainOpTree.setRetainInstance(false);
				
		//refreshNumberText();

		/*********************************************************
		 * Set Button Listeners
		 *********************************************************/
		final CompositeOnClickListener
				eql_listener = new CompositeOnClickListener(2),
				del_listener = new CompositeOnClickListener(3),
				delpar_listener = new CompositeOnClickListener(3),
				web_listener = new CompositeOnClickListener(3),
				txt_listener = new CompositeOnClickListener(2),
				keys1_listener = new CompositeOnClickListener(1),
				keys2_listener = new CompositeOnClickListener(1),
				plt_listener = new CompositeOnClickListener(1),
				addplt_listener = new CompositeOnClickListener(2);
		op_listener = new CompositeOnClickListener(3);
		swapplt_listener = new CompositeOnClickListener(2);
		delplt_listener = new CompositeOnClickListener(2);
		
		trigger_resetOpButton = 
				op_listener.addSwitchListener(
				eql_listener.addSwitchListener(
				del_listener.addSwitchListener(
				delpar_listener.addSwitchListener(
				web_listener.addSwitchListener(
				txt_listener.addSwitchListener(
				plt_listener.addSwitchListener(
				addplt_listener.addSwitchListener(
				swapplt_listener.addSwitchListener(
				delplt_listener.addSwitchListener(
						new OnViewEventListener() {
							@Override
							public void onViewEvent(View v) {
								((MainActivity)v.getContext()).resetOpButton();
							}
						}
				))))))))));
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
				keys2_listener.addSwitchListener(
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
		
		addplt_listener.addListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity)v.getContext()).openContextMenu(v);
			}
		});
		swapplt_listener.addListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity)v.getContext()).openContextMenu(
						(new ViewParentFinder()).findViewsByTag((ViewGroup)
						v.getParent(), getString(R.string.tag_plt) ).get(0));
			}
		});
		delplt_listener.addListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity)v.getContext()).onClickDeletePalette(
						(new ViewParentFinder()).findViewsById(
						v, R.id.palettebox ).get(0) );
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
					keys2_listener.addSwitchListener(
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
			keys1_listener.addSwitchListener(trigger_resetOpButton);
			keys1_listener.addSwitchListener(trigger_setEqualToText);
			keys2_listener.addSwitchListener(trigger_resetOpButton);
			trigger_hideNumKeys = null;
			trigger_showNumKeys = null;
		}
		
		/********************************************************
		 * Fill Incomplete Views
		 ********************************************************/
		// Inflate Shuffle Button (substituted in dynamically for op buttons)
		button_shuffle = View.inflate(this, R.layout.button_shuffle, null);
		
		final SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
						
		// Get palette action mode preferences
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		final String plt_actionmode = pref.getString(
				getString(R.string.prefKey_paletteActionMode), null );
		
		if (plt_actionmode.equals(getString(
				R.string.prefValue_paletteActionMode_sidebutton ))) {
			pltmanager = new SideButtonPaletteManager(this,
					(getResources().getBoolean(R.bool.canDeletePalette)
							? R.layout.palettebox_button_swapdelright
							: R.layout.palettebox_button_swapright ),
					op_listener, swapplt_listener, delplt_listener);
		}
		else if (plt_actionmode.equals(getString(
				R.string.prefValue_paletteActionMode_swipe ))) {
			pltmanager = new SwipePaletteManager(this,
					(getResources().getBoolean(R.bool.canDeletePalette)
							? R.layout.palettebox_swipe_swapleft_delright
							: R.layout.palettebox_swipe_swapleftright ),
					op_listener, swapplt_listener, delplt_listener);
		}

		// Get old palette id's from preferences
		final String keybase = getString(R.string.prefkey_paletteIds_basestr);
		final int max = getResources().getInteger(R.integer.maxPaletteQuantity);
		
		// Collect old palette ids in list (use only basic palette if none exist)
		final List<Integer> palette_ids = new ArrayList<Integer>(max);
		
		for (int i=0; pref.contains(keybase+Integer.toString(i)) && i<max; ++i)
			palette_ids.add(pref.getInt(keybase+Integer.toString(i), 0));
		
		// Add Palettes to Layout
		if (pltmanager.addPalettes(palette_ids,
				findViewById(R.id.tmp_palette) ) == null &&
				pltmanager.addPalette(R.id.palette_basic,
				findViewById(R.id.tmp_palette) ) == null )
			throw new RuntimeException();
		
		pref.registerOnSharedPreferenceChangeListener(this);
		
		/***********************************************************
		 * Sets listeners to respective views
		 */
		//		Unique Views
		findViewById(R.id.buttonEqual).setOnClickListener(eql_listener);
		findViewById(R.id.buttonDel).setOnClickListener(del_listener);
		findViewById(R.id.buttonDelParent).setOnClickListener(delpar_listener);
		number_text.setOnClickListener(txt_listener);
		
		final View button_addPalette = findViewById(R.id.buttonNewPalette);
		if (button_addPalette !=  null) {
			registerForContextMenu(button_addPalette);
			button_addPalette.setLongClickable(false);
			button_addPalette.setOnClickListener(addplt_listener);
		}

		
		/*********************************************************
		 * Set local WebView object
		 *********************************************************/
		// Enable Javascript in Webview (warning suppressed)
		webview.getSettings().setJavaScriptEnabled(true);

		// Reroute "html-links" to MainActivity
		//*
		final MathmlLinksViewClient client = new MathmlLinksViewClient();
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
		String data = "<script type='text/x-mathjax-config'>"
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
			Log.d(this.toString(), "SVG used!");
		}
		else //*/
			data = data
					.replaceFirst("output/", "output/HTML-CSS")
					.replaceFirst("OUTPUT", "\"HTML-CSS\"");
		
		webview.loadDataWithBaseURL(
				"http://bar", data, "text/html","utf-8",null );

		/*********************************************************
		 * Set Number Keyboard
		 *********************************************************/
		NumberKeyboardListener num_keyslistener
				= new NumberKeyboardListener(number_text);
		num_keyslistener.setOnClickListener
				(NumberKeyboardListener.KEYS_EDIT, keys1_listener);
		num_keyslistener.setOnClickListener
				(NumberKeyboardListener.KEYS_ACTION, keys2_listener);
		
		KeyboardView k = (KeyboardView)findViewById(R.id.keyboardNum);
		k.setKeyboard(new Keyboard(this, R.xml.numkeys));
		k.setOnKeyboardActionListener(num_keyslistener);
		k.setEnabled(false);
	}

	// This method saves the opTree object during runtime changes, and no-when else
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		fragment_retainOpTree.setRetainInstance(true);
	}
		
	@Override
	protected void onStop() {
		super.onStop();
		
		/******************************************************
		 * Save Palette Choices To Preferences
		 ******************************************************/
		final SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		final SharedPreferences.Editor pref_edit = pref.edit();
		final String keybase = getString(R.string.prefkey_paletteIds_basestr);
		// Gets current ids
		List<Integer> ids = new ArrayList<Integer>();
		for (View palette : (new ViewFinder()).findViewsByTag(
				(ViewGroup)getWindow().getDecorView().getRootView(),
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

	/*
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	//*/
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_settings) {
			// Move to settings menu activity
			startActivity(new Intent(this, SettingsActivity.class));
			/* Displays settings in the same activity (bug: overlaps main screen)
			getFragmentManager().beginTransaction()
					.replace(android.R.id.content, new SettingsFragment())
					.addToBackStack(null)
					.commit();
			//*/
			return true;
		}
		else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	/************************************************************************
	 *  Creates the popup menu for switching out op-palettes(non-Javadoc)
	 */
	@Override
	public void onCreateContextMenu(
			ContextMenu menu, View v, ContextMenuInfo menu_info) {
		super.onCreateContextMenu(menu, v, menu_info);
		getMenuInflater().inflate(R.menu.menu_palettes, menu);
		
		// Save view locally (activity-scope) for later use
		view_newpalette = v;
		// Get ID's of existing palettes
		final ViewGroup root = (ViewGroup)getWindow().getDecorView().getRootView();
		final List<View> palettes = (new ViewFinder()).findViewsByTag(
				root, getString(R.string.tag_plt) );
		int[] ids = new int[palettes.size()];
		for (int i=0; i<palettes.size(); ++i)
			ids[i] = palettes.get(i).getId();
		
		// Disable choices for existing palettes
		for (int id : ids)
			menu.findItem(id).setEnabled(false);
	}
	
	// Assumes palette is a child of palette-swap's parent
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (view_newpalette.getId() == R.id.buttonNewPalette)
			pltmanager.addPalette(item.getItemId(), view_newpalette);
		
		else if (getString(R.string.tag_plt).equals(view_newpalette.getTag()))
			pltmanager.swapPalette(view_newpalette, item.getItemId());
		
		// Cleanup
		view_newpalette = null;
		
		return true;
	}
	
	@Override
	public void onContextMenuClosed(Menu menu) {
		super.onContextMenuClosed(menu);
		
		final PaletteboxAnimator boxanimator = pltmanager.getPaletteboxAnimator();
		if (boxanimator != null)
			boxanimator.animateAfterSwap();
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
		if (key.equals(getString(R.string.prefKey_paletteActionMode))) {
			final String plt_actionmode = pref.getString(key, null);
			if (plt_actionmode.equals(getString(
					R.string.prefValue_paletteActionMode_sidebutton ))) {
				pltmanager = new SideButtonPaletteManager(this,
						(getResources().getBoolean(R.bool.canDeletePalette)
								? R.layout.palettebox_button_swapdelright
								: R.layout.palettebox_button_swapright ),
						op_listener, swapplt_listener, delplt_listener);
			}
			else if (plt_actionmode.equals(getString(
					R.string.prefValue_paletteActionMode_swipe ))) {
				pltmanager = new SwipePaletteManager(this,
						(getResources().getBoolean(R.bool.canDeletePalette)
								? R.layout.palettebox_swipe_swapleft_delright
								: R.layout.palettebox_swipe_swapleftright ),
						op_listener, swapplt_listener, delplt_listener);
			}
			pltmanager.refreshPaletteBoxes();
		}
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
		
	public void raiseToast(String str) {
		Toast t = Toast.makeText(
				getApplicationContext(), str, Toast.LENGTH_SHORT );
		// Sets toast position to bottom of WebView
		t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0,
				webview.getBottom() - 10 );
		t.show();
	}
	
//	private static FunctionType getFtypeFromViewId(int id) {}
	
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
		FunctionType ftype = ((OperationButton)v).getFtype();
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
		expression.addNumber(d);
		refreshMathmlScreen();
		//refreshNumberText();
	}
	public void onNumKeyboardCancel() {}
	
	public void onClickDeletePalette(View v) {
		pltmanager.removePalette(v);
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
		temp_out = "<mstyle" + 
				" background='#9df'" + 
				" style='border: 1pt solid #000;" + 
				" padding: 2pt;'>" + 
				"<mn>1</mn><mo>+</mo><mn>" + 
				temp_count.toString() + 
				"</mn></mstyle>";
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
	
}
