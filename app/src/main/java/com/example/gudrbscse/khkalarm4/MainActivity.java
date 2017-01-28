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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase mDb;
    private KhkDateListAdapter mAdapter;
    private AlarmManager alarmManager;
    private PendingIntent sender;


    private int year, month, day, hour, minute;
    String date_str = null;
    String time_str = null;
    private TextView mtv_set_time;
    private EditText met_note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        KhkDateDbHelper dbHelper = new KhkDateDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        GregorianCalendar calendar = new GregorianCalendar();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day= calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        mtv_set_time = (TextView) findViewById(R.id.tv_set_time);
        mtv_set_time.setText("Time Set");

        met_note = (EditText) findViewById(R.id.et_note);


        findViewById(R.id.btn_date).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(MainActivity.this, dateSetListener, year, month, day).show();
            }
        });

        findViewById(R.id.btn_time).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new TimePickerDialog(MainActivity.this, timeSetListener, hour, minute, false).show();
            }
        });

        RecyclerView khkdateRecyclerView = (RecyclerView) findViewById(R.id.rv_alarms);
        khkdateRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        khkdateRecyclerView.setItemAnimator(new DefaultItemAnimator());
        khkdateRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get all guest info from the database and save in a cursor
        Cursor cursor = getAllKhkDate();
        mAdapter = new KhkDateListAdapter(this, cursor);
        khkdateRecyclerView.setAdapter(mAdapter);

        new ItemTouchHelper(new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                long id = (long) viewHolder.itemView.getTag();
                UnRegisterAlarm((int)id);
                removeKhkDate(id);
                mAdapter.swapCursor(getAllKhkDate());
            }
        }).attachToRecyclerView(khkdateRecyclerView);

    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        Refresh_KhkDate();
    }

    public void addToKhkDateList(View view){
        String _note = "";
        if(met_note.getText().toString() != null)
            _note = met_note.getText().toString();
        // Add guest info to mDb
        int row_id = (int) addKhkDate(year,month,day,hour,minute,_note);
        RegisterAlarm(row_id);

        Refresh_KhkDate();
    }
    public void RegisterAlarm(int row_id){
        Intent intent = new Intent(MainActivity.this, MyBroadcastReceiver.class);
        intent.putExtra("extra","yes");
        intent.putExtra("row_id",row_id);

        sender = PendingIntent.getBroadcast(MainActivity.this, row_id, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, 0);

        //alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        //Toast.makeText(MainActivity.this, "Alarm add completed", Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        }
        Toast.makeText(MainActivity.this, "Alarm add completed", Toast.LENGTH_SHORT).show();
    }
    public void UnRegisterAlarm(int row_id){
        Intent intent = new Intent(MainActivity.this, MyBroadcastReceiver.class);
        sender = PendingIntent.getBroadcast(MainActivity.this, row_id, intent, 0);

        alarmManager.cancel(sender);
        Toast.makeText(MainActivity.this, "Alarm UnRegister completed", Toast.LENGTH_SHORT).show();
    }
    public void Refresh_KhkDate(){
        // Update the cursor in the adapter to trigger UI to display the new list
        mAdapter.swapCursor(getAllKhkDate());
        //clear UI text fields
        mtv_set_time.setText("Time Set");
        met_note.setText("");
    }

    private Cursor getAllKhkDate() {
        return mDb.query(
                KhkDateContract.KhkDateEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                KhkDateContract.KhkDateEntry.COLUMN_TIMESTAMP
        );
    }

    private long addKhkDate(int _year, int _month,
                            int _day, int _hour, int _minute, String _note){
        ContentValues cv = new ContentValues();
        cv.put(KhkDateContract.KhkDateEntry.COLUMN_YEAR, _year);
        cv.put(KhkDateContract.KhkDateEntry.COLUMN_MONTH, _month);
        cv.put(KhkDateContract.KhkDateEntry.COLUMN_DAY, _day);
        cv.put(KhkDateContract.KhkDateEntry.COLUMN_HOUR, _hour);
        cv.put(KhkDateContract.KhkDateEntry.COLUMN_MINUTE, _minute);
        cv.put(KhkDateContract.KhkDateEntry.COLUMN_NOTE, _note);
        return mDb.insert(KhkDateContract.KhkDateEntry.TABLE_NAME, null, cv);
    }

    private boolean removeKhkDate(long id){
        return mDb.delete(KhkDateContract.KhkDateEntry.TABLE_NAME,
                KhkDateContract.KhkDateEntry._ID + "=" + id, null) > 0;
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int _year, int _monthOfYear,int _dayOfMonth) {
            year = _year;
            month = _monthOfYear;
            day = _dayOfMonth;
            date_str = String.format("%d / %d",  _monthOfYear+1, _dayOfMonth);
            mtv_set_time.setText(date_str+" "+time_str);
            Toast.makeText(MainActivity.this, date_str, Toast.LENGTH_SHORT).show();
        }
    };

    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int _hourOfDay, int _minute) {
            hour = _hourOfDay;
            minute = _minute;
            time_str = String.format("/ %d / %d", _hourOfDay, _minute);
            mtv_set_time.setText(date_str+" "+time_str);
            Toast.makeText(MainActivity.this, time_str, Toast.LENGTH_SHORT).show();
        }
    };
}

