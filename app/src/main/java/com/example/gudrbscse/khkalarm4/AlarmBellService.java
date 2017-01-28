package com.example.gudrbscse.khkalarm4;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by gudrbscse on 2017-01-24.
 */

public class AlarmBellService extends Service {

    private boolean isRunning;
    private Context context;
    MediaPlayer mMediaPlayer;
    private int startId;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final NotificationManager mNM = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        int rid = intent.getExtras().getInt("row_id");

        Intent intent1 = new Intent(this.getApplicationContext(), AlarmExecuteActivity.class);
        intent1.putExtra("row_id",rid);

        PendingIntent pIntent = PendingIntent.getActivity(this, rid, intent1, 0);

        Notification mNotify  = new Notification.Builder(this)
                .setContentTitle("KhkAlarm")
                .setContentText("Alarm Called")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.clock)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();

        String state = intent.getExtras().getString("extra");
        assert state != null;
        switch (state) {
            case "no":
                startId = 0;
                break;
            case "yes":
                startId = 1;
                break;
            default:
                startId = 0;
                break;
        }

        if(!this.isRunning && startId == 1) {
            mMediaPlayer = MediaPlayer.create(this, R.raw.father);
            mMediaPlayer.start();
            mNM.notify(0, mNotify);

            this.isRunning = true;
            this.startId = 0;
        }
        else if (!this.isRunning && startId == 0){
            this.isRunning = false;
            this.startId = 0;
        }
        else if (this.isRunning && startId == 1){
            this.isRunning = true;
            this.startId = 0;
        }
        else {
            mMediaPlayer.stop();
            mMediaPlayer.reset();

            this.isRunning = false;
            this.startId = 0;
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mMediaPlayer.stop();
        mMediaPlayer.reset();
        this.isRunning = false;
    }
}

