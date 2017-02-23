package com.example.rickywen911.myapplication;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by rickywen911 on 2/7/17.
 */

public class AudioRecorder extends AsyncTask<Void,Void,Void> {
    public AudioRecorder audioRecorder;
    DatagramSocket r_socket;
    DatagramPacket r_packet;
    public byte[] s_data;
    public short[] a_data;
    private AudioRecord audioRecord;
    private String LOG_TAG = "AudioRecorder";
    private boolean isRecording = false;

    private int minBuffersize;

    private final int audioSource = MediaRecorder.AudioSource.MIC;
    private final int sampleRate = 44100;
    private final int channeConfig = AudioFormat.CHANNEL_IN_MONO;
    private final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    private InetAddress ip;
    private int port;




    public AudioRecorder getInstance() {
        if(audioRecorder == null) {
            audioRecorder = new AudioRecorder();
        }
        return audioRecorder;
    }

    public void startRecording() {
        minBuffersize = AudioRecord.getMinBufferSize(sampleRate,channeConfig,audioFormat);
        if(minBuffersize == AudioRecord.ERROR || minBuffersize == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(LOG_TAG,"init recorder failed");
            return;
        }
        a_data = new short[minBuffersize];
        audioRecord = new AudioRecord(audioSource,sampleRate,channeConfig,audioFormat,minBuffersize);
    }

    public void stopRecording() {
        isRecording = false;
    }



    public void initSender() {
        try {
            try {
                ip = InetAddress.getByName(DefaultConfig.SERVICE_HOST);
                this.port = DefaultConfig.receive_port;
                r_socket = new DatagramSocket();
            } catch (UnknownHostException ue) {
                ue.printStackTrace();
            }
        } catch (SocketException se) {
            se.printStackTrace();
        }
    }



    @Override
    protected Void doInBackground(Void... params) {
        startRecording();
        this.isRecording = true;
        initSender();
        Log.d(LOG_TAG,"start recording");
        if(isRecording) {
            audioRecord.startRecording();
            int bufferRead = audioRecord.read(a_data,0,minBuffersize);
            Log.e(LOG_TAG,"bufferRead" + bufferRead);
            if(bufferRead > 0) {
                Log.e(LOG_TAG,"sjdksdj");
                s_data = DataTrsansformUtil.toByteArray(a_data);
                try {
                    Log.e(LOG_TAG,"try to sending");
                    r_packet = new DatagramPacket(s_data,bufferRead,ip,port);
                    r_packet.setData(s_data);
                    r_socket.send(r_packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        stopRecording();
        audioRecord.stop();
        audioRecord.release();
        this.audioRecord = null;
        return null;
    }
}
