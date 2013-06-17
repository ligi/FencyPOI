package org.battlehack.fencypoi;


import android.app.Activity;
import android.content.ContentValues;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.MapView;

public class PoiEditFragment extends Fragment {

    private TextView locationEditText;
    private EditText nameEditText;
    private EditText descriptionEditText;
    private Spinner typeSpinner;
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private MarkerOptions marker;

    private boolean hasText = false;
    private boolean hasLocation = false;
    private Location lastLocation;
    private View view;

    private MapView mapView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //if (view == null) {
        view = inflater.inflate(R.layout.add_fence_form, container, false);
        //}
        locationEditText = (TextView) view.findViewById(R.id.location_edittext);

        nameEditText = (EditText) view.findViewById(R.id.nameEditText);
        descriptionEditText = (EditText) view.findViewById(R.id.descriptionEditText);

        view.findViewById(R.id.addButton).setEnabled(false);
        view.findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                hasText = charSequence.length() > 0;
                updateUI();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        setupSpinner(view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


        updateUI();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            MapsInitializer.initialize(getActivity());
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
        mapView=(MapView)getView().findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mMap=mapView.getMap();

    }


    private void setupSpinner(View view) {
        typeSpinner = (Spinner) view.findViewById(R.id.type_spinner);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, new String[]{"Unspecified", "Power Outlet", "Apple Tree", "Danger Zone"});
        typeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        typeSpinner.setAdapter(typeAdapter);


        Spinner alertSpinner = (Spinner) view.findViewById(R.id.alert_spinner);
        ArrayAdapter<String> alertAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, new String[]{"Never", "When walking", "when biking", "when driving"});
        alertAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        alertSpinner.setAdapter(alertAdapter);
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

        getActivity().getContentResolver().insert(POIDBContentProvider.CONTENT_URI, mNewValues);

    }

    public void updateUI() {
        if (getView() == null) {
            return;
        }

        View addButton = getView().findViewById(R.id.addButton);

        if (addButton == null) {
            return;
        }

        addButton.setEnabled(hasText && hasLocation);

        if (hasLocation) {
            locationEditText.setText("lat:" + lastLocation.getLatitude() + " lon:" + lastLocation.getLongitude() + " accuracy: " + lastLocation.getAccuracy() + " alt" + lastLocation.getAltitude());
            LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
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
        }

    }

    public void setLocation(Location location) {
        hasLocation = (location != null);
        lastLocation = location;
        updateUI();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
        mapView.onResume();
        // we need a new marker for this map
        marker=null;
    }
}