package com.aakashapp.modguru;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class QuizMetaDataActivity extends Activity {

	EditText editTextAuthor, editTextTopic, editTextTimer, editTextPassword;
	Button buttonCreateQuiz;

	String file, author, topic, timer, password;
	boolean newQuiz = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz_meta_data);

		try {
			file = getIntent().getCharSequenceExtra("file").toString();
			author = getIntent().getCharSequenceExtra("author").toString();
			topic = getIntent().getCharSequenceExtra("topic").toString();
			timer = getIntent().getCharSequenceExtra("time").toString();
			password = getIntent().getCharSequenceExtra("password").toString();
		}catch (Exception e) {
			newQuiz = true;
		}
		editTextAuthor = (EditText) findViewById(R.id.editTextAuthor);
		editTextTopic = (EditText) findViewById(R.id.editTextTopic);
		editTextTimer = (EditText) findViewById(R.id.editTextTimeLimit);
		editTextPassword = (EditText) findViewById(R.id.editTextPassword);
		buttonCreateQuiz = (Button) findViewById(R.id.buttonCreateQuiz);

		buttonCreateQuiz.setText("Start Creating Quiz");
		buttonCreateQuiz.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(validMetaData()) {
					Intent i = new Intent(QuizMetaDataActivity.this, CreateQuizActivity.class);
					if(!newQuiz)
						i.putExtra("file", file);
					i.putExtra("author", editTextAuthor.getText().toString());
					i.putExtra("topic", editTextTopic.getText().toString());
					i.putExtra("time", editTextTimer.getText().toString());
					i.putExtra("password", editTextPassword.getText().toString());
					startActivityForResult(i, 0);
				}
			}
		});

		if(!newQuiz) {
			editTextAuthor.setText(author);
			editTextPassword.setText(password);
			editTextTimer.setText(timer);
			editTextTopic.setText(topic);
		}
	}

	protected boolean validMetaData() {
		if(editTextAuthor.getText().toString().equals("")) {
			Toast.makeText(QuizMetaDataActivity.this, "Please enter Author Name!!", Toast.LENGTH_LONG).show();
			editTextAuthor.requestFocus();
			return false;
		}
		if(editTextTopic.getText().toString().equals("")) {
			Toast.makeText(QuizMetaDataActivity.this, "Please enter a Topic!!", Toast.LENGTH_LONG).show();
			editTextTopic.requestFocus();
			return false;
		}
		if(editTextTimer.getText().toString().equals("")) {
			Toast.makeText(QuizMetaDataActivity.this, "Please enter a Time Limit!!", Toast.LENGTH_LONG).show();
			editTextTimer.requestFocus();
			return false;
		}
		if(Integer.parseInt(editTextTimer.getText().toString())>180) {
			Toast.makeText(QuizMetaDataActivity.this, "Max 180 Minutes!!", Toast.LENGTH_LONG).show();
			editTextTimer.requestFocus();
			return false;
		}
		if(Integer.parseInt(editTextTimer.getText().toString())<5) {
			Toast.makeText(QuizMetaDataActivity.this, "Min 5 Minutes!!", Toast.LENGTH_LONG).show();
			editTextTimer.requestFocus();
			return false;
		}
		if(editTextPassword.getText().toString().equals("")) {
			Toast.makeText(QuizMetaDataActivity.this, "Please enter a Password!!", Toast.LENGTH_LONG).show();
			editTextPassword.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==0)
			if (resultCode == Activity.RESULT_OK) {
				setResult(RESULT_OK);
				finish();
			}
	}
}
