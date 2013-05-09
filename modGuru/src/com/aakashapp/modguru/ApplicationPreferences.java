package com.aakashapp.modguru;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aakashapp.modguru.src.ImportFile;

public class ApplicationPreferences extends PreferenceActivity {

	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	public static final int ACTIVITY_CHOOSE_FILE = 1;
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
		((EditTextPreference) findPreference("general_participant_id")).setSummary(preferences.getString("general_participant_id", "Anonymous"));

		final EditText editTextDeviceName = ((EditTextPreference) findPreference("general_participant_id")).getEditText();
		editTextDeviceName.setSingleLine();
		((EditTextPreference) findPreference("general_participant_id")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				editTextDeviceName.setText(preferences.getString("general_participant_id", "Anonymous"));
				return false;
			}
		});
		((EditTextPreference) findPreference("general_participant_id")).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				editor = preferences.edit();
				editor.putString("general_participant_id", newValue.toString());
				editor.commit();
				preference.setSummary(newValue.toString());
				editTextDeviceName.setText(newValue.toString());
				return false;
			}
		});

		((CheckBoxPreference) findPreference("general_stay_awake")).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if(Boolean.parseBoolean(newValue.toString())) {
					AlertDialog.Builder alert = new AlertDialog.Builder(ApplicationPreferences.this);
					alert.setTitle("It Increases Battery Consumption!!");
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
		/**
		 * Legal & Privacy
		 */
		((Preference) findPreference("lp_license")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(ApplicationPreferences.this,WebViewActivity.class);
				intent.putExtra("title", "License");
				intent.putExtra("url", "file:///android_asset/license.html");
				startActivity(intent);
				return false;
			}
		});
		((Preference) findPreference("lp_open_source")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(ApplicationPreferences.this,WebViewActivity.class);
				intent.putExtra("title", "Open Source Attributions");
				intent.putExtra("url", "file:///android_asset/opensource.html");
				startActivity(intent);
				return false;
			}
		});
		/**
		 * Advanced Settings
		 */
		((EditTextPreference) findPreference("advanced_network_ssid")).setSummary(preferences.getString("advanced_network_ssid", "modGuru"));

		final EditText editTextNetworkSSID = ((EditTextPreference) findPreference("advanced_network_ssid")).getEditText();
		editTextNetworkSSID.setSingleLine();
		((EditTextPreference) findPreference("advanced_network_ssid")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				editTextNetworkSSID.setText(preferences.getString("advanced_network_ssid", "modGuru"));
				return false;
			}
		});
		((EditTextPreference) findPreference("advanced_network_ssid")).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				editor = preferences.edit();
				editor.putString("advanced_network_ssid", newValue.toString());
				editor.commit();
				preference.setSummary(newValue.toString());
				editTextNetworkSSID.setText(newValue.toString());
				return false;
			}
		});

		((Preference) findPreference("advanced_import")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				AlertDialog.Builder alert = new AlertDialog.Builder(ApplicationPreferences.this);
				alert.setTitle("Quiz File Format");

				String text = "";
				try {
					InputStream inputStream = getAssets().open("sample");
					BufferedInputStream stream = new BufferedInputStream(inputStream);
					int ch;
					while ((ch = stream.read()) != -1) {
						text += (char)ch;
					}
				}catch (Exception e) {
					text = "Can't Load Sample File!!";
				}

				TextView textView = new TextView(getApplicationContext());
				textView.setText(text);
				textView.setTextColor(Color.BLACK);
				textView.setPadding(10, 10, 10, 10);
				alert.setView(textView);

				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent chooseFile;
						Intent intent;
						chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
						chooseFile.setType("file/*");
						intent = Intent.createChooser(chooseFile, "Import File");
						startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
					}
				});
				alert.show();
				return false;
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==ACTIVITY_CHOOSE_FILE) {
			if (resultCode == Activity.RESULT_OK) {
				try {
					ImportFile.importFile(data.getData().getPath());
					Toast.makeText(getApplicationContext(), "Import Successful!!", Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					Toast.makeText(getApplicationContext(), "Read/Write Permission Denied!!", Toast.LENGTH_SHORT).show();
					Log.e("modGuru - import", e.getMessage(), e);
				} catch (Exception e) {
					AlertDialog.Builder alert = new AlertDialog.Builder(ApplicationPreferences.this);
					alert.setTitle("Syntax Error");
					alert.setMessage("Selected File Doesn't seem to be a Valid Quiz File.");
					alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							/*
							 * Do Nothing
							 */
						}
					});
					alert.show();
					Log.e("modGuru - import", e.getMessage(), e);
				}
			}
		}
	}
}
