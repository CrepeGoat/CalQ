package awqatty.b.CustomEventListeners;

import java.util.ArrayList;
import java.util.List;

public class CompositeOnChangeListener implements OnChangeListener {

	private List<OnChangeListener> listener_list;
	
	public CompositeOnChangeListener() {
		listener_list = new ArrayList<OnChangeListener>();
	}
	public CompositeOnChangeListener(int capacity) {
		listener_list = new ArrayList<OnChangeListener>(capacity);
	}
	
	public void setOnChangeListener(OnChangeListener listener) {
		listener_list.add(listener);
	}
	public void removeOnChangeListener(OnChangeListener listener) {
		listener_list.remove(listener);
	}
	
	@Override
	public void onChange(ChangeEvent event) {
		for (OnChangeListener listener : listener_list) {
			listener.onChange(event);
		}
	}

}
