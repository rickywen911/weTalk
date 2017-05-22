package com.example.rickywen911.myapplication;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Queue;

/**
 * Created by rickywen911 on 2/7/17.
 */

public class AudioPlayer extends AsyncTask<Void,Boolean,Void> {
    private AudioTrack audioTrack;
    private String LOG_TAG = "AudioPlayer";
    private int size;
    private byte[] samples;
    private short[] s_data;

    public Queue<short[]> playlist;

    private final int streamType = AudioManager.STREAM_MUSIC;
    private final int sampleRate = 22050;
    private final int channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
    private final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    private final int mode = AudioTrack.MODE_STREAM;

    public AudioPlayer(Queue<short[]> playlist) {
        this.playlist = playlist;
    }

    private boolean initAudioTrack() {
        int bufferSize = AudioRecord.getMinBufferSize(sampleRate,channelConfig,audioFormat);
        Log.e(LOG_TAG,"bufferSize" + bufferSize);
        if(bufferSize < 0) {
            Log.e(LOG_TAG,"init Aduio track error");
            return false;
        }
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,channelConfig,audioFormat,bufferSize,mode);
        audioTrack.play();
        return true;
    }


    @Override
    protected Void doInBackground(Void... params) {
        if(!initAudioTrack()) {
            Log.e(LOG_TAG,"init player error");
            return null;
        }
        while(!playlist.isEmpty()) {
            Log.e(LOG_TAG,"list size " + playlist.size());
            s_data = playlist.poll();
            audioTrack.write(s_data,0,s_data.length);
        }
        if(this.audioTrack != null) {
            if(this.audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                this.audioTrack.stop();
                this.audioTrack.release();
            }
        }
        return null;
    }

}
