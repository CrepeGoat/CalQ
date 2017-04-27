package awqatty.b.GUI;

import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.view.View.OnClickListener;

import awqatty.b.OpTree.OpTree;

public final class NumberKeyboardListener implements OnKeyboardActionListener {

	// Constants
	public static final int LISTENER_COUNT = 1;
	public static final int KEYS_EDIT = 0;
	
	private final OpTree text_source;
	private final OnClickListener[] listeners;
	
	public NumberKeyboardListener(OpTree optree) {
		listeners = new OnClickListener[LISTENER_COUNT];
		text_source = optree;
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
		String text=null;
		if (arg0 != -3 && arg0 != -2) {
			text = text_source.getText();
			if (text.isEmpty()) text = "0";
		}
		
		switch (arg0) {
		case -5:	// Delete Key Logic
			if (text.length() == 1)
				text_source.setText("0");
			else if (text.length() == 2 && text.startsWith("-"))
				text_source.setText(text.charAt(1)!='0' ? "-0":"0");
			else if (text.length() > 1)
				text_source.setText(text.subSequence(0, text.length()-1).toString());
			break;
			
		case -3:	// Cancel Key Logic
			//((MainActivity) text_source.getContext()).onNumKeyboardCancel();
			break;
		
		case -2:	// Enter Key Logic
			text_source.setTextAsNumber();
			break;
		
		case 45:	// Negative Key Logic
			final int index = text.lastIndexOf("E") + 1;
			if (index < text.length() && text.charAt(index) == '-')
				text_source.setText(text.substring(0, index) + text.substring(index+1));
			else
				text_source.setText(text.substring(0,index) + '-' + text.substring(index));
			break;
		
		case 46:	// Decimal Key Logic
			if (text.length() == 0)
				text_source.setText("0.");
			else if (!text.contains("E") && !text.contains("."))
				text_source.setText(text.toString() + '.');
			break;
		
		case 69:	// Exponent Key Logic
			if (text.length() > 0) {
				if (!text.contains("E"))
					text_source.setText(text.toString() + 'E');
				else if (text.endsWith("E"))
					text_source.setText(text.substring(0,text.length()-1));
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
			text_source.setText(prefix + (char)arg0);
			break;
		}
		
		if (arg0 == -3 || arg0 == -2) {}
		else {
			//listeners[KEYS_EDIT].onClick(display);
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
