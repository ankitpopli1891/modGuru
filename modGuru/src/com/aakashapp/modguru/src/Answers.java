package com.aakashapp.modguru.src;

import java.io.Serializable;

public class Answers implements Serializable{

	private static final long serialVersionUID = 1773751895920960113L;
	private String[] answer;
	private int n;
	
	public Answers(int noOfQuestions) {
		n = 0;
		answer = new String[noOfQuestions];
		for (int i=0;i<noOfQuestions;i++) 
			answer[i] = " ";
	}
	
	public long getSerialVersionUID() {
		return serialVersionUID;
	}
	
	public void setAnswer(int ques, String ans) {
		answer[ques] = ans;
	}
	
	public String getAnswer(int ques) {
		return answer[ques];
	}
	
	public void setQuesInView(int n) {
		this.n = n;
	}
	
	public int getQuesInView() {
		return n;
	}
	
	public int getNoOfAnswers() {
		return answer.length;
	}
}
