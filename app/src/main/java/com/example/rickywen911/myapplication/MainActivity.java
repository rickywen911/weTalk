package com.example.rickywen911.myapplication;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {

    private Button talkBtn;
    private Button netBtn;

    private EditText ipText;
    private EditText localPort;
    private EditText sourcePort;

    private AudioRecorder audioRecorder;
    private AudioReceiver audioReceiver;

    private RadioGroup sendMethodGroup;
    private RadioButton UdpBtn;
    private RadioButton MltBtn;

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

        sendMethodGroup = (RadioGroup)findViewById(R.id.group);
        UdpBtn = (RadioButton)findViewById(R.id.udp);
        MltBtn = (RadioButton)findViewById(R.id.multicast);


        ipText = (EditText) findViewById(R.id.ipAdrs);
        sourcePort = (EditText) findViewById(R.id.sPort);
        localPort = (EditText) findViewById(R.id.lport);

        audioRecorder = new AudioRecorder();
        audioReceiver = new AudioReceiver();
        getMltcastPermit();


        sendMethodGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i == UdpBtn.getId()) {
                    audioReceiver.stopReceive();
                    audioReceiver.leaveGroup();
                    netBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            audioReceiver.stopReceive();
                            ip = ipText.getText().toString();
                            l_port = Integer.parseInt(localPort.getText().toString());
                            s_port = Integer.parseInt(sourcePort.getText().toString());
                            audioReceiver.startSingleReceive(l_port);

                            Log.d(LOG_TAG, "dest ip is " + ip + " port is " + s_port);
                        }
                    });

                    talkBtn.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN: {
                                    audioRecorder.startSingleRecording(ip,s_port);
                                    break;
                                }
                                case MotionEvent.ACTION_UP: {
                                    audioRecorder.stopRecording();
                                    break;
                                }
                            }
                            return false;
                        }
                    });
                } else if(i == MltBtn.getId()) {
                    audioReceiver.stopReceive();
                    netBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            audioReceiver.stopReceive();
                            ip = ipText.getText().toString();
                            l_port = Integer.parseInt(localPort.getText().toString());
                            s_port = Integer.parseInt(sourcePort.getText().toString());
                            audioReceiver.startMultiReceive(ip,l_port);

                            Log.d(LOG_TAG, "dest ip is " + ip + " port is " + l_port);
                        }
                    });

                    talkBtn.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN: {
                                    audioRecorder.startMultiRecording(ip,s_port);
                                    break;
                                }
                                case MotionEvent.ACTION_UP: {
                                    audioRecorder.stopRecording();
                                    break;
                                }
                            }
                            return false;
                        }
                    });
                }
            }
        });
    }

    private void getMltcastPermit() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiManager.MulticastLock multicastLock = wifiManager
                .createMulticastLock("multicast.test");
        multicastLock.acquire();
    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.udp:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.multicast:
                if (checked)
                    // Ninjas rule
                    break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioReceiver.stopReceive();
    }
}
