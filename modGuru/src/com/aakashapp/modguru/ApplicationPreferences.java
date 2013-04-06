package com.aakashapp.modguru;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

public class ApplicationPreferences extends PreferenceActivity {

	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences);
		
		preferences = getPreferenceScreen().getSharedPreferences();
		
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
		
		/**
		 * General Settings
		 */
		((CheckBoxPreference) findPreference("general_stay_awake")).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if(Boolean.parseBoolean(newValue.toString())) {
					AlertDialog.Builder alert = new AlertDialog.Builder(ApplicationPreferences.this);
					alert.setTitle("Warning!");
					alert.setMessage("Increases Battery Consumption!!");
					alert.setPositiveButton("Nevermind, Stay Awake!", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							((CheckBoxPreference)findPreference("general_stay_awake")).setChecked(true);
							editor = preferences.edit();
							editor.putBoolean("general_stay_awake", true);
							editor.commit();
						}
					});
					alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					alert.show();
				}
				else {
					((CheckBoxPreference)findPreference("general_stay_awake")).setChecked(false);
					editor = preferences.edit();
					editor.putBoolean("general_stay_awake", false);
					editor.commit();
				}
				
				
				return false;
			}
		});
	}
}
