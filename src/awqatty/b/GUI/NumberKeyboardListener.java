package awqatty.b.GUI;

import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.view.View.OnClickListener;
import android.widget.TextView;
import awqatty.b.calq.MainActivity;

public final class NumberKeyboardListener implements OnKeyboardActionListener {

	// Constants
	public static final int LISTENER_COUNT = 2;
	public static final int KEYS_EDIT = 0;
	public static final int KEYS_ACTION = 1;
	
	private final TextView display;
	private final OnClickListener[] listeners;
	
	public NumberKeyboardListener(TextView view) {
		listeners = new OnClickListener[LISTENER_COUNT];
		display = view;
	}
	
	public void setOnClickListener(int listener_number, OnClickListener l) {
		listeners[listener_number] = l;
	}

	@Override
	public void onKey(int arg0, int[] arg1) {}

	@Override
	public void onPress(int arg0) {}

	@Override
	public void onRelease(int arg0) {
		final String text = display.getText().toString();
		
		switch (arg0) {
		case -5:	// Delete Key Logic
			if (text.length() == 1)
				display.setText("0");
			else if (text.length() == 2 && text.startsWith("-")) {
				if (text.charAt(1) != '0')
					display.setText("-0");
				else
					display.setText("0");
			}
			else if (text.length() > 0)
				display.setText(text.subSequence(0, text.length()-1));
			break;
			
		case -3:	// Cancel Key Logic
			display.setText("");
			break;
		
		case -2:	// Enter Key Logic
			try {
				((MainActivity) display.getContext())
						.onNumKeyboardResult(Double.valueOf(text));
				display.setText("");
			} catch (NumberFormatException e) {
				// Raise toast to alert user that expression is incomplete
				((MainActivity) display.getContext())
						.raiseToast("Error: Invalid Number Format");
			}
			break;
		
		case 45:	// Negative Key Logic
			final int index = text.lastIndexOf("E") + 1;
			if (index < text.length() && text.charAt(index) == '-')
				display.setText(text.substring(0, index) + text.substring(index+1));
			else
				display.setText(text.substring(0,index) + '-' + text.substring(index));
			break;
		
		case 46:	// Decimal Key Logic
			if (text.length() == 0)
				display.setText("0.");
			else if (!text.contains("E") && !text.contains("."))
				display.setText(text.toString() + '.');
			break;
		
		case 69:	// Exponent Key Logic
			if (text.length() > 0) {
				if (!text.contains("E"))
					display.setText(text.toString() + 'E');
				else if (text.endsWith("E"))
					display.setText(text.substring(0,text.length()-1));	
			}
			break;
		
		default:	// General Numeric Key Logic
			final String prefix;
			if (text.equals("0"))
				prefix = "";
			else if (text.equals("-0"))
				prefix = "-";
			else
				prefix = text;
			display.setText(prefix + (char)arg0);
			break;
		}
		
		if (arg0 == -3 || arg0 == -2) {
			listeners[KEYS_ACTION].onClick(display);
		}
		else {
			listeners[KEYS_EDIT].onClick(display);
		}
	}

	@Override
	public void onText(CharSequence arg0) {}

	@Override
	public void swipeDown() {}

	@Override
	public void swipeLeft() {}

	@Override
	public void swipeRight() {}

	@Override
	public void swipeUp() {}

}
