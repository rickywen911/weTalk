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
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by rickywen911 on 2/7/17.
 */

public class AudioRecorder {
    public AudioRecorder audioRecorder;
    private AudioPlayer audioPlayer;
    DatagramSocket r_socket;
    DatagramPacket r_packet;
    public byte[] s_data;
    public short[] a_data1;
    public short[] a_data2;



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

    public void startRecording(final Queue<short[]> recordList) {
        minBuffersize = AudioRecord.getMinBufferSize(sampleRate,channeConfig,audioFormat);
        if(minBuffersize == AudioRecord.ERROR || minBuffersize == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(LOG_TAG,"init recorder failed");
            return;
        }
        final int frameSize = (sampleRate * (Short.SIZE / Byte.SIZE) / 2) & (Integer.MAX_VALUE - 1);
        int bufferSize = (frameSize * 4);
        if (bufferSize < minBuffersize)
            bufferSize = minBuffersize;
        a_data1 = new short[minBuffersize];
        audioRecord = new AudioRecord(audioSource,sampleRate,channeConfig,audioFormat,minBuffersize);
        this.isRecording = true;
        Log.d(LOG_TAG,"start recording");
        audioRecord.startRecording();
        new Thread() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
                while (isRecording) {
                    int bufferRead = audioRecord.read(a_data1,0,minBuffersize);
                    Log.e(LOG_TAG,"bufferRead" + bufferRead);
                    if(bufferRead > 0) {
                        recordList.add(a_data1);
                        a_data1 = new short[minBuffersize];
                    }
                }
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
            }
        }.start();
    }

    public void stopRecording() {
        Log.d(LOG_TAG,"stop recording");
        this.isRecording = false;
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


    public short[] getData1() {
        return this.a_data1;
    }

    public short[] getData2() {
        return this.a_data2;
    }
}
