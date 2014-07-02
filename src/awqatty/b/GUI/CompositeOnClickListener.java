package awqatty.b.GUI;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;

public class CompositeOnClickListener implements OnClickListener {

	/*******************************************************************
	 * Private Internal Classes
	 */
	public static final boolean ON = true;
	public static final boolean OFF = false;
	// Object stored elsewhere to activate/deactivate listeners w/o using publicly
	public final class ListenerSwitch {
		private final SwitchListenerBox box;
		public ListenerSwitch(SwitchListenerBox b) {
			box = b;
		}
		public void activateListener() {
			box.state = true;
		}
		public void deactivateListener() {
			box.state = false;
		}
	}
	
	private final class SwitchListenerBox implements OnClickListener {
		protected final OnClickListener listener;
		public boolean state;

		public SwitchListenerBox(OnClickListener l) {
			listener = l;
			state = ON;
		}
		public ListenerSwitch getSwitch() {
			return new ListenerSwitch(this);
		}
		@Override
		public void onClick(View v) {
			if (state)
				listener.onClick(v);
		}
	}

	/*******************************************************************
	 * Internal Data
	 */
	private final List<OnClickListener> listener_list;
	
	/*******************************************************************
	 * Methods
	 */
	// Constructors
	public CompositeOnClickListener() {
		listener_list = new ArrayList<OnClickListener>();
	}
	public CompositeOnClickListener(int capacity) {
		listener_list = new ArrayList<OnClickListener>(capacity);
	}
	
	// Add Listener Methods
	// (Note - onClick methods are called in the order they are added, i.e. FIFO)
	public void addListener(OnClickListener l) {
		listener_list.add(l);
	}
	public ListenerSwitch addSwitchListener(OnClickListener l) {
		listener_list.add(new SwitchListenerBox(l));
		return ((SwitchListenerBox)listener_list.get(listener_list.size()-1)).getSwitch();
	}
	public ListenerSwitch addSwitchListener(ListenerSwitch ls) {
		if (!listener_list.contains(ls)) {
			listener_list.add(ls.box);
		}
		return ls;
	}

	@Override
	public void onClick(View v) {
		for (OnClickListener listener:listener_list)
			listener.onClick(v);
	}

}
