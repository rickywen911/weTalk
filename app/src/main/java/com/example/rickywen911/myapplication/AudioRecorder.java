package com.example.rickywen911.myapplication;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Process;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by rickywen911 on 2/7/17.
 */

public class AudioRecorder {
    public AudioRecorder audioRecorder;
    private byte[] barrBuf;

    private DatagramSocket sendSocket;
    private DatagramPacket audioData;
    private MulticastSocket sendMltSocket;


    private AudioRecord audioRecord;
    private String LOG_TAG = "AudioRecorder";
    private boolean isRecording = false;

    private int minBuffersize;

    private final int audioSource = MediaRecorder.AudioSource.MIC;
    private final int sampleRate = 8000;
    private final int sampleInterval = 20;
    private final int sampleSize = 2;
    private final int bufSize = sampleInterval*sampleInterval*sampleSize*2;
    private final int channeConfig = AudioFormat.CHANNEL_IN_MONO;
    private final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    private InetAddress ip;


    public AudioRecorder getInstance() {
        if(audioRecorder == null) {
            audioRecorder = new AudioRecorder();
        }
        return audioRecorder;
    }
    public void startMultiRecording(String group, final int port) {
        minBuffersize = AudioRecord.getMinBufferSize(sampleRate,channeConfig,audioFormat);
        if(minBuffersize == AudioRecord.ERROR || minBuffersize == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(LOG_TAG,"init recorder failed");
            return;
        }
        barrBuf = new byte[bufSize];
        audioRecord = new AudioRecord(audioSource,sampleRate,channeConfig,audioFormat,minBuffersize*10);
        this.isRecording = true;
        Log.d(LOG_TAG,"start recording");

        try {
            ip = InetAddress.getByName(group);
            sendMltSocket = new MulticastSocket();
            NetworkInterface nwi = Util.getNetworkAdapter();
            sendMltSocket.setNetworkInterface(nwi);
        } catch (UnknownHostException ue) {
            ue.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        }
        new Thread() {
            @Override
            public void run() {
                audioRecord.startRecording();
                Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                while (isRecording) {
                    int bufferRead = audioRecord.read(barrBuf,0,bufSize);
                    Log.e(LOG_TAG,"bufferRead" + bufferRead);
                    if(bufferRead > 0 ) {
                        audioData = new DatagramPacket(barrBuf,0,bufferRead,ip,port);
                        Log.e(LOG_TAG,"multicast");
                        try {
                            sendMltSocket.send(audioData);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
            }
        }.start();
    }

    public void startSingleRecording(String ipAdrs,final int port) {
        minBuffersize = AudioRecord.getMinBufferSize(sampleRate,channeConfig,audioFormat);
        if(minBuffersize == AudioRecord.ERROR || minBuffersize == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(LOG_TAG,"init recorder failed");
            return;
        }
        barrBuf = new byte[bufSize];
        audioRecord = new AudioRecord(audioSource,sampleRate,channeConfig,audioFormat,minBuffersize*10);
        this.isRecording = true;
        Log.d(LOG_TAG,"start recording");

        try {
            ip = InetAddress.getByName(ipAdrs);
            sendSocket = new DatagramSocket();
        } catch(UnknownHostException ue) {
            ue.printStackTrace();
        } catch (SocketException se) {
            se.printStackTrace();
        }
        new Thread() {
            @Override
            public void run() {
                audioRecord.startRecording();
                Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                while (isRecording) {
                    int bufferRead = audioRecord.read(barrBuf,0,bufSize);
                    Log.e(LOG_TAG,"bufferRead" + bufferRead);
                    if(bufferRead > 0 ) {
                            audioData = new DatagramPacket(barrBuf,0,bufferRead,ip,port);
                            Log.e(LOG_TAG,"sending");
                        try {
                            sendSocket.send(audioData);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
}
