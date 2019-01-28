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
    static String LOG_TAG = "32XND-TwPagerActivity";
    private ViewPager mViewPager;
    private ArrayList<TwFile> mTwBrowsableFiles ;
    @Override
    public void onCreate(Bundle savedlnstanceState) {
        super.onCreate(savedlnstanceState);

        int position = getIntent().getIntExtra(TwActivity.LAUNCH_PAGE,0) ;

        // Fragment Manager Code
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView (mViewPager) ;
        mTwBrowsableFiles = TwManager.get(this).getBrowsableFiles() ;
        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public int getCount() {
                return mTwBrowsableFiles.size();
            }

            @Override
            public TwFragment getItem(int pos) {
                TwFile twFile = mTwBrowsableFiles.get(pos);
                return TwFragment.newInstance(twFile.getId());
            }
        });

        mViewPager.setCurrentItem(position);
    }
}
