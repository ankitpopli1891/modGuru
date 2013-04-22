package com.aakashapp.modguru.src;

public class Option {

	private boolean isAnswer;
	private String value;
	
	public Option(String option,boolean isAnswer) {
		this.value=option;
		this.isAnswer=isAnswer;
	}
	
	public String getOptionValue() {
		return value;
	}
	
	public boolean isOptionCorrectAnswer() {
		return isAnswer;
	}
}
