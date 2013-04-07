package com.aakashapp.modguru;

import android.os.Bundle;
import android.webkit.WebView;
import android.app.Activity;

public class WebViewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);
		
		setTitle(getIntent().getCharSequenceExtra("title").toString());
		((WebView) findViewById(R.id.webView)).loadUrl(getIntent().getCharSequenceExtra("url").toString());
	}
}
