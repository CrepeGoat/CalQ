package awqatty.b.calq;

import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

public class NumberKeyboardListener implements OnKeyboardActionListener {

	private MainActivity activity;
	private TextView display;
	
	private CharSequence getDisplayText() {
		return display.getText();
	}
	private void setDisplayText(CharSequence chars) {
		display.setText(chars);
	}
	
	public NumberKeyboardListener(MainActivity main, TextView view) {
		activity = main;
		display = view;
	}

	@Override
	public void onKey(int arg0, int[] arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPress(int arg0) {

		//display.setText(Integer.toString(arg0));
		//*
		CharSequence text = getDisplayText();
		
		// Delete Key Logic
		if (arg0 == -5) {
			if (text.length() > 0)
				setDisplayText(text.subSequence(0, text.length()-1));
		}
		// Cancel Key Logic
		else if (arg0 == -3) {
			setDisplayText("");
			activity.onNumKeyboardCancel();
		}
		// Enter Key Logic
		else if (arg0 == -2) {
			try {
				activity.onNumKeyboardResult(Double.valueOf(text.toString()));
				setDisplayText("");
			} catch (NumberFormatException e) {
				// Raise toast to alert user that expression is incomplete
				activity.raiseToast("Error: Invalid Number Format");
			}
		}
		// Negative Key Logic
		else if (arg0 == 45) {
			if (text.length() == 0)
				setDisplayText("-");
			else if (text.charAt(text.length()-1) == 'E')
				setDisplayText(text.toString() + '-');
		}
		// Decimal Key Logic
		else if (arg0 == 46) {
			if (text.length() == 0)
				setDisplayText("0.");
			else if (!text.toString().contains("E") && !text.toString().contains("."))
				setDisplayText(text.toString() + '.');
		}
		// Exponent Key Logic
		else if (arg0 == 69) {
			if (text.length() > 0) {
				if (!text.toString().contains("E"))
					setDisplayText(text.toString() + 'E');
			}
		}
		// General Numeric Key Logic
		else {
			if (text.toString() == "0")
				setDisplayText(String.valueOf((char) arg0));
			else
				setDisplayText(text.toString() + (char)arg0);
		}
		//*/
	}

	@Override
	public void onRelease(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onText(CharSequence arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void swipeDown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void swipeLeft() {
		// TODO Auto-generated method stub

	}

	@Override
	public void swipeRight() {
		// TODO Auto-generated method stub

	}

	@Override
	public void swipeUp() {
		// TODO Auto-generated method stub

	}

}
