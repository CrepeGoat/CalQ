package awqatty.b.JSInterface;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import awqatty.b.calq.MainActivity;

/********************************************
 * Used to auto-load WebView when initial JavaScript loading is complete.
 * 
 * (Needs access to MainActivity, since loaded MathML string comes from op. stack)
 */

public class MathmlViewClient extends WebViewClient {

	@Override
	public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (!url.startsWith("http://bar"))
        	throw new RuntimeException();
        ((MainActivity)view.getContext()).refreshMathmlScreen();
        // Debug Log
        //Log.d(this.toString(), "onPageFinished overloaded method called.");
	}

}
