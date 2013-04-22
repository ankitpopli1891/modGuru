package com.aakashapp.modguru;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class Main extends Activity {

	public static String PACKAGE_NAME;
	Button startQuiz, createQuiz;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		PACKAGE_NAME = getApplicationContext().getPackageName();

		startQuiz = (Button) findViewById(R.id.buttonStartQuiz);
		startQuiz.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Main.this,SelectQuizActivity.class));
			}
		});

		createQuiz = (Button) findViewById(R.id.buttonCreateNewQuiz);
		createQuiz.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Main.this,QuizMetaDataActivity.class));
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		createQuiz.setAnimation(AnimationUtils.loadAnimation(Main.this, R.anim.from_left));
		startQuiz.setAnimation(AnimationUtils.loadAnimation(Main.this, R.anim.from_right));			
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemHelp:
			startActivity(new Intent(Main.this,HelpActivity.class));
			break;
		case R.id.itemSettings:
			startActivity(new Intent(Main.this,ApplicationPreferences.class));
			break;
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}
