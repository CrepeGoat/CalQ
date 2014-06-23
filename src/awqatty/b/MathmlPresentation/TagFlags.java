package awqatty.b.MathmlPresentation;

public class TagFlags {
	// Flag Constants
	public static final int 
				NONE 			= 0,
				DISABLE_ID		= 1,
				LOWLIGHT 		= 2,
				SUBHIGHLIGHT 	= 4,
				HIGHLIGHT 		= 8,
				PARENTHESES 	= 16;
	
	// Internal Memory & Constructor
	private int flags;
	public TagFlags(int values) {
		flags = values;
	}
	
	// Get Methods
	public boolean flagIsActive(int f) {
		return (flags & f) == f;
	}
	public boolean flagIsInactive(int f) {
		return (~flags & f) == f;
	}
	
	// Set Methods For Given Flags
	public TagFlags enableFlag(int f) {
		flags = (flags | f);
		return this;
	}
	public TagFlags disableFlag(int f) {
		flags = (flags & ~f);
		return this;
	}
	public int get() {
		return flags;
	}
	/*
	public static boolean coincide(TagFlags f1, TagFlags f2) {
		return (f1.flags | f2.flags) != 0;
	}
	 */

}
