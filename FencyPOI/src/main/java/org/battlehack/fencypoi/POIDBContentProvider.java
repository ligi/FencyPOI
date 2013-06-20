package org.battlehack.fencypoi;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by aschildbach on 6/8/13.
 */
public class POIDBContentProvider extends ContentProvider {

    public static final String TABLE_NAME = "POIS";
    public static final Uri CONTENT_URI = Uri.parse("content://org.battlehack.fencypoi." + TABLE_NAME);
    public static final String KEY_LAT = "LAT";
    public static final String KEY_LON = "LON";
    public static final String KEY_NAME = "NAME";
    public static final String KEY_RADIUS = "RADIUS";
    public static final String KEY_ALTITUDE = "ALTITUDE";
    public static final String KEY_DESCRIPTION = "DESCRIPTION";
    public static final String KEY_CREATOR = "CREATOR";
    public static final String KEY_CREATED_AT = "CREATED_AT";
    public static final String KEY_TYPE = "TYPE";
    private POIDBCreateHelper helper;

    @Override
    public boolean onCreate() {
        helper = new POIDBCreateHelper(getContext());
        return true;
    }

    @Override
    public String getType(final Uri uri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues values) {
        long rowId = helper.getWritableDatabase().insert(TABLE_NAME, null, values);

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(CONTENT_URI, rowId);
    }

    @Override
    public int update(final Uri uri, final ContentValues values,  String selection, String[] selectionArgs) {
        if (ContentUris.parseId(uri)>0) {
            selection= BaseColumns._ID+"=?";
            selectionArgs=new String[] { String.valueOf(ContentUris.parseId(uri)) };
        }
        final int count = helper.getWritableDatabase().update(TABLE_NAME, values, selection, selectionArgs);

        if (count > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public int delete(final Uri uri,  String selection,  String[] selectionArgs) {
        if (ContentUris.parseId(uri)>0) {
            selection= BaseColumns._ID+"=?";
            selectionArgs=new String[] { String.valueOf(ContentUris.parseId(uri)) };
        }

        final int count = helper.getWritableDatabase().delete(TABLE_NAME, selection, selectionArgs);

        if (count > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder) {

        final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);

        final Cursor cursor = qb.query(helper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }
}
