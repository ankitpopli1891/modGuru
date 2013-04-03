package com.aakashapp.modguru.src;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import org.xmlpull.v1.XmlSerializer;

import android.os.Environment;
import android.util.Xml;

public class CreateQuizXML {
	File xmlFile;
	FileOutputStream fileOutputStream;
	XmlSerializer serializer;

	public void createXML(String author, String topic, String time, String fileName, String password) throws IOException {
		File sdCard = Environment.getExternalStorageDirectory();
		File myDir = new File (sdCard.getAbsolutePath() + "/CQ/");
		if(!myDir.exists())
			myDir.mkdir();
		xmlFile = new File(sdCard.getAbsolutePath() + "/CQ/" + fileName);

		if(!xmlFile.exists())
			xmlFile.createNewFile();
		fileOutputStream = new FileOutputStream(xmlFile);
		serializer = Xml.newSerializer();
		serializer.setOutput(fileOutputStream, "UTF-8");
		serializer.startDocument(null, true);
		serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
		serializer.startTag(null, "quiz");
		serializer.attribute(null, "author", author);
		serializer.attribute(null, "topic", topic);
		serializer.attribute(null, "time", time);
		serializer.attribute(null, "score", "");
		serializer.attribute(null, "password", password);
		Calendar calendar = Calendar.getInstance();
		serializer.attribute(null, "date",""+calendar.get(Calendar.DATE)+"/"+(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.YEAR));
	}

	public void startQuestion(String ques) throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag(null, "question");
		serializer.startTag(null, "ques");
		serializer.text(ques);
		serializer.endTag(null, "ques");
	}

	public void addOption(String option, boolean correct) throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag(null, "option");
		if(correct)
			serializer.attribute(null, "correct", "true");
		serializer.text(option);
		serializer.endTag(null, "option");
	}

	public void addNote(String note) throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag(null, "note");
		serializer.text(note);
		serializer.endTag(null, "note");
	}

	public void endQuestion() throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.endTag(null, "question");
	}

	public void finishXMLFile() throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.endTag(null, "quiz");
		serializer.endDocument();
		serializer.flush();
		fileOutputStream.close();
	}
}
