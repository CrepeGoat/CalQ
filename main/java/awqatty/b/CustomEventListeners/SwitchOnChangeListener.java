package awqatty.b.CustomEventListeners;

public class SwitchOnChangeListener extends SwitchedEventListenerBase implements
		OnChangeListener {

	private final OnChangeListener listener;
	public SwitchOnChangeListener(OnChangeListener listener) {
		this.listener = listener;
	}

	@Override
	public void onChange(ChangeEvent event) {
		if (state) listener.onChange(event);
	}

}
