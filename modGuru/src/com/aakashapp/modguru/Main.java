package com.aakashapp.modguru;

import com.aakash.app.wi_net.java.BroadcastMessageReceiver;
import com.aakash.app.wi_net.java.Server;
import com.aakash.app.wi_net.java.SupplicantBroadcast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class Main extends Activity {

	public static String PACKAGE_NAME;
	Button startQuiz, createQuiz;
	private SupplicantBroadcast broadcast;
	private BroadcastMessageReceiver receiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		broadcast = new SupplicantBroadcast();
		IntentFilter filter=new IntentFilter();
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		filter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
		registerReceiver(broadcast, filter);
		
		receiver=new BroadcastMessageReceiver();
		IntentFilter receiverFilter=new IntentFilter(SupplicantBroadcast.ACTION);
		registerReceiver(receiver, receiverFilter);
		
		((WifiManager)this.getSystemService(Context.WIFI_SERVICE)).createMulticastLock("modGuru").acquire();

		PACKAGE_NAME = getApplicationContext().getPackageName();

		startQuiz = (Button) findViewById(R.id.buttonStartQuiz);
		startQuiz.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Main.this,SelectQuizActivity.class));
			}
		});

		createQuiz = (Button) findViewById(R.id.buttonCreateNewQuiz);
		createQuiz.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Main.this,QuizMetaDataActivity.class));
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		createQuiz.setAnimation(AnimationUtils.loadAnimation(Main.this, R.anim.from_left));
		startQuiz.setAnimation(AnimationUtils.loadAnimation(Main.this, R.anim.from_right));			
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemHelp:
			startActivity(new Intent(Main.this,HelpActivity.class));
			break;
		case R.id.itemSettings:
			startActivity(new Intent(Main.this,ApplicationPreferences.class));
			break;
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Server.clearpath();
		unregisterReceiver(broadcast);
		unregisterReceiver(receiver);
	}
}
