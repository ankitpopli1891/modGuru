package com.aakash.app.wi_net.java;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.aakashapp.modguru.SelectQuizActivity;

//import com.aakash.app.wi_net.service.WiNetService;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

public class Server {

	private ServerSocket server;
	private Context context;
	private static String path;
	private ArrayList<String> clients;
	private boolean isFirst=true;
	
	public Server(Context context,int port) {
		try
		{
			server=new ServerSocket(port);
			this.context=context;
			clients=new ArrayList<String>();
			new Thread(){
				public void run() {
					startAcceptingClients();
					};
			}.start();
		}
		catch (Exception e) {
			Log.e("Wi-Net Error", e.getMessage() + "");
			for (StackTraceElement s : e.getStackTrace())
				Log.e("Wi-Net Stack Trace", s.toString());
		}
	}
	
	public static void setPath(String filePath)
	{
		path=filePath;
	}
	
	public void destroy()
	{
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void startAcceptingClients()
	{
		try{
			while(true)
			{
				final Socket client=server.accept();
				Log.e("Client", client.getLocalAddress().getHostAddress()+"    "+client.getInetAddress().getHostAddress());
				Thread.sleep(2000);
				sendData(client);
			}
		}catch (Exception e) {
			Log.e("Wi-Net Error", e.getMessage() + "");
			for (StackTraceElement s : e.getStackTrace())
				Log.e("Wi-Net Stack Trace", s.toString());
		}
	}
	
	public void addClient(String ip)
	{
		try{
			Log.e("Set Data", ip);
			clients.add(ip);
		}catch (Exception e) {
			Log.e("Wi-Net Error", e.getMessage() + "");
			for (StackTraceElement s : e.getStackTrace())
				Log.e("Wi-Net Stack Trace", s.toString());
		}
	}
	
	private void sendData(final Socket client)
	{
		Log.e("Send","1");
		final String ip=client.getInetAddress().getHostAddress();
		if(clients.contains(ip))
		{
			Log.e("Send","2");
			if(path!=null)
			{
				new Handler(context.getMainLooper()).post(new Runnable() {
					
					@Override
					public void run() {

						new SendFileAsync(context, client).execute(new File(path));
						clients.remove(ip);
					}
				});
			}
		}
	}
}
