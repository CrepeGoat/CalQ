package awqatty.b.TextPresentation;

import java.util.List;

public interface TextPresForm {

	public String getTextPres(List<String> str_list);
	
	public void setIdValue(int id);
	public void enableTagFlag(int f);
	public void disableTagFlag(int f);
	
}
