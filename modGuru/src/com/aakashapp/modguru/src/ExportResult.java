package com.aakashapp.modguru.src;

import java.io.File;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlSerializer;

import android.os.Environment;
import android.util.Xml;

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
	}
}
