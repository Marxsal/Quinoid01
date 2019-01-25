package com.example.my32ndapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class TwFragmentActivity extends AppCompatActivity {
     static final public String LOG_TAG = "32XND-TFA" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tw_fragment_activity);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer) ;
        Intent intent = getIntent();
        String sFilename = intent.getStringExtra(TwFragment.TW_FILE_NAME);
        if (fragment == null) {
            fragment = TwFragment.newInstance(sFilename) ;
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, fragment )
                    .commitNow();
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        //Log.d(LOG_TAG, "onAttachFragment");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.d(LOG_TAG, "onDestroy");
    }
}
