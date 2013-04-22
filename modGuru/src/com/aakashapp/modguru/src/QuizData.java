package com.aakashapp.modguru.src;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class QuizData implements Serializable{

	private static final long serialVersionUID = 8106094584450509119L;
	private ArrayList<String> questions;
	private ArrayList<String[]> options;
	private ArrayList<String> correctOpt;
	private ArrayList<String> notes;
	private String author, topic, time, password, score;

	public QuizData() {
		questions = new ArrayList<String>();
		options = new ArrayList<String[]>();
		correctOpt = new ArrayList<String>();
		notes = new ArrayList<String>();
	}

	public void setMetaData(String author, String topic, String time, String password, String score) {
		this.author = author;
		this.topic = topic;
		this.time = time;
		this.password = password;
		this.score = score;
	}
	
	public void addQuestion(String question) {
		questions.add(question);
	}

	public void addOptions(String []opts) {
		options.add(opts);
	}

	public void setCorrectOpt(String copt) {
		correctOpt.add(copt);
	}

	public void addNote(String note) {
		notes.add(note);
	}
	
	public void addQuestion(String question, int index) {
		questions.add(index, question);
	}

	public void addOptions(String []opts, int index) {
		options.add(index, opts);
	}

	public void setCorrectOpt(String copt, int index) {
		correctOpt.add(index, copt);
	}

	public void addNote(String note, int index) {
		notes.add(index, note);
	}

	public ArrayList<String> getQuestions() {
		return questions;
	}	

	public String getQuestion(int index) {
		return questions.get(index);
	}
	
	public String[] getOptions(int index) {
			return options.get(index);
	}
	
	public String getNote(int index) {
		return notes.get(index);
	}
	
	public String getCorrectOpts(int index) {
		return correctOpt.get(index);
	}
	
	public void deleteQuestion(int index) {
		questions.remove(index);
		notes.remove(index);
		options.remove(index);
		correctOpt.remove(index);
	}
	
	public long getSerialVersionUID() {
		return serialVersionUID;
	}

	public void writeToXML(String xmlFile) throws IOException {
		CreateQuizXML quizXML = new CreateQuizXML();
		quizXML.createXML(author, topic, time, xmlFile, password, score);
		int noOfQuestions = questions.size();
		for (int i = 0; i<noOfQuestions; i++) {
			quizXML.startQuestion(questions.get(i));
			String[] stringOptions = options.get(i);
			for (int j=0; j<4;j++)
				if(correctOpt.get(i).contains(""+j))
					quizXML.addOption(stringOptions[j], true);
				else
					quizXML.addOption(stringOptions[j], false);
			quizXML.addNote(notes.get(i));
			quizXML.endQuestion();
		}
		quizXML.finishXMLFile();
	}
}
