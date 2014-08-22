package awqatty.b.CustomEventListeners;

public class ChangeEvent {

	private final Object source;
	private final byte code_type, code_time;
	public ChangeEvent(Object obj) {
		source = obj;
		code_type = 0;
		code_time = 0;
	}
	public ChangeEvent(Object change_obj, byte change_code, byte timing_code) {
		source = change_obj;
		code_type = change_code;
		code_time = timing_code;
	}

	public Object getSourceObject() {return source;}
	public byte getTypeCode() {return code_type;}
	public byte getTimingCode() {return code_time;}
}
