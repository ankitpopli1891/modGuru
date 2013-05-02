package com.aakash.app.wi_net.java;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.Socket;


import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

//import com.aakash.app.wi_net.service.WiNetService;

public class SendFileAsync extends AsyncTask<File, Integer, String> {

	private Socket client;
	private Context context;

	public SendFileAsync(Context context, Socket socket) {
		try {
			client = socket;
			this.context = context;
		} catch (Exception e) {
			Log.e("Wi-Net Error", e.getMessage() + "");
			for (StackTraceElement s : e.getStackTrace())
				Log.e("Wi-Net Stack Trace", s.toString());
		}
	}

	@Override
	protected String doInBackground(File... params) {
		String msg = sendData(params[0]);
		return msg;
	}

	private String sendData(File file) {
		String msg = "Done";
		try {
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
				meta="end/data";
				writer.writeInt(meta.getBytes().length);
				writer.write(meta.getBytes("UTF-8"));
			
			meta="transcomp/term";
			writer.writeInt(meta.getBytes().length);
			writer.write(meta.getBytes("UTF-8"));
			publishProgress(100);
			client.close();
		} catch (Exception e) {
			msg = "Error Sending File";
			Log.e("Wi-Net Error", e.getMessage() + "");
			for (StackTraceElement s : e.getStackTrace())
				Log.e("Wi-Net Stack Trace", s.toString());
		}
		return msg;
	}

	private String getName(File file)
	{
		String filename=file.getAbsolutePath();
		if(filename.contains("/quiz"))
			filename=filename.substring(filename.indexOf("/quiz"));
		else
			filename=filename.substring(filename.indexOf("/res"))+"_"+PreferenceManager.getDefaultSharedPreferences(context).getString("general_participant_id", "Anonymous");
		return filename;
	}

	@Override
	protected void onPostExecute(String result) {
		
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
