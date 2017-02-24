package com.example.rickywen911.myapplication;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.util.Log;

import java.net.DatagramSocket;

/**
 * Created by rickywen911 on 2/7/17.
 */

public class AudioPlayer extends AsyncTask<Void,Void,Void> {
    private AudioTrack audioTrack;
    private String LOG_TAG = "AudioPlayer";
    private int size;
    private byte[] samples;
    private short[] s_data;

    private final int streamType = AudioManager.STREAM_MUSIC;
    private final int sampleRate = 44100;
    private final int channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
    private final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    private final int mode = AudioTrack.MODE_STREAM;

    public AudioPlayer(short[] s_data, int size) {
        this.s_data = s_data;
        this.size = size;
//        s_data = DataTrsansformUtil.toShortArray(samples);
    }

    private boolean initAudioTrack() {
        int bufferSize = AudioRecord.getMinBufferSize(sampleRate,channelConfig,audioFormat);
        Log.e(LOG_TAG,"bufferSize" + bufferSize);
        if(bufferSize < 0) {
            Log.e(LOG_TAG,"init Aduio track error");
            return false;
        }
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,channelConfig,audioFormat,bufferSize,AudioTrack.MODE_STREAM);
        audioTrack.play();
        return true;
    }


    @Override
    protected Void doInBackground(Void... params) {
        if(!initAudioTrack()) {
            Log.e(LOG_TAG,"init player error");
            return null;
        }
        audioTrack.write(s_data,0,size);
        if(this.audioTrack != null) {
            if(this.audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                this.audioTrack.stop();
                this.audioTrack.release();
            }
        }
        return null;
    }
}
