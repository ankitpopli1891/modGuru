package com.aakashapp.modguru.src;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class AcessPoint {

	Context context;
	String networkSSID;
	WifiConfiguration wifiConfiguration;
	WifiManager wifiManager;

	public AcessPoint(Context context) {
		this.context = context;
		this.networkSSID = PreferenceManager.getDefaultSharedPreferences(
				context).getString("advanced_network_ssid", "modGuru");
	}

	public AcessPoint(Context context, String networkSSID) {
		this.context = context;
		this.networkSSID = networkSSID;
	}

	public void create() throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		wifiConfiguration = new WifiConfiguration();
		wifiConfiguration.SSID = networkSSID;
		wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);

		if (wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(false);
		}

		Boolean enabled = false;
		String name="";

		try {
			Method method = wifiManager.getClass().getMethod("isWifiApEnabled");
			enabled = (Boolean) method.invoke(wifiManager);
			method = wifiManager.getClass().getMethod("getWifiApConfiguration");
			name = ((WifiConfiguration)method.invoke(wifiManager)).SSID;
			Log.e("Method", enabled + "  " + name);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!enabled) 
		{
			Method[] methods = wifiManager.getClass().getDeclaredMethods();
			for (Method m : methods) {
				if (m.getName().equals("setWifiApEnabled")) {
					m.invoke(wifiManager, wifiConfiguration, true);
					name = wifiConfiguration.SSID;
				}
			}
		}
		Toast.makeText(context, "Network created with name: "+name, Toast.LENGTH_SHORT).show();
	}

}
