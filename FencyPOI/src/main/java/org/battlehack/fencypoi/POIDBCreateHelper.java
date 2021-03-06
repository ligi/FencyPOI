package org.battlehack.fencypoi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by ligi on 6/8/13.
 */
public class POIDBCreateHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "DBNAME";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + POIDBContentProvider.TABLE_NAME + " (" +
                    BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    POIDBContentProvider.KEY_LAT + " INTEGER NOT NULL, " +
                    POIDBContentProvider.KEY_LON + " INTEGER NOT NULL, " +
                    POIDBContentProvider.KEY_ALTITUDE + " INTEGER, " +
                    POIDBContentProvider.KEY_NAME + " TEXT, " +
                    POIDBContentProvider.KEY_RADIUS + " INTEGER, " +
                    POIDBContentProvider.KEY_DESCRIPTION + " TEXT, " +
                    POIDBContentProvider.KEY_CREATOR + " TEXT NOT NULL, " +
                    POIDBContentProvider.KEY_CREATED_AT + " INTEGER NOT NULL, " +
                    POIDBContentProvider.KEY_TYPE + " TEXT NOT NULL);";

    POIDBCreateHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
    }
}
