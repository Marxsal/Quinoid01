package com.example.my32ndapplication;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
//import android.widget.ListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nononsenseapps.filepicker.FilePickerActivity;
import com.nononsenseapps.filepicker.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwActivity extends AppCompatActivity implements TwDialogFragment.TwDialogFragmentListener {
    public static final String LOG_TAG = "32XND-TwActivity";
    public static final String DIALOG_TAG = "FirstDialogTag";
    public static final String LAUNCH_PAGE = "PagerView Launch Page Position";
    public static final String EXTRA_MESSAGE = "com.example.my32ndapplication.MESSAGE";
    public static final String AUTHORITY = "com.example.my32ndapplication.provider"; // Needed by FileUtils2 and file provider
    public static final String RESOURCE_LIST_FILE = "TwResources.json";

    public static final long REFERENCE_UNAVAILABLE = -1 ;
    public static final String TW_SUBDIR = "TwFiles";
    public static TwUtils sTwUtils ;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 42;
    final int REQUEST_FILE_OPEN = 2;
    final int NNF_FILEPICKER = 3;
    private long downloadReference ;

    private ArrayList<TwFile> mTwFiles;

    private ArrayList<TwResource> mTwResources ; // ONLY DURING TESTING. THIS SHOULD GO INTO THE ASSOCIATED DIALOG CLASS

    private Map<Long, TwFile> mTwDownloads = new HashMap<Long, TwFile>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Code to shutdown app completely -- using "finish" inside of back-button code was unsuccessfull
        // ... or maybe it was fine. Hard to tell
        Intent intent = getIntent();
        if (intent.hasExtra("exit")) finish();

        sTwUtils = TwUtils.get(this);

sTwUtils.copySpecificAssets();

//String resourceFile = (new File(sTwUtils.getTWDocumentPath(TW_SUBDIR),RESOURCE_LIST_FILE)).toString() ;
//        Log.d(LOG_TAG, "I see resource file name: " + resourceFile);
//
//        mTwResources = TwResource.loadTwResourceFromJSON(resourceFile);
//
//        if(mTwResources == null || mTwResources.isEmpty()) {
//
//            Log.d(LOG_TAG, "Resources empty!");
//        }
//        Log.d(LOG_TAG, "Resources retrieved, maybe");
//        TwResource tempResource = mTwResources.get(0);
//        if(tempResource != null)
//        Log.d(LOG_TAG, "Resource title is: " + tempResource.getTitle());


        mTwFiles = TwManager.get(this).getTwFiles();

        // Get action and MIME type
        String intentAction = intent.getAction();
        String intentType = intent.getType();
        //        Uri intentData = intent.getData() ;
        if (Intent.ACTION_SEND.equals(intentAction) && intentType != null) {

            if (intent.getType() != null && intent.getType().equals("text/plain")) {

                Log.d(LOG_TAG, "I see incoming intent !!");

                int position = TwManager.get(this).getClipboardIndex() ;
                TwFile twFile = mTwFiles.get(position);

                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                String message = twFile.getMessage() + "\n" + sharedText;

                Log.d(LOG_TAG, "Setting up return message: " + message);
                twFile.setMessage(message);

                // TODO: TW-SHARE After stashing shared value, SAVE the state of all tw files
                TwManager.get(this).saveTwFilesToJSON() ;
                finish();
            }
        }

        setContentView(R.layout.activity_main);

        registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        //TwManager.get(this).loadTwFilesFromPreferences();





        ListView listView = findViewById(R.id.listview);
//        ArrayAdapter<TwFile> adapter =
//                new ArrayAdapter<TwFile>(this, android.R.layout.simple_list_item_1, mTwFiles);
        TwFileAdapter adapter =
                new TwFileAdapter(mTwFiles);

        listView.setAdapter(adapter);

        // Create a message handling object as an anonymous class.
        AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {

                if (TwManager.get(TwActivity.this).getBrowsableFiles().size() < 1) {
                    sTwUtils.makeToast("There are no files marked for browsing.");
                    return;
                }

                if (TwManager.get(TwActivity.this).anyFileBased()) {
                    if (ContextCompat.checkSelfPermission(TwActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        Log.d(LOG_TAG, "onItemClickListener thinks there are file-type items and NO PERMISSION GRANTED.");
                    }
                }
                Log.d(LOG_TAG, "onItemClickListener - about to request permissions.");

                TwFile twFile = mTwFiles.get(0);
                Intent intent = new Intent(TwActivity.this, TwPagerActivity.class);
                //intent.putExtra(TwFragment.TW_FILE_NAME, twFile.getTitle());
                intent.putExtra(LAUNCH_PAGE, position);
                startActivity(intent);
                // Do something in response to the click

            }
        };
        listView.setOnItemClickListener(mMessageClickedHandler);

        //DONE: 00 Make dialog menu on long press
        // Handle long presses
        AdapterView.OnItemLongClickListener mItemLongClickListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

//                TwManager.get(TwActivity.this).deleteTwFile(i);

                TwDialogFragment dialogFragment = TwDialogFragment.newInstance(i);
                dialogFragment.show(getSupportFragmentManager(), DIALOG_TAG);
                ListView listView = findViewById(R.id.listview);
                ((ArrayAdapter<TwFile>) listView.getAdapter()).notifyDataSetChanged();
                return true;
            }
        };
        listView.setOnItemLongClickListener(mItemLongClickListener);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_WRITE_EXTERNAL_STORAGE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuCompat.setGroupDividerEnabled(menu,true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.launch_sys_explorer:
                launchSystemExplorer();
                return true;
            case R.id.launch_local_explorer :
                launchLocalExplorer();
                return true ;
            case R.id.download_empty :

                //downloadTW();
                Log.d(LOG_TAG, "onOptions... download empty");
                if (!TwUtils.isConnected(this)) {
                    sTwUtils.makeToast("No internet connected.");
                    return true;
                }

                Intent intentResource = new Intent(this, TwResourceActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //intent.putExtra("exit", true);
                startActivity(intentResource);

//                String twFilePath = sTwUtils.getTWInternalStoragePathname("internals") ;
//                if ( twFilePath == null ) {
//                    sTwUtils.makeToast("Not able to obtain storage.");
//                    return true;
//                }

//                String fileName = sTwUtils.makeRandomizedFileName("empty", ".html");
//                File dirPath = sTwUtils.getTWDocumentPath(TW_SUBDIR);
//                downloadReference = DownloadTw("https://tiddlywiki.com/empty", dirPath, fileName ,"Basic - empty TW") ;

                return true ;

            case R.id.menu_exit :

                Intent intent = new Intent(this, TwActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("exit", true);
                startActivity(intent);

            // DONE: Menu Exit option (TW-DOWNLOADS)

            default:

                return super.onOptionsItemSelected(item);
        }
    }




    // DONE: Move code from onBackPressed to exit option (TW-DOWNLOADS #CURRENT)
//    @Override
//    public void onBackPressed() {
//        Intent intent = new Intent(this, TwActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("exit", true);
//        startActivity(intent);
//        //super.onBackPressed();
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(LOG_TAG, "onRequestPermissionResult -- receiving something");
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(LOG_TAG, "onRequestPermissionResult says permission granted.");
                } else {
                    Log.d(LOG_TAG, "onRequestPermissionResult says permission NOT granted.");
                }
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    public void launchLocalExplorer() {
        //DONE: Change method name
        // TODO: Need to update NNF library to filter out non-HTML files
        Intent i = new Intent(this, FilePickerActivity.class);
        // This works if you defined the intent filter
        // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

        // Set these depending on your use case. These are the defaults.
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, true);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

        // Configure initial directory by specifying a String.
        // You could specify a String like "/storage/emulated/0/", but that can
        // dangerous. Always use Android's API calls to get paths to the SD-card or
        // internal memory.
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        startActivityForResult(i, NNF_FILEPICKER);

    }

    public void launchLocalExplorer(View view) {
        launchLocalExplorer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // This code may be obsolete if the file-picker approach works.
        // But may be good backup as various file-pickers come and go
        if (requestCode == REQUEST_FILE_OPEN && resultCode == RESULT_OK) {

            Uri uriFile = data.getData();

            // Indicate we really want to keep these documents
            final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(uriFile, takeFlags);

            //TwManager.get(this).addTwFile(new TwFile(uriFile.getLastPathSegment()));
            TwManager.get(this).addTwFile(new TwFile(this, uriFile.toString()));

            // Launch something 2019-01-10 We will need this code in the
            // listview listener, maybe
            ListView listView = findViewById(R.id.listview);
            ((ArrayAdapter<TwFile>) listView.getAdapter()).notifyDataSetChanged();

        }

        if (requestCode == NNF_FILEPICKER && resultCode == RESULT_OK) {
            // Use the provided utility method to parse the result
            List<Uri> uriFiles = Utils.getSelectedFilesFromResult(data);
            for (Uri uriFile : uriFiles) {
                //File file = Utils.getFileForUri(uriFile);
                String path = uriFile.getPath();
                int rootpos = path.indexOf("/root/");
                if (rootpos != -1) {
                    path = path.substring(6);
                }
                //path = "file:///" + path ;
                TwManager.get(this).addTwFile(new TwFile(this, path));
                // Launch something 2019-01-10 We will need this code in the
                // listview listener, maybe
                ListView listView = findViewById(R.id.listview);
                ((ArrayAdapter<TwFile>) listView.getAdapter()).notifyDataSetChanged();
            }
        }


    }



    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "TA:onPause ");
        //TwManager.get(this).saveTwFilesToPreferences();
        TwManager.get(this).saveTwFilesToJSON();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        TwManager.get(this).saveTwFilesToJSON();
        Log.d(LOG_TAG, "onResume - saving to JSON complete.");
        ListView listView = findViewById(R.id.listview);
        ((ArrayAdapter<TwFile>) listView.getAdapter()).notifyDataSetChanged();
    }

    public String getPublicAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), albumName);
        if (!file.mkdirs()) {
            Log.d(LOG_TAG, "Directory not created");
        }

        return file.getPath();
    }


    public void launchSystemExplorer() { //DONE: Change name
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        //intent.setType("file/*");
        intent.setType("text/*");
        //intent.setType("text/html,text/htm,text.txt,html/tw");
        //intent.setType("*/*");
        // String[] mimetypes = {"text/html", "text/htm", "html/tw"};
        // intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);

        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Only the system receives the ACTION_OPEN_DOCUMENT, so no need to test.
        startActivityForResult(intent, REQUEST_FILE_OPEN);
        //launchLocalExplorer();

       /* TwFile twFile =  mTwFiles.get(0) ;
        Intent intent = new Intent(TwActivity.this, TwPagerActivity.class) ;
        intent.putExtra(TwFragment.TW_FILE_NAME, twFile.getTitle());
        startActivity(intent);*/
    }

    // This version needed for easy button mapping with layout resource
    public void launchSystemExplorer(View view) { //DONE: Change name

        launchSystemExplorer();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // Nothing really to do, yet
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Log.d(LOG_TAG, "TA-onDialogPositiveClick: I see display title");
        notifyListAdapter();
    }

    public void notifyListAdapter() {
        ListView listView = findViewById(R.id.listview);
        ((ArrayAdapter<TwFile>) listView.getAdapter()).notifyDataSetChanged();
    }



    private class TwFileAdapter extends ArrayAdapter<TwFile> {

        public TwFileAdapter(ArrayList<TwFile> twFiles) {
            super(TwActivity.this, 0, twFiles);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item, null);
            }
            TwFile twFile = mTwFiles.get(position);
            TextView titleView = (TextView) convertView.findViewById(R.id.itemtitle);
            titleView.setText(twFile.toString());
            CheckBox checkBoxBrowse = (CheckBox) convertView.findViewById(R.id.checkboxBrowse);
            checkBoxBrowse.setChecked(twFile.isBrowsable());

            CheckBox checkBoxClipboard = (CheckBox) convertView.findViewById(R.id.checkboxClip);
            checkBoxClipboard.setChecked(twFile.isClipboard());

            if (twFile.getIconPath() != null && !twFile.getIconPath().isEmpty()) {
                ImageView imageView = (ImageView) convertView.findViewById(R.id.iconView);
                Bitmap bitmap = BitmapFactory.decodeFile(twFile.getIconPath());
                imageView.setImageBitmap(bitmap);
            }
            return convertView;
        }
    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        public void onReceive(Context ctxt, Intent intent) {


            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);


            Log.d(LOG_TAG, "Broadcast received:" + referenceId);

            TwFile twFile = mTwDownloads.get(new Long(referenceId));
            if(twFile != null ) {
                Log.d(LOG_TAG, "broadcastReceiver - Adding twfile to list");
                TwManager.get(TwActivity.this).addTwFile(twFile);
                TwManager.get(TwActivity.this).saveTwFilesToJSON() ;
                ListView listView = findViewById(R.id.listview);
                ((ArrayAdapter<TwFile>) listView.getAdapter()).notifyDataSetChanged();
                mTwDownloads.remove(new Long(referenceId))  ;

            }
            //list.remove(referenceId);


//            if (list.isEmpty())
//            {
//
//            }

        }
    };

}

