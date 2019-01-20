package com.example.my32ndapplication;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TwManager {
    public static final String LOG_TAG = "32XND";

    private static TwManager sTwManager;
    private Context mAppContext;
    private ArrayList<TwFile> mTwFiles;

    public void loadTwFilesFromPreferences() {

        mTwFiles.clear();
        Set<String> stringDefaults = new HashSet<String>();
        // See if there is a set of TW files to import
        Set<String> stringSet = PreferenceManager.getDefaultSharedPreferences(mAppContext)
                .getStringSet(mAppContext.getString(R.string.preferences_string), stringDefaults);
        int cnt = 0 ;
        for (String s : stringSet) {
            mTwFiles.add(new TwFile(mAppContext,s));
        cnt++ ;
        }
        Log.d(LOG_TAG,"I loaded "+cnt+" files.");
    }

    public void saveTwFilesToPreferences() {

        Set<String> stringSet = new HashSet<String>();
        for (TwFile s : mTwFiles) {
            stringSet.add(s.getId());
        }
        // See if there is a set of TW files to import
        PreferenceManager.getDefaultSharedPreferences(mAppContext).edit().putStringSet(mAppContext.getString(R.string.preferences_string),stringSet).commit() ;

    }


    private TwManager(Context app) {
        mAppContext = app;
        mTwFiles = new ArrayList<TwFile>();
        /*
        for (int i = 0; i < 100; i++) {
            TwFile c = new TwFile();
            c.setTitle("File #" + i);
            mTwFiles.add(c);
        }
        */
    }

    public ArrayList<TwFile> getTwFiles() {
        return mTwFiles;
    }


    public void addTwFile(TwFile c) {
        for(TwFile x : mTwFiles) {
            if(c.getId().equals(x.getId())) {
                Log.d(LOG_TAG, "I think I see file " + c.getId() + " and am exiting.");
                return;}
        }
        Log.d(LOG_TAG, "Adding file " + c.getId());
        mTwFiles.add(c);
        //saveCrimes();
    }

    public TwFile getTwFile(String id) {
        Log.d(LOG_TAG,"Getting passed file: " + id  ) ;

        for(TwFile x : mTwFiles) {
            if(id.equals(x.getId())) {
                Log.d(LOG_TAG, "getTwFile: I think I see file " + id + " and am returning it.");
                return x;}
        }
        return null ;

    }


    public void deleteTwFile(TwFile c) {
        mTwFiles.remove(c);
        //saveCrimes();
    }

    public void deleteTwFile(int i) {
        mTwFiles.remove(i);
    }

    public static TwManager get(Context c) {
        if (sTwManager == null) {
            sTwManager = new TwManager(c.getApplicationContext());
        }
        return sTwManager;
    }
}
