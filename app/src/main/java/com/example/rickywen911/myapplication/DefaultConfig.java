package com.example.rickywen911.myapplication;

/**
 * Created by rickywen911 on 2/9/17.
 */

public class DefaultConfig {
    public static String SERVICE_HOST = "104.194.98.233";
    public static int send_port = 8000;
    public static int receive_port = 15000;

    public void setServiceHost(String s) {
        SERVICE_HOST = s;
    }

    public void setSend_port(int n) {
        send_port = n;
    }

    public void setReceive_port(int n) {
        receive_port = n;
    }
}
