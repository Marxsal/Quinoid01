package com.example.my32ndapplication;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class TwFile {
    public static final String LOG_TAG = "32XND-TwFile";
    public static final String DEFAULT_TITLE = "(Title unknown)";
    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_IS_CONTENT = "iscontent";
    private static final String JSON_IS_BROWSABLE = "isbrowsable";
    private static final String JSON_DISPLAY_TITLE = "displaytitle";

    private String mTitle = DEFAULT_TITLE;
    private String mId;
    private String mDisplay ="" ;  // User-provided name to display in list
    private boolean mIsContentType;     //    private Context context;
    private boolean mIsBrowsable ; // Will we be loading a page for this item?

    // This is the path to the physical file that we are using in WebView
    // but without the file:/// part because we need that to create streams
    // for saving. WebView requires a path with "file:///" in it.
    private String unschemedFilePath; // Set when loading

    public String getDisplayName() {
        return mDisplay;
    }

    public void setDisplayName(String mDisplay) {
        this.mDisplay = mDisplay;
    }

    public boolean isBrowsable() {
        return mIsBrowsable;
    }

    public void setIsBrowsable(boolean mIsBrowsable) {
        this.mIsBrowsable = mIsBrowsable;
    }

    public boolean isIsContentType() {
        return mIsContentType;
    }




    // Constructor, I hope. Used when resource/filepath is first selected
    // from pick list.
    TwFile(Context context , String resourceString) {

        // ***********************************************
        // Start Beta. These are for development only
        File tempFile = context.getFilesDir();
//        File tempFile = context.getDir("provided", Context.MODE_PRIVATE).getAbsoluteFile()  ;
        StringBuilder sb = new StringBuilder();
        int cnt = 0 ;
        for(File reallyTempFile : tempFile.listFiles() ) {
            if(reallyTempFile.isFile()) sb.append("F "+reallyTempFile.getName()+"\n") ;
            if(reallyTempFile.isDirectory()) sb.append("D "+reallyTempFile.getName()+"\n") ;
            cnt++;
        }
        //Log.d(LOG_TAG,sb.toString() + "There were " + Integer.toString(cnt) + " files in temp directory.") ;

        // End Beta
        // ***********************************************

        //Log.d(LOG_TAG, "Constructor: Seeing resource string: " + resourceString);
        setId(resourceString);
        //setContext(context);
        mIsContentType = false ;
        setTitle(DEFAULT_TITLE);
        setIsBrowsable(true);

        if(mId.startsWith("file")) {
            // setUnschemedFilePath will strip off "file"
            setUnschemedFilePath(mId);
            return;
        }
        if(mId.startsWith("content")) {
            //Log.d(LOG_TAG, "About to call loadFilePath");
            mIsContentType = true ;
            loadFilePath(context) ;
            return ;
        }

        //Log.d(LOG_TAG, "I dont think content starts with 'content', so I'll pretend it's a regular file.");
        setUnschemedFilePath(mId);
    }

    // Constructor #2
    public TwFile(Context c, JSONObject json) throws JSONException {
        mId = json.getString(JSON_ID);
        mTitle = json.getString(JSON_TITLE);
        mIsContentType = json.getBoolean(JSON_IS_CONTENT);
        mDisplay = json.getString(JSON_DISPLAY_TITLE);
        mIsBrowsable = json.getBoolean(JSON_IS_BROWSABLE);
        loadFilePath(c);
        if(!mId.startsWith("content")) setUnschemedFilePath(mId);
    }

    public void loadFilePath(Context c) {
        String newFilePath = "IF YOU CAN READ THIS SOMETHING WENT WRONG.";
        // We only need to sources that are content types. At least so far
        if(!mIsContentType) {return; }

        try {
            //Log.d(LOG_TAG, "Trying to create file name.");
            newFilePath = File.createTempFile("TIDDLYWIKISTUB", ".html",
                   c.getDir("provided", Context.MODE_PRIVATE)).getAbsolutePath() ;
            //Log.d(LOG_TAG, "Created file name: " + newFilePath);
            int cnt = 0 ;  // For DEBUG
            setUnschemedFilePath(newFilePath);
            Uri uriFile = Uri.parse(mId);
            InputStream inputStream = c.getContentResolver().openInputStream(uriFile);
            OutputStream outputStream = new FileOutputStream(newFilePath);

            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                cnt++ ;
                outputStream.write(buf, 0, len);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();
            //Log.d(LOG_TAG, "I counted " + cnt + "k bytes");
        } catch (IOException e) {
            Log.d(LOG_TAG, "IOException: " + e.getMessage());
            e.printStackTrace();

        } catch (Exception e) {
            Log.e(LOG_TAG, "Something not an IOE happened: " + e.getMessage());
        }
        //Log.d(LOG_TAG, "I think I've successfully initialzed working file " + getUnschemedFilePath());

    }

    public void saveFile(String text,Context c) {

        try {
            //Log.d(LOG_TAG, "Attempting to stream to: " + getUnschemedFilePath());
            FileOutputStream outputStream = new FileOutputStream(getUnschemedFilePath());
            outputStream.write(text.getBytes());
            outputStream.close();
        } catch (Exception e) {
            //Log.d(LOG_TAG, "IOE?: See stacktrace." + e.getMessage());
            e.printStackTrace();
        }

        if (mIsContentType) {
            try {
                OutputStream outputStream = c.getContentResolver().openOutputStream(Uri.parse(mId));
                //OutputStream  = new FileOutputStream(newFilePath);
                Writer writer = new OutputStreamWriter(outputStream, "UTF-8");
                writer.write(text);
                writer.close();
            } catch (IOException e) {
                Log.d(LOG_TAG, "saveFile ERR trying to write to original system file.");
            }
        }
    }

    public void setUnschemedFilePath(String filePath) {
        Log.d(LOG_TAG, "719X Before setting file path: " + filePath);
        filePath = filePath.replaceFirst("^file:/+","/") ;
        Log.d(LOG_TAG, "719X After setting file path: " + filePath);
        this.unschemedFilePath = filePath;
    }

    public String getUnschemedFilePath() {
        // Maybe this is same as absolute path. It's the path to the WORKING file which may be a temp file.
         return this.unschemedFilePath;
    }

    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String pTitle) {
       // if(mTitle == null || mTitle.equals(DEFAULT_TITLE)) || mTitle.equals(pTitle){
            //Log.d(LOG_TAG, "Setting mTitle to: " + pTitle);
          this.mTitle = pTitle;
       // }
    }

//    public Context getContext() {
//        return context;
//    }
//
//    public void setContext(Context context) {
//        this.context = context;
//    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        //Log.d(LOG_TAG, "Setting mId to: " + id);
        this.mId = id;
    }



    @Override
    public  String toString() {
        Log.d(LOG_TAG, "toString: I think display is this length: " + getDisplayName().length());
        if (getDisplayName() != null && getDisplayName().length() > 1 ) return getDisplayName() ;
        if( getTitle() != null && !getTitle().startsWith(DEFAULT_TITLE)) return getTitle() ;
        return DEFAULT_TITLE + ": " +
                mId.substring(0,14) + "..." +
                mId.substring(mId.length()-14) ;

    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mId.toString());
        json.put(JSON_TITLE,   mTitle.toString());
        json.put(JSON_IS_CONTENT, mIsContentType);
        json.put(JSON_DISPLAY_TITLE, mDisplay.toString());
        json.put(JSON_IS_BROWSABLE, mIsBrowsable);
        return json;

    }


}
