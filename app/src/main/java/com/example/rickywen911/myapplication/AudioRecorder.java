package com.example.rickywen911.myapplication;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Process;
import android.util.Log;

import java.net.InetAddress;

/**
 * Created by rickywen911 on 2/7/17.
 */

public class AudioRecorder {
    public AudioRecorder audioRecorder;
    public byte[] s_data;
    public short[] a_data1;



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
        a_data1 = new short[minBuffersize];
        audioRecord = new AudioRecord(audioSource,sampleRate,channeConfig,audioFormat,minBuffersize);
        this.isRecording = true;
        Log.d(LOG_TAG,"start recording");
        audioRecord.startRecording();
        new Thread() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                while (isRecording) {
                    int bufferRead = audioRecord.read(a_data1,0,minBuffersize);
                    Log.e(LOG_TAG,"bufferRead" + bufferRead);
                    if(bufferRead > 0) {
                            s_data = DataTrsansformUtil.toByteArray(a_data1);
                            SendingService.getInstance().newVoiceByte(s_data);
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
}
