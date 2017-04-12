package com.example.misaya.ttword;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

public class AudioService extends Service {
    private MediaPlayer mp;
    private String query;

    @Override
    public void onCreate() {
        //Log.e("Audio","初始化音乐资源");
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        if (query != null && !query.equals(intent.getStringExtra("query")) && mp != null) {
            mp.start();
        } else {
            String query = intent.getStringExtra("query");
            Uri location = Uri.parse("http://dict.youdao.com/dictvoice?audio=" + query);
            mp = MediaPlayer.create(this, location);
            mp.start();

            mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    try {
                        mp.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        mp.stop();
        mp.release();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
