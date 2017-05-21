package com.example.rickywen911.myapplication;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by rickywen911 on 2/8/17.
 */

public class DataTrsansformUtil {
    public static short[] toShortArray(byte[] src) {
        short[] res = new short[src.length/2];
        ByteBuffer.wrap(src).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(res);
        return res;
    }

    public static byte[] toByteArray(short[] src) {

        byte[] res = new byte[src.length*2];
        ByteBuffer.wrap(res).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(src);
        return res;
    }
}
