package com.aakash.app.wi_net.java;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class Server {

    private ServerSocket server;
    private Context context;
    private static String path;
    private UniqueList<String> sendClientList, receiveClientList;

    public Server(Context context, int port) {
        try {
            server = new ServerSocket(port);
            this.context = context;
            sendClientList = new UniqueList<String>();
            receiveClientList = new UniqueList<String>();
            new Thread() {
                public void run() {
                    startAcceptingClients();
                }
            ;
        } .start();
		} catch (Exception e) {
            Log.e("Wi-Net Error", e.getMessage(), e);
        }
    }

    public static void setPath(String filePath) {
        path = filePath;
    }
    
    public static void clearpath()
    {
    	path=null;
    }

    public void destroy() {
        try {
            server.close();
        } catch (IOException e) {
            Log.e("Wi-Net Error", e.getMessage(), e);
        }
    }

    private void startAcceptingClients() {
        while (true) {
        	try {
                final Socket client = server.accept();
                Log.e("Client", client.getLocalAddress().getHostAddress() + "    " + client.getInetAddress().getHostAddress());
                sendData(client);
        } catch (Exception e) {
            Log.e("Wi-Net Error", e.getMessage(), e);
        }
        }
    }

    public void addSendClient(String ip) {
        try {
            Log.e("Set Data", ip);
            sendClientList.add(ip);
        } catch (Exception e) {
            Log.e("Wi-Net Error", e.getMessage(), e);
        }
    }

    public void addReceiveClient(String ip) {
        try {
            Log.e("Set Data", ip);
            receiveClientList.add(ip);
        } catch (Exception e) {
            Log.e("Wi-Net Error", e.getMessage(), e);
        }
    }
    
    private void sendData(final Socket client) {
        Log.e("Send", "1");
        final String ip = client.getInetAddress().getHostAddress();
        if (sendClientList.contains(ip)) {
            if (path != null) {
                new Handler(context.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        new SendFileAsync(context, client, true).execute(new File(path));
                        sendClientList.remove(ip);
                    }
                });
            }
            else{
            	try{
            		client.close();
            	}
            catch (Exception e) {
            	 Log.e("Wi-Net Error", "", e);
			}
            }
        }
        else if (receiveClientList.contains(ip)) {
                new Handler(context.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        new SendFileAsync(context, client, false).execute();
                        receiveClientList.remove(ip);
                    }
                });
        }
        else{
        	try{
        		client.close();
        	}
        catch (Exception e) {
        	 Log.e("Wi-Net Error", "", e);
		}
        }
    }
}
