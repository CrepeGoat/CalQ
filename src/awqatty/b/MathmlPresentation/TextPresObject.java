package awqatty.b.MathmlPresentation;

import java.util.List;


public class TextPresObject {
	private static final int pre=0, mid=1, suf=2;
	private final String[] strList;
	private final TagBox[] tagList;
	private final TagFlags flags;

	public TextPresObject(String[] sl, TagBox[] tl) {
		strList = sl;
		tagList = tl;
		flags = new TagFlags(TagFlags.NONE);
	}
	
	public void setIdValue(int id) {
		// Method 1: Assumes IdTagBox is ALWAYS the first entry
		//*
		if (tagList[0].isIdTag()) {
			((IdTagBox)tagList[0]).setIdTag(id);
		}	
		/*/
		// Method 2: Makes no assumption
		for (int i=0; i<tagList.length; ++i) {
			if (tagList[i].isIdTag()) {
				((IdTagBox)tagList[i]).setIdTag(id);
				break;
			}
		}
		//*/
	}
	public void enableTagFlag(int f) {
		flags.enableFlag(f);
	}
	public void disableTagFlag(int f) {
		flags.disableFlag(f);
	}
	
	public String getTextPres(List<String> inList) {
		// Makes immutable copy of strings
		String[] tmpList = new String[strList.length];
		System.arraycopy(strList,0, tmpList,0, strList.length);
		// Replaces tags with correct strings (based on flag state)
		int i;
		// TODO make tb cycle in reverse order (parentheses have id's too)
		for (TagBox tb:tagList)
			if (tb.isActive(flags))
				for (i=0; i<tmpList.length; ++i)
					tmpList[i] = tb.replaceTags(tmpList[i]);
		// Concatenates strings into single output string
		String rStr;
		rStr = tmpList[pre];
		if (inList.size() > 0) {
			rStr += inList.get(0);
			for (i=1; i<inList.size(); ++i) {
				rStr += tmpList[mid] + inList.get(i);
			}
		}
		rStr += tmpList[suf];
		
		return rStr;
	}


}
