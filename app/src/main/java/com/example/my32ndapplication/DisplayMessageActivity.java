package com.example.my32ndapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        Intent intent = getIntent() ;
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the textview and set the string as its text
        TextView textView = findViewById(R.id.textView);
        textView.setText(message);
    }
}
