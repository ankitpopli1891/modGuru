package com.aakashapp.modguru;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

public class ApplicationPreferences extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		loadPreferences();
	}

	@SuppressWarnings("deprecation")
	private void loadPreferences() {
		/**
		 * About Application
		 */
		try {
			((Preference) findPreference("about_build_version")).setSummary(getPackageManager().getPackageInfo(getPackageName(), 0).versionName+"."+getPackageManager().getPackageInfo(getPackageName(), 0).versionCode);
		} catch (NameNotFoundException e) {
			Log.e("Preference - Build Version", e.getMessage(), e);
		}
		((Preference) findPreference("about_send_feedback")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:feedback@modguru.com"));
				startActivity(intent);
				return false;
			}
		});
	}
}
