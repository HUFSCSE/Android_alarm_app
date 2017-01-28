package com.example.gudrbscse.khkalarm4;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by gudrbscse on 2017-01-28.
 */

public class OnBootAlarmHelper {
    public static void OnBootAlarm(Context context){

        KhkDateDbHelper dbHelper = new KhkDateDbHelper(context);
        SQLiteDatabase mDB = dbHelper.getReadableDatabase();
        Cursor cursor = mDB.query(
                KhkDateContract.KhkDateEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                KhkDateContract.KhkDateEntry.COLUMN_TIMESTAMP
        );
        while(cursor.moveToNext()){
            int eid,eyear, emonth, eday, ehour, eminute;

            eyear = cursor.getInt(cursor.getColumnIndex(KhkDateContract.KhkDateEntry.COLUMN_YEAR));
            emonth = cursor.getInt(cursor.getColumnIndex(KhkDateContract.KhkDateEntry.COLUMN_MONTH));
            eday = cursor.getInt(cursor.getColumnIndex(KhkDateContract.KhkDateEntry.COLUMN_DAY));
            ehour = cursor.getInt(cursor.getColumnIndex(KhkDateContract.KhkDateEntry.COLUMN_HOUR));
            eminute = cursor.getInt(cursor.getColumnIndex(KhkDateContract.KhkDateEntry.COLUMN_MINUTE));
            eid = (int) cursor.getLong(cursor.getColumnIndex(KhkDateContract.KhkDateEntry._ID));

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, MyBroadcastReceiver.class);
            intent.putExtra("extra","yes");
            intent.putExtra("row_id",eid);

            PendingIntent sender = PendingIntent.getBroadcast(context, eid, intent, 0);

            Calendar calendar = Calendar.getInstance();
            calendar.set(eyear, emonth, eday, ehour, eminute, 0);
            //alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
            }
            Toast.makeText(context, "Alarm OnBoot completed", Toast.LENGTH_SHORT).show();
        }
        cursor.close();

    }
}
