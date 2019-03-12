package com.example.my32ndapplication;

import android.app.DownloadManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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


    public File getTWDocumentPath(String subdir) {
        File file ;
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),subdir);

            if (!file.mkdirs()) {
                Log.d(LOG_TAG, "Directory for " + subdir + " not created.");
            }
            Log.d(LOG_TAG, "Returning path: " + file.getAbsolutePath());
        return file ;
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

    boolean fileExistsInDocuments(String pFilename) {
        boolean result = false ;
        File f = new File(pFilename);
        String name = f.getName() ;
        File target = new File(getTWDocumentPath(TwActivity.TW_SUBDIR),name) ;
        if(target.exists() && !target.isDirectory()) {
            Log.d(LOG_TAG, "I think asset file already exists: " + pFilename);
            result = true ;
        }
        return result ;
    }

    // Will do a "safe" copy (no overwrite) to start
    void copySpecificAssets() {

        String[] myAssets = {"TwResources.json"} ;

        Log.d(LOG_TAG, "Asset copier being called");
        AssetManager assetManager = mAppContext.getAssets() ;
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.d(LOG_TAG, "Failed to get asset file list.", e);
        }



        if (files != null) for (String filename : files) {


            if(!Arrays.asList(myAssets).contains(filename)) {
                Log.d(LOG_TAG, "Didn't find " + filename + " in " + myAssets );
                return;
            }

            Log.d(LOG_TAG, "Looking for asset: " + filename);
            InputStream in = null;
            OutputStream out = null;
            if(fileExistsInDocuments(filename)) continue;  // In the future we may want to overwrite existing files.
            //if((new File(filename)).isDirectory()) continue; // For the moment, skip sub-directories
            //   if(!(new File(filename)).exists()) {
            //    Log.d(LOG_TAG, "I think asset doesn't exist??: " + filename);
            //   continue ; } // Some things that get listed don't actually exist ??
            try {
                in = assetManager.open(filename);
                File outFile = new File(getTWDocumentPath(TwActivity.TW_SUBDIR), filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch(IOException e) {
                Log.d(LOG_TAG, "IOE - Failed to copy asset file: " + filename, e);
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
            }
        }
    }
    void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    private  long DownloadTw(String pUrl,File pFilePath, String pFileName ,String pDescription) {

        Uri uri = Uri.parse(pUrl);

        long downloadReference;

        // Downloading to internal instead.
//        // Make isExternalStorageWritable part of the getTWExternalStoragePublicDirPathname logic
//        if (! sTwUtils.isExternalStorageWritable()) {
//            sTwUtils.makeToast("This operation requires external storage.");
//            return 0 ;
//        } else {
//            Log.d(LOG_TAG, "Apparently I think there is external storage.");
//        }


//        String twFilePath = sTwUtils.getTWExternalStoragePublicDirPathname() ;
//        if ( twFilePath == null ) {
//            sTwUtils.makeToast("Not able to obtain external public storage.");
//            return REFERENCE_UNAVAILABLE ;
//        }
//
//        twFilePath = twFilePath + "/" + sTwUtils.makeRandomizedFileName(pFileStem, ".html");


        // Create request for android download manager
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle("Download file"  );

        //Setting description of request
        request.setDescription("Download: " + pDescription);

        //Set the local destination for the downloaded file to a path within the application's external files directory
//        if (v.getId() == R.id.DownloadMusic)
//            request.setDestinationInExternalFilesDir(MainActivity.this, Environment.DIRECTORY_DOWNLOADS, "AndroidTutorialPoint.mp3");
//        else if (v.getId() == R.id.DownloadImage)
//            request.setDestinationInExternalFilesDir(MainActivity.this, Environment.DIRECTORY_DOWNLOADS, "AndroidTutorialPoint.jpg");

        //request.setDestinationInExternalFilesDir(TwActivity.this, Environment.DIRECTORY_DOWNLOADS, "TW-test.html");
        //File file = new File(TwUtils.get(this).getTWExternalStoragePublicDirPathname(), "TW-test.html");
        File file = new File(pFilePath,pFileName);
        //request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "TW-test.html");
        Log.d(LOG_TAG, "I think requesting this file path: " + file.getPath());
        Log.d(LOG_TAG, "I think requesting this file : " + file.getName() );


        request.setDestinationUri(Uri.fromFile(file));
        //Enqueue download and save into referenceId
        downloadReference = downloadManager.enqueue(request);

        TwFile twFile = new TwFile(this, file.getPath());
        twFile.setTitle(pDescription);
        mTwDownloads.put(downloadReference, twFile);


//        Button DownloadStatus = (Button) findViewById(R.id.DoSwnloadStatus);
//        DownloadStatus.setEnabled(true);
//        Button CancelDownload = (Button) findViewById(R.id.CancelDownload);
//        CancelDownload.setEnabled(true);

        return downloadReference;
    }


}