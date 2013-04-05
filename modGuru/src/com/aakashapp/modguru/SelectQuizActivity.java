package com.aakashapp.modguru;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.aakashapp.modguru.src.Parser;
import com.aakashapp.modguru.src.Quiz;

public class SelectQuizActivity extends Activity {

	ArrayList<HashMap<String, String>> listView;
	//ArrayList<String> author, topic;
	String file, password, timeLimit, author, topic;
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
				listView.add(listValues);
			}
		}
		else {
			Toast.makeText(SelectQuizActivity.this, "No Quiz Found!!", Toast.LENGTH_LONG).show();
			SelectQuizActivity.this.finish();
		}

		listViewQuizList = (ListView) findViewById(R.id.listViewQuiz);
		listViewQuizList.setFastScrollEnabled(true);
		adapter = new SimpleAdapter(this, listView, R.layout.list_layout, new String[]{ "topic", "author", "score", "time", "file", "date", "password"}, new int[]{R.id.textViewTopic, R.id.textViewTopicCreator, R.id.textViewScore,R.id.textViewTimeLimit, R.id.textViewFileName, R.id.textViewDate, R.id.textViewPassword});
		listViewQuizList.setAdapter(adapter);
		listViewQuizList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				file = listView.get(arg2).get("file");
				timeLimit = listView.get(arg2).get("time");

				Intent i = new Intent(SelectQuizActivity.this, QuizActivity.class);
				i.putExtra("file", file);
				i.putExtra("time", timeLimit);
				startActivity(i);
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
		author = listView.get((int) menuInfo.id).get("author");
		topic = listView.get((int) menuInfo.id).get("file");

		Intent i;
		switch (item.getItemId()) {

		case R.id.itemStartQuiz:
			i = new Intent(SelectQuizActivity.this, QuizActivity.class);
			i.putExtra("file", file);
			i.putExtra("time", timeLimit);
			startActivity(i);
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
					i.putExtra("timer", timeLimit);
					i.putExtra("topic", topic);
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

	private boolean isPasswordCorrect(String password) {
		if(password.equals(password))
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.quiz_list_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemImportFile:		
			Toast.makeText(SelectQuizActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}
