package com.example.my32ndapplication;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TwResource {
    public static final String LOG_TAG = "32XND-TwResource";
    public static final String DEFAULT_TITLE = "(Unspecified Resource)";
    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_FILESTEM = "filestem";
    private static final String DEFAULT_DESCRIPTION = "No description provided";

    public static  ArrayList<TwResource> mTwResources = new ArrayList<TwResource>();

    static {
        String resourceFile = (new File(TwActivity.sTwUtils.getTWDocumentPath(TwActivity.TW_SUBDIR),TwActivity.RESOURCE_LIST_FILE)).toString() ;
        Log.d(LOG_TAG, "I see resource file name: " + resourceFile);

        mTwResources = TwResource.loadTwResourceFromJSON(resourceFile);

        if(mTwResources == null || mTwResources.isEmpty()) {

            Log.d(LOG_TAG, "Resources empty!");
        }
        Log.d(LOG_TAG, "Resources retrieved, maybe");

//        TwResource tempResource = mTwResources.get(1);
//        if(tempResource != null)
//            Log.d(LOG_TAG, "Resource title is: " + tempResource.getTitle());

    }
    //DONE - SAVE JSON for CLIPBOARD
    //DONE - Make methods in TW Manager to select and set just one TW as clipboard

    private String mTitle = DEFAULT_TITLE;
    private String mId;
    // Need to provide a file stem for the downloader to use, since the URL may not actually be in a good form
    private String mFileStem;

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    private String mDescription ;

    public String getFileStem() {
        return mFileStem;
    }

    public void setFileStem(String mFileName) {
        this.mFileStem = mFileName;
    }



    // This is the path to the physical file that we are using in WebView
    // but without the file:/// part because we need that to create streams
    // for saving. WebView requires a path with "file:///" in it.
    private String unschemedFilePath; // Set when loading


    // Constructor #2
    public TwResource(JSONObject json) throws JSONException {
        mId = json.getString(JSON_ID);
        mTitle = json.getString(JSON_TITLE);
        mDescription = json.getString(JSON_DESCRIPTION) ;
        mFileStem = json.getString(JSON_FILESTEM);
        //loadFilePath(c);
    }

//    public void loadFilePath(Context c) {
//        String newFilePath = "IF YOU CAN READ THIS SOMETHING WENT WRONG.";
//        // We only need to sources that are content types. At least so far
//
//        try {
//            //Log.d(LOG_TAG, "Trying to create file name.");
//            newFilePath = File.createTempFile("TIDDLYWIKISTUB", ".html",
//                   c.getDir("provided", Context.MODE_PRIVATE)).getAbsolutePath() ;
//            //Log.d(LOG_TAG, "Created file name: " + newFilePath);
//            int cnt = 0 ;  // For DEBUG
//            setUnschemedFilePath(newFilePath);
//            Uri uriFile = Uri.parse(mId);
//            InputStream inputStream = c.getContentResolver().openInputStream(uriFile);
//            OutputStream outputStream = new FileOutputStream(newFilePath);
//
//            byte[] buf = new byte[1024];
//            int len;
//            while ((len = inputStream.read(buf)) > 0) {
//                cnt++ ;
//                outputStream.write(buf, 0, len);
//            }
//
//            outputStream.flush();
//            outputStream.close();
//            inputStream.close();
//            //Log.d(LOG_TAG, "I counted " + cnt + "k bytes");
//        } catch (IOException e) {
//            Log.d(LOG_TAG, "IOException: " + e.getMessage());
//            e.printStackTrace();
//
//        } catch (Exception e) {
//            Log.e(LOG_TAG, "Something not an IOE happened: " + e.getMessage());
//        }
//        //Log.d(LOG_TAG, "I think I've successfully initialzed working file " + getUnschemedFilePath());
//
//    }

//    public void saveFile(String text,Context c) {
//
//        try {
//            //Log.d(LOG_TAG, "Attempting to stream to: " + getUnschemedFilePath());
//            FileOutputStream outputStream = new FileOutputStream(getUnschemedFilePath());
//            outputStream.write(text.getBytes());
//            outputStream.close();
//        } catch (Exception e) {
//            //Log.d(LOG_TAG, "IOE?: See stacktrace." + e.getMessage());
//            e.printStackTrace();
//        }
//
//        if (mIsContentType) {
//            try {
//                OutputStream outputStream = c.getContentResolver().openOutputStream(Uri.parse(mId));
//                //OutputStream  = new FileOutputStream(newFilePath);
//                Writer writer = new OutputStreamWriter(outputStream, "UTF-8");
//                writer.write(text);
//                writer.close();
//            } catch (IOException e) {
//                Log.d(LOG_TAG, "saveFile ERR trying to write to original system file.");
//            }
//        }
//    }

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


    public String getId() {
        return mId;
    }

    public void setId(String id) {
        //Log.d(LOG_TAG, "Setting mId to: " + id);
        this.mId = id;
    }




    @Override
    public  String toString() {
        return mId;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mId.toString());
        json.put(JSON_TITLE,   mTitle.toString());
        json.put(JSON_DESCRIPTION, mDescription.toString());
        json.put(JSON_FILESTEM, mFileStem.toString());
        return json;


    }


    public static ArrayList<TwResource> loadTwResourceFromJSON(String pFilename)  {
        ArrayList<TwResource> fileArrayList = new ArrayList<TwResource>() ;
        BufferedReader reader = null ;
        try {
            InputStream in = new FileInputStream(pFilename) ;
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String jsonString = null ;
            while ((jsonString = reader.readLine()) !=null ) {
                sb.append(jsonString);
            }
            Log.d(LOG_TAG, "I see JSON string: " + sb.toString());
            JSONArray array = (JSONArray) new JSONTokener(sb.toString()).nextValue() ;
            for(int i=0;i<array.length();i++) {
                Log.d(LOG_TAG, "Doing JSON array# " + i);
                fileArrayList.add(new TwResource(array.getJSONObject(i))) ;
//                fileArrayList.add(new TwResource( array.getJSONObject(i)) {
//                    @Override
//                    public JSONObject toJSON() throws JSONException {
//                        return null;
//                    }
//                }) ;
            }

        } catch (FileNotFoundException e) {
            // This one will always happen when set up is fresh
            Log.d(LOG_TAG, "loadTwResourceFilesFromJSON - Unfound file failure: " + e.getMessage());
        } catch (IOException e) {

            Log.d(LOG_TAG, "loadTwResourceFilesFromJSON - IO failure: " + e.getMessage());
        } catch (JSONException e) {
                    Log.d(LOG_TAG, "loadTwResourceFilesFromJSON - JSON failure: " + e.getMessage());

        }
        finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {

                }
        }

        return fileArrayList ;
    }


}
