package com.example.rickywen911.myapplication;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by rickywen on 2017/5/24.
 */

public class SenderThread implements Callable<Boolean>{
    private static int id;

    private DatagramSocket s_socket;
    private DatagramPacket r_packet;

    private final String LOG_TAG = "SenderThread";

    private InetAddress ip;
    private int port;

    public byte[] audioData;

    boolean isRunning=true;

    public boolean isSending = true;
    private Queue<byte[]> voicePacketQueue = new LinkedBlockingDeque<byte[]>();



    public SenderThread(String ipAdrs, int port) {
        try {
            ip = InetAddress.getByName(ipAdrs);
            s_socket = new DatagramSocket();
        } catch(UnknownHostException ue) {
            ue.printStackTrace();
        } catch (SocketException se) {
            se.printStackTrace();
        }
        id++;
        this.port = port;
        Log.d(LOG_TAG,"This is " + id + "th sending thread");
    }


    public String getIp() {
        return ip.getHostName();
    }



    @Override
    public Boolean call() {
        while(isRunning) {
            if(Thread.currentThread().isInterrupted()){
                s_socket.disconnect();
                s_socket.close();
                return true;
            }
            if(!voicePacketQueue.isEmpty()) {
                try {
                    audioData = voicePacketQueue.remove();
                    r_packet = new DatagramPacket(audioData, SendingService.length, ip, port);

                    s_socket.send(r_packet);
                    Log.d(LOG_TAG, "sending " + SendingService.length + " bytes...");
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        s_socket.disconnect();
        s_socket.close();
        return true;
    }

    public  Queue<byte[]> getQueue(){
        return voicePacketQueue;
    }


    public void setIsRunning(boolean isRunning){
        this.isRunning=isRunning;
    }
}
