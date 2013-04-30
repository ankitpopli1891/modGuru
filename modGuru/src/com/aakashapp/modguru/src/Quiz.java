package com.aakashapp.modguru.src;

import java.util.ArrayList;

public class Quiz {
	private String author;
	private String topic;
	private String time;
	private String score;
	private String date;
	private String password;
	private String quesCount;
	private ArrayList<Question> questions;
	
	public Quiz(String author, String topic, String time, String score, String date, String password, String quesCount) throws Exception {
		this.author = author;
		this.topic = topic;
		this.time = time;
		this.score = score;		
		this.date = date;
		this.password = password;
		this.quesCount = quesCount;
	}
	
	public void setQuestions(ArrayList<Question> ques) {
		questions=ques;
	}	
	
	public String getAuthor() {
		return this.author;
	}
	
	public String getTopic() {
		return this.topic;
	}
	
	public String getTime() {
		return this.time;
	}
	
	public String getScore() {
		return this.score;
	}
	
	public String getDate() {
		return this.date;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public String getQuesCount() {
		return this.quesCount;
	}
	
	public ArrayList<Question> getQuestions() {
		return this.questions;
	}
}
