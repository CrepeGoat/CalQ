package awqatty.b.calq;

import android.app.Fragment;

public class RetainDataFragment<T> extends Fragment {

	private T expression;
	
	public void setData(T xp) {
		expression = xp;
	}
	public T getData() {
		return expression;
	}
}
