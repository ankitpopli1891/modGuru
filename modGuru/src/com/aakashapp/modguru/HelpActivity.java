package com.aakashapp.modguru;

import com.aakashapp.modguru.src.PageIndicator;
import com.aakashapp.modguru.src.HelpFragmentAdapter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class HelpActivity extends FragmentActivity {

	private HelpFragmentAdapter mAdapter;
	private ViewPager mPager;
	private PageIndicator mIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		mAdapter = new HelpFragmentAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.helpPager);
        mPager.setAdapter(mAdapter);

        mIndicator = (PageIndicator)findViewById(R.id.helpPageIndicator);
        mIndicator.setViewPager(mPager);
	}
}
