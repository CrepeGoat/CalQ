package awqatty.b.JSInterface;

import android.webkit.WebView;
import awqatty.b.calq.MainActivity;

/************************************************************
 * Used to override url linking, in order to implement "on-click"-like actions
 * If using Javascript function binding, this class is not used.
 * 		(in this case, use MathmlViewClient instead)
 */
public class MathmlLinksViewClient extends MathmlViewClient {
		
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		// TODO (?) make method safer
		
		// Reroutes id int value to MainActivity method
		((MainActivity)view.getContext()).onClickMathml(
				Integer.valueOf(HtmlIdFormat.getIdFromString(url)) );
		
		return true;
	}
}
