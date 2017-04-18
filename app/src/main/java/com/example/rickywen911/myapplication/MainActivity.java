package com.example.rickywen911.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.util.LinkedList;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {

    private Button talkBtn;
    private Button playBtn;
    private Button stopBtn;
    private AudioPlayer audioPlayer;
    private AudioRecorder audioRecorder;
    private AudioReceiver audioReceiver;

    private Queue<short[]> playlist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        talkBtn = (Button) findViewById(R.id.speak_btn);
        playBtn = (Button) findViewById(R.id.play_btn);

        audioRecorder = new AudioRecorder();
        playlist = new LinkedList<>();
        audioReceiver = new AudioReceiver();
        audioReceiver.startReceive(playlist);


        talkBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    {
                        audioRecorder.startRecording();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        audioRecorder.stopRecording();
                        break;
                    }
                }
                return false;
            }
        });


        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioPlayer = new AudioPlayer(playlist);
                audioPlayer.execute();
            }
        });
    }
}
