package awqatty.b.GUI;

import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.view.View.OnClickListener;
import android.widget.TextView;
import awqatty.b.calq.MainActivity;

public final class NumberKeyboardListener implements OnKeyboardActionListener {

	private final TextView display;
	private OnClickListener listener_hideKeyboard;
	
	public NumberKeyboardListener(TextView view) {
		display = view;
	}
	
	public void setOnClickListener(OnClickListener l) {
		listener_hideKeyboard = l;
	}

	@Override
	public void onKey(int arg0, int[] arg1) {}

	@Override
	public void onPress(int arg0) {}

	@Override
	public void onRelease(int arg0) {
		final String text = display.getText().toString();
		
		switch (arg0) {
		// Delete Key Logic
		case -5:
			if (text.length() > 0)
				display.setText(text.subSequence(0, text.length()-1));
			break;
		// Cancel Key Logic
		case -3:
			display.setText("");
			listener_hideKeyboard.onClick(display);
			break;
		// Enter Key Logic
		case -2:
			try {
				((MainActivity) display.getContext())
						.onNumKeyboardResult(Double.valueOf(text.toString()));
				display.setText("");
				listener_hideKeyboard.onClick(display);
			} catch (NumberFormatException e) {
				// Raise toast to alert user that expression is incomplete
				((MainActivity) display.getContext())
						.raiseToast("Error: Invalid Number Format");
			}
			break;
		// Negative Key Logic
		case 45:
			final int index = text.lastIndexOf("E") + 1;
			if (index < text.length())
				if (text.charAt(index) == '-') {
					display.setText(text.substring(0, index) + text.substring(index+1));
					return;
				}
			display.setText(text.substring(0,index) + '-' + text.substring(index));
			break;
		// Decimal Key Logic
		case 46:
			if (text.length() == 0)
				display.setText("0.");
			else if (!text.toString().contains("E") && !text.toString().contains("."))
				display.setText(text.toString() + '.');
			break;
		// Exponent Key Logic
		case 69:
			if (text.length() > 0) {
				if (!text.toString().contains("E"))
					display.setText(text.toString() + 'E');
			}
			break;
		// General Numeric Key Logic
		default:
			display.setText((text.equals("0") ? "":text) + (char)arg0);
			break;
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
