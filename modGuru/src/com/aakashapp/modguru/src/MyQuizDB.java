package com.aakashapp.modguru.src;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MyQuizDB {

	public static final String DB_QUIZ = "QuizByMe";
	SharedPreferences preferences;
	String []quizes;

	public MyQuizDB(Context context) {
		preferences = context.getSharedPreferences(DB_QUIZ, Context.MODE_PRIVATE);
	}

	public void addQuiz(String quizFile) {
		int count = preferences.getInt("count", 0);
		Editor editor = preferences.edit();
		editor.putInt("count", count++);
		editor.putString(String.valueOf(count), quizFile);
		editor.commit();
	}

	public void deleteQuiz(String quizFile) {
		int count = preferences.getInt("count", 0);
		Editor editor = preferences.edit();
		for(int i=1; i<=count ;i++)
			if(preferences.contains(String.valueOf(i)) && preferences.getString(String.valueOf(i), "").equals(quizFile)) {
				editor.remove(String.valueOf(i));
				editor.commit();
			}
	}

	public boolean contains(String file) {
		int count = preferences.getInt("count", 0);
		for(int i=1; i<=count ;i++)
			if(preferences.contains(String.valueOf(i)) && preferences.getString(String.valueOf(i), "").equals(file))
				return true;
		return false;
	}

	public ArrayList<String> getQuizFiles() {
		ArrayList<String> quizFiles = new ArrayList<String>();;
		int count = preferences.getInt("count", 0);
		for(int i=1; i<=count ;i++)
			if(preferences.contains(String.valueOf(i)))
				quizFiles.add(preferences.getString(String.valueOf(i), ""));
		return quizFiles;
	}

}
