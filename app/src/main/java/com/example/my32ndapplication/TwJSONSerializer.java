package com.example.my32ndapplication;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

public class TwJSONSerializer {
    private Context mContext;
    private String mFilename ;
    final public String SERIALIZER_DIR = "persist";
    final public String LOG_TAG = "TwJSONSerializer";

    public TwJSONSerializer(Context c, String s) {
        mContext = c ;
        mFilename = s ;
     }

     public void saveTwFiles(ArrayList<TwFile> twFiles) throws JSONException, IOException {
         JSONArray array = new JSONArray() ;
         for (TwFile tw : twFiles) {
             array.put(tw.toJSON()) ;
         }
        // Write to internal storage
        // This first draft doesn't allow specifying directory. Problem?
         Writer writer = null ;
         try {
             OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
             writer = new OutputStreamWriter(out);
             writer.write(array.toString());

         } finally {
             if(writer!= null) writer.close();
         }
    }
}
