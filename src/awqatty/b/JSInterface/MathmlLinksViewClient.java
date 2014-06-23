package awqatty.b.JSInterface;

//import android.util.Log;
import android.webkit.WebView;
import awqatty.b.calq.MainActivity;

/************************************************************
 * Used to override url linking, in order to implement "on-click"-like actions
 * If using Javascript function binding, this class is not used.
 * 		(in this case, use MathmlViewClient instead)
 */
public class MathmlLinksViewClient extends MathmlViewClient {

	//protected MainActivity activity;
	public MathmlLinksViewClient(MainActivity context) {
		//activity = context;
		super(context);
	}
	
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		// TODO (?) make method safer
		// Debug Log
		// Log.d(this.toString(), url+", index = "+HtmlIdFormat.getIdFromString(url));
		
		// Reroutes id int value to MainActivity method
		activity.onMathmlClick(
				Integer.valueOf(HtmlIdFormat.getIdFromString(url)) );
		return true;
	}

}
