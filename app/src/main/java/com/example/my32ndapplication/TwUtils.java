package com.example.my32ndapplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.widget.Toast;

public class TwUtils {

    static TwUtils sTwUtils;
    static Context mAppContext ;

    public static TwUtils get(Context c) {
        if (sTwUtils == null) {
            sTwUtils = new TwUtils(c.getApplicationContext());

        }
        return sTwUtils;
    }

    private TwUtils(Context app) {
        mAppContext = app;
    }


    public void makeToast(String tm) {
        Toast.makeText(mAppContext, tm, Toast.LENGTH_SHORT).show();
    }


    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    static boolean isConnected(Context c) {
        boolean isConnected = false;
        ConnectivityManager cm =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
