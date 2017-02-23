package com.example.rickywen911.myapplication;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by rickywen911 on 2/14/17.
 */

public class AudioReceiver implements Runnable {

    private String LOG_TAG = "AduioReceiver";
    private int port = DefaultConfig.receive_port;
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;
    private boolean isReceiving = false;

    private byte[] packet_buffer = new byte[4096];
    private int packetSize = 4096;

    public void startReceive() {
        if(datagramSocket == null) {
            try {
                datagramSocket = new DatagramSocket(port);
                datagramPacket = new DatagramPacket(packet_buffer,packetSize);
            } catch(SocketException se) {
                se.printStackTrace();
            }
        }
        new Thread(this).start();
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
            datagramSocket = null;
        }

        Log.v(LOG_TAG,"release completed");
    }

    @Override
    public void run() {
        isReceiving = true;
        try {
            while(isReceiving) {
                datagramSocket.receive(datagramPacket);
                //about to finish
                if(datagramPacket.getLength() > 0) {
                    Log.e(LOG_TAG,"data length=" + datagramPacket.getLength());
                    AudioPlayer audioPlayer = new AudioPlayer(datagramPacket.getData(),datagramPacket.getLength());
                    audioPlayer.execute();
                }
            }
        } catch(IOException io) {
            io.printStackTrace();
        }
        stopReceive();
        release();
    }
}
