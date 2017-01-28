package com.example.gudrbscse.khkalarm4;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ChildActivity extends AppCompatActivity  {

    private SQLiteDatabase mDb_child;
    private SQLiteDatabase mDb_read;

    private int eyear, emonth, eday, ehour, eminute;
    private String _update_note = null;
    private EditText met_update_note;
    private TextView mtv_update_time;
    private int _id = 0;
    String update_date_str = null;
    String update_time_str = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);


        met_update_note = (EditText) findViewById(R.id.et_update_note);
        mtv_update_time = (TextView) findViewById(R.id.tv_update_time);
        mtv_update_time.setText("update time");

        Intent intent = getIntent();
        _id = intent.getIntExtra("_id", 0);

        KhkDateDbHelper dbHelper = new KhkDateDbHelper(this);
        mDb_child = dbHelper.getWritableDatabase();
        mDb_read = dbHelper.getReadableDatabase();
        Cursor cursor = mDb_read.rawQuery("select * from " +
                        KhkDateContract.KhkDateEntry.TABLE_NAME + " where _id = ?",
                new String[]{String.valueOf(_id)});
        if (cursor.moveToNext()) {
            eyear = cursor.getInt(cursor.getColumnIndex(KhkDateContract.KhkDateEntry.COLUMN_YEAR));
            emonth = cursor.getInt(cursor.getColumnIndex(KhkDateContract.KhkDateEntry.COLUMN_MONTH));
            eday = cursor.getInt(cursor.getColumnIndex(KhkDateContract.KhkDateEntry.COLUMN_DAY));
            ehour = cursor.getInt(cursor.getColumnIndex(KhkDateContract.KhkDateEntry.COLUMN_HOUR));
            eminute = cursor.getInt(cursor.getColumnIndex(KhkDateContract.KhkDateEntry.COLUMN_MINUTE));
            _update_note = cursor.getString(cursor.getColumnIndex(KhkDateContract.KhkDateEntry.COLUMN_NOTE));
            String alarm_str = String.valueOf(emonth + 1) + " / "
                    + String.valueOf(eday) + " / "
                    + String.valueOf(ehour) + " / "
                    + String.valueOf(eminute);
            mtv_update_time.setText(alarm_str);
            met_update_note.setText(_update_note);
            cursor.close();
            Toast.makeText(ChildActivity.this, "Cursor getted", Toast.LENGTH_SHORT).show();

        } else {
            if (intent.hasExtra("date"))
                mtv_update_time.setText(intent.getStringExtra("date"));

            if (intent.hasExtra("note"))
                _update_note = intent.getStringExtra("note");
            else
                _update_note = "";
            met_update_note.setText(_update_note);

            GregorianCalendar calendar = new GregorianCalendar();
            eyear = calendar.get(Calendar.YEAR);
            emonth = calendar.get(Calendar.MONTH);
            eday = calendar.get(Calendar.DAY_OF_MONTH);
            ehour = calendar.get(Calendar.HOUR_OF_DAY);
            eminute = calendar.get(Calendar.MINUTE);
        }

        findViewById(R.id.btn_child_date).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(ChildActivity.this, update_dateSetListener, eyear, emonth, eday).show();
            }
        });

        findViewById(R.id.btn_child_time).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new TimePickerDialog(ChildActivity.this, update_timeSetListener, ehour, eminute, false).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
        }
        return super.onOptionsItemSelected(item);
    }


    public void goToMaps(View vi){
        Intent mapintent = new Intent(this, MapsActivity.class);
        startActivity(mapintent);
    }

    public void updateToKhkDateList(View v){
        _update_note = met_update_note.getText().toString();
        ContentValues cv = new ContentValues();
        cv.put(KhkDateContract.KhkDateEntry.COLUMN_YEAR, eyear);
        cv.put(KhkDateContract.KhkDateEntry.COLUMN_MONTH, emonth);
        cv.put(KhkDateContract.KhkDateEntry.COLUMN_DAY, eday);
        cv.put(KhkDateContract.KhkDateEntry.COLUMN_HOUR, ehour);
        cv.put(KhkDateContract.KhkDateEntry.COLUMN_MINUTE, eminute);
        cv.put(KhkDateContract.KhkDateEntry.COLUMN_NOTE, _update_note);

        if( mDb_child.update(KhkDateContract.KhkDateEntry.TABLE_NAME,cv,"_id="+_id,null) > 0 ) {
            UpdateAlarm(_id);
            Toast.makeText(ChildActivity.this, "DB Update Completed", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(ChildActivity.this, "DB Update Failed", Toast.LENGTH_SHORT).show();
    }


    public void UpdateAlarm(int row_id){
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ChildActivity.this, MyBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(ChildActivity.this, row_id, intent, 0);
        alarmManager.cancel(sender);

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
        Toast.makeText(ChildActivity.this, "Alarm Update completed", Toast.LENGTH_SHORT).show();
    }



    private DatePickerDialog.OnDateSetListener update_dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int _year, int _monthOfYear, int _dayOfMonth) {
            eyear = _year;
            emonth = _monthOfYear;
            eday = _dayOfMonth;
            update_date_str = String.format("%d / %d",  _monthOfYear+1, _dayOfMonth);
            mtv_update_time.setText(update_date_str+" "+update_time_str);
            Toast.makeText(ChildActivity.this, update_date_str, Toast.LENGTH_SHORT).show();
        }
    };

    private TimePickerDialog.OnTimeSetListener update_timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int _hourOfDay, int _minute) {
            ehour = _hourOfDay;
            eminute = _minute;
            update_time_str = String.format("/ %d / %d", _hourOfDay, _minute);
            mtv_update_time.setText(update_date_str+" "+update_time_str);
            Toast.makeText(ChildActivity.this, update_time_str, Toast.LENGTH_SHORT).show();
        }
    };
}
