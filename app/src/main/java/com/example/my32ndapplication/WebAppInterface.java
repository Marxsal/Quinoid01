package com.example.my32ndapplication;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.io.FileOutputStream;

public class  WebAppInterface {
    Context mContext ;
    TwFile mTwFile ;
    public static final String LOG_TAG = "32XND-WebAppInterface";

    WebAppInterface(Context c, TwFile twFile) {
        mContext = c; mTwFile = twFile ;
    }

    @JavascriptInterface
    public void saveFile(String pathname, String text) {
        //Toast.makeText(mContext,pathname,Toast.LENGTH_SHORT).show();
        mTwFile.saveFile(text);
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
}
