package awqatty.b.MathmlPresentation;

import java.util.List;


public class TextPresObject {
	private static final int pre=0, mid=1, suf=2;
	private final String[] strlist;
	private final TagFillBase[] taglist;
	private final TagFlags flags;

	public TextPresObject(String[] sl, TagFillBase[] tl) {
		strlist = sl;
		taglist = tl;
		flags = new TagFlags(TagFlags.NONE);
	}
	
	public void setIdValue(int id) {
		// Method 1: Assumes ID TagFill is ALWAYS the first entry
		if (taglist.length > 0)
			if (taglist[0].tag == Tags.ID.getTag())
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
	
	public String getTextPres(List<String> inList) {
		int i;
		// Concatenates strings into single output string
		String str_out;
		str_out = strlist[pre];
		if (inList.size() > 0) {
			str_out += inList.get(0);
			for (i=1; i<inList.size(); ++i) {
				str_out += strlist[mid] + inList.get(i);
			}
		}
		str_out += strlist[suf];
		
		// Replaces tags with correct strings (based on flag state)
		for (i=taglist.length-1; i >= 0; --i) {
			taglist[i].setActivity(flags);
			str_out = taglist[i].replaceTagsIn(str_out);
		}
		
		return str_out;
	}


}
