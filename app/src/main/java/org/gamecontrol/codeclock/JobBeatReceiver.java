package org.gamecontrol.codeclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Corbin on 11/21/2014.
 */
public class JobBeatReceiver extends BroadcastReceiver {
    private JobActivity mJobActivity;

    public JobBeatReceiver(JobActivity jobActivity) {
        mJobActivity = jobActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mJobActivity.update();
    }
}
