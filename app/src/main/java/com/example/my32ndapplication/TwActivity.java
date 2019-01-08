package com.example.my32ndapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;

public class TwActivity extends AppCompatActivity {
    public static final String LOG_TAG = "32XND";
    final int REQUEST_FILE_OPEN = 2 ;
    public static final String EXTRA_MESSAGE = "com.example.my32ndapplication.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



    public void sendMessage(View view) {
        selectFile();
    }

        @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FILE_OPEN && resultCode == RESULT_OK) {
            Uri uriFile = data.getData();
            Toast.makeText(this,uriFile.toString(),Toast.LENGTH_SHORT).show();

            EditText editText = findViewById(R.id.editText) ;
            editText.setText(uriFile.toString());
            Intent intent = new Intent(TwActivity.this, TwFragmentActivity.class) ;
            intent.putExtra(TwFragment.TW_FILE_NAME, uriFile.toString());
            startActivity(intent);

            /*WebView webView = (WebView) findViewById(R.id.webview);
            webView.addJavascriptInterface(new WebAppInterface(this),"twi");
            WebSettings webSettings = webView.getSettings() ;
            webSettings.setJavaScriptEnabled(true);
            webView.loadUrl(uriFile.toString());*/
        }
    }

    public void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Only the system receives the ACTION_OPEN_DOCUMENT, so no need to test.
        startActivityForResult(intent, REQUEST_FILE_OPEN);

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


}
