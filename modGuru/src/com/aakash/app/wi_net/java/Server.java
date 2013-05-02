package com.aakash.app.wi_net.java;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class Server {

    private ServerSocket server;
    private Context context;
    private static String path;
    private ArrayList<String> clients;

    public Server(Context context, int port) {
        try {
            server = new ServerSocket(port);
            this.context = context;
            clients = new ArrayList<String>();
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

    public void destroy() {
        try {
            server.close();
        } catch (IOException e) {
            Log.e("Wi-Net Error", e.getMessage(), e);
        }
    }

    private void startAcceptingClients() {
        try {
            while (true) {
                final Socket client = server.accept();
                Log.e("Client", client.getLocalAddress().getHostAddress() + "    " + client.getInetAddress().getHostAddress());
                Thread.sleep(2000);
                sendData(client);
            }
        } catch (Exception e) {
            Log.e("Wi-Net Error", e.getMessage(), e);
        }
    }

    public void addClient(String ip) {
        try {
            Log.e("Set Data", ip);
            clients.add(ip);
        } catch (Exception e) {
            Log.e("Wi-Net Error", e.getMessage(), e);
        }
    }

    private void sendData(final Socket client) {
        Log.e("Send", "1");
        final String ip = client.getInetAddress().getHostAddress();
        if (clients.contains(ip)) {
            if (path != null) {
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
