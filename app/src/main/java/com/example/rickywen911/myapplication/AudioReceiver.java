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
    public boolean isPlaying = false;

    private byte[] packet_buffer;
    private int minBuffersize;
    private short[] s_data;


    private final int audioSource = MediaRecorder.AudioSource.MIC;
    private final int channeConfig = AudioFormat.CHANNEL_IN_MONO;
    private final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private final int streamType = AudioManager.STREAM_MUSIC;
    private final int playRate = 8000;
    private final int playConfig = AudioFormat.CHANNEL_OUT_MONO;
    private final int mode = AudioTrack.MODE_STREAM;
    private final int sampleRate = 8000;
    private final int sampleInterval = 20;
    private final int sampleSize = 2;
    private final int bufSize = sampleInterval*sampleInterval*sampleSize*2;
    private Thread receivingThread;


    public void startReceive(int port) {
        minBuffersize = AudioRecord.getMinBufferSize(sampleRate,channeConfig,audioFormat);
        packet_buffer = new byte[bufSize];
        if(minBuffersize == AudioRecord.ERROR || minBuffersize == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(LOG_TAG,"init recceiver failed");
            return;
        }


        if(datagramSocket == null) {
            try {
                datagramSocket = new DatagramSocket(port);
                datagramPacket = new DatagramPacket(packet_buffer,bufSize);
                Log.e(LOG_TAG,"A is" + datagramSocket.isClosed());
            } catch(SocketException se) {
                Log.e(LOG_TAG,"A");
                se.printStackTrace();
                datagramSocket.close();
                datagramSocket.disconnect();
            }
        } else {
            datagramSocket.close();
            datagramSocket.disconnect();
            try {
                datagramSocket = new DatagramSocket(port);
                Log.e(LOG_TAG,"B is" + datagramSocket.isClosed());
                datagramPacket = new DatagramPacket(packet_buffer,bufSize);
            } catch(SocketException se) {
                Log.e(LOG_TAG,"B");
                se.printStackTrace();
                datagramSocket.close();
                datagramSocket.disconnect();
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
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, playRate,playConfig,audioFormat,bufSize,mode);
            audioTrack.play();
        }
        receivingThread= new Thread() {
            @Override
            public void run() {
                isReceiving = true;
                while(isReceiving) {
                    if(receivingThread.currentThread().isInterrupted()){
                        Log.d(LOG_TAG,"INTERRUPTTED!!!!!");
                        return;
                    }
                    try {
                            datagramSocket.receive(datagramPacket);
                            if(datagramPacket.getData() != null) {

                                //s_data = DataTrsansformUtil.toShortArray(datagramPacket.getData());
                                //receiveList.add(s_data);
                                audioTrack.write(datagramPacket.getData(),0,bufSize);
                            }
                            isPlaying = false;
                    } catch(IOException ioe) {
                        Log.e(LOG_TAG,"C");
                        ioe.printStackTrace();
                    }
                }
            }
        };
        receivingThread.start();
    }

    public void stopReceive() {
        Log.v(LOG_TAG,"stop receiving");
        isReceiving = false;
        if(receivingThread!=null) {
            receivingThread.interrupt();
            release();
        }
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
