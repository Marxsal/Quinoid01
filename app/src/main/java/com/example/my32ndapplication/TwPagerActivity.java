package com.example.my32ndapplication;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class TwPagerActivity extends FragmentActivity {
    private ViewPager mViewPager;
    private ArrayList<TwFile> mTwFiles ;
    @Override
    public void onCreate(Bundle savedlnstanceState) {
        super.onCreate(savedlnstanceState);

        int position = getIntent().getIntExtra(TwActivity.LAUNCH_PAGE,0) ;

        // Fragment Manager Code
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView (mViewPager) ;
        mTwFiles = TwManager.get(this).getTwFiles() ;
        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
        @Override
        public int getCount() {
            return mTwFiles.size();
        }
        @Override
        public TwFragment getItem(int pos) {
            TwFile twFile = mTwFiles.get(pos);
            Log.d(TwActivity.LOG_TAG, "TPA-sees-file: " + twFile.getFilePath());
            return TwFragment.newInstance(twFile.getFilePath());
        }
    });

        mViewPager.setCurrentItem(position);
    }
}
