package com.aakash.app.wi_net.java;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.Socket;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

public class Client extends AsyncTask<Object, Integer, String> {

	private Socket client;
	private boolean receiving;

	public Client(boolean receive) {
		receiving=receive;
		Log.e("Client", "Created");
	}

	@Override
	protected String doInBackground(Object... params) {
		String ip = (String)params[0];
		int port = (Integer)params[1];
		String msg;
		if(receiving)
			msg = receiveData(ip, port);
		else
			msg = sendData(ip, port, (File) params[2]);
		return msg;
	}

	public String receiveData(String address, int port) {
		String msg = "Done";
		try {
			client = new Socket(address, port);
			Log.e("Client-Server","Connected");
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
		} catch (Exception e) {
			msg = "Error occurred while downloading file";
			Log.e("Wi-Net Error", "", e);
		}
		finally{
			try {
				client.close();
			} catch (Exception e) {
				Log.e("Wi-Net Error", e.getMessage(), e);
			}
		}
		return msg;
	}

	private String sendData(String address, int port, File file) {
		String msg = "Done";
		try {
			client = new Socket(address, port);
			DataOutputStream writer = new DataOutputStream(client.getOutputStream());

			long total = file.length();
			String meta = "start/metadata\nname:" + getName(file)
			+ "\nlen:" + total + "\nend/metadata\nstart/data";
			Log.e("Sending", meta);
			writer.writeInt(meta.getBytes().length);
			writer.write(meta.getBytes(), 0, meta.getBytes().length);
			writer.flush();
			InputStream stream = new FileInputStream(file);
			byte[] buffer = new byte[1500];
			int read;
			while (stream.available() > 0) {
				read = stream.read(buffer);
				writer.writeInt(read);
				writer.write(buffer, 0, read);
				Log.e("Sending", new String(buffer));
			}
			meta = "end/data";
			writer.writeInt(meta.getBytes().length);
			writer.write(meta.getBytes("UTF-8"));

			meta = "transcomp/term";
			writer.writeInt(meta.getBytes().length);
			writer.write(meta.getBytes("UTF-8"));
			publishProgress(100);
			client.close();
		} catch (Exception e) {
			msg = "Error Sending File";
			Log.e("Wi-Net Error", e.getMessage(), e);
		}
		return msg;
	}

	private String getName(File file) {
		return "/res/"+file.getParentFile().getName()+"/"+System.currentTimeMillis() + "_" + PreferenceManager.getDefaultSharedPreferences(SupplicantBroadcast.context).getString("general_participant_id", "Anonymous"); 
	}

	@Override
	protected void onPostExecute(String result) {
		Log.e("Receiver", result);
		if(result.equalsIgnoreCase("Done")){
			WifiManager manager=(WifiManager)SupplicantBroadcast.context.getSystemService(Context.WIFI_SERVICE);
			manager.setWifiEnabled(false);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		try {
			client.close();
		} catch (Exception e) {
			Log.e("Wi-Net Error", e.getMessage() + "");
			for (StackTraceElement s : e.getStackTrace()) {
				Log.e("Wi-Net Stack Trace", s.toString());
			}
		}
	}
}
