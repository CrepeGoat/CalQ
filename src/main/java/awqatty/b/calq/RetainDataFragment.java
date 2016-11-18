package awqatty.b.calq;

import android.app.Fragment;

public class RetainDataFragment<T> extends Fragment {

	private T data;
	
	public RetainDataFragment() 	{data = null;}
	public RetainDataFragment(T t) 	{data = t;}
	
	public T getData() 			{return data;}
	public void setData(T xp) 	{data = xp;}
}
