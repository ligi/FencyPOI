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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.otto.Subscribe;

import org.ligi.androidhelper.AndroidHelper;
import org.ligi.androidhelper.views.ActivityFinishOnViewClickListener;

public class PoiEditFragment extends Fragment {

    private TextView locationEditText;
    private EditText nameEditText;
    private EditText descriptionEditText;
    private Spinner typeSpinner;
    private GoogleMap mMap;
    private Marker marker;

    private boolean hasText = false;
    private boolean hasLocation = false;
    private LatLng lastLatLng;
    private View view;
    private MapView mapView;
    private Poi actPoi;

    public PoiEditFragment() {
        this.actPoi = new Poi();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getBus().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //if (view == null) {
        view = inflater.inflate(R.layout.add_fence_form, container, false);
        //}
        locationEditText = (TextView) view.findViewById(R.id.location_edittext);

        nameEditText = (EditText) view.findViewById(R.id.nameEditText);
        descriptionEditText = (EditText) view.findViewById(R.id.descriptionEditText);

        view.findViewById(R.id.switchLayerButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

            }
        });

        view.findViewById(R.id.switchLayerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchLayer();
            }
        });

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
            }

            @Override
            public void afterTextChanged(Editable editable) {
                actPoi.setName(editable.toString());
                if (hasText != editable.length() > 0) { // must have change - prevent stackoveflow
                    hasText = editable.length() > 0;
                    updateUI();
                }

            }
        });
        setupSpinner(view);
        return view;
    }

    private void switchLayer() {
        if (mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
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
        mapView = (MapView) getView().findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mMap = mapView.getMap();

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

        mNewValues.put(POIDBContentProvider.KEY_LAT, (int) (1E6 * lastLatLng.latitude));
        mNewValues.put(POIDBContentProvider.KEY_LON, (int) (1E6 * lastLatLng.longitude));
        mNewValues.put(POIDBContentProvider.KEY_CREATED_AT, System.currentTimeMillis());

        mNewValues.put(POIDBContentProvider.KEY_NAME, nameEditText.getText().toString());
        mNewValues.put(POIDBContentProvider.KEY_DESCRIPTION, descriptionEditText.getText().toString());
        mNewValues.put(POIDBContentProvider.KEY_TYPE, typeSpinner.getSelectedItem().toString());
        mNewValues.put(POIDBContentProvider.KEY_CREATOR, "undefined");

        if (actPoi.getUri() == null) {
            actPoi.setUri(getActivity().getContentResolver().insert(POIDBContentProvider.CONTENT_URI, mNewValues)); //, null, null);

        } else {
            getActivity().getContentResolver().update(actPoi.getUri(), mNewValues, null, null);
        }

    }


    public void updateUI() {
        if (getView() == null) {
            return;
        }

        AndroidHelper.at(nameEditText).changeTextIfNeeded(actPoi.getName());
        AndroidHelper.at(descriptionEditText).changeTextIfNeeded(actPoi.getDescription());

        lastLatLng = new LatLng(actPoi.getLatDbl(), actPoi.getLonDbl());


        View addButton = getView().findViewById(R.id.addButton);

        if (addButton == null) {
            return;
        }

        addButton.setEnabled(hasText && hasLocation);

        if (hasLocation) {
            locationEditText.setText("lat:" + lastLatLng.latitude + " lon:" + lastLatLng.longitude);

            if (marker != null) {
                marker.remove();

            }
            marker = mMap.addMarker(new MarkerOptions()
                    .position(lastLatLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon))
                    .title("Your Position"));

            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(lastLatLng, 18f);
            mMap.moveCamera(cu);

        }

    }

    public void setLocation(Location location) {
        if (hasLocation) {
            return;
        }
        hasLocation = (location != null);
        lastLatLng = new LatLng(location.getLatitude(), location.getLongitude());
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
        marker = null;
    }

    @Subscribe
    public void onPoiChanged(Poi poi) {
        actPoi = poi;
        updateUI();
    }
}