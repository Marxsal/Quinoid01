package com.example.my32ndapplication;

import android.content.Context;

import java.util.ArrayList;

public class TwManager {
    private static TwManager sTwManager;
    private Context mAppContext;
    private ArrayList<TwFile> mTwFiles;

    private TwManager(Context app) {
        mAppContext = app;
        mTwFiles = new ArrayList<TwFile>();
    }

    public ArrayList<TwFile> getTwFiles() {
        return mTwFiles;
    }

    public static TwManager get(Context c) {
        if (sTwManager == null) {
            sTwManager = new TwManager(c.getApplicationContext());
        }
        return sTwManager;
    }
}
