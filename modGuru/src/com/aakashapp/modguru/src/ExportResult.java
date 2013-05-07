package com.aakashapp.modguru.src;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import org.xmlpull.v1.XmlSerializer;

import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.Xml;

import com.aakash.app.wi_net.java.BroadcastMessageReceiver;
import com.aakash.app.wi_net.java.Client;
import com.aakash.app.wi_net.java.SupplicantBroadcast;
import com.aakashapp.modguru.Main;

public class ExportResult {
	int resultSummary[];
	Answers answers;
	File quizFile;
	FileOutputStream fileOutputStream;
	XmlSerializer serializer;

	public ExportResult(String quizFile, int resultSummary[]) {
		this.quizFile = new File(quizFile);
		this.resultSummary = resultSummary;
	}

	public void setAnswers(Answers answers) {
		this.answers = answers;
	}

	public void export() throws Exception {
		File quizFolder = new File(Environment.getDataDirectory()+"/data/"+Main.PACKAGE_NAME+"/res/"+quizFile+"/");
		if(!quizFolder.exists())
			quizFolder.mkdirs();

		quizFile = new File(quizFolder+"/"+quizFile);
		if(!quizFile.exists())
			quizFile.createNewFile();

		fileOutputStream = new FileOutputStream(quizFile);
		serializer = Xml.newSerializer();
		serializer.setOutput(fileOutputStream, "UTF-8");
		serializer.startDocument(null, true);
		serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
		serializer.startTag(null, "result");

		serializer.startTag(null, "summary");
		String temp = "";
		for(int i:resultSummary)
			temp+=i;
		serializer.text(temp);
		serializer.endTag(null, "summary");

		int i = answers.getNoOfAnswers();
		for(int j=0; j<i; j++) {
			serializer.startTag(null, "ans");
			serializer.text(answers.getAnswer(j));
			serializer.endTag(null, "ans");
		}

		serializer.endTag(null, "result");
		serializer.endDocument();
		serializer.flush();
		fileOutputStream.close();
		
		sendResult(quizFile);
	}
	
	private void sendResult(final File file)
	{
		WifiManager manager=SupplicantBroadcast.manager;
		if(!manager.isWifiEnabled())
			manager.setWifiEnabled(true);
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		SupplicantBroadcast.sendQuizThread.interrupt();
		final Random random=new Random();
		final Thread thread = new Thread(){
			@Override
			public void run() {
				do {
					try {
						Thread.sleep(2000);
						SupplicantBroadcast.broadcastMessage.sendMessage("[sendingResult"+SupplicantBroadcast.serverIP+"]", SupplicantBroadcast.mtu, 5573, SupplicantBroadcast.serverIP.getHostAddress());
						SupplicantBroadcast.broadcastMessage.receiveMessage(5000);
						SupplicantBroadcast.sendQuizThread.interrupt();
					} catch (Exception e) {
					}
				}while(!BroadcastMessageReceiver.ackResult);
					Log.e("Result", "Sending");
					BroadcastMessageReceiver.ackResult=false;
					try{
						long time=random.nextInt(1000)+1000L;
						Thread.sleep(time);
					}catch (Exception e) {
					}
					new Client(false).execute(SupplicantBroadcast.serverIP.getHostAddress(), 5572, file);
				}
		};
		thread.start();
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Log.e("Result", "Stopping");
				thread.interrupt();
			}
		}, 2*60*1000);
	}
}
