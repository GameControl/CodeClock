package org.gamecontrol.codeclock;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.UUID;

public class TimeService extends Service {
    private boolean running;

    @Override
    public void onCreate() {
        super.onCreate();
        running = false;
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

    public static String msToHourMinSec(long ms) {
        if(ms == 0) {
            return "00:00";
        } else {
            long seconds = (ms / 1000) % 60;
            long minutes = (ms / 1000) / 60;
            long hours = minutes / 60;

            StringBuilder sb = new StringBuilder();
            if(hours > 0) {
                sb.append(hours);
                sb.append(':');
            }
            if(minutes > 0) {
                minutes = minutes % 60;
                if(minutes >= 10) {
                    sb.append(minutes);
                } else {
                    sb.append(0);
                    sb.append(minutes);
                }
            } else {
                sb.append('0');
                sb.append('0');
            }
            sb.append(':');
            if(seconds > 0) {
                if(seconds >= 10) {
                    sb.append(seconds);
                } else {
                    sb.append(0);
                    sb.append(seconds);
                }
            } else {
                sb.append('0');
                sb.append('0');
            }
            return sb.toString();
        }
    }

    public static class TimeContainer {

        public static final int STATE_INIT = 0;
        public static final int STATE_PAUSED = 1;
        public static final int STATE_RUNNING = 2;

        private int currentState;
        private Job job;
        private static TimeContainer instance;

        private final Object mSynchronizedObject = new Object();

        private TimeContainer() {
            currentState = STATE_INIT;
        }

        public UUID getJobUUID() {
            if(job != null)
                return job.getUUID();
            else
                return null;
        }

        public int getCurrentState() {
            return currentState;
        }

        public void setCurrentState(int currentState) {
            this.currentState = currentState;
        }

        public long getTotalElapsed() {
            if(currentState == STATE_RUNNING){
                return job.getElapsed() + (System.currentTimeMillis() - job.getLastStartTime());
            } else
                return job.getElapsed();
        }

        public void start() {
            if(currentState != STATE_RUNNING) {
                synchronized (mSynchronizedObject) {
                    job.addStartTime(System.currentTimeMillis());
                    currentState = STATE_RUNNING;
                }
            }
        }

        public void pause() {
            if(currentState == STATE_RUNNING) {
                synchronized (mSynchronizedObject) {
                    currentState = STATE_PAUSED;
                    long currentLap = (System.currentTimeMillis() - job.getLastStartTime());
                    job.addRunningTimes(currentLap);
                }
            }
        }

        public static TimeContainer getInstance() {
            if(instance == null) {
                instance = new TimeContainer();
            }
            return instance;
        }

        public void saveJob() {
            if(currentState == STATE_RUNNING){
                pause();
            }
        }

        public void loadJob(Job job) {
            this.job = job;

        }

    }
}
