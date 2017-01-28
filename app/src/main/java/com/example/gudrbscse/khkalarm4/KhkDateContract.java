package com.example.gudrbscse.khkalarm4;

import android.provider.BaseColumns;

/**
 * Created by gudrbscse on 2017-01-24.
 */

public class KhkDateContract {
    public static final class KhkDateEntry implements BaseColumns {
        public static final String TABLE_NAME = "khkdate";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_MONTH = "month";
        public static final String COLUMN_DAY = "day";
        public static final String COLUMN_HOUR = "hour";
        public static final String COLUMN_MINUTE = "minute";
        public static final String COLUMN_NOTE = "note";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
