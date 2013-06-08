package org.battlehack.fencypoi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ligi on 6/8/13.
 */
public class POIDBCreateHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "DBNAME";

    private static final String DICTIONARY_TABLE_CREATE =
            "CREATE TABLE " + POIDBContentProvider.TABLE_NAME + " (" +
                    POIDBContentProvider.KEY_LAT + " INTEGER, " +
                    POIDBContentProvider.KEY_LON + " INTEGER," +
                    POIDBContentProvider.KEY_ALTITUDE + " INTEGER," +
                    POIDBContentProvider.KEY_NAME + " TEXT," +
                    POIDBContentProvider.KEY_RADIUS + " TEXT," +
                    POIDBContentProvider.KEY_DESCRIPTION + " TEXT," +
                    POIDBContentProvider.KEY_CREATED + " CREATOR," +
                    POIDBContentProvider.KEY_TYPE + " TYPE);";

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
