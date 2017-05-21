package com.example.rickywen911.myapplication;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Queue;

/**
 * Created by rickywen911 on 2/14/17.
 */

public class AudioReceiver {

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

    public void startReceive(final Queue<short[]> receiveList, int port) {
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
        new Thread() {
            @Override
            public void run() {
                isReceiving = true;
                while(isReceiving) {
                    try {
                            datagramSocket.receive(datagramPacket);
                            if(datagramPacket.getData() != null) {
                                s_data = DataTrsansformUtil.toShortArray(datagramPacket.getData());
                                receiveList.add(s_data);
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



        Log.v(LOG_TAG,"release completed");
    }
}
