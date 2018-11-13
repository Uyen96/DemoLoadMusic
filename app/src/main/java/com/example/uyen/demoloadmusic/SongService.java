package com.example.uyen.demoloadmusic;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;

import android.os.IBinder;
import android.support.annotation.Nullable;

public class SongService extends Service {
    private MediaPlayer mPlayer;

    public SongService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int pos) {
        mPlayer = MediaPlayer.create(this, R.raw.votinh);
        mPlayer.setLooping(true);
        mPlayer.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayer.stop();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }
}
