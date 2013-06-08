package org.battlehack.fencypoi;

import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setupLocationListener();

        setContentView(R.layout.add_fence);
        Spinner spinner = (Spinner) findViewById(R.id.type_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{"Power Outlet", "Apple Tree", "Danger Zone"});
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);

        locationEditText=(TextView) findViewById(R.id.location_edittext);
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
        locationEditText.setText(location.toString());
    }
}
