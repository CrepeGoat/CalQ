package awqatty.b.calq;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public final class SettingsActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

}
