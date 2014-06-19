package awqatty.b.MathmlPresentation;

public class IdTagBox extends TagBox {

	public IdTagBox(String t) {
		super(TagFlags.NONE, true,true, t, "");
	}
	
	@Override
	public boolean isIdTag() {
		return true;
	}
	public void setIdTag(int idTag) {
		insert = Integer.toString(idTag);
	}

}
