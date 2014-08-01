package awqatty.b.CompositeSwitchEventListener;

import android.view.View;
import android.view.View.OnLongClickListener;

public final class CompositeOnLongClickListener 
		extends	CompositeSwitchEventListenerBase implements OnLongClickListener {
	private OnLongClickListener listener_main = null;
	
	public CompositeOnLongClickListener() {
		super();
	}
	public CompositeOnLongClickListener(int capacity) {
		super(capacity);
	}
	
	// Add Listener Methods
	// (Note - onClick methods are called in the order they are added, i.e. FIFO)
	public void addListener(OnLongClickListener l) {
		listener_main = l;
		order = listener_list.size();
	}

	@Override
	public boolean onLongClick(View v) {
		boolean value;
		for (OnViewEventListener listener:listener_list.subList(0, order))
			listener.onViewEvent(v);
		value = (listener_main != null ? listener_main.onLongClick(v) : false);
		for (OnViewEventListener listener:listener_list.subList(order, listener_list.size()))
			listener.onViewEvent(v);		
		return value;
	}

}
