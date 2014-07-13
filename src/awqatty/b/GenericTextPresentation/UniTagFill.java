package awqatty.b.GenericTextPresentation;

public class UniTagFill extends SwitchTagFillBase {

	public final String insert;
	
	public UniTagFill(int pat, int act, String t, String in) {
		super(pat, act, t);
		insert = in;
	}

	@Override
	public boolean isActive() {
		return activity;
	}
	
	@Override
	public String replaceTagsIn(String str) {
		return activity ? str.replaceAll(tag, insert) : str;
	}

}
