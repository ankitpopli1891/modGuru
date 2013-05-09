package com.aakashapp.modguru;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
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

import com.aakash.app.wi_net.java.Server;
import com.aakashapp.modguru.src.AcessPoint;
import com.aakashapp.modguru.src.ModifyQuizXML;
import com.aakashapp.modguru.src.Parser;
import com.aakashapp.modguru.src.Quiz;

public class SelectQuizActivity extends Activity {

	ArrayList<HashMap<String, String>> listView;
	String file, password, timeLimit, author, topic, totalQues, score;
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

		File f = new File(Environment.getDataDirectory().getAbsolutePath()+"/data/"+Main.PACKAGE_NAME+"/quiz/");
		if(f.isDirectory() && f.exists()) {
			String[] list = f.list();
			listSize = list.length;

			for(int i=0;i<list.length;i++) {
				try {
					parser = new Parser(new FileInputStream(new File(Environment.getDataDirectory().getAbsolutePath() + "/data/" + Main.PACKAGE_NAME + "/quiz/" + list[i])));
				} catch (Exception e) {
					Log.e("ParserError", e.getMessage(), e);
				}
				Quiz quiz = null;
				try {
					quiz = parser.extractQuiz();
				} catch (Exception e) {
					Log.e("ParserError", "Can't parse "+ list[i], e);
					continue;
				} 
				listValues = new HashMap<String, String>();
				listValues.put("topic", quiz.getTopic());
				listValues.put("author", "By "+quiz.getAuthor());
				listValues.put("score", quiz.getScore());
				listValues.put("time", quiz.getTime());
				listValues.put("date", quiz.getDate());
				listValues.put("password", quiz.getPassword());
				listValues.put("file", list[i]);
				String quesCount = quiz.getQuesCount();
				int totalQues = quiz.getQuestions().size();
				if((quesCount!=null && !(quesCount.equals("") || quesCount.equals(" "))) && totalQues>Integer.parseInt(quesCount))
					listValues.put("totalQues", quesCount);
				else
					listValues.put("totalQues", String.valueOf(totalQues));

				listView.add(listValues);
			}
			if(listView.size()<1) {
				Toast.makeText(SelectQuizActivity.this, "No Quiz Found!!", Toast.LENGTH_LONG).show();
				SelectQuizActivity.this.finish();
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
				score = listView.get(arg2).get("score");
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
		score = listView.get((int) menuInfo.id).get("score");

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
			broadcastQuiz();
			break;

		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	private void broadcastQuiz() {
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
					AlertDialog.Builder alert = new AlertDialog.Builder(SelectQuizActivity.this);
					alert.setTitle("Broadcast Quiz");
					alert.setMessage("You can limit the Quiz to a lesser no. of Questions!!");
					View inflate = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.quiz_details_prompt, null);
					final EditText editTextQuizSize = (EditText) inflate.findViewById(R.id.editTextNewQuizSize);
					final EditText editTextTimeLimit = (EditText) inflate.findViewById(R.id.editTextNewTimeLimit);
					alert.setView(inflate);
					alert.setPositiveButton("Full Quiz", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							try {
								ModifyQuizXML modifyQuizXML = new ModifyQuizXML(Environment.getDataDirectory().getAbsolutePath() +"/data/"+Main.PACKAGE_NAME+"/quiz/" + file);
								modifyQuizXML.setAttribute("quiz", "score", "");
								modifyQuizXML.setAttribute("quiz", "quesCount", "");
								modifyQuizXML.saveQuizFile();
								broadcastQuizFile(file);
							} catch (Exception e) {
								Log.e("Broadcast", e.getMessage(), e);
							}
						}
					});

					alert.setNegativeButton("Limited Quiz", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							try {
								ModifyQuizXML modifyQuizXML = new ModifyQuizXML(Environment.getDataDirectory().getAbsolutePath() +"/data/"+Main.PACKAGE_NAME+"/quiz/" + file);
								modifyQuizXML.setAttribute("quiz", "quesCount",editTextQuizSize.getText().toString());
								modifyQuizXML.setAttribute("quiz", "time",editTextTimeLimit.getText().toString());
								modifyQuizXML.setAttribute("quiz", "score", "");
								modifyQuizXML.saveQuizFile();
								broadcastQuizFile(file);
							} catch (Exception e) {
								Log.e("Broadcast", e.getMessage(), e);
							}
						}
					});
					alert.show();
				}
				else
					Toast.makeText(SelectQuizActivity.this, "Incorrect Password!!", Toast.LENGTH_SHORT).show();
			}
		});

		alert.setNegativeButton("Cancel", null);
		alert.show();
	}

	private void startQuiz() {
		if (score.equals("")) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Start Quiz");
			View view = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_start_quiz_message, null);
			((TextView)view.findViewById(R.id.textViewTotalQues)).setText("No. of Questions: " + totalQues);
			((TextView)view.findViewById(R.id.textViewTimeLimit)).setText("Time Limit: " + timeLimit + " Minutes");
			((TextView)view.findViewById(R.id.textViewInstructions)).setText("Instructions:" +
					"\n1. Once you start the quiz, it will be marked as attempted, whether you complete it or not!" +
					"\n2. When the Time Limit is over, your answers will be automatically submitted!!" +
			"\n3. There's no negative marking.");
			((TextView)view.findViewById(R.id.textViewLine01)).setText("Are you sure you want to Start the Quiz?");
			alert.setView(view);

			alert.setPositiveButton("Yes, Go Ahead!!", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					Intent i = new Intent(SelectQuizActivity.this, QuizActivity.class);
					i.putExtra("file", file);
					i.putExtra("time", timeLimit);
					startActivity(i);
					SelectQuizActivity.this.finish();
				}
			});

			alert.setNegativeButton("No, Go Back!!", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/*
					 * Do Nothing
					 */
				}
			});
			alert.show();
		}
		else {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("View Result");
			View view = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_start_quiz_message, null);
			((TextView)view.findViewById(R.id.textViewTotalQues)).setVisibility(View.GONE);
			((TextView)view.findViewById(R.id.textViewTimeLimit)).setVisibility(View.GONE);
			((TextView)view.findViewById(R.id.textViewInstructions)).setText("You Have already attempted the Quiz!!");
			((TextView)view.findViewById(R.id.textViewLine01)).setText("Do You Want to have a look at Your Previous Result ?");
			alert.setView(view);

			alert.setPositiveButton("Yes, Go Ahead!!", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					Intent i = new Intent(SelectQuizActivity.this, QuizActivity.class);
					i.putExtra("file", file);
					i.putExtra("time", timeLimit);
					startActivity(i);
					SelectQuizActivity.this.finish();
				}
			});

			alert.setNegativeButton("No, Go Back!!", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/*
					 * Do Nothing
					 */
				}
			});
			alert.show();
		}
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
					File f = new File(Environment.getDataDirectory().getAbsolutePath()+"/data/"+Main.PACKAGE_NAME+"/quiz/"+file);
					if(f.exists()) {
						boolean delete = f.delete();
						if(delete) {
							Toast.makeText(SelectQuizActivity.this, "Quiz has been Deleted!!", Toast.LENGTH_SHORT).show();
							startActivity(SelectQuizActivity.this.getIntent());
							SelectQuizActivity.this.finish();
						}
						else
							Toast.makeText(SelectQuizActivity.this, "Insufficient Permissions to perform this Action!!", Toast.LENGTH_SHORT).show();
					}
					else
						Toast.makeText(SelectQuizActivity.this, "File Not Found!!", Toast.LENGTH_SHORT).show();
				}
				else
					Toast.makeText(SelectQuizActivity.this, "Incorrect Password!!", Toast.LENGTH_SHORT).show();
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				/*
				 * Do Nothing
				 */
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
				/*
				 * Do Nothing
				 */
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
				/*
				 * Do Nothing
				 */
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.list_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemRefresh:
			startActivity(SelectQuizActivity.this.getIntent());
			SelectQuizActivity.this.finish();
			break;
		case R.id.itemCreateAP:
			try {
				new AcessPoint(SelectQuizActivity.this).create();
			} catch (Exception e) {
				Log.e("AP", e.getMessage(), e);
			}
			break;
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	AlertDialog.Builder broadcastDialog;
	public static TextView textViewBroadcastDialogMessage;

	private void broadcastQuizFile(String file) {
		broadcastDialog = new AlertDialog.Builder(this);
		broadcastDialog.setTitle("Broadcasting Quiz");
		View inflate = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_broadcast, null);
		textViewBroadcastDialogMessage = ((TextView) inflate.findViewById(R.id.textViewBroadcastDialogMessage));
		textViewBroadcastDialogMessage.setText("Broadcasting Quiz..");
		broadcastDialog.setView(inflate);
		broadcastDialog.show();
		Server.setPath(Environment.getDataDirectory()+"/data/"+Main.PACKAGE_NAME+"/quiz/"+file);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				stopBroadcasting();
			}
		}, 5*60*1000);
	}

	private void stopBroadcasting() {
		Log.e("Broadcast", "Stopping");
		try{
			broadcastDialog.show().dismiss();
			WifiManager wifiManager=(WifiManager) this.getSystemService(Context.WIFI_SERVICE);
			Method[] methods = wifiManager.getClass().getDeclaredMethods();
			for (Method m : methods) 
				if (m.getName().equals("setWifiApEnabled")) 
					m.invoke(wifiManager, null, false);				
		} catch (Exception e) {
			Log.e("Broadcast", e.getMessage(), e);
		}
	}

	@Override
	public void onBackPressed() {
		stopBroadcasting();
		super.onBackPressed();
	}
}
