package com.aakashapp.modguru;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aakashapp.modguru.src.Answers;
import com.aakashapp.modguru.src.ExportResult;
import com.aakashapp.modguru.src.Option;
import com.aakashapp.modguru.src.Parser;
import com.aakashapp.modguru.src.Question;
import com.aakashapp.modguru.src.Quiz;

public class QuizActivity extends Activity {

	Button buttonNext, buttonPrevious, buttonSubmit, buttonViewAnswers, buttonFirst, buttonLast;
	Button b[];
	CheckBox checkBox[];
	LinearLayout linearLayoutFlags, linearLayoutQuizData, linearLayoutSummary;
	RelativeLayout relativeLayoutNavigationButtons, masterContainer;
	TextView textViewAtQues, textViewQuestion, textViewExplanation, textViewTimer, textViewSummary;

	String xmlFile;
	Parser parser;
	ArrayList<Question> questions;
	Quiz quiz;
	Answers answers;
	String[] correctAnswers;
	CountDownTimer timer;

	int totalQuestions, atQues, attemptedQuestions, progress;
	int min, sec;
	int result[];
	boolean submitted, enableSwipe;
	GestureDetector detector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz);

		xmlFile = getIntent().getCharSequenceExtra("file").toString();
		Toast.makeText(QuizActivity.this, "Swipe Left or Right to Navigate!!", Toast.LENGTH_LONG).show();

		initializeViews();
		loadXML();
		startCountdown(Integer.parseInt(getIntent().getCharSequenceExtra("time").toString())*60*1000);

		buttonNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveAnswer();
				if(atQues<totalQuestions-1) {
					atQues++;
					refreshView();
				}
			}
		});	
		buttonLast.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveAnswer();
				atQues=totalQuestions-1;
				refreshView();
			}
		});	
		buttonPrevious.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveAnswer();
				if (atQues>0)
					atQues--;
				refreshView();
			}
		});	
		buttonFirst.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveAnswer();
				atQues=0;
				refreshView();
			}
		});	
		buttonSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				submitTest();
			}
		});	
		buttonViewAnswers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				atQues=0;
				enableSwipe = true;
				linearLayoutQuizData.setVisibility(View.VISIBLE);
				linearLayoutSummary.setVisibility(View.GONE);
				textViewAtQues.setVisibility(View.VISIBLE);
				for (int i=0; i<totalQuestions; i++)
					b[i].setClickable(true);
				refreshView();
				Toast.makeText(QuizActivity.this, "Swipe Left or Right to Navigate!!", Toast.LENGTH_LONG).show();
			}
		});
		masterContainer.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				detector.onTouchEvent(event);
				return true;
			}
		});
		b = new Button[totalQuestions];
		for (int i=0; i<totalQuestions; i++) {
			final int i1 = i;
			b[i] = new Button(this);
			b[i].setLayoutParams(new LayoutParams(48, 48));
			b[i].setTextColor(Color.WHITE);
			b[i].setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			b[i].setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
			b[i].setBackgroundResource(R.drawable.button_checkbox_unattempted);
			linearLayoutFlags.addView(b[i]);
			b[i].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					saveAnswer();
					atQues = i1;
					refreshView();
				}
			});
		}
		refreshView();
	}
	private void startCountdown(long timeInMillis) {
		timer = new CountDownTimer(timeInMillis, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				if(!submitted) {
					min = (int) millisUntilFinished / (60*1000);
					sec = (int) (millisUntilFinished % (60*1000))/1000;
					textViewTimer.setText(min+":"+sec);
				}
			}
			@Override
			public void onFinish() {
				if(!submitted)
					submitTest();
			}
		};
		timer.start();
	}
	protected void saveAnswer() {
		String ans = "";
		for(int i=0; i<4;i++)
			if(checkBox[i].isChecked())
				ans +=i;
		answers.setAnswer(atQues, ans);
		if(answers.getAnswer(atQues).equals(correctAnswers[atQues]))
			result[atQues] = 1;
		else 
			result[atQues] = 0;
	}
	protected void submitTest() {
		saveAnswer();
		attemptedQuestions = 1;
		for (int i=0; i<totalQuestions; i++) {
			if(!answers.getAnswer(i).equals(""))
				attemptedQuestions++;
		}

		int noca = 0;
		for(int i:result)
			noca+=i;
		for(int i=0 ;i<4;i++) 
			checkBox[i].setEnabled(false);

		linearLayoutQuizData.setVisibility(View.GONE);
		relativeLayoutNavigationButtons.setVisibility(View.GONE);
		linearLayoutSummary.setVisibility(View.VISIBLE);
		textViewAtQues.setVisibility(View.GONE);

		textViewSummary.setText("\nTotal No. of Questions\t: "+totalQuestions);
		textViewSummary.append("\nAttempted Questions\t: "+(attemptedQuestions-1));
		textViewSummary.append("\nCorrect Answers\t\t\t: "+noca);
		textViewSummary.append("\nIncorrect Answers\t\t: "+(attemptedQuestions - 1 - noca));
		textViewSummary.append("\nAggregate\t\t\t\t\t: "+(float)(noca*100/totalQuestions)+"%\n\n\n");

		int length = result.length;
		for (int i=0; i<length; i++) {
			b[i].setText(String.valueOf(i+1));
			b[i].setClickable(false);
			if(result[i]==1) {
				b[i].setTextColor(Color.rgb(0, 255, 0));
				b[i].setBackgroundResource(R.drawable.button_checkbox_correct);
			}
			else {
				b[i].setTextColor(Color.rgb(255, 0, 0));
				b[i].setBackgroundResource(R.drawable.button_checkbox_wrong);
			}
		}

		if(!submitted) {
			ExportResult exportResult = new ExportResult(xmlFile, result);
			try {
				exportResult.export();
			} catch (Exception e) {
				Log.e("IO", "Result Export", e);
			}
		}

		submitted = true;
		enableSwipe = false;
		timer.cancel();
		min=0;
		sec=0;
		textViewTimer.setText("00:00");
	}
	private void loadXML() {
		quiz = parser.extractQuiz();
		questions = quiz.getQuestions();
		totalQuestions = questions.size();
		answers = new Answers(totalQuestions);
		result = new int[totalQuestions];
		correctAnswers = new String[totalQuestions];
		for(int i=0; i<totalQuestions; i++) {
			correctAnswers[i] = "";
			Option opts[] = questions.get(i).getOptions();
			for(int j=0;j<4;j++)
				if(opts[j].isOptionCorrectAnswer())
					correctAnswers[i] +=j;
		}
	}
	private void refreshView() {
		textViewQuestion.setText(questions.get(atQues).getQuestion());
		Option[] opts = questions.get(atQues).getOptions();
		for (int i=0;i<4;i++) {
			checkBox[i].setText(opts[i].getOptionValue());
			String ans = answers.getAnswer(atQues);
			if(ans.contains(String.valueOf(i)))
				checkBox[i].setChecked(true);
			else
				checkBox[i].setChecked(false);
		}
		textViewAtQues.setText(atQues+1+"/"+totalQuestions);
		if(!submitted) {	
			if(atQues==0) {
				buttonFirst.setEnabled(false);
				buttonPrevious.setEnabled(false);
			}
			else {
				buttonFirst.setEnabled(true);
				buttonPrevious.setEnabled(true);
			}
			if(atQues==totalQuestions-1) {
				buttonNext.setEnabled(false);
				buttonLast.setEnabled(false);
			}
			else {
				buttonNext.setEnabled(true);
				buttonLast.setEnabled(true);
			}
			for (int i=0; i<totalQuestions; i++) {
				if(answers.getAnswer(i).equals("")) {
					b[i].setBackgroundResource(R.drawable.button_checkbox_unattempted);
					b[i].setText(String.valueOf(i+1));
				}
				else {
					b[i].setBackgroundResource(R.drawable.button_checkbox_attempted);
					b[i].setText("");
				}
			}
			b[atQues].setText(String.valueOf(atQues + 1));
			b[atQues].setBackgroundResource(R.drawable.button_checkbox_focused);
			//progressBar.setProgress(attemptedQuestions);
		}
		else {
			textViewExplanation.setText("Correct Answer: ");
			for(int i=0;i<4;i++)
				if(correctAnswers[atQues].contains(String.valueOf(i)))
					textViewExplanation.append("("+(char)(65+i)+") ");
			String note = questions.get(atQues).getNote();
			if(!note.equals(""))
				textViewExplanation.append("\n\nExplanation: "+ note);
			int length = result.length;
			for (int i=0; i<length; i++) {
				b[i].setText(String.valueOf(i+1));
				if(result[i]==1) {
					b[i].setBackgroundResource(R.drawable.button_checkbox_correct);
				}
				else {
					b[i].setBackgroundResource(R.drawable.button_checkbox_wrong);
				}
			}
			if(result[atQues]==1) {
				b[atQues].setText("");
				b[atQues].setBackgroundResource(R.drawable.bt_correct);
			}
			else {
				b[atQues].setText("");
				b[atQues].setBackgroundResource(R.drawable.bt_wrong);
			}
		}
		b[atQues].requestFocusFromTouch();
	}
	@SuppressWarnings("deprecation")
	private void initializeViews() {
		submitted = false;
		enableSwipe = true;
		atQues = 0;
		attemptedQuestions = 1;
		try {
			parser = new Parser(new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/CQ/" +xmlFile)));
		} catch (Exception e) {
			Log.e("ParserError", e.getMessage());
		}
		checkBox = new CheckBox[4];

		buttonNext = (Button) findViewById(R.id.ButtonNext);
		buttonPrevious = (Button) findViewById(R.id.ButtonPrevious);
		buttonSubmit = (Button) findViewById(R.id.ButtonSubmit);
		buttonFirst = (Button) findViewById(R.id.ButtonFirst);
		buttonLast = (Button) findViewById(R.id.ButtonLast); 
		buttonViewAnswers = (Button) findViewById(R.id.buttonViewAnswers);
		checkBox[0] = (CheckBox) findViewById(R.id.checkBox01);
		checkBox[1] = (CheckBox) findViewById(R.id.checkBox02);
		checkBox[2] = (CheckBox) findViewById(R.id.checkBox03);
		checkBox[3] = (CheckBox) findViewById(R.id.checkBox04);
		linearLayoutFlags = (LinearLayout) findViewById(R.id.linearLayoutFlags);
		linearLayoutQuizData = (LinearLayout) findViewById(R.id.linearLayoutQuizData);
		linearLayoutSummary = (LinearLayout) findViewById(R.id.linearLayoutSummary);
		relativeLayoutNavigationButtons = (RelativeLayout) findViewById(R.id.relativeLayoutNavigationButtons);
		masterContainer = (RelativeLayout) findViewById(R.id.masterContainer);
		textViewAtQues = (TextView) findViewById(R.id.textViewAtQues);
		textViewTimer = (TextView) findViewById(R.id.textViewTimer);
		textViewQuestion = (TextView) findViewById(R.id.textViewQuestion);
		textViewExplanation = (TextView) findViewById(R.id.textViewExplanation);
		textViewSummary = (TextView) findViewById(R.id.textViewSummary);

		detector = new GestureDetector(new MyGestureListener());
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		answers.setQuesInView(atQues);
		outState.putBoolean("submitted", submitted);
		outState.putBoolean("enableSwipe", enableSwipe);
		outState.putSerializable(String.valueOf(answers.getSerialVersionUID()), answers);
		if(!submitted) {
			outState.putInt("min", min);
			outState.putInt("sec", sec);
		}
	}
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		answers = (Answers) savedInstanceState.getSerializable(String.valueOf(answers.getSerialVersionUID()));
		atQues = answers.getQuesInView();
		submitted = savedInstanceState.getBoolean("submitted");
		enableSwipe = savedInstanceState.getBoolean("enableSwipe");
		if(submitted)
			submitTest();
		else {
			min = savedInstanceState.getInt("min");
			sec = savedInstanceState.getInt("sec");
			startCountdown(min*60*1000+sec*1000);
		}
		refreshView();
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		super.dispatchTouchEvent(ev);
		return detector.onTouchEvent(ev); 
	}

	class MyGestureListener extends GestureDetector.SimpleOnGestureListener{
		private static final int SWIPE_MIN_DISTANCE = 50;
		private static final int SWIPE_MAX_OFF_PATH = 200;
		private static final int SWIPE_THRESHOLD_VELOCITY = 100;
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			float dX = e2.getX()-e1.getX();
			float dY = e1.getY()-e2.getY();
			if (Math.abs(dY)<SWIPE_MAX_OFF_PATH && Math.abs(velocityX)>=SWIPE_THRESHOLD_VELOCITY && Math.abs(dX)>=SWIPE_MIN_DISTANCE && enableSwipe) {
				if (dX>0) {
					saveAnswer();
					if (atQues>0) {
						atQues--;
						refreshView();
					}
					else
						if(!submitted)
							Toast.makeText(QuizActivity.this, "You are on First Question!!", Toast.LENGTH_SHORT).show();
				} 
				else {
					saveAnswer();
					if(atQues<totalQuestions-1) {
						atQues++;
						refreshView();
					}
					else
						if(!submitted)
							Toast.makeText(QuizActivity.this, "You are on Last Question!!", Toast.LENGTH_SHORT).show();
				}
				return true;
			} 
			return false;
		}
	}
}