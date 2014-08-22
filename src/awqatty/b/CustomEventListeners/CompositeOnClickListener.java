package awqatty.b.CustomEventListeners;

import android.view.View;
import android.view.View.OnClickListener;

public class CompositeOnClickListener
		extends CompositeSwitchEventListenerBase implements OnClickListener {
	private OnClickListener listener_main = null;

	public CompositeOnClickListener() {
		super();
	}
	public CompositeOnClickListener(int capacity) {
		super(capacity);
	}
	
	// Add Listener Methods
	// (Note - onClick methods are called in the order they are added, i.e. FIFO)
	public void addListener(OnClickListener l) {
		listener_main = l;
		order = listener_list.size();
	}
	
	@Override
	public void onClick(View v) {
		for (OnViewEventListener listener:listener_list.subList(0, order))
			listener.onViewEvent(v);
		if (listener_main != null) listener_main.onClick(v);
		for (OnViewEventListener listener:listener_list.subList(order, listener_list.size()))
			listener.onViewEvent(v);		
	}

}
