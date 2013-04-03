package com.aakashapp.modguru.src;

import java.io.File;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlSerializer;

import com.aakashapp.modguru.Main;

import android.os.Environment;
import android.util.Xml;

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
		//File quizFolder = new File(Environment.getExternalStorageDirectory() + "/CQR/"+quizFile);
		File quizFolder = new File(Environment.getExternalStorageDirectory() + "/Android/data/"+Main.PACKAGE_NAME+"/"+quizFile+"/");
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
		
		serializer.endTag(null, "result");
		serializer.endDocument();
		serializer.flush();
		fileOutputStream.close();
	}
}
/*
 *
 * File dataDirectory = Environment.getDataDirectory();
		try {
			(new File(dataDirectory+"/"+Main.PACKAGE_NAME)).createNewFile();
		} catch (IOException e) {
			Log.e("io", "ROOT");
		}
		File sdCard = Environment.getExternalStorageDirectory();
		File myDir = new File (sdCard.getAbsolutePath() + "/Android/data/"+Main.PACKAGE_NAME);
		
 * */
