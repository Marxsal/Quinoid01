package com.example.my32ndapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
//import android.widget.ListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class TwActivity extends AppCompatActivity {
    public static final String LOG_TAG = "32XND";
    public static final String LAUNCH_PAGE = "PagerView Launch Page Position" ;
    final int REQUEST_FILE_OPEN = 2 ;
    private ArrayList<TwFile> mTwFiles ;
    public static final String EXTRA_MESSAGE = "com.example.my32ndapplication.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TwManager.get(this).loadTwFilesFromPreferences();
        mTwFiles = TwManager.get(this).getTwFiles() ;

        ListView listView = findViewById(R.id.listview) ;
        ArrayAdapter<TwFile> adapter =
                new ArrayAdapter<TwFile>(this, android.R.layout.simple_list_item_1, mTwFiles);

        listView.setAdapter(adapter);

        // Create a message handling object as an anonymous class.
        AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                TwFile twFile =  mTwFiles.get(0) ;
                Intent intent = new Intent(TwActivity.this, TwPagerActivity.class) ;
                //intent.putExtra(TwFragment.TW_FILE_NAME, twFile.getTitle());
                intent.putExtra(LAUNCH_PAGE,position) ;
                startActivity(intent);
                // Do something in response to the click
            }
        };
       listView.setOnItemClickListener(mMessageClickedHandler);

       // Handle long presses
        AdapterView.OnItemLongClickListener mItemLongClickListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                TwManager.get(TwActivity.this).deleteTwFile(i);
                ListView listView = findViewById(R.id.listview) ;
                ((ArrayAdapter<TwFile>) listView.getAdapter()).notifyDataSetChanged();
                return true;
            }
        } ;
       listView.setOnItemLongClickListener(mItemLongClickListener);


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void selectFile(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        //intent.setType("text/html");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Only the system receives the ACTION_OPEN_DOCUMENT, so no need to test.
        startActivityForResult(intent, REQUEST_FILE_OPEN);
        //selectFile();
    }

        @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FILE_OPEN && resultCode == RESULT_OK) {
            Uri uriFile = data.getData();
            Toast.makeText(this,uriFile.toString(),Toast.LENGTH_SHORT).show();

            //EditText editText = findViewById(R.id.editText) ;
            //editText.setText(uriFile.toString());

            TwManager.get(this).addTwFile(new TwFile(uriFile.toString()));
            // Launch something 2019-01-10 We will need this code in the
            // listview listener, maybe

            ListView listView = findViewById(R.id.listview) ;
            ((ArrayAdapter<TwFile>) listView.getAdapter()).notifyDataSetChanged();



            /* THIS IS CODE WE USE TO LAUNCH FRAGMENT WITH WEBVIEW -- IT WORKS.
            Intent intent = new Intent(TwActivity.this, TwFragmentActivity.class) ;
            intent.putExtra(TwFragment.TW_FILE_NAME, uriFile.toString());
            startActivity(intent);
            /*

            /*WebView webView = (WebView) findViewById(R.id.webview);
            webView.addJavascriptInterface(new WebAppInterface(this),"twi");
            WebSettings webSettings = webView.getSettings() ;
            webSettings.setJavaScriptEnabled(true);
            webView.loadUrl(uriFile.toString());*/
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        TwManager.get(this).saveTwFilesToPreferences();
    }

    public String getPublicAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), albumName);
        if (!file.mkdirs()) {
            Log.d(LOG_TAG, "Directory not created");
        }

        return file.getPath() ;
    }


    public void sendLaunchMessage(View view) {
        // TODO: Make this either launch from list press, or do a check that mTwFiles is populated.
       /* TwFile twFile =  mTwFiles.get(0) ;
        Intent intent = new Intent(TwActivity.this, TwPagerActivity.class) ;
        intent.putExtra(TwFragment.TW_FILE_NAME, twFile.getTitle());
        startActivity(intent);*/
    }
}
