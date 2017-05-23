package com.example.rickywen911.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.LinkedList;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {

    private Button talkBtn;
    private Button netBtn;

    private EditText ipText;
    private EditText localPortT;
    private EditText sourcePort;

    private AudioPlayer audioPlayer;
    private AudioRecorder audioRecorder;
    private AudioReceiver audioReceiver;

    private Queue<short[]> playlist;
    private final String LOG_TAG = "main act";
    private String ip;
    private int l_port;
    private int s_port;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        talkBtn = (Button) findViewById(R.id.speak_btn);
        netBtn = (Button) findViewById(R.id.network);

        ipText = (EditText) findViewById(R.id.ipAdrs);
        localPortT = (EditText) findViewById(R.id.lport);
        sourcePort = (EditText) findViewById(R.id.sPort);


        audioRecorder = new AudioRecorder();
        playlist = new LinkedList<>();
        audioReceiver = new AudioReceiver();


        netBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ip = ipText.getText().toString();
                l_port = Integer.parseInt(localPortT.getText().toString());
                s_port = Integer.parseInt(sourcePort.getText().toString());
                audioReceiver.stopReceive();
                audioReceiver.startReceive(l_port);
                Log.d(LOG_TAG, "dest ip is " + ip + "port is " + s_port);
            }
        });



        talkBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    {
                        audioRecorder.startRecording(ip, s_port);
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
    }
}
