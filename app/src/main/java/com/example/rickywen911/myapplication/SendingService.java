package com.example.rickywen911.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by rickywen on 2017/5/25.
 */

public class SendingService extends AsyncTask<Void,Void,Void> {
    private ExecutorService sendService;
    private ExecutorCompletionService completionService;//
    private static ArrayList<SenderThread> threadList;//记录正在执行的线程，用于停止单一线程
    private static SendingService sendingServiceInstance;

    private boolean sendServiceEnable = false;

    public static int length = 0;


    public static SendingService getInstance(){
        if(sendingServiceInstance==null){
            sendingServiceInstance=new SendingService();
        }
        return sendingServiceInstance;
    }

    private SendingService() {
        threadList = new ArrayList<SenderThread>();
        sendService = Executors.newFixedThreadPool(10);
        completionService = new ExecutorCompletionService(sendService);
        sendServiceEnable = true;

    }

    @Override
    protected Void doInBackground(Void... voids) {
        while(sendServiceEnable) {
            // TODO: 2017/5/25 get future.
            Future<?> future = completionService.poll();
            if(future != null) {
                try {
                    Log.d("SendingService", "captured future");
                    future.get();
                    future.cancel(true);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public boolean addThread(SenderThread st) {
        completionService.submit(st);
        threadList.add(st);
        return true;
    }

    public boolean removeTread(String ip) {
        for(SenderThread s:threadList) {
            //// TODO: 2017/5/25  remove thread using ip adrs.
            if(s.getIp().equals(ip)) {
                threadList.remove(s);
                s.setIsRunning(false);
            }
        }
        return true;
    }

    public void newVoiceByte(byte[] voiceByte,int length){
        this.length = length;
        for(SenderThread s: threadList){
            s.getQueue().add(voiceByte);
        }
    }

    public void stopAll() {
        sendService.shutdownNow();
        sendServiceEnable = false;
        try {
            sendService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

    }
}
