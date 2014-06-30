package awqatty.b.MathmlPresentation;

public class BiTagFill extends SwitchTagFillBase {
	/****************************************************
	 * inserts[0] - the replacement string used when active
	 * inserts[1] - the replacement string used when inactive
	 */
	public final String[] inserts = new String[2];
	
	public BiTagFill(int pat, int act, String t, String on, String off) {
		super(pat, act, t);
		inserts[0] = on;
		inserts[1] = off;
	}
	
	@Override
	public boolean isActive() {
		return true;
	}
	
	@Override
	public String replaceTagsIn(String str) {
		return str.replaceAll(tag, inserts[activity ? 0:1]);
	}

}
