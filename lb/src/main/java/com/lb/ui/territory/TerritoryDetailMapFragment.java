package com.lb.ui.territory;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.lb.Intents.EXTRA_TERRITORY;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.lb.api.Territory;

public class TerritoryDetailMapFragment extends SupportMapFragment {
    private Territory territory;

    public TerritoryDetailMapFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = super.onCreateView(inflater, container, savedInstanceState);

        territory = (Territory) getActivity().getIntent().getExtras().getSerializable(EXTRA_TERRITORY);

        final GoogleMap gMap = getMap();

        if (gMap != null) {
            v.setVisibility(View.INVISIBLE);
            gMap.setMyLocationEnabled(true);
            UiSettings settings = gMap.getUiSettings();
            settings.setMyLocationButtonEnabled(true);
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(territory.getCoordinate().getLatitude(), territory.getCoordinate().getLongitude()), 15));

            gMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    Log.i("dump", "zoom=" + cameraPosition.zoom);
                }
            });

            v.setVisibility(View.VISIBLE);
            territory.getMarker().addTo(gMap);
        }

        return v;
    }
}
