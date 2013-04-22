package com.aakashapp.modguru;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
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
import com.aakashapp.modguru.src.Option;
import com.aakashapp.modguru.src.Parser;
import com.aakashapp.modguru.src.Question;
import com.aakashapp.modguru.src.Quiz;
import com.aakashapp.modguru.src.QuizData;

public class CreateQuizActivity extends Activity {

	EditText editTextQuestion, editTextOpt[], editTextNotes;
	CheckBox checkBox[];
	Button b_add_ques, b_create_quiz, b_clear_fields, b_discard_quiz;
	public QuizData quizData;
	ListView listViewQuestions;
	GestureDetector detector;
	RelativeLayout masterContainer;
	int atQues;
	String file, author, topic, timer, password;
	boolean newQuiz;
	ArrayList<HashMap<String, String>> listQuestions;
	CustomListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_quiz);

		Toast.makeText(CreateQuizActivity.this, "Swipe to View the List of Questions Added!!", Toast.LENGTH_LONG).show();

		try {
			file = getIntent().getCharSequenceExtra("file").toString();
			newQuiz = false;
		} catch (Exception e) {
			newQuiz = true;
		}

		author = getIntent().getCharSequenceExtra("author").toString();
		topic = getIntent().getCharSequenceExtra("topic").toString();
		timer = getIntent().getCharSequenceExtra("time").toString();
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
							}
						}
					});
					alert.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (quizData.getQuestions().size() == 0)
								Toast.makeText(CreateQuizActivity.this, "No Questions Added!!\nQuiz can't be Created!!", Toast.LENGTH_LONG).show();
							writeQuizFile();
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
		b_clear_fields.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clearForm();
			}
		});
		b_discard_quiz.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(listQuestions.size()>0) {
					AlertDialog.Builder alert = new AlertDialog.Builder(CreateQuizActivity.this);
					alert.setTitle("Discard Quiz");
					alert.setMessage("Are you sure you want to Discard the Quiz?");
					alert.setPositiveButton("Discard & Exit", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(isQuestionDataValid()) {
								CreateQuizActivity.this.setResult(Activity.RESULT_OK);
								CreateQuizActivity.this.finish();
							}
						}
					});
					alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							/*
							 * Do Nothing
							 */
						}
					});
					alert.show();
				}
				else {
					CreateQuizActivity.this.setResult(Activity.RESULT_OK);
					CreateQuizActivity.this.finish();
				}
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
		adapter = new CustomListAdapter(CreateQuizActivity.this, listQuestions, R.layout.ques_list_layout, new String[]{"count", "ques"}, new int[]{R.id.textViewQuesNo, R.id.textViewListQuestion});
		if(listQuestions.size()>0)
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
			if(newQuiz) {
				quizData.writeToXML(String.valueOf(System.currentTimeMillis()));
				CreateQuizActivity.this.setResult(Activity.RESULT_OK);
				CreateQuizActivity.this.finish();
			}
			else {
				AlertDialog.Builder alert = new AlertDialog.Builder(CreateQuizActivity.this);
				alert.setTitle(topic+" - By "+author);
				alert.setMessage("Do You Want to Create a New Quiz or Modify Existing One?");
				alert.setPositiveButton("Create New", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							quizData.writeToXML(String.valueOf(System.currentTimeMillis()));
							CreateQuizActivity.this.setResult(Activity.RESULT_OK);
							CreateQuizActivity.this.finish();
						} catch (Exception e) {
							Log.e("WriteFail", e.getMessage(), e);
						}
					}
				});
				alert.setNegativeButton("Modify Existing", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							File oldResFolder = new File(Environment.getDataDirectory().getAbsolutePath()+"/data/"+Main.PACKAGE_NAME+"/res/"+file);
							File newResFolder = new File(Environment.getDataDirectory().getAbsolutePath()+"/data/"+Main.PACKAGE_NAME+"/res/"+file+"-"+System.currentTimeMillis());
							if(oldResFolder.isDirectory())
								oldResFolder.renameTo(newResFolder);
							
							quizData.writeToXML(CreateQuizActivity.this.file);
							CreateQuizActivity.this.setResult(Activity.RESULT_OK);
							CreateQuizActivity.this.finish();
						} catch (Exception e) {
							Log.e("WriteFail", e.getMessage(), e);
						}
					}
				});
				alert.show();
			}
		} catch (Exception e) {
			Log.e("WriteFail", e.getMessage(), e);
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
		b_clear_fields = (Button) findViewById(R.id.button_clear_fields);
		b_discard_quiz = (Button) findViewById(R.id.button_discard_quiz);

		quizData = new QuizData();
		quizData.setMetaData(author, topic, timer, password, "");

		listViewQuestions = (ListView) findViewById(R.id.listViewQuestions);
		listQuestions = new ArrayList<HashMap<String,String>>();

		masterContainer = (RelativeLayout) findViewById(R.id.newParentContainer);
		detector = new GestureDetector(new MyGestureListener());

		atQues = -1;
		if(!newQuiz) {
			Parser parser = null;
			try {
				parser = new Parser(new FileInputStream(new File(Environment.getDataDirectory().getAbsolutePath()+"/data/"+Main.PACKAGE_NAME+"/quiz/"+file)));
			} catch (Exception e) {
				Log.e("ParserError", e.getMessage());
			}
			Quiz quiz = null;
			try {
				quiz = parser.extractQuiz();
			} catch (Exception e) {
				Log.e("ParserError", "Can't parse "+ file);
				Toast.makeText(CreateQuizActivity.this, "Can't Open File for Editing!!", Toast.LENGTH_SHORT).show();
				CreateQuizActivity.this.finish();
			} 
			ArrayList<Question> questions = quiz.getQuestions();
			for(Question q:questions) {
				quizData.addQuestion(q.getQuestion());
				Option[] options = q.getOptions();
				String[] opts = new String[4];
				String copt = "";
				for(int i=0;i<4;i++) {
					opts[i] = options[i].getOptionValue();
					if(options[i].isOptionCorrectAnswer())
						copt+= String.valueOf(i);
				}
				quizData.addOptions(opts);
				quizData.setCorrectOpt(copt);
				quizData.addNote(q.getNote());
				refreshQuestionList();
			}
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

	public void refreshQuestionList() {
		int c = 0;
		listQuestions.clear();
		ArrayList<String> questions = quizData.getQuestions();
		for (String q:questions) {
			c++;
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("count", c+".");
			hashMap.put("ques", q);
			listQuestions.add(hashMap);
		}
		adapter = new CustomListAdapter(CreateQuizActivity.this, listQuestions, R.layout.ques_list_layout, new String[]{"count", "ques"}, new int[]{R.id.textViewQuesNo, R.id.textViewListQuestion});
		if(listQuestions.size()>0)
			listViewQuestions.setAdapter(adapter);
		atQues=-1;
		if(listViewQuestions.getVisibility() == View.VISIBLE && listQuestions.size()<1) {
			listViewQuestions.setAnimation(AnimationUtils.loadAnimation(CreateQuizActivity.this, R.anim.to_right));
			listViewQuestions.setVisibility(View.GONE);
		}
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
		else {
			if(listQuestions.size()>0) {
				AlertDialog.Builder alert = new AlertDialog.Builder(CreateQuizActivity.this);
				alert.setTitle(topic+" - By "+author);
				alert.setMessage("The Quiz has not been saved!!");
				alert.setPositiveButton("Save & Exit", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						writeQuizFile();
					}
				});
				alert.setNegativeButton("Discard Quiz", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						CreateQuizActivity.this.setResult(Activity.RESULT_OK);
						CreateQuizActivity.this.finish();
					}
				});
				alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						/*
						 * Do Nothing
						 */
					}
				});
				alert.show();
			}
			else
				super.onBackPressed();	
		}
	}
}