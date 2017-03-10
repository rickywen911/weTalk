package com.example.rickywen911.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
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
    private short[] sound1;
    private short[] sound2;

    private Queue<short[]> playlist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        talkBtn = (Button) findViewById(R.id.speak_btn);
        playBtn = (Button) findViewById(R.id.play_btn);
        stopBtn = (Button) findViewById(R.id.stop_record);

        audioRecorder = new AudioRecorder();
        playlist = new LinkedList<>();
        talkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                audioRecorder.startRecording(playlist);
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioRecorder.stopRecording();
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
