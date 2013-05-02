package com.aakashapp.modguru.src;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;

public class AcessPoint {

	Context context;
	String networkSSID;
	WifiConfiguration wifiConfiguration;
	WifiManager wifiManager;

	public AcessPoint(Context context) {
		this.context = context;
		this.networkSSID = PreferenceManager.getDefaultSharedPreferences(context).getString("advanced_network_ssid", "modGuru");
	}

	public AcessPoint(Context context, String networkSSID) {
		this.context = context;
		this.networkSSID =networkSSID;
	}

	public void create() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		wifiConfiguration = new WifiConfiguration();
		wifiConfiguration.SSID = networkSSID;
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		
		if(wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(false);
		}
		
		Method[] methods = wifiManager.getClass().getDeclaredMethods();
		for (Method m : methods) {
			if (m.getName().equals("setWifiApEnabled")) {
				m.invoke(wifiManager, wifiConfiguration, true);
			}
		}
	}

}
