package awqatty.b.JSInterface;

import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import awqatty.b.calq.MainActivity; 

/************************************************************
 * Used to implement Javascript binding functions & on-click methods
 * If using html links to operate "onclick" events in MathML, this class is not used.
 */
public class JSBinder {

	private final MainActivity main;
	private final WebView view;
	private View.OnClickListener listener;
	
	public JSBinder(WebView v) {
		main = (MainActivity)v.getContext();
		view = v;
		listener = null;
	}
	
	public void setOnClickListener(View.OnClickListener l) {
		listener = l;
	}
	
	@JavascriptInterface
	public void onClickMathml(String id) {
		main.onClickMathml(Integer.valueOf(
				HtmlIdFormat.getIdFromString(id) ));
		if (listener != null) {
			listener.onClick(view);
		}
	}

}
