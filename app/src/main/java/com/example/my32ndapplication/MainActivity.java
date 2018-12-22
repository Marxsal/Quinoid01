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

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = "32XND";
    final int REQUEST_FILE_OPEN = 2 ;
    public static final String EXTRA_MESSAGE = "com.example.my32ndapplication.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user taps the send button */
    public void sendMessage2(View view) {
        Intent intent = new Intent(this,DisplayMessageActivity.class) ;
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        //startActivity(intent);
        // Do something in response to button
        FileOutputStream outputStream;
        File file ;
        String mydir = "findme";
        String filename = "myfile.txt";
        String fileContents = "Hello world!";

        StringBuilder text = new StringBuilder();
        File sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        Log.d(LOG_TAG, "sdcard was: " + sdcard.getPath());
        file = new File(sdcard.getPath() + "/findme", "empty.html");
        Log.d(LOG_TAG, "full path trying is: " + file.getName());
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line ;
            while ((line = bufferedReader.readLine()) != null) {
                text.append(line);
            }
        } catch (Exception e) {
            Log.d(LOG_TAG,e.getMessage()) ;
            e.printStackTrace();
        }
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.addJavascriptInterface(new WebAppInterface(this),"twi");
        WebSettings webSettings = webView.getSettings() ;
        webSettings.setJavaScriptEnabled(true);
        String encoded = Base64.encodeToString(text.toString().getBytes(), Base64.NO_PADDING) ;
        //webView.loadData(encoded,"text/html","base64");
        String myfullpath = "file:///"+sdcard.getAbsolutePath()+"/findme/empty.html";
        Log.d(LOG_TAG, "Using url path: " + myfullpath);
        webView.loadUrl(myfullpath);
        Log.d(LOG_TAG, "Length was: " + Integer.toString(text.toString().length()));
        /*
        try {
            String dir = getPublicAlbumStorageDir(mydir);
            Log.e(LOG_TAG, dir + "/" + filename);
            outputStream = new FileOutputStream(dir + "/" + filename);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
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
            WebView webView = (WebView) findViewById(R.id.webview);
            webView.addJavascriptInterface(new WebAppInterface(this),"twi");
            WebSettings webSettings = webView.getSettings() ;
            webSettings.setJavaScriptEnabled(true);
            webView.loadUrl(uriFile.toString());
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
            Log.e(LOG_TAG, "Directory not created");
        }

        return file.getPath() ;
    }


}
