package org.battlehack.fencypoi.geofence;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import org.battlehack.fencypoi.POIDBContentProvider;
import org.battlehack.fencypoi.R;

import java.util.List;

/**
 * This class receives geofence transition events from Location Services, in the
 * form of an Intent containing the transition type and geofence id(s) that triggered
 * the event.
 */
public class ReceiveTransitionsIntentService extends IntentService {

    /**
     * Sets an identifier for this class' background thread
     */
    public ReceiveTransitionsIntentService() {
        super("ReceiveTransitionsIntentService");

    }

    /**
     * Handles incoming intents
     * @param intent The Intent sent by Location Services. This Intent is provided
     * to Location Services (inside a PendingIntent) when you call addGeofences()
     */
    @Override
    protected void onHandleIntent(Intent intent) {


        // First check for errors
        if (LocationClient.hasError(intent)) {
            // Get the error code
            int errorCode = LocationClient.getErrorCode(intent);
            new NotificationHelper(getApplicationContext()).sendNotification("problem", "fence problem " + errorCode);
        // If there's no error, get the transition type and create a notification
        } else {

            // Get the type of transition (entry or exit)
            int transition = LocationClient.getGeofenceTransition(intent);

            // Test that a valid transition was reported
            if (
                    (transition == Geofence.GEOFENCE_TRANSITION_ENTER)
                    ||
                    (transition == Geofence.GEOFENCE_TRANSITION_EXIT)
               ) {

                // Post a notification
                List<Geofence> geofences = LocationClient.getTriggeringGeofences(intent);
                String[] geofenceIds = new String[geofences.size()];
                for (int index = 0; index < geofences.size() ; index++) {
                    geofenceIds[index] = geofences.get(index).getRequestId();
                }
                String ids = TextUtils.join(",",geofenceIds);
                String transitionType = getTransitionString(transition);

                try {
                    Cursor query = getApplicationContext().getContentResolver().query(POIDBContentProvider.CONTENT_URI, null,
                            BaseColumns._ID + " = ?",
                            new String[]{geofences.get(0).getRequestId()}, null);
                    query.moveToNext();
                    new NotificationHelper(getApplicationContext()).sendNotification( query.getString(query.getColumnIndex(POIDBContentProvider.KEY_NAME)),
                            query.getString(query.getColumnIndex(POIDBContentProvider.KEY_DESCRIPTION)));
                } catch (Exception e) {
                    Log.w("FencyPOI","could not notify user about geofences " + ids + " because " + e.toString());
                }
                // Log the transition type and a message
                Log.d("FencyPOI",
                        getString(
                                R.string.geofence_transition_notification_title,
                                transitionType,
                                ids));
                Log.d("FencyPOI",
                        getString(R.string.geofence_transition_notification_text));

            // An invalid transition was reported
            } else {
                // Always log as an error
                Log.e("FencyPOI",
                        getString(R.string.geofence_transition_invalid_type, transition));
            }
        }
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     * @param transitionType A transition type constant defined in Geofence
     * @return A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {

            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);

            default:
                return getString(R.string.geofence_transition_unknown);
        }
    }
}
