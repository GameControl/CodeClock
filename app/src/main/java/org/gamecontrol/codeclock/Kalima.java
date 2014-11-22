package org.gamecontrol.codeclock;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class Kalima extends Service {
    private boolean running;
    private Timer mTimer;

    private final Runnable HEART = new Runnable() {
        @Override
        public void run() {
            heartbeat();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        running = false;
        mTimer = new Timer();
        HandlerThread thread = new HandlerThread(CCUtils.BEAT, android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        final Looper looper = thread.getLooper();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(looper).post(HEART);
            }
        }, 0, 16);
    }

    private void heartbeat() {
        Intent intent = new Intent();
        intent.setAction(CCUtils.BEAT);
        sendBroadcast(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!running) {
            running = true;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
