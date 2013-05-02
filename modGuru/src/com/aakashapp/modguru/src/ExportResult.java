package com.aakashapp.modguru.src;

import java.io.File;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.Xml;

import com.aakash.app.wi_net.java.BroadcastMessageReceiver;
import com.aakash.app.wi_net.java.Server;
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

		quizFile = new File(quizFolder+"/"+System.currentTimeMillis());
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
		final Thread thread = new Thread(){
			@Override
			public void run() {
				do{
					try{
						Thread.sleep(500);
						SupplicantBroadcast.broadcastMessage.sendMessage("[sendingResult"+SupplicantBroadcast.serverIP+"]", SupplicantBroadcast.mtu, 5573, SupplicantBroadcast.serverIP.getHostAddress());
						SupplicantBroadcast.broadcastMessage.receiveMessage(5000);
					}
					catch (Exception e) {
					}
				}while(!BroadcastMessageReceiver.ackResult);
				BroadcastMessageReceiver.ackResult=false;
				Server.setPath(file.getAbsolutePath());
				SupplicantBroadcast.server=new Server(SupplicantBroadcast.context, 5571);
				SupplicantBroadcast.server.addClient(SupplicantBroadcast.serverIP.getHostAddress());
			}
		};
		thread.start();
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Log.e("Result","Stopping");
				thread.interrupt();
				if(SupplicantBroadcast.server!=null)
				{
					SupplicantBroadcast.server.destroy();
					SupplicantBroadcast.server=null;
				}
			}
		}, 2*60*1000);
	}
}
