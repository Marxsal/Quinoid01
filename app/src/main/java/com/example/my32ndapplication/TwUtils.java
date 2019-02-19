package com.example.my32ndapplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class TwUtils {

    static TwUtils sTwUtils;
    static Context mAppContext ;
    static final String LOG_TAG = "32XND.TwUtils:";

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

    public String getTWExternalStoragePublicDirPathname() {
        String publicPathStr;
        try {
            publicPathStr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).
                    getCanonicalPath();
        } catch (IOException e) {
            return null ;
        }
        return publicPathStr+ "/TW-Files/" ;

    }

    public String makeRandomizedFileName(String filename, String suffix) {
        String formattedDate = new SimpleDateFormat("yyyyMMdd-HH-mm-ss").format(new Date());
        return filename + "-" + formattedDate + suffix ;
    }




    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String publicPathStr = getTWExternalStoragePublicDirPathname();

        Log.d(LOG_TAG, "I am testing path: " + publicPathStr);

        if (publicPathStr == null) return false ;
        File publicFilePath = new File(publicPathStr);
        //File file = new File(, "TW-Files/empty1.html");
        //    if (Environment.MEDIA_MOUNTED.equals(state)) {
        //     return true;
        // }
        publicFilePath.mkdirs() ;
        if (publicFilePath.exists() ) return true ;
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
