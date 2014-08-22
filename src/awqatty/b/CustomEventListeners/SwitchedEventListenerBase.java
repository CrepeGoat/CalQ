package awqatty.b.CustomEventListeners;

abstract public class SwitchedEventListenerBase {

	protected boolean state;
	
	static public class ListenerSwitch {
		private SwitchedEventListenerBase listener;
		public ListenerSwitch(SwitchedEventListenerBase l) {
			listener = l;
		}
		public void enableListener() {listener.state = true;}
		public void disableListener() {listener.state = false;}
	}
	
	public SwitchedEventListenerBase() {
		state = true;
	}
	
	public ListenerSwitch getSwitch() {
		return new ListenerSwitch(this);
	}
}
