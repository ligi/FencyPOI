package org.battlehack.fencypoi;

import android.content.ContentValues;
import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class MainActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener,LocationListener {

    private LocationClient locationclient;
    private TextView locationEditText;
    private Location lastLocation;

    private EditText nameEditText;
    private EditText descriptionEditText;
    private  Spinner typeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setupLocationListener();

        setContentView(R.layout.add_fence);

        setupSpinner();

        locationEditText=(TextView) findViewById(R.id.location_edittext);

        nameEditText=(EditText)findViewById(R.id.nameEditText);
        descriptionEditText=(EditText)findViewById(R.id.descriptionEditText);;


        findViewById(R.id.addButton).setEnabled(false);
        findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });
    }

    public void add() {
        ContentValues mNewValues = new ContentValues();

        mNewValues.put(POIDBContentProvider.KEY_LAT,(int)(1E6*lastLocation.getLatitude()));
        mNewValues.put(POIDBContentProvider.KEY_LON,(int)(1E6*lastLocation.getLongitude()));
        mNewValues.put(POIDBContentProvider.KEY_CREATED_AT,lastLocation.getTime());

        mNewValues.put(POIDBContentProvider.KEY_NAME,nameEditText.getText().toString());
        mNewValues.put(POIDBContentProvider.KEY_DESCRIPTION,descriptionEditText.getText().toString());
        mNewValues.put(POIDBContentProvider.KEY_TYPE, typeSpinner.getSelectedItem().toString());
        mNewValues.put(POIDBContentProvider.KEY_CREATOR,"undefined");

        getContentResolver().insert(POIDBContentProvider.CONTENT_URI,mNewValues);

    }

    private void setupSpinner() {
        typeSpinner = (Spinner) findViewById(R.id.type_spinner);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{"Power Outlet", "Apple Tree", "Danger Zone","Configure types"});
        typeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        typeSpinner.setAdapter(typeAdapter);


        Spinner alertSpinner = (Spinner) findViewById(R.id.alert_spinner);
        ArrayAdapter<String> alertAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{"Never","When walking","when biking","when driving"});
        alertAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        alertSpinner.setAdapter(alertAdapter);
    }

    private void setupLocationListener() {
        locationclient = new LocationClient(this,this,this);
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
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation=location;
        findViewById(R.id.addButton).setEnabled(true);
        locationEditText.setText("lat:"+location.getLatitude() + " lon:"+location.getLongitude() + " accuracy: " + location.getAccuracy() + " alt"+location.getAltitude());

    }
}
