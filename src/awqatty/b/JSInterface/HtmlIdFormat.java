package awqatty.b.JSInterface;

public abstract class HtmlIdFormat {

	public static final String id_prefix = "id_";
	public static final String id_suffix = "_";

	// Convert ID to tagged string
	public static String encloseIdInTags(int id) {
		return id_prefix + Integer.toString(id) + id_suffix;
	}
	public static String encloseIdInTags(String id) {
		return id_prefix + id + id_suffix;
	}
	
	public static int getIdFromString(String full_id) {
		int start = full_id.indexOf(id_prefix)+id_prefix.length();
		return Integer.valueOf(
				full_id.substring(start, full_id.indexOf(id_suffix, start)) );
	}

}
