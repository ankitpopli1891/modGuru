package com.aakashapp.modguru;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aakashapp.modguru.src.CustomListAdapter;
import com.aakashapp.modguru.src.QuizData;

public class CreateQuizActivity extends Activity {

	EditText editTextQuestion, editTextOpt[], editTextNotes;
	CheckBox checkBox[];
	Button b_add_ques, b_create_quiz;
	public static QuizData quizData;
	ListView listViewQuestions;
	GestureDetector detector;
	RelativeLayout masterContainer;
	static int atQues;
	String file, author, topic, timer, password;
	boolean newQuiz;
	static ArrayList<HashMap<String, String>> listQuestions;
	static CustomListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_quiz);

		Toast.makeText(CreateQuizActivity.this, "Swipe to View the List of Questions Added!!", Toast.LENGTH_LONG).show();

		try {
			file = getIntent().getCharSequenceExtra("file").toString();
			newQuiz = false;
		}catch (Exception e) {
			newQuiz = true;
		}

		author = getIntent().getCharSequenceExtra("author").toString();
		topic = getIntent().getCharSequenceExtra("topic").toString();
		timer = getIntent().getCharSequenceExtra("timer").toString();
		password = getIntent().getCharSequenceExtra("password").toString();

		initializeViews();
		clearForm();

		b_create_quiz.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(editTextQuestion.getText().toString().equals("")) {
					if (quizData.getQuestions().size() == 0)
						Toast.makeText(CreateQuizActivity.this, "No Questions Added!!\nQuiz can't be Created!!", Toast.LENGTH_LONG).show();
					else {
						writeQuizFile();
					}
					CreateQuizActivity.this.setResult(Activity.RESULT_OK);
					CreateQuizActivity.this.finish();
				}
				else {
					AlertDialog.Builder alert = new AlertDialog.Builder(CreateQuizActivity.this);
					alert.setTitle("Save Quiz");
					alert.setMessage("What do you want to do with the unsaved Question?");
					alert.setPositiveButton("Add to Quiz", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(isQuestionDataValid()) {
								addQuestion();
								writeQuizFile();
								CreateQuizActivity.this.setResult(Activity.RESULT_OK);
								CreateQuizActivity.this.finish();
							}
						}
					});
					alert.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (quizData.getQuestions().size() == 0)
								Toast.makeText(CreateQuizActivity.this, "No Questions Added!!\nQuiz can't be Created!!", Toast.LENGTH_LONG).show();
							writeQuizFile();
							CreateQuizActivity.this.setResult(Activity.RESULT_OK);
							CreateQuizActivity.this.finish();
						}
					});
					alert.show();
				}
			}
		});
		b_add_ques.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addQuestion();
			}
		});
		listViewQuestions.setItemsCanFocus(true);
		listViewQuestions.setFocusable(false);
		listViewQuestions.setFocusableInTouchMode(false);
		listViewQuestions.setClickable(false);
		listViewQuestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
					long arg3) {
				atQues = arg2;
				setView();
				listViewQuestions.setAnimation(AnimationUtils.loadAnimation(CreateQuizActivity.this, R.anim.to_right));
				listViewQuestions.setVisibility(View.GONE);
			}
		});
		listQuestions = new ArrayList<HashMap<String,String>>();
		adapter = new CustomListAdapter(CreateQuizActivity.this, listQuestions, R.layout.ques_list_layout, new String[]{"count", "ques"}, new int[]{R.id.textViewQuesNo, R.id.textViewListQuestion});
		listViewQuestions.setAdapter(adapter);
		listViewQuestions.refreshDrawableState();

		masterContainer.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				detector.onTouchEvent(event);
				return true;
			}
		});
	}

	protected void writeQuizFile() {
		try {
			String file = String.valueOf(System.currentTimeMillis());
			quizData.writeToXML(file);
		} catch (Exception e) {
			Log.e("WriteFail", e.getMessage());
		}
	}

	protected void addQuestion() {
		if(atQues==-1 && isQuestionDataValid()) {
			String copt = "";
			String opts[] = new String[4];
			quizData.addQuestion(editTextQuestion.getText().toString());
			for (int i=0; i<4; i++) {
				opts[i] = editTextOpt[i].getText().toString();
				if(checkBox[i].isChecked())
					copt += i;
			}
			quizData.addOptions(opts);
			quizData.setCorrectOpt(copt);
			quizData.addNote(editTextNotes.getText().toString());
			clearForm();
			refreshQuestionList();
			Toast.makeText(CreateQuizActivity.this, "Question Added!!", Toast.LENGTH_LONG).show();
			editTextQuestion.requestFocus();
		}
		if(atQues!=-1 && isQuestionDataValid()) {
			quizData.deleteQuestion(atQues);
			String copt = "";
			String opts[] = new String[4];
			quizData.addQuestion(editTextQuestion.getText().toString(),atQues);
			for (int i=0; i<4; i++) {
				opts[i] = editTextOpt[i].getText().toString();
				if(checkBox[i].isChecked())
					copt += i;
			}
			quizData.addOptions(opts,atQues);
			quizData.setCorrectOpt(copt,atQues);
			quizData.addNote(editTextNotes.getText().toString(),atQues);
			clearForm();
			refreshQuestionList();
			Toast.makeText(CreateQuizActivity.this, "Question Updated!!", Toast.LENGTH_LONG).show();
			editTextQuestion.requestFocus();
		}
	}

	protected void setView() {
		editTextQuestion.setText(quizData.getQuestions().get(atQues));
		String[] options = quizData.getOptions(atQues);
		String correctOpt = quizData.getCorrectOpts(atQues);
		for(int i=0;i<4;i++) {
			editTextOpt[i].setText(options[i]);
			if(correctOpt.contains(String.valueOf(i)))
				checkBox[i].setChecked(true);
			else
				checkBox[i].setChecked(false);
		}
		editTextNotes.setText(quizData.getNote(atQues));

	}

	protected boolean isQuestionDataValid() {
		String string = editTextQuestion.getText().toString();
		if(string.equals("")) {
			editTextQuestion.requestFocus();
			Toast.makeText(CreateQuizActivity.this, "Question can't be left Empty!!", Toast.LENGTH_LONG).show();
			return false;
		}
		for (int i=0;i<4;i++) {
			string = editTextOpt[i].getText().toString();
			if(string.equals("")) {
				editTextOpt[i].requestFocus();
				Toast.makeText(CreateQuizActivity.this, "Field can't be left Empty!!", Toast.LENGTH_LONG).show();
				return false;
			}
		}
		for(int i=0;i<4;i++)
			if(checkBox[i].isChecked())
				return true;
		Toast.makeText(CreateQuizActivity.this, "Please select the CORRECT Option!!", Toast.LENGTH_LONG).show();
		return false;
	}

	protected void clearForm() {
		editTextQuestion.setText("");
		editTextNotes.setText("");
		for(int i=0;i<4;i++) {
			editTextOpt[i].setText("");
			checkBox[i].setChecked(false);
		}
		atQues=-1;
	}

	@SuppressWarnings("deprecation")
	private void initializeViews() {
		editTextOpt = new EditText[4];
		checkBox = new CheckBox[4];

		editTextQuestion = (EditText) findViewById(R.id.editTextQues);
		editTextOpt[0] = (EditText) findViewById(R.id.editTextOpt1);
		editTextOpt[1] = (EditText) findViewById(R.id.editTextOpt2);
		editTextOpt[2] = (EditText) findViewById(R.id.editTextOpt3);
		editTextOpt[3] = (EditText) findViewById(R.id.editTextOpt4);
		editTextNotes = (EditText) findViewById(R.id.editTextNotes);
		checkBox[0] = (CheckBox) findViewById(R.id.checkBox1);
		checkBox[1] = (CheckBox) findViewById(R.id.checkBox2);
		checkBox[2] = (CheckBox) findViewById(R.id.checkBox3);
		checkBox[3] = (CheckBox) findViewById(R.id.checkBox4);
		b_add_ques = (Button) findViewById(R.id.button_add_ques);
		b_create_quiz = (Button) findViewById(R.id.button_create_quiz);

		quizData = new QuizData();
		quizData.setMetaData(author, topic, timer, password);

		listViewQuestions = (ListView) findViewById(R.id.listViewQuestions);

		masterContainer = (RelativeLayout) findViewById(R.id.newParentContainer);
		detector = new GestureDetector(new MyGestureListener());

		atQues = -1;
		if(!newQuiz) {
			//////////parse Quiz
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(String.valueOf(quizData.getSerialVersionUID()), quizData);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		quizData = (QuizData) savedInstanceState.getSerializable(String.valueOf(quizData.getSerialVersionUID()));
		refreshQuestionList();
	}

	public static void refreshQuestionList() {
		int c = 0;
		listQuestions.clear();
		ArrayList<String> questions = quizData.getQuestions();
		for (String q:questions) {
			c++;
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("count", String.valueOf(c)+".");
			hashMap.put("ques", q);
			listQuestions.add(hashMap);
		}
		adapter.notifyDataSetChanged();
		atQues=-1;
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
			if (Math.abs(dY)<SWIPE_MAX_OFF_PATH && Math.abs(velocityX)>=SWIPE_THRESHOLD_VELOCITY && Math.abs(dX)>=SWIPE_MIN_DISTANCE ) {
				if (dX>0) {
					if(quizData.getQuestions().size()!=0 || listViewQuestions.getVisibility() == View.VISIBLE)
						if(listViewQuestions.getVisibility() == View.VISIBLE) {
							listViewQuestions.setAnimation(AnimationUtils.loadAnimation(CreateQuizActivity.this, R.anim.to_right));
							listViewQuestions.setVisibility(View.GONE);
						}
						else {
							listViewQuestions.setAnimation(AnimationUtils.loadAnimation(CreateQuizActivity.this, R.anim.from_left));
							listViewQuestions.setVisibility(View.VISIBLE);
						}
					else
						Toast.makeText(CreateQuizActivity.this, "No Questions Added!!", Toast.LENGTH_SHORT).show();
				} 
				else {
					if(quizData.getQuestions().size()!=0 || listViewQuestions.getVisibility() == View.VISIBLE)
						if(listViewQuestions.getVisibility() == View.VISIBLE) {
							listViewQuestions.setAnimation(AnimationUtils.loadAnimation(CreateQuizActivity.this, R.anim.to_left));
							listViewQuestions.setVisibility(View.GONE);
						}
						else {
							listViewQuestions.setAnimation(AnimationUtils.loadAnimation(CreateQuizActivity.this, R.anim.from_right));
							listViewQuestions.setVisibility(View.VISIBLE);
						}
					else
						Toast.makeText(CreateQuizActivity.this, "No Questions Added!!", Toast.LENGTH_SHORT).show();
				}
				return true;
			} 
			return false;
		}
	}

	@Override
	public void onBackPressed() {
		if(listViewQuestions.getVisibility() == View.VISIBLE) {
			listViewQuestions.setAnimation(AnimationUtils.loadAnimation(CreateQuizActivity.this, R.anim.to_right));
			listViewQuestions.setVisibility(View.GONE);
		}
		else
			super.onBackPressed();
	}
}

/**
 * 
 * package com.aakashapp.modguru;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aakashapp.modguru.src.CustomListAdapter;
import com.aakashapp.modguru.src.QuizData;

public class CreateQuizActivity extends Activity {

	EditText editTextQuestion, editTextOpt[], editTextNotes;
	CheckBox checkBox[];
	Button b_add_ques, b_create_quiz;
	public static QuizData quizData;
	ListView listViewQuestions;
	GestureDetector detector;
	RelativeLayout masterContainer;
	static int atQues;
	String file, author, topic, timer, password;
	boolean newQuiz;
	static ArrayList<HashMap<String, String>> listQuestions;
	static CustomListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_quiz);

		Toast.makeText(CreateQuizActivity.this, "Swipe to View the List of Questions Added!!", Toast.LENGTH_LONG).show();

		try {
			file = getIntent().getCharSequenceExtra("file").toString();
			newQuiz = false;
		}catch (Exception e) {
			newQuiz = true;
		}

		author = getIntent().getCharSequenceExtra("author").toString();
		topic = getIntent().getCharSequenceExtra("topic").toString();
		timer = getIntent().getCharSequenceExtra("timer").toString();
		password = getIntent().getCharSequenceExtra("password").toString();

		initializeViews();
		clearForm();

		b_create_quiz.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				AlertDialog.Builder alert = new AlertDialog.Builder(CreateQuizActivity.this);
				alert.setTitle("Save Quiz");

				if(isQuestionDataValid()) {
					addQuestion();
					CreateQuizActivity.this.setResult(Activity.RESULT_OK);
					CreateQuizActivity.this.finish();
				}
				else if(editTextQuestion.getText().toString().equals("")) {
					if (quizData.getQuestions().size() == 0) {
						alert.setMessage("You haven't added any Question! Quiz won't be saved! Are you sure you want to Exit?");
						alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								CreateQuizActivity.this.setResult(Activity.RESULT_OK);
								CreateQuizActivity.this.finish();
							}
						});
						alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						});
					}
					else {
						try {
							String file = String.valueOf(System.currentTimeMillis());
							quizData.writeToXML(file);
						} catch (Exception e) {
							Log.e("WriteFail", e.getMessage());
						}
					}
				}
				else {

				}

			}
		});
		b_add_ques.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addQuestion();
			}
		});
		listViewQuestions.setItemsCanFocus(true);
		listViewQuestions.setFocusable(false);
		listViewQuestions.setFocusableInTouchMode(false);
		listViewQuestions.setClickable(false);
		listViewQuestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
					long arg3) {
				atQues = arg2;
				setView();
				listViewQuestions.setAnimation(AnimationUtils.loadAnimation(CreateQuizActivity.this, R.anim.to_right));
				listViewQuestions.setVisibility(View.GONE);
			}
		});
		listQuestions = new ArrayList<HashMap<String,String>>();
		adapter = new CustomListAdapter(CreateQuizActivity.this, listQuestions, R.layout.ques_list_layout, new String[]{"count", "ques"}, new int[]{R.id.textViewQuesNo, R.id.textViewListQuestion});
		listViewQuestions.setAdapter(adapter);
		listViewQuestions.refreshDrawableState();

		masterContainer.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				detector.onTouchEvent(event);
				return true;
			}
		});
	}

	protected void addQuestion() {
		if(atQues==-1 && isQuestionDataValid()) {
			String copt = "";
			String opts[] = new String[4];
			quizData.addQuestion(editTextQuestion.getText().toString());
			for (int i=0; i<4; i++) {
				opts[i] = editTextOpt[i].getText().toString();
				if(checkBox[i].isChecked())
					copt += i;
			}
			quizData.addOptions(opts);
			quizData.setCorrectOpt(copt);
			quizData.addNote(editTextNotes.getText().toString());
			clearForm();
			refreshQuestionList();
			Toast.makeText(CreateQuizActivity.this, "Question Added!!", Toast.LENGTH_LONG).show();
			editTextQuestion.requestFocus();
		}
		if(atQues!=-1 && isQuestionDataValid()) {
			quizData.deleteQuestion(atQues);
			String copt = "";
			String opts[] = new String[4];
			quizData.addQuestion(editTextQuestion.getText().toString(),atQues);
			for (int i=0; i<4; i++) {
				opts[i] = editTextOpt[i].getText().toString();
				if(checkBox[i].isChecked())
					copt += i;
			}
			quizData.addOptions(opts,atQues);
			quizData.setCorrectOpt(copt,atQues);
			quizData.addNote(editTextNotes.getText().toString(),atQues);
			clearForm();
			refreshQuestionList();
			Toast.makeText(CreateQuizActivity.this, "Question Updated!!", Toast.LENGTH_LONG).show();
			editTextQuestion.requestFocus();
		}
	}

	protected void setView() {
		editTextQuestion.setText(quizData.getQuestions().get(atQues));
		String[] options = quizData.getOptions(atQues);
		String correctOpt = quizData.getCorrectOpts(atQues);
		for(int i=0;i<4;i++) {
			editTextOpt[i].setText(options[i]);
			if(correctOpt.contains(String.valueOf(i)))
				checkBox[i].setChecked(true);
			else
				checkBox[i].setChecked(false);
		}
		editTextNotes.setText(quizData.getNote(atQues));

	}

	protected boolean isQuestionDataValid() {
		String string = editTextQuestion.getText().toString();
		if(string.equals("")) {
			editTextQuestion.requestFocus();
			Toast.makeText(CreateQuizActivity.this, "Question can't be left Empty!!", Toast.LENGTH_LONG).show();
			return false;
		}
		for (int i=0;i<4;i++) {
			string = editTextOpt[i].getText().toString();
			if(string.equals("")) {
				editTextOpt[i].requestFocus();
				Toast.makeText(CreateQuizActivity.this, "Field can't be left Empty!!", Toast.LENGTH_LONG).show();
				return false;
			}
		}
		for(int i=0;i<4;i++)
			if(checkBox[i].isChecked())
				return true;
		Toast.makeText(CreateQuizActivity.this, "Please select the CORRECT Option!!", Toast.LENGTH_LONG).show();
		return false;
	}

	protected void clearForm() {
		editTextQuestion.setText("");
		editTextNotes.setText("");
		for(int i=0;i<4;i++) {
			editTextOpt[i].setText("");
			checkBox[i].setChecked(false);
		}
		atQues=-1;
	}

	@SuppressWarnings("deprecation")
	private void initializeViews() {
		editTextOpt = new EditText[4];
		checkBox = new CheckBox[4];

		editTextQuestion = (EditText) findViewById(R.id.editTextQues);
		editTextOpt[0] = (EditText) findViewById(R.id.editTextOpt1);
		editTextOpt[1] = (EditText) findViewById(R.id.editTextOpt2);
		editTextOpt[2] = (EditText) findViewById(R.id.editTextOpt3);
		editTextOpt[3] = (EditText) findViewById(R.id.editTextOpt4);
		editTextNotes = (EditText) findViewById(R.id.editTextNotes);
		checkBox[0] = (CheckBox) findViewById(R.id.checkBox1);
		checkBox[1] = (CheckBox) findViewById(R.id.checkBox2);
		checkBox[2] = (CheckBox) findViewById(R.id.checkBox3);
		checkBox[3] = (CheckBox) findViewById(R.id.checkBox4);
		b_add_ques = (Button) findViewById(R.id.button_add_ques);
		b_create_quiz = (Button) findViewById(R.id.button_create_quiz);

		quizData = new QuizData();
		quizData.setMetaData(author, topic, timer, password);

		listViewQuestions = (ListView) findViewById(R.id.listViewQuestions);

		masterContainer = (RelativeLayout) findViewById(R.id.newParentContainer);
		detector = new GestureDetector(new MyGestureListener());

		atQues = -1;
		if(!newQuiz) {
			//////////parse Quiz
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(String.valueOf(quizData.getSerialVersionUID()), quizData);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		quizData = (QuizData) savedInstanceState.getSerializable(String.valueOf(quizData.getSerialVersionUID()));
		refreshQuestionList();
	}

	public static void refreshQuestionList() {
		int c = 0;
		listQuestions.clear();
		ArrayList<String> questions = quizData.getQuestions();
		for (String q:questions) {
			c++;
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("count", String.valueOf(c)+".");
			hashMap.put("ques", q);
			listQuestions.add(hashMap);
		}
		adapter.notifyDataSetChanged();
		atQues=-1;
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
			if (Math.abs(dY)<SWIPE_MAX_OFF_PATH && Math.abs(velocityX)>=SWIPE_THRESHOLD_VELOCITY && Math.abs(dX)>=SWIPE_MIN_DISTANCE ) {
				if (dX>0) {
					if(quizData.getQuestions().size()!=0 || listViewQuestions.getVisibility() == View.VISIBLE)
						if(listViewQuestions.getVisibility() == View.VISIBLE) {
							listViewQuestions.setAnimation(AnimationUtils.loadAnimation(CreateQuizActivity.this, R.anim.to_right));
							listViewQuestions.setVisibility(View.GONE);
						}
						else {
							listViewQuestions.setAnimation(AnimationUtils.loadAnimation(CreateQuizActivity.this, R.anim.from_left));
							listViewQuestions.setVisibility(View.VISIBLE);
						}
					else
						Toast.makeText(CreateQuizActivity.this, "No Questions Added!!", Toast.LENGTH_SHORT).show();
				} 
				else {
					if(quizData.getQuestions().size()!=0 || listViewQuestions.getVisibility() == View.VISIBLE)
						if(listViewQuestions.getVisibility() == View.VISIBLE) {
							listViewQuestions.setAnimation(AnimationUtils.loadAnimation(CreateQuizActivity.this, R.anim.to_left));
							listViewQuestions.setVisibility(View.GONE);
						}
						else {
							listViewQuestions.setAnimation(AnimationUtils.loadAnimation(CreateQuizActivity.this, R.anim.from_right));
							listViewQuestions.setVisibility(View.VISIBLE);
						}
					else
						Toast.makeText(CreateQuizActivity.this, "No Questions Added!!", Toast.LENGTH_SHORT).show();
				}
				return true;
			} 
			return false;
		}
	}

	@Override
	public void onBackPressed() {
		if(listViewQuestions.getVisibility() == View.VISIBLE) {
			listViewQuestions.setAnimation(AnimationUtils.loadAnimation(CreateQuizActivity.this, R.anim.to_right));
			listViewQuestions.setVisibility(View.GONE);
		}
		else
			super.onBackPressed();
	}
}

 * 
 */
