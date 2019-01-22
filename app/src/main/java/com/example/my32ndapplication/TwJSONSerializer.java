package com.example.my32ndapplication;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    public ArrayList<TwFile> loadTwFilesFromJSON() throws IOException, JSONException {
        ArrayList<TwFile> twFiles = new ArrayList<TwFile>() ;
        BufferedReader reader = null ;
        try {
            InputStream in = mContext.openFileInput(mFilename) ;
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String jsonString = null ;
            while ((jsonString = reader.readLine()) !=null ) {
                sb.append(jsonString);
            }
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue() ;
            for(int i=0;i<array.length();i++) {
                twFiles.add(new TwFile(array.getJSONObject(i))) ;
            }

        } catch (FileNotFoundException e) {
            // This one will always happen when set up is fresh
        } finally {
            if (reader != null) reader.close();
        }

        return twFiles ;
    }
}
