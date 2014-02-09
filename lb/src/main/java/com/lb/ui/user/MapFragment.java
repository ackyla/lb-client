package com.lb.ui.user;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.lb.model.Utils;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MapFragment extends SupportMapFragment {
    private OnGoogleMapFragmentListener listener;
    private LocationClient locationClient;

    public MapFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (OnGoogleMapFragmentListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = super.onCreateView(inflater, container, savedInstanceState);

        if (listener != null) {
            final GoogleMap gMap = getMap();

            if (gMap != null) {
                v.setVisibility(View.INVISIBLE);
                gMap.setMyLocationEnabled(true);
                UiSettings settings = gMap.getUiSettings();
                settings.setMyLocationButtonEnabled(true);

                locationClient = new LocationClient(getActivity(), new GooglePlayServicesClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Location location = locationClient.getLastLocation();
                        LatLng latlng = Utils.getDefaultLatLng();
                        if (location != null)
                            latlng = new LatLng(location.getLatitude(), location.getLongitude());
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 12));
                        v.setVisibility(View.VISIBLE);
                        locationClient.disconnect();
                    }

                    @Override
                    public void onDisconnected() {
                    }
                }, new GooglePlayServicesClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        v.setVisibility(View.VISIBLE);
                    }
                });
                locationClient.connect();

                listener.onMapReady(gMap, v);
            }
        }
        return v;
    }

    public static interface OnGoogleMapFragmentListener {
        void onMapReady(GoogleMap map, View v);
    }

    public static interface OnMapReadyListener {
        void onMapReady(GoogleMap map, View v);
    }
}
