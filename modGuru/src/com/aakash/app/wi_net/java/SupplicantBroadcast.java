package com.aakash.app.wi_net.java;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

public class SupplicantBroadcast extends BroadcastReceiver {

	public static Context context;
	public static InetAddress serverIP;
	public static int connected = -1;
	public static int wifiEnabled = -1;
	public static int apEnabled = -1;
	public static int mtu;
	public static InetAddress broadcast;
	public static Server sendServer;
	public static Server receiveServer;
	public static WifiManager manager;
	public static InetAddress ipAddr;
	public static BroadcastMessage broadcastMessage;
	public static final String ACTION = "com.aakash.app.wi_net.BroadcastMessageReceived";
	public static final String EXTRA_IPADDR = "IPADDR";
	public static final String EXTRA_MESSAGE = "message";
	public static Thread sendQuizThread;

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Server.clearpath();
			SupplicantBroadcast.context = context;
			manager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			String action = intent.getAction();
			if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
				int iTemp = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
						WifiManager.WIFI_STATE_UNKNOWN);
				Log.d("Wi-Net", "WiFi State Changed");
				checkState(iTemp);
			} else if (action.equals("android.net.wifi.WIFI_AP_STATE_CHANGED")) {
				int state = intent.getIntExtra("wifi_state", 0);
				checkState(state);
			} else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				Log.d("Wi-Net", "Connection State Changed");
				DetailedState state = ((NetworkInfo) intent
						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO))
						.getDetailedState();
				changeState(state);
			}
		} catch (Exception e) {
			Log.e("Supplicant Error", " ", e);
		}
	}

	private void changeState(DetailedState aState) {
		if (aState == DetailedState.SCANNING) {
			connected = -1;
			Log.d("Wi-Net", "Connection State Change:" + aState);
		} else if (aState == DetailedState.CONNECTING) {
			connected = 3;
			Log.d("Wi-Net", "Connection State Change:" + aState);
		} else if (aState == DetailedState.OBTAINING_IPADDR) {
			connected = 2;
			Log.d("Wi-Net", "Connection State Change:" + aState);
		} else if (aState == DetailedState.CONNECTED) {
			connected = 1;
			Log.d("Wi-Net", "Connection State Change:" + aState);
			ipAddr = getLocalIpAddress();
			try {
				serverIP = getAP();
			} catch (Exception e) {
			}
			broadcastMessage = new BroadcastMessage(context, 5573);
			sendQuizThread=new Thread() {
				public void run() {
					while (true) {
						try {
							Thread.sleep(2000);
							broadcastMessage
									.sendMessage("[sendQuiz" + serverIP + "]",
											mtu, 5573,
											serverIP.getHostAddress());
							broadcastMessage.receiveMessage(5000);
						} catch (Exception e) {
						}
						if (BroadcastMessageReceiver.ackQuiz) {
							Log.e("Quiz Receive", "ack");
							BroadcastMessageReceiver.ackQuiz = false;
							try {
								Thread.sleep(2500);
							} catch (Exception e) {
							}
							try {
								new Handler(context.getMainLooper())
										.post(new Runnable() {
											@Override
											public void run() {
												new Client(true).execute(serverIP
														.getHostAddress(),
														5571);
											}
										});
							} catch (Exception e) {
							}
						}
					}
				};
			};
			sendQuizThread.start();
		} else if (aState == DetailedState.DISCONNECTING) {
			connected = -1;
			broadcastMessage.disconnect();
			Log.d("Wi-Net", "Connection State Change:" + aState);
		} else if (aState == DetailedState.DISCONNECTED) {
			connected = -1;
			Log.d("Wi-Net", "Connection State Change: " + aState);
			broadcastMessage.disconnect();
			broadcastMessage = null;
		} else if (aState == DetailedState.FAILED) {
			connected = -1;
			Log.d("Wi-Net", "Connection State Change:" + aState);
		}
	}

	public void checkState(int aInt) {
		if (aInt == WifiManager.WIFI_STATE_ENABLING) {
			wifiEnabled = 0;
			Log.d("WifiManager", "WIFI_STATE_ENABLING");
		} else if (aInt == WifiManager.WIFI_STATE_ENABLED) {
			wifiEnabled = 1;
			Log.d("WifiManager", "WIFI_STATE_ENABLED");
		} else if (aInt == WifiManager.WIFI_STATE_DISABLING) {
			wifiEnabled = 3;
			connected = -1;
			Log.d("WifiManager", "WIFI_STATE_DISABLING");
		} else if (aInt == WifiManager.WIFI_STATE_DISABLED) {
			if (wifiEnabled == 3) {
				wifiEnabled = -1;
				connected = -1;
				broadcastMessage.disconnect();
				broadcastMessage = null;
				Log.d("WifiManager", "WIFI_STATE_DISABLED");
			}
		} else if (aInt == 11) {
			apEnabled = -1;
			if (wifiEnabled == -1) {
				sendServer.destroy();
				sendServer = null;
				broadcastMessage.disconnect();
				broadcastMessage = null;
			}
			Log.d("Wi-Net", "AP State Change: Disabled");
		} else if (aInt == 13) {
			connected = -1;
			wifiEnabled = -1;
			Log.d("Wi-Net", "AP State Change: Enabled1");
			broadcastMessage = new BroadcastMessage(context, 5573);
			new Thread() {
				public void run() {
					try {
						Thread.sleep(2500);
					} catch (Exception e) {
					}
					Log.d("Wi-Net", "AP State Change: Enabled2");
					receiveServer=new Server(context, 5572);
					sendServer = new Server(context, 5571);
					ipAddr = getLocalIpAddress();
					apEnabled = 1;
					new Thread() {
						public void run() {
							while (true) {
								try {
									broadcastMessage.receiveMessage(5000);
								} catch (Exception e) {
								}
							}
						};
					}.start();
					Log.d("Wi-Net", "AP State Change: Enabled3");
				};
			}.start();

		} else if (aInt == 14) {
			apEnabled = -1;
			Log.d("Wi-Net", "AP State Change: Failed");
		}
	}

	public InetAddress getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					for (InterfaceAddress add : intf.getInterfaceAddresses()) {
						Log.e("Braoscast", add.getBroadcast() + "");
						broadcast = add.getBroadcast();
					}
					if (!inetAddress.isLoopbackAddress()
							&& inetAddress instanceof Inet4Address) {
						Log.e("IP Address", inetAddress.getHostAddress());
						mtu = intf.getMTU();
						return inetAddress;
					}
				}
			}
		} catch (Exception ex) {
			Log.e("IP Address", ex.toString());
		}
		return null;
	}

	private InetAddress getAP() throws UnknownHostException {
		DhcpInfo info = manager.getDhcpInfo();
		int dns = info.gateway;
		byte[] bytes = new byte[4];
		for (int i = 0; i < 4; i++) {
			bytes[i] = (byte) (dns >> (8 * i));
		}
		return InetAddress.getByAddress(bytes);
	}
}
