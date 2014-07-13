package awqatty.b.GenericTextPresentation;

public class StaticTagFill extends TagFillBase{

	private String insert;
	
	public StaticTagFill(String t, String i) {
		super(t);
		insert = i;
	}

	@Override
	public void setActivity(int flags){}
	@Override
	public boolean isActive() {
		return true;
	}
	@Override
	public String replaceTagsIn(String str) {
		return str.replaceAll(tag, insert);
	}

}
