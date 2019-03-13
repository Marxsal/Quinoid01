package com.example.my32ndapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;

public class TwResourceActivity extends AppCompatActivity
implements  TwResourceFragment.OnListFragmentInteractionListener {
    public static final String LOG_TAG = "32XND-TwRes..Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw_resource_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, TwResourceFragment.newInstance(0))
                    .commitNow();
        }
    }

    @Override
    public void onListFragmentInteraction(TwResource twResource) {
        String fileName = TwUtils.get(this).makeRandomizedFileName(twResource.getFileStem(), ".html");
        File dirPath = TwUtils.get(this).getTWDocumentPath(TwActivity.TW_SUBDIR);

        long downloadReference = TwUtils.get(this).DownloadTw(twResource.getId(), dirPath, fileName, twResource.getTitle());
TwUtils.get(this).makeToast("Downloading: " + twResource.getTitle());
               // DownloadTw("https://tiddlywiki.com/empty", dirPath, fileName ,"Basic - empty TW") ;


        Log.d(LOG_TAG, twResource.getTitle());

    }
}
