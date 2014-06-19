package awqatty.b.JSInterface;

//import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import awqatty.b.calq.MainActivity;

/*
 * Used to override url linking, in order to implement "on-click"-like actions
 * If using Javascript function binding, this class is not used.
 */
public class MathmlViewClient extends WebViewClient {

	private MainActivity activity;
	public MathmlViewClient(MainActivity context) {
		activity = context;
	}
	
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		// TODO (?) make method safer
		// For testing: (TODO remove)
		// Log.d(this.toString(), url+", index = "+IdFormat.getIdFromString(url));
		// Reroutes id int value to MainActivity method
		activity.onMathmlClick(
				Integer.valueOf(IdFormat.getIdFromString(url)) );
		return true;
	}

}
