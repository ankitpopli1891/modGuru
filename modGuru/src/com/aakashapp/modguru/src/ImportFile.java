package com.aakashapp.modguru.src;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.os.Environment;

import com.aakashapp.modguru.Main;

public class ImportFile {

	public static void importFile(String file) throws SAXException, ParserConfigurationException, IOException {
		Parser parser = new Parser(new FileInputStream(new File(file)));
		if(!parser.extractQuiz().getScore().equals(""))
			throw new SAXException("Score not set to blank");

		File sourceFile = new File(file);

		File myDir = new File(Environment.getDataDirectory().getAbsolutePath()+"/data/"+Main.PACKAGE_NAME+"/quiz/");
		if(!myDir.exists())
			myDir.mkdirs();
		File destFile = new File(myDir+"/"+System.currentTimeMillis());
		if(!destFile.exists()) {
			destFile.createNewFile();
		}

		FileChannel source = new FileInputStream(sourceFile).getChannel();
		FileChannel destination = new FileOutputStream(destFile).getChannel();

		long count = 0;
		long size = source.size(); 

		while((count += destination.transferFrom(source, count, size-count))<size);
		if(source != null) {
			source.close();
		}
		if(destination != null) {
			destination.close();
		}
	}
}
