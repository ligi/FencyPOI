package org.battlehack.fencypoi;

import android.content.ContentUris;
import android.database.Cursor;
import android.provider.BaseColumns;

/**
 * Created by ligi on 6/18/13.
 */
public class Poi2CursorConverter {

    public static Poi fromCursor(Cursor poiCursor, int pos) {
        int lat = poiCursor.getInt(poiCursor.getColumnIndexOrThrow(POIDBContentProvider.KEY_LAT));
        int lon = poiCursor.getInt(poiCursor.getColumnIndexOrThrow(POIDBContentProvider.KEY_LON));
        int id = poiCursor.getInt(poiCursor.getColumnIndexOrThrow(BaseColumns._ID));
        String name = poiCursor.getString(poiCursor.getColumnIndexOrThrow(POIDBContentProvider.KEY_NAME));
        String description = poiCursor.getString(poiCursor.getColumnIndexOrThrow(POIDBContentProvider.KEY_DESCRIPTION));

        Poi poi = new Poi(lat, lon, name, description);
        poi.setUri(ContentUris.withAppendedId(POIDBContentProvider.CONTENT_URI, id));
        return poi;
    }

}
