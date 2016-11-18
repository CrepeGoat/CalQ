package awqatty.b.CustomEventListeners;

public class ChangeEvent {

	public final Object source_obj;
	public final byte changetype_code;
	public final byte timing_code;
	/*
	public ChangeEvent(Object obj) {
		source_obj = obj;
		changetype_code = -1;
		timing_code = -1;
	}//*/
	public ChangeEvent(Object change_obj, byte changetype, byte timing) {
		source_obj = change_obj;
		changetype_code = changetype;
		timing_code = timing;
	}
}
