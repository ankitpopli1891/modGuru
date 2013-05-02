package com.aakash.app.wi_net.java;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BroadcastMessage {

    private DatagramSocket send;
    private MulticastSocket receive;
    private InetAddress broadcastAddress;
    private Context parent;

    public BroadcastMessage(Context parent, int inPort) {
        try {
            this.parent = parent;
            broadcastAddress = InetAddress.getByName("255.255.255.255");
            if (SupplicantBroadcast.apEnabled == 1) {
                broadcastAddress = SupplicantBroadcast.broadcast;
            }
            send = new DatagramSocket();
            receive = new MulticastSocket(inPort);
            receive.joinGroup(InetAddress.getByName("224.0.0.1"));
        } catch (Exception e) {
            Log.e("Wi-Net Error", "BroadcastMessage", e);
        }
    }

    public void sendMessage(String message, int mtu, int targetPort, String address) {
        try {
            InputStream stream = new ByteArrayInputStream(message.getBytes("UTF-8"));
            byte[] data = new byte[mtu];
            while (stream.available() > 0) {
                int len = stream.read(data);
                DatagramPacket packet = new DatagramPacket(data, len, InetAddress.getByName(address), targetPort);
                send.send(packet);
            }
        } catch (Exception e) {
            Log.e("Wi-Net Error", broadcastAddress.getHostAddress());
            Log.e("Wi-Net Error", " ", e);
        }

    }

    public void receiveMessage(int timeout) {
        try {
            byte[] buf = new byte[1500];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            receive.setSoTimeout(timeout);
            receive.receive(packet);
            Log.e("receive", new String(packet.getData(), 0, packet.getLength(), "UTF-8") + "    " + !packet.getAddress().getHostAddress().equals(SupplicantBroadcast.ipAddr.getHostAddress()));
            if (!packet.getAddress().getHostAddress().equals(SupplicantBroadcast.ipAddr.getHostAddress())) {
                Log.e("Custom Receiver", "Calling");
                Intent intent = new Intent();
                intent.setAction(SupplicantBroadcast.ACTION);
                intent.putExtra(SupplicantBroadcast.EXTRA_MESSAGE, new String(packet.getData(), 0, packet.getLength(), "UTF-8"));
                intent.putExtra(SupplicantBroadcast.EXTRA_IPADDR, packet.getAddress().getHostAddress());
                parent.sendBroadcast(intent);
            }
        } catch (SocketTimeoutException st) {
            Log.e("Wi-Net Error", "Timeout");
        } catch (Exception e) {
            Log.e("Wi-Net Error", " ", e);
        }
    }

    public void disconnect() {
        send.close();
        receive.close();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        disconnect();
    }
}
