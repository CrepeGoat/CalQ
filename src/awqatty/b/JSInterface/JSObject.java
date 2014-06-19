package awqatty.b.JSInterface;

import android.webkit.JavascriptInterface;
import awqatty.b.calq.MainActivity; 

/*
 * Used to implement Javascript binding functions & on-click methods
 * If using html links to operate "onclick" events in MathML, this class is not used.
 */
public class JSObject {

	MainActivity main;
	
	public JSObject(MainActivity m) {
		// TODO Auto-generated constructor stub
		main = m;
	}
	
	@JavascriptInterface
	public void onClick(String id) {
		main.onMathmlClick(
				Integer.valueOf(IdFormat.getIdFromString(id)) );
	}

}
