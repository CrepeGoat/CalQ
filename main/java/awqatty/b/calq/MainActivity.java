package awqatty.b.calq;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import awqatty.b.CustomEventListeners.CompositeOnChangeListener;
import awqatty.b.CustomEventListeners.CompositeOnClickListener;
import awqatty.b.FunctionDictionary.FunctionType;
import awqatty.b.GUI.NumberKeyboardListener;
import awqatty.b.GUI.PaletteManager;
import awqatty.b.GUI.PaletteboxAnimator;
import awqatty.b.GUI.SideButtonPaletteManager;
import awqatty.b.GUI.SwipePaletteManager;
import awqatty.b.CustomEventListeners.ObservedOpTree;
import awqatty.b.GUI.TouchableMathView;
import awqatty.b.OpButtons.OperationButton;
import awqatty.b.ViewUtilities.ViewFinder;
import awqatty.b.ViewUtilities.ViewParentFinder;

/***************************************************************************************
 * Author - Becker Awqatty
 * 
 * References:
 * StackOverflow Community
 * MathJax in Android Sample Code (https://github.com/leathrum/android-apps/blob/master
 * 		/MathJaxApp/mml-full/MainActivity.java)
 * 
 * TODO new features
 * Add _ftype values to buttons (to avoid switch table for OnPressOperator & addFunction)
 * 
 * GUI Scheme (colors, custom buttons, TextView borders)
 * 
 * Add larger device support
 * (also change drawables to be API specific)
 *
 * convert single-palette view to be swipe up/down (not scroll) stack
 * 
 * divide panels into fragments (?)
 * 
 * change numkeys to normal buttons
 * 	-> makes a more uniform appearance
 *
 * 	implement 'undo' button (i.e., keep running log of actions)
 *
 * 	align objects to origin (will fix alignment of, e.g., exponential expressions)
 *
 * TODO working features
 * parentheses assignment inside neg function (f(x)=-x)
 *
 * on mathview reload: shift view to focus on selection
 *
 * get SVG images to render
 *
 * minimum shrink scale for elements (i.e. fix nested exponent functions)
 *
 *****************************************************************************************/


public final class MainActivity extends Activity
		implements SharedPreferences.OnSharedPreferenceChangeListener {
	
	/*********************************************************
	 * Private Fields
	 *********************************************************/
	private ObservedOpTree expression = null;
	//private int blank_index;	// stores node loc. from calc.exception
	
	// Click Listeners stored for inflation of new palettes
	private PaletteManager pltmanager;
	private View.OnClickListener swapplt_listener, delplt_listener;

	// Listener-Box Switches to (de)activate on-view-event listeners
	//private ListenerBoxSwitch
	//		trigger_setEqualToText,	// on click equal, or click num
	//		trigger_showNumKeys;	// on Num click, or textNum click
	//private SwitchedEventListenerBase.ListenerSwitch
	//		switch_unsetShuffleButton,
	//		switch_setTextToEqual,
	//		switch_hideNumKeys;

	// used to swap operators with shuffle button
	//private View button_shuffle;
	//private View button_temp;

	private View view_newpalette;	// local storage for context-menu operation
	
	// trigger for storage function in onDestroy()
	private RetainDataFragment<ObservedOpTree> fragment_retainOpTree;

	// Local References (used for faster access of commonly-accessed views)
	//private TextView number_text;

	//---------------------------------------------------
	// Init Functions
	@Override
	@SuppressLint("SetJavaScriptEnabled")
	@SuppressWarnings("unchecked")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		final TouchableMathView mathview = (TouchableMathView) findViewById(R.id.mathview);
		//number_text = (TextView) findViewById(R.id.textNum);

		/*********************************************************
		 * Fragment Management
		 *********************************************************/
		
		// If activity was restarted, get old OpTree object
		fragment_retainOpTree = (RetainDataFragment<ObservedOpTree>)
				getFragmentManager().findFragmentByTag(
				getString(R.string.fragtag_retainOpTree) );
		
		if (fragment_retainOpTree == null) {
			fragment_retainOpTree = new RetainDataFragment<>();
			getFragmentManager().beginTransaction()
					.add(fragment_retainOpTree,
							getString(R.string.fragtag_retainOpTree) )
					.commit();
		}
		if (fragment_retainOpTree.getData() == null) {
			expression = new ObservedOpTree(this);
			fragment_retainOpTree.setData(expression);
		}
		else
			expression = fragment_retainOpTree.getData();
		
		fragment_retainOpTree.setRetainInstance(false);
				
		//refreshNumberText();

		/*********************************************************
		 * Set Button Listeners
		 *********************************************************/
		// Make new onChangeListeners
		//		Collect listeners into one composite listener
		final CompositeOnChangeListener change_listener = 
				new CompositeOnChangeListener(4);
		//		Set change listener
		expression.setOnChangeListener(change_listener);
		
		//		Observer to refresh MathML screen
		change_listener.setOnChangeListener(mathview);

		//		Observer to Unset Shuffle Button
		/*
		final SwitchOnChangeListener listener_unsetShuffleButton = 
				new SwitchOnChangeListener(new OnChangeListener() {
					@Override
					public void onChange(ChangeEvent event) {
						if (event.source_obj instanceof ObservedOpTree &&
								event.timing_code == ObservedOpTree.POST_EVENT &&
								event.changetype_code != ObservedOpTree.EVENT_SHUFFLE) {
							unsetShuffleButton();
						}
					}
				});
		switch_unsetShuffleButton = listener_unsetShuffleButton.getSwitch();
		switch_unsetShuffleButton.disableListener();
		change_listener.setOnChangeListener(listener_unsetShuffleButton);
		//		Observer to SetTextToEqual
		final SwitchOnChangeListener listener_setTextToEqual = 
			new SwitchOnChangeListener(new OnChangeListener() {
				@Override
				public void onChange(ChangeEvent event) {
					setTextToEqual();
				}
			});
		switch_setTextToEqual = listener_setTextToEqual.getSwitch();
		switch_setTextToEqual.disableListener();		
		change_listener.setOnChangeListener(listener_setTextToEqual);
		//*/

		// Create OnClickListeners
		final CompositeOnClickListener
		//		eql_listener = new CompositeOnClickListener(1),
		//		txt_listener = new CompositeOnClickListener(1),
				keys1_listener = new CompositeOnClickListener(1);

		/*
		eql_listener.addListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickEquals(v);
			}
		});
		txt_listener.addListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickNumberText(v);
			}
		});
		trigger_setEqualToText = 
				eql_listener.addSwitchListener(
					new OnViewEventListener() {
						@Override
						public void onViewEvent(View v) {
							setEqualToText();
						}
					}
				);
		trigger_setEqualToText.enableListener();
		//*/
		swapplt_listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openContextMenu(
						(new ViewParentFinder()).findViewsByTag(
						v, getString(R.string.tag_plt) ).get(0));
			}
		};
		delplt_listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickDeletePalette(
						(new ViewParentFinder()).findViewsById(
						v, R.id.palettebox ).get(0) );
			}
		};
		
				
		// Platform-dependent logic
		//final View button_num = findViewById(R.id.buttonNum);
		// For smaller screens which hide the number keyboard
		//if (button_num != null) {
			// Set listeners to _number-keyboard button
			//final CompositeOnClickListener
			//		num_listener = new CompositeOnClickListener(2);
			//num_listener.addSwitchListener(trigger_setEqualToText);
			/*num_listener.addListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onClickNumbersButton(v);
				}
			});
			button_num.setOnClickListener(num_listener);

			//Set trigger to show keyboard
			trigger_showNumKeys = 
					txt_listener.addSwitchListener(
						new OnViewEventListener() {
							@Override
							public void onViewEvent(View v) {
								showNumKeys();
							}
						}
					);
			// Set trigger to hide keyboard
			final SwitchOnChangeListener keys2_listener = 
					new SwitchOnChangeListener(new OnChangeListener() {
						@Override
						public void onChange(ChangeEvent event) {
							hideNumKeys();
						}
					});
			switch_hideNumKeys = keys2_listener.getSwitch();
			switch_hideNumKeys.disableListener();
			change_listener.setOnChangeListener(keys2_listener);
			
			trigger_showNumKeys.enableListener();
			//*/
		//}
		// For larger screens which leave the number keyboard on-screen
		//else {
			//keys1_listener.addSwitchListener(trigger_setEqualToText);
			//switch_hideNumKeys = null;
			//trigger_showNumKeys = null;
		//}

		/********************************************************
		 * Fill Incomplete Views
		 ********************************************************/
		// Inflate Shuffle Button (substituted in dynamically for op buttons)
		//button_shuffle = View.inflate(this, R.layout.button_shuffle, null);
		
		final SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
						
		// Get palette action mode preferences		
		pltmanager = initPaletteManager(pref.getString(
				getString(R.string.prefKey_paletteActionMode), null ));
		
		// Get old palette id's from preferences
		final String keybase = getString(R.string.prefKey_paletteIds_baseStr);
		final int max = getResources().getInteger(R.integer.maxPaletteQuantity);
		
		// Collect old palette ids in stack (use only basic palette if none exist)
		final List<Integer> palette_ids = new ArrayList<>(max);
		
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
		//findViewById(R.id.buttonEqual).setOnClickListener(eql_listener);
		//number_text.setOnClickListener(txt_listener);
		
		final View button_addPalette = findViewById(R.id.buttonNewPalette);
		if (button_addPalette !=  null) {
			registerForContextMenu(button_addPalette);
		}

		
		/*********************************************************
		 * Set local MathView object
		 *********************************************************/
		mathview.setOpTree(expression);

		/*********************************************************
		 * Set Number Keyboard
		 *********************************************************/
		NumberKeyboardListener num_keyslistener
				= new NumberKeyboardListener(expression);
		num_keyslistener.setOnClickListener
				(NumberKeyboardListener.KEYS_EDIT, keys1_listener);
		
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
		final String keybase = getString(R.string.prefKey_paletteIds_baseStr);
		// Gets current ids
		List<Integer> ids = new ArrayList<>();
		for (View palette : (new ViewFinder()).findViewsByTag(
				getWindow().getDecorView().getRootView(),
				getString(R.string.tag_plt)
		)) {
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
		// Inflate the menu; this adds items to the action bar if it's present.
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
			pltmanager = initPaletteManager(pref.getString(key, null));
			pltmanager.refreshPaletteBoxes();
		}
	}

	//////////////////////////////////////////////////////////////////////
	// INTERNAL FUNCTIONS
	//////////////////////////////////////////////////////////////////////

	/*
	private void refreshNumberText() {
		number_text.setText("");
		// Sets result of selection to textNum hint
		try {
			// Calculate result (throws CalcEx)
			double result = expression.getSelectionCalculation();
			// Set text representation of result to view
			number_text.setHint(NumberStringConverter.toString(result));
		}
		catch (CalculationException ce) {
			// Set failed index
			blank_index = (Integer)ce.getCauseObject();
			number_text.setHint(getString(R.string.textNum_blank));
		}
	}

	// Called on button click after Equals has been clicked
	public void setEqualToText() {
		((ViewSwitcher) findViewById(R.id.switchEqText)).showNext();
		switch_setTextToEqual.enableListener();
		trigger_setEqualToText.disableListener();
	}
	public void setTextToEqual() {
		((ViewSwitcher) findViewById(R.id.switchEqText)).showPrevious();
		// TO/DO this shouldn't have to be here
		number_text.setText("");
		switch_setTextToEqual.disableListener();
		trigger_setEqualToText.enableListener();
	}

	public void hideNumKeys() {
		// Shows button panel
		findViewById(R.id.panelOps).setVisibility(View.VISIBLE);
		//*** Use only if root view is a RelativeLayout ***
		// Aligns bottom of WebView back to button panel
		((RelativeLayout.LayoutParams)number_text.getLayoutParams())
				.addRule(RelativeLayout.ABOVE, R.id.opPanel);
		//^/

		// Hides Keyboard
		KeyboardView keyboard = (KeyboardView)findViewById(R.id.keyboardNum);
		keyboard.setVisibility(View.GONE);
		keyboard.setEnabled(false);
		// Set triggers
		trigger_showNumKeys.enableListener();
		switch_hideNumKeys.disableListener();
	}
	public void showNumKeys() {
		// Shows keyboard
		KeyboardView keyboard = (KeyboardView)findViewById(R.id.keyboardNum);
		keyboard.setVisibility(View.VISIBLE);
		keyboard.setEnabled(true);
		//*** Use only if root view is a RelativeLayout ***
		// Aligns bottom of WebView to top of keyboard
		((RelativeLayout.LayoutParams)number_text.getLayoutParams())
				.addRule(RelativeLayout.ABOVE, R.id.keyboardNum);
		//^/
		// Hides button panel
		findViewById(R.id.panelOps).setVisibility(View.GONE);
		// Set triggers
		trigger_showNumKeys.disableListener();
		switch_hideNumKeys.enableListener();
	}
	//*/

	public void raiseToast(String str) {
		Toast t = Toast.makeText(
				getApplicationContext(), str, Toast.LENGTH_SHORT );
		// Sets toast position to bottom of WebView
		t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0,
				findViewById(R.id.mathview).getBottom() - 10 );
		t.show();
	}
	
	private PaletteManager initPaletteManager(String plt_actionmode) {
		if (plt_actionmode != null && plt_actionmode.equals(getString(
				R.string.prefValue_paletteActionMode_sidebutton )))
			return new SideButtonPaletteManager(this,
					(getResources().getBoolean(R.bool.canDeletePalette)
							? R.layout.palettebox_button_swapdelright
							: R.layout.palettebox_button_swapright ),
					swapplt_listener, delplt_listener);
		else
			//if (plt_actionmode.equals(getString(
			//	R.string.prefValue_paletteActionMode_swipe )))
			return new SwipePaletteManager(this,
					(getResources().getBoolean(R.bool.canDeletePalette)
							? R.layout.palettebox_swipe_swapleft_delright
							: R.layout.palettebox_swipe_swapleftright ),
					swapplt_listener, delplt_listener);
	}
	
//	private static FunctionType getFtypeFromViewId(int id) {}
	
	//////////////////////////////////////////////////////////////////////
	// ON-CLICK/BUTTON METHODS
	//////////////////////////////////////////////////////////////////////
	
	// Called when a MathML element is clicked in the WebView
	//public void onClickMathml(int index) {
	//	// TODO (?) if clicked element is a child element of the current selection,
	//	//		set selector to the index of its parent
	//	expression.setSelection(index);
	//	//refreshNumberText();
	//}
	
	/*
	public void onClickEquals(View v) {
		try {			
			// Calculate result (throws CalcEx)
			double result = expression.getCalculation();

			expression.setSelection(OpTree.null_index);

			// Set text representation of result to view
			((TextView) findViewById(R.id.textNum))
					.setHint(NumberStringConverter.toString(result));
		}
		catch (CalculationException ce) {
			// Set text representation of result to view
			((TextView) findViewById(R.id.textNum))
					.setHint(getString(R.string.textNum_blank));

			// set failed index
			blank_index = (Integer) ce.getCauseObject();
		}
	}
	// Called when the result text box is clicked
	public void onClickNumberText(View v) {
		if (number_text.getHint() == getString(R.string.textNum_blank)) {
			switch_setTextToEqual.disableListener();
			expression.setSelection(blank_index);
			switch_setTextToEqual.enableListener();
			number_text.setText(getString(R.string.textNum_default));
		}
		else {
			number_text.setText(number_text.getHint());
		}
	}
	//*/

	// Called when the user clicks the delete button
	public void onClickDelete(View v) {
		expression.delete();
		//refreshNumberText();
	}

	// Called when the user clicks the delete-parent button
	public void onClickDeleteParent(View v) {
		expression.deleteParent();
		//refreshNumberText();
	}

	// Called when the user clicks an operator button
	public void onClickOperator(View v) {
		// Adds function to expression
		FunctionType ftype = ((OperationButton)v).getFtype();
		// (sets selector in function)
		//final boolean canShuffle =
		expression.addFunction(ftype);
		//refreshNumberText();
		
		// Replaces op-button with shuffle button
		//if (canShuffle) {
		//	(new ViewReplacer()).replaceView(v, button_shuffle);
		//	button_temp = v;
		//	switch_unsetShuffleButton.enableListener();
		//}
	}
	public void onClickShuffle(View v) {
		expression.shuffleOrder();
		// Selection remains the same in shuffle, no refreshNumberText() necessary
	}
	/*
	public void unsetShuffleButton() {
		// Replaces shuffle button w/ op-button
		(new ViewReplacer()).replaceView(button_shuffle, button_temp);
		button_temp = null;
		switch_unsetShuffleButton.disableListener();
	}
	//*/

	// Called when the user clicks the _number insertion button
	/*
	public void onClickNumbersButton(View v) {
		showNumKeys();
		if (number_text.getText() == "")
			number_text.setText(getString(R.string.textNum_default));
	}
	public void onNumKeyboardResult() {
		String str = number_text.getText().toString();
		if (!str.isEmpty()) {
			try {expression.addNumber(Double.valueOf(str));}
			catch (NumberFormatException e) {
				// Raise toast to alert user that expression is incomplete
				raiseToast("Error: Invalid Number Format");
			}
			number_text.setText("");
		}
		// Set state components
		//refreshNumberText();
	}
	public void onNumKeyboardCancel() {
		number_text.setText("");
		onNumKeyboardResult();
		// TO/DO make numkeys hide in some other way (this is kinda dumb)
		expression.setSelection(expression.selection);
	}
	//*/
	public void onClickDeletePalette(View v) {
		pltmanager.removePalette(v);
	}

	//////////////////////////////////////////////////////////////////////
	// TEST FUNCTIONS
	//////////////////////////////////////////////////////////////////////

	/*
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
		//loadMathmlToScreen(temp_out);
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
