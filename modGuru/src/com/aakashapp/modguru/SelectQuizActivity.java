package com.aakashapp.modguru;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.aakashapp.modguru.src.Parser;
import com.aakashapp.modguru.src.Quiz;

public class SelectQuizActivity extends Activity {

	ArrayList<HashMap<String, String>> listView;
	String file, password, timeLimit, author, topic, totalQues;
	int listSize;
	Parser parser;
	ListView listViewQuizList;
	SimpleAdapter adapter;
	HashMap<String, String> listValues;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_quiz);

		listView = new ArrayList<HashMap<String,String>>();

		String s =Environment.getExternalStorageDirectory().getAbsolutePath();
		File f = new File(s+"/CQ/");

		if(f.isDirectory() && f.exists()) {
			String[] list = f.list();
			listSize = list.length;
			
			for(int i=0;i<list.length;i++) {
				try {
					parser = new Parser(new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/CQ/" +list[i])));
				} catch (Exception e) {
					Log.e("ParserError", e.getMessage());
				}
				Quiz quiz = parser.extractQuiz();
				listValues = new HashMap<String, String>();
				listValues.put("topic", quiz.getTopic());
				listValues.put("author", "By "+quiz.getAuthor());
				listValues.put("score", quiz.getScore());
				listValues.put("time", quiz.getTime());
				listValues.put("date", quiz.getDate());
				listValues.put("password", quiz.getPassword());
				listValues.put("file", list[i]);
				listValues.put("totalQues", String.valueOf(quiz.getQuestions().size()));
				listView.add(listValues);
			}
		}
		else {
			Toast.makeText(SelectQuizActivity.this, "No Quiz Found!!", Toast.LENGTH_LONG).show();
			SelectQuizActivity.this.finish();
		}

		listViewQuizList = (ListView) findViewById(R.id.listViewQuiz);
		listViewQuizList.setFastScrollEnabled(true);
		adapter = new SimpleAdapter(this, listView, R.layout.list_layout, new String[]{ "topic", "author", "score", "date"}, new int[]{R.id.textViewTopic, R.id.textViewTopicCreator, R.id.textViewScore, R.id.textViewDate});
		listViewQuizList.setAdapter(adapter);
		listViewQuizList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				file = listView.get(arg2).get("file");
				timeLimit = listView.get(arg2).get("time");
				totalQues = listView.get(arg2).get("totalQues");
				startQuiz();
			}
		});
		registerForContextMenu(listViewQuizList);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.quiz_list_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();

		file = listView.get((int) menuInfo.id).get("file"); 
		timeLimit = listView.get((int) menuInfo.id).get("time");
		password = listView.get((int) menuInfo.id).get("password");
		author = listView.get((int) menuInfo.id).get("author").substring(3);
		topic = listView.get((int) menuInfo.id).get("topic");
		totalQues = listView.get((int) menuInfo.id).get("totalQues");
		
		switch (item.getItemId()) {

		case R.id.itemStartQuiz:
			startQuiz();
			break;

		case R.id.itemResult:
			showResult();
			break;

		case R.id.itemDeleteQuiz:
			deleteQuiz();
			break;

		case R.id.itemEditQuiz:
			editQuiz();
			break;

		case R.id.itemBroadcast:

		default:
			break;
		}
		return super.onContextItemSelected(item);
	}


	private void startQuiz() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Start Quiz");
		
		View view = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_start_quiz_message, null);
		((TextView)view.findViewById(R.id.textViewTotalQues)).setText("No. of Questions: " + totalQues);
		((TextView)view.findViewById(R.id.textViewTimeLimit)).setText("Time Limit: " + timeLimit + " Minutes");
		((TextView)view.findViewById(R.id.textViewInstructions)).setText("Instructions:" +
				"\n1. Once you start the quiz, it will be marked as attempted, whether you complete it or not!" +
				"\n2. When the Time Limit is over, your answers will be automatically submitted!!");
		((TextView)view.findViewById(R.id.textViewLine01)).setText("Are you sure you want to Start the Quiz?");
		alert.setView(view);
		
		alert.setPositiveButton("Yes, Go Ahead!!", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Intent i = new Intent(SelectQuizActivity.this, QuizActivity.class);
				i.putExtra("file", file);
				i.putExtra("time", timeLimit);
				startActivity(i);
			}
		});

		alert.setNegativeButton("No, Go Back!!", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				
			}
		});
		alert.show();
	}

	private void deleteQuiz() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Password");
		alert.setMessage("Authentication Needed for this Action!!");
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
		input.setHint("Password");
		input.setPadding(20, 20, 20, 20);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if(isPasswordCorrect(input.getText().toString())) {
					
					File f = new File(Environment.getExternalStorageDirectory()+"/CQ/"+file);
					if(f.exists()) {
						boolean delete = f.delete();
						if(delete) {
							Toast.makeText(SelectQuizActivity.this, "Quiz has been Deleted!!", Toast.LENGTH_SHORT).show();
							startActivity(SelectQuizActivity.this.getIntent());
						}
						else
							Toast.makeText(SelectQuizActivity.this, "Insufficient Permissions to perform this Action!!", Toast.LENGTH_SHORT).show();
					}
					else
						Toast.makeText(SelectQuizActivity.this, "File Not Found!!\n"+f.getAbsolutePath(), Toast.LENGTH_SHORT).show();
				}
				else
					Toast.makeText(SelectQuizActivity.this, "Incorrect Password!!", Toast.LENGTH_SHORT).show();
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});
		alert.show();
	}

	private void editQuiz() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Password");
		alert.setMessage("Authentication Needed for this Action!!");
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
		input.setHint("Password");
		input.setPadding(20, 20, 20, 20);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if(isPasswordCorrect(input.getText().toString())) {
					Intent i = new Intent(SelectQuizActivity.this, QuizMetaDataActivity.class);
					i.putExtra("file", file);
					i.putExtra("author", author);
					i.putExtra("password", password);
					i.putExtra("time", timeLimit);
					i.putExtra("topic", topic);
					startActivityForResult(i, 0);
				}
				else
					Toast.makeText(SelectQuizActivity.this, "Incorrect Password!!", Toast.LENGTH_SHORT).show();
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});
		alert.show();
	}

	private boolean isPasswordCorrect(String password) {
		if(this.password.equals(password))
			return true;
		else
			return false;
	}

	private void showResult() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Password");
		alert.setMessage("Authentication Needed for this Action!!");
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
		input.setHint("Password");
		input.setPadding(20, 20, 20, 20);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if(isPasswordCorrect(input.getText().toString())) {
					Intent i = new Intent(SelectQuizActivity.this,ResultActivity.class);
					i.putExtra("file", file);
					startActivity(i);
				}
				else
					Toast.makeText(SelectQuizActivity.this, "Incorrect Password!!", Toast.LENGTH_SHORT).show();
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		alert.show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==0)
			if (resultCode == Activity.RESULT_OK)
				finish();
	}
}
