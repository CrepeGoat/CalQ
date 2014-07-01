package awqatty.b.MathmlPresentation;

public abstract class SwitchTagFillBase extends TagFillBase {
	/***********************************************************
	 * flags_pattern - when these flags are matched, the TagFill object is active
	 * flags_active - the flags from flags_pattern that must be matched
	 * tag - that string tag to be replaced by other strings
	 */
	private final int flags_pattern;
	private final int flags_active;
	protected boolean activity=false;
	
	public SwitchTagFillBase(int pat, int act, String t) {
		super(t);
		flags_pattern = pat;
		flags_active = act;
	}
	
	public void setActivity(int flags) {
		activity = (flags_active & flags) == (flags_active & flags_pattern);
	}
	public void setActivity(TagFlags flags) {
		setActivity(flags.get());
	}
	
	abstract public boolean isActive();	
	abstract public String replaceTagsIn(String str);
	
}