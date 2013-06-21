package org.battlehack.fencypoi;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import org.battlehack.fencypoi.geofence.GeofenceFromProviderAdder;

public class MainActivity extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

    private LocationClient locationclient;

    private PoiListFragment poiListFragment;
    private PoiEditFragment poiEditFragment;

    private NFCSharing nfcSharing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setupLocationListener();

        setContentView(R.layout.activity_main);

        setupFragments();

        nfcSharing = new NFCSharing(this);
    }

    private void setupFragments() {
        poiListFragment = new PoiListFragment();
        poiEditFragment = new PoiEditFragment();

        setFragment(R.id.fragment_main, poiListFragment, "list", null);
        setFragment(R.id.fragment_left, poiListFragment, "list", null);

        setFragment(R.id.fragment_right, poiEditFragment, "edit", null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcSharing.onResume();
    }

    @Override
    public void onPause() {
        nfcSharing.onPause();
        super.onPause();
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        nfcSharing.onNewIntent(intent);
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

    private void setFragment(int targetRes, Fragment fragment, String tag, String backstack) {
        if (findViewById(targetRes) == null) {
            return;
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(targetRes, fragment, tag);
        if (backstack != null) {
            fragmentTransaction.addToBackStack(backstack);
        }

        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_add:
                setFragment(R.id.fragment_main, poiEditFragment, "edit", "edit");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest locationrequest = LocationRequest.create();
        locationrequest.setInterval(100);
        locationclient.requestLocationUpdates(locationrequest, this);

        new GeofenceFromProviderAdder(this, locationclient).add();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_add).setVisible(!hasVisibleEditFragment());
        return true;
    }

    @Override
    public void onDisconnected() {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private boolean hasVisibleEditFragment() {
        Fragment editFragment = getSupportFragmentManager().findFragmentByTag("edit");
        return editFragment != null && editFragment.isVisible();
    }

    private PoiEditFragment getEditFragment() {
        return (PoiEditFragment) getSupportFragmentManager().findFragmentByTag("edit");
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO use otto bus with producer cache
        if (getEditFragment()!=null) {
            getEditFragment().setGPSLocation(location);
        }
    }
}
