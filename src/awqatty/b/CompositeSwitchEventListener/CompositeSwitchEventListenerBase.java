package awqatty.b.CompositeSwitchEventListener;

import java.util.ArrayList;
import java.util.List;

import android.view.View;

public abstract class CompositeSwitchEventListenerBase {

	/*******************************************************************
	 * Private Internal Classes
	 */
	public static final boolean ON = true;
	public static final boolean OFF = false;
		
	// Object stored elsewhere to activate/deactivate listeners w/o using publicly
	public final class ListenerBoxSwitch {
		private final ListenerBox box;
		public ListenerBoxSwitch(ListenerBox b) {
			box = b;
		}
		public void enableListener() {
			box.state = true;
		}
		public void disableListener() {
			box.state = false;
		}
	}

	private abstract class ListenerBox implements OnViewEventListener {
		public boolean state;		
	}
	private final class SwitchListenerBox extends ListenerBox {
		private final OnViewEventListener listener;

		public SwitchListenerBox(OnViewEventListener l) {
			listener = l;
			state = ON;
		}
		public ListenerBoxSwitch getSwitch() {
			return new ListenerBoxSwitch(this);
		}
		@Override
		public void onViewEvent(View v) {
			if (state == ON)
				listener.onViewEvent(v);
		}
	}
	/*
	private final class SwitchBiListenerBox extends ListenerBox {
		protected final OnViewEventListener listener_on, listener_off;

		public SwitchBiListenerBox(OnViewEventListener l_on, OnViewEventListener l_off) {
			listener_on = l_on;
			listener_off = l_off;
			state = ON;
		}
		public ListenerBoxSwitch getSwitch() {
			return new ListenerBoxSwitch(this);
		}
		@Override
		public void onViewEvent(View v) {
			if (state == ON)
				listener_on.onViewEvent(v);
			else
				listener_off.onViewEvent(v);
		}
	}
	//*/

	/*******************************************************************
	 * Internal Data
	 */
	protected final List<OnViewEventListener> listener_list;
	protected int order = 0;
	
	/*******************************************************************
	 * Methods
	 */
	public CompositeSwitchEventListenerBase() {
		listener_list = new ArrayList<OnViewEventListener>();
	}
	public CompositeSwitchEventListenerBase(int capacity) {
		listener_list = new ArrayList<OnViewEventListener>(capacity);
	}
	
	public void addListener(OnViewEventListener l) {
		listener_list.add(l);
	}
	public ListenerBoxSwitch addSwitchListener(OnViewEventListener l) {
		if (l != null) {
			listener_list.add(new SwitchListenerBox(l));
			return ((SwitchListenerBox)listener_list.get(listener_list.size()-1)).getSwitch();
		}
		else {
			return null;
		}
	}
	/*
	public ListenerBoxSwitch addSwitchBiListener(OnClickListener l_on, OnClickListener l_off) {
		listener_list.add(new SwitchBiListenerBox(l_on, l_off));
		return ((SwitchBiListenerBox)listener_list.get(listener_list.size()-1)).getSwitch();
	}
	//*/
	public ListenerBoxSwitch addSwitchListener(ListenerBoxSwitch lt) {
		if (lt != null) {
			listener_list.add(lt.box);
		}
		return lt;
	}


}
