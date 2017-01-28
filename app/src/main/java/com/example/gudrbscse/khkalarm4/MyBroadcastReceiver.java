package com.example.gudrbscse.khkalarm4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

/**
 * Created by gudrbscse on 2017-01-24.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {
    private static PowerManager.WakeLock sCpuWakeLock;

    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getExtras().getString("extra");
        int rid = intent.getExtras().getInt("row_id");
        Log.e("MyActivity", "In the receiver with " + state);

        Intent serviceIntent = new Intent(context, AlarmBellService.class);
        serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        serviceIntent.putExtra("extra", state);
        serviceIntent.putExtra("row_id", rid);
        context.startService(serviceIntent);

        if (sCpuWakeLock != null) {
            return;
        }
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        sCpuWakeLock = powerManager.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, "hi");
        //안드로이드 화면을 깨운다
        sCpuWakeLock.acquire();

        if (sCpuWakeLock != null) {
            //점유하고 있는 CPU 를 풀어준다
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }

        /*
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.clock)
                .setTicker("KHKALARM")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("KhkAlarm")
                .setContentText("Alarm Called")
                .setSound(Uri.parse("android.resource://"+ context.getPackageName() + "/" + R.raw.father))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
                //.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent).setAutoCancel(true);

        notificationmanager.notify(1, builder.build());
        */
    }
}
