package com.example.my32ndapplication;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.FileOutputStream;

public class  WebAppInterface {
    Context mContext ;
    TwFile mTwFile ;
    WebView mWebView ;
    public static final String LOG_TAG = "32XND-WebAppInterface";

    WebAppInterface(Context c, TwFile twFile, WebView webView) {
        mContext = c; mTwFile = twFile ; mWebView = webView ;
    }

    @JavascriptInterface
    public void saveFile(String pathname, String text) {
        //Toast.makeText(mContext,pathname,Toast.LENGTH_SHORT).show();
        mTwFile.saveFile(text,mContext);
      /*  try {
            //Log.e(LOG_TAG, dir + "/" + filename);
            FileOutputStream outputStream = new FileOutputStream(pathname);
            outputStream.write(text.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.d(LOG_TAG, "WAI: error" + e.getMessage());
            e.printStackTrace();
        }*/
    }

    @JavascriptInterface
    public void reload() {
        Log.d(LOG_TAG, "reload: being invoked");
        try {
           // String temp = "file:///"+mTwFile.getUnschemedFilePath() ;
            //Log.d(LOG_TAG, "About to load file " + temp);
            Log.d(LOG_TAG, "About to reload file.");
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.reload();
                }
            }) ;
                        //mWebView.loadUrl(temp);
            //mWebView.reload();
        } catch(Exception e) { Log.d(LOG_TAG,"Exception while trying to reload: " + e.getMessage());
        e.printStackTrace();
        }
    }
}
