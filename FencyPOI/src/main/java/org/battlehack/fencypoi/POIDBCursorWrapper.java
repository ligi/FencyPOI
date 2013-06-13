package org.battlehack.fencypoi;

import android.database.Cursor;
import android.provider.BaseColumns;

/**
 * Created by ligi on 6/11/13.
 */
public class POIDBCursorWrapper {
    public final Cursor poiCursor;


    public POIDBCursorWrapper(Cursor poiCursor) {
        this.poiCursor = poiCursor;
    }

    public int getID() {
        return poiCursor.getInt(poiCursor.getColumnIndexOrThrow(BaseColumns._ID));
    }

    public int getLat() {
        return poiCursor.getInt(poiCursor.getColumnIndexOrThrow(POIDBContentProvider.KEY_LAT));
    }

    public double getLatDbl() {
        return (double)getLat()/1E6;
    }


    public double getLonDbl() {
        return (double)getLon()/1E6;
    }

    public int getLon() {
        return poiCursor.getInt(poiCursor.getColumnIndexOrThrow(POIDBContentProvider.KEY_LON));
    }


    public String getName() {
        return poiCursor.getString(poiCursor.getColumnIndexOrThrow(POIDBContentProvider.KEY_NAME));
    }


    public Cursor getCursor() {
        return poiCursor;
    }

    public void setPosition(int pos) {
        poiCursor.moveToPosition(pos);
    }
}
