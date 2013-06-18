package org.battlehack.fencypoi;

import android.database.Cursor;
import android.provider.BaseColumns;

/**
 * Created by ligi on 6/11/13.
 */
public class Poi {

    private int lat;
    private int lon;
    private int id;
    private String name;

    private String description;

    public Poi(Cursor poiCursor,int pos) {
        id= poiCursor.getInt(poiCursor.getColumnIndexOrThrow(BaseColumns._ID));
        lat = poiCursor.getInt(poiCursor.getColumnIndexOrThrow(POIDBContentProvider.KEY_LAT));
        lon = poiCursor.getInt(poiCursor.getColumnIndexOrThrow(POIDBContentProvider.KEY_LON));
        name=poiCursor.getString(poiCursor.getColumnIndexOrThrow(POIDBContentProvider.KEY_NAME));
        description=poiCursor.getString(poiCursor.getColumnIndexOrThrow(POIDBContentProvider.KEY_DESCRIPTION));
    }

    public int getID() {
        return id;
    }

    public int getLat() {

        return lat;
    }

    public double getLatDbl() {
        return (double)getLat()/1E6;
    }


    public double getLonDbl() {
        return (double)getLon()/1E6;
    }

    public int getLon() {
        return lon;
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
