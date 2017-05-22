package com.example.rickywen911.myapplication;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by rickywen911 on 2/14/17.
 */

public class AudioReceiver {

    private AudioTrack audioTrack;

    private String LOG_TAG = "AduioReceiver";
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;
    private boolean isReceiving = false;

    private byte[] packet_buffer;
    private int minBuffersize;
    private short[] s_data;

    private final int audioSource = MediaRecorder.AudioSource.MIC;
    private final int sampleRate = 44100;
    private final int channeConfig = AudioFormat.CHANNEL_IN_MONO;
    private final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private final int streamType = AudioManager.STREAM_MUSIC;
    private final int playRate = 22050;
    private final int playConfig = AudioFormat.CHANNEL_OUT_STEREO;
    private final int mode = AudioTrack.MODE_STREAM;


    public void startReceive(int port) {
        minBuffersize = AudioRecord.getMinBufferSize(sampleRate,channeConfig,audioFormat);
        packet_buffer = new byte[minBuffersize*2];
        if(minBuffersize == AudioRecord.ERROR || minBuffersize == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(LOG_TAG,"init recorder failed");
            return;
        }
        if(datagramSocket == null) {
            try {
                datagramSocket = new DatagramSocket(port);
                datagramPacket = new DatagramPacket(packet_buffer,minBuffersize*2);
            } catch(SocketException se) {
                se.printStackTrace();
            }
        }

        if(this.audioTrack != null) {
            if(this.audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                this.audioTrack.stop();
                this.audioTrack.release();
            }
        } else {
            int bufferSize = AudioRecord.getMinBufferSize(playRate,playConfig,audioFormat);
            Log.e(LOG_TAG,"bufferSize" + bufferSize);
            if(bufferSize < 0) {
                Log.e(LOG_TAG,"init Aduio track error");
            }
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, playRate,playConfig,audioFormat,bufferSize,mode);
            audioTrack.play();
        }
        new Thread() {
            @Override
            public void run() {
                isReceiving = true;
                while(isReceiving) {
                    try {
                            datagramSocket.receive(datagramPacket);
                            if(datagramPacket.getData() != null) {
                                s_data = DataTrsansformUtil.toShortArray(datagramPacket.getData());
                                //receiveList.add(s_data);
                                audioTrack.write(s_data,0,s_data.length);
                            }
                    } catch(IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
                release();
            }
        }.start();
    }

    public void stopReceive() {
        Log.v(LOG_TAG,"stop receiving");
        isReceiving = false;
    }

    private void release() {
        if(datagramPacket != null) {
            datagramPacket = null;
        }

        if(datagramSocket != null) {
            datagramSocket.close();
            datagramSocket.disconnect();
            datagramSocket = null;
        }

        if(this.audioTrack != null) {
            if(this.audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                this.audioTrack.stop();
                this.audioTrack.release();
                this.audioTrack = null;
            }
        }

        Log.v(LOG_TAG,"release completed");
    }
}
