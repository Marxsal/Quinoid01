package com.example.my32ndapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


public class TwFragment extends Fragment {
    public static final String LOG_TAG = "32XND-TwFragment";
    public static String TW_FILE_NAME = "com.markqz.tw-fragment-file-name";

    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR=5;
    //select whether you want to upload multiple files (set 'true' for yes)
    private boolean multiple_files = false;

    public String tw_file_name;
    WebView webView;
    TwFile twFile ;
    String javascriptEvalResult = "";  // A kluge to get around limits of inner classes


    public static TwFragment newInstance(String sFilename) {
        TwFragment fragment = new TwFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TW_FILE_NAME,sFilename);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if(Build.VERSION.SDK_INT >= 21){
            Uri[] results = null;
            //checking if response is positive
            if(resultCode== RESULT_OK){
                Log.d(LOG_TAG, "onAR - I think result OK");
                if(requestCode == FCR){
                    if(null == mUMA){
                        return;
                    }
                    if(intent == null || intent.getData() == null){
                        if(mCM != null){
                            results = new Uri[]{Uri.parse(mCM)};
                        }
                    }else{
                        String dataString = intent.getDataString();
                        if(dataString != null){
                            results = new Uri[]{Uri.parse(dataString)};
                        } else {
                            if(multiple_files) {
                                if (intent.getClipData() != null) {
                                    final int numSelectedFiles = intent.getClipData().getItemCount();
                                    results = new Uri[numSelectedFiles];
                                    for (int i = 0; i < numSelectedFiles; i++) {
                                        results[i] = intent.getClipData().getItemAt(i).getUri();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            mUMA.onReceiveValue(results);
            mUMA = null;
        }else{
            if(requestCode == FCR){
                if(null == mUM) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUM.onReceiveValue(result);
                mUM = null;
            }
        }
    }




    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
       tw_file_name = getArguments().getString(TW_FILE_NAME);
        View v = inflater.inflate(R.layout.tw_fragment, container, false);

        twFile = TwManager.get(getActivity()).getTwFile(tw_file_name) ;
        Log.d(LOG_TAG, "I am using id tw_file_name: " + tw_file_name + " and have found file "
                + twFile.getId() + " which has absolute path " + twFile.getUnschemedFilePath());

        webView = v.findViewById(R.id.webview) ;
        webView.addJavascriptInterface(new WebAppInterface(getActivity(),twFile,webView),"twi" );
        WebSettings webSettings = webView.getSettings() ;


        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        final MyAction titleSetter = new MyAction() {
            @Override
            void doSomethingWithValue(String useme) {
                //Log.d(LOG_TAG, "Inside myaction, seeing message " + useme);
                twFile.setTitle(useme);
            }
        } ;
        final MyAction iconSetter = new MyAction() {
            @Override
            void doSomethingWithValue(String useme) {
                Log.d(LOG_TAG, "Inside myaction iconSetter, seeing message " + useme);

                twFile.makeIconAndPath(getActivity(),useme);
                //twFile.setTitle(useme);
            }
        } ;
        final MyAction nullSetter = new MyAction() {
            @Override
            void doSomethingWithValue(String useme) {
                Log.d(LOG_TAG, "Inside myaction nullSetter, seeing message " + useme);
            }
        } ;



        webView.setWebViewClient(new WebViewClient() {


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith("http")) {
                    Log.d(LOG_TAG, "Attempt to start " + url);

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                return true ;
            }




            @Override
            public void onPageFinished(WebView view, String url) {
                //Log.d(LOG_TAG, "Inside onActivityCreated");
                twFile.setTitle(view.getTitle());
                //Log.d(LOG_TAG, "EXP1: Seeing title: " + view.getTitle());
                //String title = loadJavascript ("$tw.wiki.getTiddlerText('$:/SiteTitle')",titleSetter);
                String icoText = loadJavascript ("$tw.wiki.getTiddlerText('$:/favicon.ico')",iconSetter);
                String message = twFile.getMessage() ;
                if(message != null && !message.isEmpty()) {
                    message = JSONObject.quote(message);
                    String command =
                            "console.log('COMMAND 1');" +
                                    "var draftTiddler = new $tw.Tiddler( " +
                                    "{ title: \"CLIPBOARD\",   text: " + message + "}, " +
                                    "$tw.wiki.getModificationFields()); " +
                                    "$tw.wiki.addTiddler(draftTiddler);";
                    Log.d(LOG_TAG, "Javascript - about to create tiddler with message: " + message);
                    loadJavascript(command, nullSetter);
                }

//twFile.makeIconAndPath(getActivity(),icoText);
                  //view.getFavicon()
                //twFile.saveIconPath(getActivity(),view.getFavicon());
                         }
        } );

        //twFile.loadFilePath(); // Need to do this in case content: hasn't been loaded
        String temp = "file:///"+twFile.getUnschemedFilePath() ; // This is probably what we usually call an absolute path
        Log.d(LOG_TAG, "About to load file " + temp);
        webView.loadUrl(temp);

        webView.setWebChromeClient(new WebChromeClient() {

            //handling input[type="file"] requests for android API 16+
            @SuppressLint("ObsoleteSdkInt")
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                Log.d(LOG_TAG, "Inside file chooser method 1");
                mUM = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                if (multiple_files && Build.VERSION.SDK_INT >= 18) {
                    i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FCR);
            }

            //handling input[type="file"] requests for android API 21+
            @SuppressLint("InlinedApi")
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                Log.d(LOG_TAG, "Inside file chooser method 2");
                if (file_permission()) {
                    Log.d(LOG_TAG, "Passed file permissions check");
                    String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                    //        Manifest.permission.CAMERA
                    };

                    //checking for storage permission to write images for upload
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(getActivity(),
                                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                     )
                    { ActivityCompat.requestPermissions(getActivity(), perms, FCR);

                        //checking for WRITE_EXTERNAL_STORAGE permission
                    }
                    else if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, FCR);

                        //checking for CAMERA permissions
                    }
//                    else if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, FCR);
//                    }
                    if (mUMA != null) {
                        mUMA.onReceiveValue(null);
                    }
                    mUMA = filePathCallback;
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                            takePictureIntent.putExtra("PhotoPath", mCM);
                        } catch (IOException ex) {
                            Log.e(LOG_TAG, "Image file creation failed", ex);
                        }
                        if (photoFile != null) {
                            mCM = "file:" + photoFile.getAbsolutePath();
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        } else {
                            takePictureIntent = null;
                        }
                    }
                    Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    contentSelectionIntent.setType("*/*");
                    if (multiple_files) {
                        contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    }
                    Intent[] intentArray;
                    if (takePictureIntent != null) {
                        intentArray = new Intent[]{takePictureIntent};
                    } else {
                        intentArray = new Intent[0];
                    }

                    Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "File Chooser");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                    startActivityForResult(chooserIntent, FCR);
                    return true;
                }else{
                    Log.d(LOG_TAG, "Did NOT Pass file permissions check");
                    return false;
                }
            }
        });




        //loadTWfromUri(webView, tw_file_name);

        return v ;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

   }

    public boolean file_permission(){
        // 190129 Taking out camera permission for now.
        if(Build.VERSION.SDK_INT >=23 &&
                (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                 //       || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                ))
        {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                      //      , Manifest.permission.CAMERA
                    } , 1);
            return false;
        }else{
            return true;
        }
    }

    //creating new image file here
    private File createImageFile() throws IOException{
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_"+timeStamp+"_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName,".jpg",storageDir);
    }





    @Override
    public void onPause() {
        super.onPause();
        TwManager.get(getActivity()).saveTwFilesToJSON();
        //Log.d(LOG_TAG, "onPause - saving to JSON complete.");
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        Log.d(LOG_TAG, "onAttachFragment");
    }
    @Override
    public void onDetach() {
        super.onDetach();
        //Log.d(LOG_TAG, "onDetach");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.d(LOG_TAG, "onDestroy");
    }


    /* UTILITIES */

    public String loadJavascript(String javascript, final MyAction pMyAction) {
        //Log.d(LOG_TAG, "Receiving javascript message: " + javascript);
        javascriptEvalResult = "" ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // In KitKat+ you should use the evaluateJavascript method
            webView.evaluateJavascript(javascript, new ValueCallback<String>() {

                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void onReceiveValue(String s) {
                    //Log.d(LOG_TAG, "Inside ValueCallBack seeing string: " + s);
                    JsonReader reader = new JsonReader(new StringReader(s));

                    // Must set lenient to parse single values
                    reader.setLenient(true);
                    try {
                        if (reader.peek() != JsonToken.NULL) {
                            if (reader.peek() == JsonToken.STRING) {
                                String msg = reader.nextString();
                                //Log.d(LOG_TAG, "I think I am returning value " + msg);
                                if (msg != null) {
                                    pMyAction.doSomethingWithValue(msg);

                                }
                            }
                        }
                    } catch (IOException e) {
                        Log.e("TAG", "MainActivity: IOException", e);
                    } finally {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            // NOOP
                        }
                    }
                }
            });
        } else {
            /**
             * For pre-KitKat+ you should use loadUrl("javascript:<JS Code Here>");
             * To then call back to Java you would need to use addJavascriptInterface()
             * and have your JS call the interface
             **/
             // Leave open for now. Not planning to support <4.4
            //mWebView.loadUrl("javascript:" + javascript);
        }
    return javascriptEvalResult ;
    }



    /* NOT USED */
    public void loadTWfromUri(WebView wv, String uriString) {
        //GDRIVE & OTHER ANDROID 6+
        Uri uriFile = Uri.parse(uriString);
        String sGdrive = "";
        FileOutputStream outputStream;
        File file = null;
        String fileName = "huh";
        // there should be more try/catch
        String unencodedHtml =
              "&lt;!DOCTYPE&gt;&lt;html&gt;&lt;body&gt;'%23' is the percent code for ‘#‘ &lt;/body&gt;&lt;/html&gt;";
        unencodedHtml = "<!DOCTYPE><html><body>'23' is the percent code for '#'</body></html>" ;
        String encodedHtml = Base64.encodeToString(unencodedHtml.getBytes(),                 Base64.NO_PADDING);
        //encodedHtml = "This is some <b>sample</b> text" ;
        StringBuilder html = new StringBuilder();

        try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(uriFile);
            //outputStream = openFileOutput(file.getName(), MODE_PRIVATE);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            byte[] buf = new byte[1024];
            int len, cnt = 0 ;
             String line;
             while((line = br.readLine()) != null) {
                 cnt++;
                 html.append(line);
                 if(cnt == 5) Log.d(LOG_TAG, html.toString());
             }

            /* while ((len = inputStream.read(buf)) > 0) {
                cnt++ ;
                html.append(buf);
                if(cnt < 3) {Log.d(LOG_TAG,buf.toString())}
            }*/
            Log.d(LOG_TAG, "End of html: " + line);
            //inputStream.close();
            br.close();
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
        }

        //wv.loadData(Base64.encodeToString(html.toString().getBytes()), "text/html", "base64");
        //wv.loadData(html.toString(), "text/html", "UTF-8");
        //wv.loadData(html.toString() , "text/html; charset=utf-8", "UTF-8");
        encodedHtml = Base64.encodeToString(html.toString().getBytes(), Base64.NO_PADDING);
        //wv.setLayerType(WebView.LAYER_TYPE_NONE, null);
        //wv.loadData(encodedHtml, "text/html", "base64");
        wv.getSettings().setDefaultTextEncodingName("utf-8");
        wv.getSettings().setUserAgentString(Locale.getDefault().getLanguage());
        wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        wv.getSettings().setDomStorageEnabled(true);
        String root = "file://"+getActivity().getFilesDir().getAbsolutePath();
        Log.d(LOG_TAG, "root: " + root);
        //wv.setWebViewClient(new MyWebViewClient());

        wv.loadDataWithBaseURL(root, html.toString(), "text/html", "UTF-8", root);
        //wv.loadDataWithBaseURL("", encodedHtml, "text/html", "base64", "");
    } // LOAD TW FROM URI

    /* NOT USED ?? */
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
             // Let's try overriding everything
            Log.d(LOG_TAG, "Attempt to use resource " + url);
            if(url.startsWith("http")) {
                Log.d(LOG_TAG, "Attempt to start " + url);

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
            return true;
        }

        @TargetApi(21)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            //Log.d(LOG_TAG,request.getUrl().toString()) ;
            return null ;
        }
    }





}

abstract class MyAction{
    void doSomethingWithValue(String useme) {

    }
        }