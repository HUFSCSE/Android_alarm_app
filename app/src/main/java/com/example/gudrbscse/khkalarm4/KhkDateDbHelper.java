package com.example.gudrbscse.khkalarm4;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.gudrbscse.khkalarm4.KhkDateContract.*;
/**
 * Created by gudrbscse on 2017-01-24.
 */

public class KhkDateDbHelper extends SQLiteOpenHelper {
    // The database name
    private static final String DATABASE_NAME = "khkdate.db";
    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    public  KhkDateDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_KHKDATE_TABLE = "CREATE TABLE " + KhkDateEntry.TABLE_NAME + " (" +
                KhkDateEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KhkDateEntry.COLUMN_YEAR + " INTEGER NOT NULL, " +
                KhkDateEntry.COLUMN_MONTH + " INTEGER NOT NULL, " +
                KhkDateEntry.COLUMN_DAY + " INTEGER NOT NULL, " +
                KhkDateEntry.COLUMN_HOUR + " INTEGER NOT NULL, " +
                KhkDateEntry.COLUMN_MINUTE + " INTEGER NOT NULL, " +
                KhkDateEntry.COLUMN_NOTE + " STRING NOT NULL, " +
                KhkDateEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";
        sqLiteDatabase.execSQL(SQL_CREATE_KHKDATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // For now simply drop the table and create a new one. This means if you change the
        // DATABASE_VERSION the table will be dropped.
        // In a production app, this method might be modified to ALTER the table
        // instead of dropping it, so that existing data is not deleted.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + KhkDateEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
