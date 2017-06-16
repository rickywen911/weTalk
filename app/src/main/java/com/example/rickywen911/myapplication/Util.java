package com.example.rickywen911.myapplication;

import android.util.Log;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static android.content.ContentValues.TAG;

/**
 * Created by rickywen on 2017/6/16.
 */

public class Util {
    public static NetworkInterface getNetworkAdapter(String name) {
        Enumeration<NetworkInterface> enumeration = null;
        try {
            enumeration = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            Log.e(TAG, "get network adapter failed ", e);
            return null;
        }
        NetworkInterface netIF;
        while (enumeration.hasMoreElements()) {
            netIF = enumeration.nextElement();
            String s = netIF.getName();
            Log.e("aaa" ," s" + s);
            if (netIF.getName().toUpperCase().startsWith(name.toUpperCase())) {
                return netIF;
            }
        }
        return null;
    }

    public static NetworkInterface getNetworkAdapter() {
        NetworkInterface ifc = getNetworkAdapter("p2p");
        if (null == ifc || (!ifc.getInetAddresses().hasMoreElements())) {
            return getNetworkAdapter("wlan");
        } else {
            return ifc;
        }
    }
}
