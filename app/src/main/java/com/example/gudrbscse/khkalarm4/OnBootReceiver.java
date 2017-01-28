package com.example.gudrbscse.khkalarm4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by gudrbscse on 2017-01-28.
 */

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        OnBootAlarmHelper.OnBootAlarm(context);
    }
}
