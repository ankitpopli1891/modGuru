package com.aakash.app.wi_net.java;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

public class Client extends AsyncTask<String, Integer, String> {

	private Socket client;
	private Context context;
	
	public Client(Context context) {
		this.context=context;
	}

	@Override
	protected String doInBackground(String... params) {
		String ip = params[0];
		int port = Integer.parseInt(params[1]);
		String msg = receiveData(ip, port);
		return msg;
	}

	public String receiveData(String address, int port) {
		String msg = "Done";
		try {
			try {
				client = new Socket(address, port);
			} catch (IOException e) {
				return receiveData(address, port);
			}
			StroreData store = new StroreData();
			DataInputStream input = new DataInputStream(client.getInputStream());
			byte[] buffer = new byte[1500];
			int read = input.readInt();
			while (!store.complete) {
				if (read > 0 && read <= SupplicantBroadcast.mtu) {
					input.read(buffer, 0, read);
					store.readData(buffer, read);
				}
				try {
					read = input.readInt();
				} catch (Exception e) {
					read = -1;
				}
			}
			client.close();
		} catch (Exception e) {
			msg = "Error occurred while downloading file";
			Log.e("Wi-Net Error", "", e);
		}
		return msg;
	}

	
	@Override
	protected void onPostExecute(String result) {
		Log.e("Receiver", result);
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		try {
			client.close();
		} catch (Exception e) {
			Log.e("Wi-Net Error", e.getMessage() + "");
			for (StackTraceElement s : e.getStackTrace())
				Log.e("Wi-Net Stack Trace", s.toString());
		}
	}
	
}
