package com.aakashapp.modguru.src;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class Parser {

	private XMLReader reader;
	private InputStream stream;

	public Parser(InputStream stream) {
		try {
			reader = SAXParserFactory.newInstance().newSAXParser()
					.getXMLReader();
			this.stream=stream;
		} catch (Exception e) {
			Log.e("Parser Error", e.getMessage());
			for(StackTraceElement s:e.getStackTrace())
				Log.e("Parser Stack Trace", s.toString());
		}
	}
	
	@SuppressWarnings("finally")
	public Quiz extractQuiz()
	{
		Quiz quiz=null;
		try {
			reader = SAXParserFactory.newInstance().newSAXParser()
					.getXMLReader();
			QuizExtracter quizExtracter = new QuizExtracter();
			reader.setContentHandler(quizExtracter);
			reader.parse(new InputSource(stream));
			quiz=quizExtracter.getQuiz();
		} catch (Exception e) {
			Log.e("Parser Error", e.getMessage());
			for(StackTraceElement s:e.getStackTrace())
				Log.e("Parser Stack Trace", s.toString());
		}
		finally
		{
			return quiz;
		}
	}
	
	@SuppressWarnings("finally")
	public String extractResult()
	{
		String summary="";
		try {
			reader = SAXParserFactory.newInstance().newSAXParser()
					.getXMLReader();
			ResultExtracter resultExtracter=new ResultExtracter();
			reader.setContentHandler(resultExtracter);
			reader.parse(new InputSource(stream));
			summary=resultExtracter.getSummary();
		} catch (Exception e) {
			Log.e("Parser Error", e.getMessage());
			for(StackTraceElement s:e.getStackTrace())
				Log.e("Parser Stack Trace", s.toString());
		}
		finally
		{
			return summary;
		}
	}
	
	private class QuizExtracter extends DefaultHandler {
		private String value, options[];
		private Question question;
		private ArrayList<Question> questions;
		private int optionsParsed;
		private boolean isAnswer[];
		private Quiz quiz;

		public QuizExtracter() {
			questions = new ArrayList<Question>();
			optionsParsed = 0;
			options = new String[4];
			isAnswer = new boolean[4];
		}

		public Quiz getQuiz() {
			return quiz;
		}

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			try
			{
			value = "";
			if (qName.equalsIgnoreCase("quiz")) {
				String author = attributes.getValue("author");
				String topic = attributes.getValue("topic");
				String time = attributes.getValue("time");
				String score = attributes.getValue("score");
				String date = attributes.getValue("date");
				String password = attributes.getValue("password");
				quiz = new Quiz(author, topic, time, score, date, password);
			} else if (qName.equalsIgnoreCase("question")) {
				question = new Question();
				optionsParsed = 0;
			} else if (qName.equalsIgnoreCase("option")) {
				String correct = attributes.getValue("correct");
				isAnswer[optionsParsed] = Boolean.parseBoolean(correct);
			}
			}
			catch (Exception e) {
				Log.e("Parser Error", e.getMessage());
				for(StackTraceElement s:e.getStackTrace())
					Log.e("Parser Stack Trace", s.toString());
			}
		}

		public void characters(char[] ch, int start, int length)
				throws SAXException {
			value = new String(ch, start, length);
		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			try
			{
			if (qName.equalsIgnoreCase("quiz")) {
				quiz.setQuestions(questions);
			}
			else if (qName.equalsIgnoreCase("question")) {
				questions.add(question);
			} else if (qName.equalsIgnoreCase("ques")) {
				question.setQuestion(value);
			} else if (qName.equalsIgnoreCase("option")) {
				options[optionsParsed] = value;
				optionsParsed++;
				if (optionsParsed == 4)
					question.setOptions(options, isAnswer);
			} else if (qName.equalsIgnoreCase("note")) {
				question.setNote(value);
			}
			}
			catch (Exception e) {
				Log.e("Parser Error", e.getMessage());
				for(StackTraceElement s:e.getStackTrace())
					Log.e("Parser Stack Trace", s.toString());
			}
		}
	}
	
	private class ResultExtracter extends DefaultHandler
	{
		private String value, summary;// option;

		public String getSummary() {
			return summary;
		}
		
//		public String getOption() {
//			return option;
//		}

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			try
			{
			value = "";
			
			}
			catch (Exception e) {
				Log.e("Parser Error", e.getMessage());
				for(StackTraceElement s:e.getStackTrace())
					Log.e("Parser Stack Trace", s.toString());
			}
		}

		public void characters(char[] ch, int start, int length)
				throws SAXException {
			value = new String(ch, start, length);
		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			try
			{
			if (qName.equalsIgnoreCase("summary")) {
				summary=value;
			}
//			else if (qName.equals("OPTION")) {
//				option=value;
//			}
			}
			catch (Exception e) {
				Log.e("Parser Error", e.getMessage());
				for(StackTraceElement s:e.getStackTrace())
					Log.e("Parser Stack Trace", s.toString());
			}
		}
	}
}
