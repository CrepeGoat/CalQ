package awqatty.b.GUI;

import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.widget.TextView;
import awqatty.b.calq.MainActivity;

public final class NumberKeyboardListener implements OnKeyboardActionListener {

	private final MainActivity activity;
	private final TextView display;
		
	public NumberKeyboardListener(MainActivity main, TextView view) {
		activity = main;
		display = view;
	}

	@Override
	public void onKey(int arg0, int[] arg1) {}

	@Override
	public void onPress(int arg0) {}

	@Override
	public void onRelease(int arg0) {
		CharSequence text = display.getText();
		
		// Delete Key Logic
		if (arg0 == -5) {
			if (text.length() > 0)
				display.setText(text.subSequence(0, text.length()-1));
		}
		// Cancel Key Logic
		else if (arg0 == -3) {
			display.setText("");
			activity.onNumKeyboardCancel();
		}
		// Enter Key Logic
		else if (arg0 == -2) {
			try {
				activity.onNumKeyboardResult(Double.valueOf(text.toString()));
				display.setText("");
			} catch (NumberFormatException e) {
				// Raise toast to alert user that expression is incomplete
				activity.raiseToast("Error: Invalid Number Format");
			}
		}
		// Negative Key Logic
		else if (arg0 == 45) {
			if (text.length() == 0)
				display.setText("-");
			else if (text.charAt(text.length()-1) == 'E')
				display.setText(text.toString() + '-');
		}
		// Decimal Key Logic
		else if (arg0 == 46) {
			if (text.length() == 0)
				display.setText("0.");
			else if (!text.toString().contains("E") && !text.toString().contains("."))
				display.setText(text.toString() + '.');
		}
		// Exponent Key Logic
		else if (arg0 == 69) {
			if (text.length() > 0) {
				if (!text.toString().contains("E"))
					display.setText(text.toString() + 'E');
			}
		}
		// General Numeric Key Logic
		else {
			if (text.toString() == "0")
				display.setText(String.valueOf((char) arg0));
			else
				display.setText(text.toString() + (char)arg0);
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
