package org.battlehack.fencypoi;

import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.battlehack.fencypoi.geofence.ReceiveTransitionsIntentService;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener, LocationClient.OnAddGeofencesResultListener{

    private LocationClient locationclient;
    private TextView locationEditText;
    private Location lastLocation;

    private EditText nameEditText;
    private EditText descriptionEditText;
    private Spinner typeSpinner;

    private GoogleMap mMap;

    private MarkerOptions marker;
    
	private BluetoothAdapter bluetoothAdapter;
	private BluetoothListenThread acceptThread;
	private String btMac;

    private boolean hasText=false;
    private boolean hasLocation=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setupLocationListener();

        setContentView(R.layout.add_fence);

        setupSpinner();

        locationEditText = (TextView) findViewById(R.id.location_edittext);

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);

        findViewById(R.id.addButton).setEnabled(false);
        findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });

        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                hasText=charSequence.length()>0;
                setButtonEnabledState();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    
    @Override
    protected void onResume()
    {    	
    	super.onResume();

		if (bluetoothAdapter != null && bluetoothAdapter.isEnabled())
			maybeInitBluetoothListening();
    }
    
	@Override
	public void onPause()
	{
		if (acceptThread != null)
			acceptThread.stopAccepting();
		
		super.onPause();
	}

    public void add() {
        ContentValues mNewValues = new ContentValues();

        mNewValues.put(POIDBContentProvider.KEY_LAT, (int) (1E6 * lastLocation.getLatitude()));
        mNewValues.put(POIDBContentProvider.KEY_LON, (int) (1E6 * lastLocation.getLongitude()));
        mNewValues.put(POIDBContentProvider.KEY_CREATED_AT, lastLocation.getTime());

        mNewValues.put(POIDBContentProvider.KEY_NAME, nameEditText.getText().toString());
        mNewValues.put(POIDBContentProvider.KEY_DESCRIPTION, descriptionEditText.getText().toString());
        mNewValues.put(POIDBContentProvider.KEY_TYPE, typeSpinner.getSelectedItem().toString());
        mNewValues.put(POIDBContentProvider.KEY_CREATOR, "undefined");

        getContentResolver().insert(POIDBContentProvider.CONTENT_URI, mNewValues);

    }

    private void setupSpinner() {
        typeSpinner = (Spinner) findViewById(R.id.type_spinner);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{"Power Outlet", "Apple Tree", "Danger Zone", "Configure types"});
        typeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        typeSpinner.setAdapter(typeAdapter);


        Spinner alertSpinner = (Spinner) findViewById(R.id.alert_spinner);
        ArrayAdapter<String> alertAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{"Never", "When walking", "when biking", "when driving"});
        alertAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        alertSpinner.setAdapter(alertAdapter);
    }

    private void setupLocationListener() {
        locationclient = new LocationClient(this, this, this);
        locationclient.connect();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest locationrequest = LocationRequest.create();
        locationrequest.setInterval(100);
        locationclient.requestLocationUpdates(locationrequest, this);

        List<Geofence> geofences = new ArrayList<Geofence>();

        final Cursor cursor = managedQuery(POIDBContentProvider.CONTENT_URI, null, null, null, null);

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

        if (geofences.size()>0) {
            locationclient.addGeofences(geofences, getTransitionPendingIntent(), this);
        }
    }

    /*
    * Create a PendingIntent that triggers an IntentService in your
    * app when a geofence transition occurs.
    */
    private PendingIntent getTransitionPendingIntent() {
        // Create an explicit Intent
        Intent intent = new Intent(this,
                ReceiveTransitionsIntentService.class);
        /*
         * Return the PendingIntent
         */
        return PendingIntent.getService(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        hasLocation=(location!=null);
        setButtonEnabledState();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (marker == null) {
            marker = new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon))
                    .title("Your Position");
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, 18f);
            mMap.moveCamera(cu);

            mMap.addMarker(marker);
        } else {
            marker.position(latLng);

        }

        lastLocation = location;

        setButtonEnabledState();

        locationEditText.setText("lat:" + location.getLatitude() + " lon:" + location.getLongitude() + " accuracy: " + location.getAccuracy() + " alt" + location.getAltitude());
    }

    @Override
    public void onAddGeofencesResult(int i, String[] strings) {

    }

    public void setButtonEnabledState() {
        findViewById(R.id.addButton).setEnabled(hasText && hasLocation);
    }
	private void maybeInitBluetoothListening()
	{
		btMac = bluetoothAdapter.getAddress();

		acceptThread = new BluetoothListenThread(bluetoothAdapter)
		{
			@Override
			public void handleMsg(final byte[] msg)
			{
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						System.out.println("=== BTTX bluetooth message arrived");
						Toast.makeText(MainActivity.this, "BTTX bluetooth message arrived", Toast.LENGTH_LONG).show();

						try
						{
							final DataInputStream dis = new DataInputStream(new ByteArrayInputStream(msg));
							final ContentValues values = new ContentValues();
							values.put(POIDBContentProvider.KEY_LAT, dis.readInt());
							values.put(POIDBContentProvider.KEY_LON, dis.readInt());
							values.put(POIDBContentProvider.KEY_ALTITUDE, dis.readInt());
							values.put(POIDBContentProvider.KEY_RADIUS, dis.readInt());
							values.put(POIDBContentProvider.KEY_TYPE, dis.readUTF());
							values.put(POIDBContentProvider.KEY_NAME, dis.readUTF());
							values.put(POIDBContentProvider.KEY_DESCRIPTION, dis.readInt());
							values.put(POIDBContentProvider.KEY_CREATOR, dis.readUTF());
							values.put(POIDBContentProvider.KEY_CREATED_AT, dis.readInt());
							dis.close();

							getContentResolver().insert(POIDBContentProvider.CONTENT_URI, values);
						}
						catch (final Exception x)
						{
							Toast.makeText(MainActivity.this, "exception: " + x, Toast.LENGTH_LONG).show();
							x.printStackTrace();
						}
					}
				});
			}
		};
	}
}
