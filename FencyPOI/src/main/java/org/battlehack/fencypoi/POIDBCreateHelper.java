package org.battlehack.fencypoi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ligi on 6/8/13.
 */
public class POIDBCreateHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_NAME = "DBNAME";

    private static final String KEY_LAT = "LAT";
    private static final String KEY_LON = "LON";
    private static final String KEY_NAME = "NAME";
    private static final String KEY_RADIUS = "RADIUS";
    private static final String KEY_ALTITUDE = "ALTITUDE";
    private static final String KEY_DESCRIPTION = "DESCRIPTION";
    private static final String KEY_CREATED = "CREATED";
    private static final String KEY_TYPE = "TYPE";

    private static final String DICTIONARY_TABLE_NAME = "dictionary";

    private static final String DICTIONARY_TABLE_CREATE =
            "CREATE TABLE " + DICTIONARY_TABLE_NAME + " (" +
                    KEY_LAT + " INTEGER, " +
                    KEY_LON + " INTEGER," +
                    KEY_ALTITUDE + " INTEGER," +
                    KEY_NAME + " TEXT," +
                    KEY_RADIUS + " TEXT," +
                    KEY_DESCRIPTION + " TEXT," +
                    KEY_CREATED + " CREATOR," +
                    KEY_TYPE + " TYPE);";

    POIDBCreateHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DICTIONARY_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
    }
}
