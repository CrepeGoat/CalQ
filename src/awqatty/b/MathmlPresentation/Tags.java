package awqatty.b.MathmlPresentation;

public enum Tags {
	ID				(0),
	SELECT_L		(1),
	SELECT_R		(2),
	PARENTHESIS_L	(3),
	PARENTHESIS_R	(4);
	
	// Tag Id
	private int value;
	private Tags(int v) {
		value=v;
	}
	
	// Tag Value Storage
	private static String[] tag_strings = {
		"#id#",
		"#s1#",
		"#s2#",
		"#pl#",
		"#pr#",
	};
	public String getTag() {
		return tag_strings[value];
	}
	public static String getTag(Tags t) {
		return t.getTag();
	}

}
