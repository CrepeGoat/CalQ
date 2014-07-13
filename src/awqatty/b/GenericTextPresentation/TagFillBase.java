package awqatty.b.GenericTextPresentation;

abstract public class TagFillBase {

	public final String tag;

	public TagFillBase(String t) {
		tag = t;
	}
	
	abstract public void setActivity(int flags);
	public void setActivity(TagFlags flags) {
		setActivity(flags.get());
	}
	abstract public boolean isActive();
	abstract public String replaceTagsIn(String str);

}
