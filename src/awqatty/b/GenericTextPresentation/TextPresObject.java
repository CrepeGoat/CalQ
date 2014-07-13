package awqatty.b.GenericTextPresentation;

import java.util.List;

import awqatty.b.TextPresentation.TextPresForm;


public class TextPresObject implements TextPresForm {
	private final String[] strlist;
	private final TagFillBase[] taglist;
	private final TagFillBase[] childtaglist;
	private final TagFlags flags;

	public TextPresObject(String[] sl, TagFillBase[] tl, TagFillBase[] ctl) {
		strlist = sl;
		taglist = tl;
		childtaglist = ctl;
		flags = new TagFlags(TagFlags.NONE);
	}
	
	public void setIdValue(int id) {
		// Assumes taglist is not empty, and ID TagFill is ALWAYS the first entry
		/*
		if (taglist.length > 0)
			if (taglist[0].tag == Tags.ID.getTag())
		 */
		// TODO put string replacement operation in another function
		((BiTagFill)taglist[0]).inserts[0]
				= Integer.toString(id);
	}
	public void enableTagFlag(int f) {
		flags.enableFlag(f);
	}
	public void disableTagFlag(int f) {
		flags.disableFlag(f);
	}
	
	public String getTextPres(List<String> str_list) {
		int i;
		for (i=0; i<str_list.size(); ++i)
			for (TagFillBase t:childtaglist)
				str_list.set(i, t.replaceTagsIn(str_list.get(i)));
		// Concatenates strings into single output string
		// Case 0+ branches
		String str_out = strlist[0];
		
		// Case 1+ branches
		if (str_list.size() > 0) {
			str_out += str_list.get(0) + strlist[1];
			
			// Case 2+ branches
			if (str_list.size() > 1) {
				str_out += str_list.get(1);
				for (i=2; i<str_list.size(); ++i) {
					str_out += strlist[1] + str_list.get(i);
				}
				str_out += strlist[2];
			}
		}
		
		// Replaces tags with correct strings (based on flag state)
		for (i=taglist.length-1; i >= 0; --i) {
			taglist[i].setActivity(flags);
			str_out = taglist[i].replaceTagsIn(str_out);
		}
		
		return str_out;
	}


}
