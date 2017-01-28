package com.example.gudrbscse.khkalarm4;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AlarmExecuteActivity extends AppCompatActivity {

    private final static int FIVE_MINUTE = 5 * 60 * 1000;

    private ImageView imageView;
    private SQLiteDatabase mDb_exec;
    private SQLiteDatabase mDb_read;

    private int eyear, emonth, eday, ehour, eminute;
    private int rid=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_execute);

        //actionBar 객체를 가져올 수 있다.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        imageView = (ImageView) findViewById(R.id.iv_exec);
        Glide.with(this).load(R.drawable.clock).into(imageView);

        KhkDateDbHelper dbHelper = new KhkDateDbHelper(this);
        mDb_exec = dbHelper.getWritableDatabase();

        Intent intent = getIntent();
        rid = intent.getExtras().getInt("row_id");

        findViewById(R.id.btn_exec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(AlarmExecuteActivity.this, MyBroadcastReceiver.class);
                intent1.putExtra("extra", "no");
                sendBroadcast(intent1);

                AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                PendingIntent pending_intent = PendingIntent.getBroadcast(AlarmExecuteActivity.this, rid, intent1, 0);
                alarmManager.cancel(pending_intent);

                mDb_exec.delete(KhkDateContract.KhkDateEntry.TABLE_NAME,
                        KhkDateContract.KhkDateEntry._ID + "=" + rid, null);

            }
        });

        mDb_read = dbHelper.getReadableDatabase();
        Cursor cursor = mDb_read.rawQuery("select * from " +
                        KhkDateContract.KhkDateEntry.TABLE_NAME + " where _id = ?",
                new String[]{String.valueOf(rid)});
        if (cursor.moveToNext()) {
            eyear = cursor.getInt(cursor.getColumnIndex(KhkDateContract.KhkDateEntry.COLUMN_YEAR));
            emonth = cursor.getInt(cursor.getColumnIndex(KhkDateContract.KhkDateEntry.COLUMN_MONTH));
            eday = cursor.getInt(cursor.getColumnIndex(KhkDateContract.KhkDateEntry.COLUMN_DAY));
            ehour = cursor.getInt(cursor.getColumnIndex(KhkDateContract.KhkDateEntry.COLUMN_HOUR));
            eminute = cursor.getInt(cursor.getColumnIndex(KhkDateContract.KhkDateEntry.COLUMN_MINUTE));
            cursor.close();
        }else{
            GregorianCalendar calendar = new GregorianCalendar();
            eyear = calendar.get(Calendar.YEAR);
            emonth = calendar.get(Calendar.MONTH);
            eday = calendar.get(Calendar.DAY_OF_MONTH);
            ehour = calendar.get(Calendar.HOUR_OF_DAY);
            eminute = calendar.get(Calendar.MINUTE);
        }

        findViewById(R.id.btn_later).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                int rid = intent.getExtras().getInt("row_id");
                Intent intent1 = new Intent(AlarmExecuteActivity.this, MyBroadcastReceiver.class);
                intent1.putExtra("extra", "no");
                sendBroadcast(intent1);

                AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                PendingIntent pending_intent = PendingIntent.getBroadcast(AlarmExecuteActivity.this, rid, intent1, 0);
                alarmManager.cancel(pending_intent);

                Calendar calendar = Calendar.getInstance();
                calendar.set(eyear, emonth, eday, ehour, eminute, 0);

                //alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + FIVE_MINUTE, pending_intent);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + FIVE_MINUTE, pending_intent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + FIVE_MINUTE, pending_intent);
                }
                Toast.makeText(AlarmExecuteActivity.this, "Alarm Later completed", Toast.LENGTH_SHORT).show();

            }
        });
    }
}