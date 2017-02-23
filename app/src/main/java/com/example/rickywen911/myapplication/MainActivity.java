package com.example.rickywen911.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button talkBtn;
    private AudioReceiver audioReceiver;
    private AudioRecorder audioRecorder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        talkBtn = (Button) findViewById(R.id.speak_btn);
        audioReceiver = new AudioReceiver();
        audioReceiver.startReceive();


        audioRecorder = new AudioRecorder();
        talkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioRecorder.execute();
            }
        });

    }
}
