package com.aakash.app.wi_net.java;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BroadcastMessageReceiver extends BroadcastReceiver {

    public static boolean ackQuiz, ackResult;

    @Override
    public synchronized void onReceive(final Context context, Intent intent) {
        Log.e("Custom Receiver", intent.getAction().equals(SupplicantBroadcast.ACTION) + "");
        if (intent.getAction().equals(SupplicantBroadcast.ACTION)) {
            String message = intent.getStringExtra(SupplicantBroadcast.EXTRA_MESSAGE);
            final String ipAddr = intent.getStringExtra(SupplicantBroadcast.EXTRA_IPADDR);
            Log.d("Custom Receiver", message);
            if (message.equals("[sendQuiz" + SupplicantBroadcast.ipAddr + "]")) {
                Log.d("Custom Receiver", message + "  " + ipAddr);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("Msgs", "[ack/quiz/" + ipAddr + "]");
                        SupplicantBroadcast.sendServer.addSendClient(ipAddr);
                        SupplicantBroadcast.broadcastMessage.sendMessage("[ack/quiz/" + ipAddr + "]", SupplicantBroadcast.mtu, 5573, ipAddr);
                    }
                }).start();
            } else if (message.equals("[sendingResult" + SupplicantBroadcast.ipAddr + "]")) {
                Log.d("Custom Receiver", message + "  " + ipAddr);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("Msgs", "[ack/result/" + ipAddr + "]");
                  		SupplicantBroadcast.receiveServer.addReceiveClient(ipAddr);
                        SupplicantBroadcast.broadcastMessage.sendMessage("[ack/result/" + ipAddr + "]", SupplicantBroadcast.mtu, 5573, ipAddr);
                        Log.e("ResClient", "Created"+"  "+ipAddr);
                    }
                }).start();
            } else if (message.equals("[ack/quiz" + SupplicantBroadcast.ipAddr + "]")) {
                Log.d("Custom Receiver", message + "  " + ipAddr);
                ackQuiz = true;
            } else if (message.equals("[ack/result" + SupplicantBroadcast.ipAddr + "]")) {
                Log.d("Custom Receiver", message + "  " + ipAddr);
                ackResult = true;
            }
        }
    }
}
