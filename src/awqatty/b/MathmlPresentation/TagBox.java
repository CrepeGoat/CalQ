package awqatty.b.MathmlPresentation;

// Used to replace text tags in the strings with optional pieces of text
// 	i.e. inserting parentheses, id value, etc.
public class TagBox {
	/*****************************
	 * activation_flag - a 1-bit flag. When this bit is true in the flags variable,
	 * 				it indicates that this object will be active.
	 * flag_case - determines whether this object becomes active on a true or false value
	 * 				from the activation_flag
	 * flag_state - determines whether it checks for active (true) or inactive (false)
	 * tag		- the tag in text that acts as a placeholder for this object
	 * insert 	- the text with which to replace the tag text when this object is active
	 * 
	 *****************************/
	private final int activation_flag;
	private final boolean flag_case;
	private final boolean flag_state;
	private final String tag;
	protected String insert;
	
	public TagBox (int flag, boolean c, boolean s, String t, String i) {
		activation_flag = flag;
		flag_case = c;
		flag_state = s;
		tag = t;
		insert = i;
	}
	
	public boolean isActive(TagFlags flags) {
		if (flag_state)
			return flag_case == flags.flagIsActive(activation_flag);
		else
			return flag_case == flags.flagIsInactive(activation_flag);
	}
	public boolean isIdTag() {
		return false;
	}
	public String getTag() {
		return tag;
	}
	
	public String replaceTags(String input) {
		return input.replaceAll(tag, insert);
	}
}
