package com.example.my32ndapplication;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;


public class TwFragment extends Fragment {
    public static final String LOG_TAG = "32XND-TwFragment";
    public static String TW_FILE_NAME = "com.markqz.tw-fragment-file-name";
    public String tw_file_name;

    //private TwViewModel mViewModel;

    public static TwFragment newInstance(String sFilename) {
        TwFragment fragment = new TwFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TW_FILE_NAME,sFilename);
        fragment.setArguments(bundle);
        return fragment;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
       tw_file_name = getArguments().getString(TW_FILE_NAME);
        View v = inflater.inflate(R.layout.tw_fragment, container, false);

        TwFile twFile = TwManager.get(getActivity()).getTwFile(tw_file_name) ;
        Log.d(LOG_TAG, "I am using id tw_file_name: " + tw_file_name);

        WebView webView = v.findViewById(R.id.webview) ;
        webView.addJavascriptInterface(new WebAppInterface(getActivity(),twFile),"twi");
        WebSettings webSettings = webView.getSettings() ;

        webSettings.setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        //twFile.loadFilePath(); // Need to do this in case content: hasn't been loaded
        String temp = "file:///"+twFile.getUnschemedFilePath() ; // This is probably what we usually call an absolute path
        Log.d(LOG_TAG, "About to load file " + temp);
        webView.loadUrl(temp);
        //loadTWfromUri(webView, tw_file_name);

        return v ;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mViewModel = ViewModelProviders.of(this).get(TwViewModel.class);
        // TODO: Use the ViewModel
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        Log.d(LOG_TAG, "onAttachFragment");
    }
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(LOG_TAG, "onDetach");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    /* UTILITIES */
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

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
             // Let's try overriding everything
            Log.d(LOG_TAG, "Attempt to use resource " + url);
            return true;
        }

        @TargetApi(21)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            Log.d(LOG_TAG,request.getUrl().toString()) ;
            return null ;
        }
    }


}
