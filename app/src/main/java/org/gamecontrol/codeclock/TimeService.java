package org.gamecontrol.codeclock;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.UUID;

public class TimeService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String msToHourMinSec(long ms) {
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

        public static final int STATE_START = 0;
        public static final int STATE_PAUSED = 1;
        public static final int STATE_RUNNING = 2;

        private int currentState;
        private ArrayList<Long> startTimes;
        private ArrayList<Long> runningTimes;
        private static TimeContainer instance;

        private long totalElapsed;

        private final Object mSynchronizedObject = new Object();

        private TimeContainer() {
            currentState = STATE_START;
            startTimes = new ArrayList<Long>();
            runningTimes = new ArrayList<Long>();
            totalElapsed = 0;
        }

        public int getCurrentState() {
            return currentState;
        }

        public void setCurrentState(int currentState) {
            this.currentState = currentState;
        }

        public ArrayList<Long> getStartTimes() {
            return startTimes;
        }

        public void setStartTimes(ArrayList<Long> startTimes) {
            this.startTimes = startTimes;
        }

        public ArrayList<Long> getRunningTimes() {
            return runningTimes;
        }

        public void setRunningTimes(ArrayList<Long> runningTimes) {
            this.runningTimes = runningTimes;
        }

        public void setTotalElapsed(long totalElapsed) {
            this.totalElapsed = totalElapsed;
        }

        public long getTotalElapsed() {
            if(currentState == STATE_RUNNING){
                return totalElapsed + (System.currentTimeMillis() - startTimes.get(startTimes.size()));
            } else
                return totalElapsed;
        }

        public void start() {
            if(currentState != STATE_RUNNING) {
                synchronized (mSynchronizedObject) {
                    startTimes.add(System.currentTimeMillis());
                    currentState = STATE_RUNNING;
                }
            }
        }

        public void pause() {
            if(currentState == STATE_RUNNING) {
                synchronized (mSynchronizedObject) {
                    currentState = STATE_PAUSED;
                    long currentLap = (System.currentTimeMillis() - startTimes.get(startTimes.size()));
                    totalElapsed += currentLap;
                    runningTimes.add(currentLap);
                }
            }
        }

        public static TimeContainer getInstance() {
            if(instance == null) {
                instance = new TimeContainer();
            }
            return instance;
        }

    }
}
