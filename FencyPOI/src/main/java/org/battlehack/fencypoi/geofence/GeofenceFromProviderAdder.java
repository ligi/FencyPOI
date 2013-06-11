package org.battlehack.fencypoi.geofence;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import org.battlehack.fencypoi.POIDBContentProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ligi on 6/11/13.
 */
public class GeofenceFromProviderAdder implements LocationClient.OnAddGeofencesResultListener {

    private final Activity ctx;
    private final LocationClient locationClient;

    public GeofenceFromProviderAdder(Activity ctx, LocationClient locationClient) {
        this.ctx = ctx;
        this.locationClient = locationClient;
    }

    public void add() {
        List<Geofence> geofences = new ArrayList<Geofence>();

        final Cursor cursor = ctx.managedQuery(POIDBContentProvider.CONTENT_URI, null, null, null, null);

        while (cursor.moveToNext()) {
            geofences.add(new Geofence.Builder()
                    .setRequestId(Integer.toString(cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID))))
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .setCircularRegion((double) cursor.getInt(cursor.getColumnIndexOrThrow(POIDBContentProvider.KEY_LAT)) / 1E6,
                            (double) cursor.getInt(cursor.getColumnIndexOrThrow(POIDBContentProvider.KEY_LON)) / 1E6,
                            50f
                    )
                    .setExpirationDuration(1000 * 60 * 60 * 24)
                    .build());

        }

        if (geofences.size() > 0) {
            locationClient.addGeofences(geofences, getTransitionPendingIntent(), this);
        }
    }

    /*
     * Create a PendingIntent that triggers an IntentService in your
     * app when a geofence transition occurs.
    */
    private PendingIntent getTransitionPendingIntent() {
        // Create an explicit Intent
        Intent intent = new Intent(ctx,
                ReceiveTransitionsIntentService.class);
        /*
         * Return the PendingIntent
         */
        return PendingIntent.getService(
                ctx,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onAddGeofencesResult(int i, String[] strings) {

    }
}
