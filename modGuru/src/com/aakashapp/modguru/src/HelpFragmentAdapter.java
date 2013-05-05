package com.aakashapp.modguru.src;

import com.aakashapp.modguru.R;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HelpFragmentAdapter extends FragmentPagerAdapter {
    protected static final int[] CONTENT = new int[] { R.drawable.help1, R.drawable.help2, R.drawable.help3, R.drawable.help4, R.drawable.help5, R.drawable.help6, R.drawable.help7, R.drawable.help8};

    private int mCount = CONTENT.length;

    public HelpFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return HelpFragment.newInstance(CONTENT[position % CONTENT.length]);
    }

    @Override
    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }
}