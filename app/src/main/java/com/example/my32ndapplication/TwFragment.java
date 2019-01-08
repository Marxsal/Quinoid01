package com.example.my32ndapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;


public class TwFragment extends Fragment {
    public static final String LOG_TAG = "32XND";
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

        WebView webView = v.findViewById(R.id.webview) ;
        webView.addJavascriptInterface(new WebAppInterface(getActivity()),"twi");
        WebSettings webSettings = webView.getSettings() ;

        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl( tw_file_name );

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

}
