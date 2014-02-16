package com.lb.core.notification;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lb.model.Utils;

public class EnteringMarker {

    private MarkerOptions options;
    private Marker marker;

    public EnteringMarker(LatLng latLng) {
        options = new MarkerOptions();
        options.position(latLng);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
    }

    public EnteringMarker addTo(GoogleMap map) {
        if (map != null) {
            marker = map.addMarker(options);
        }
        return this;
    }

    public String getMarkerId() {
        if (marker == null) return "0";
        return marker.getId();
    }

    public void showInfoWindow() {
        if (marker == null) return;
        marker.showInfoWindow();
    }
}