package com.lb.core.character;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lb.model.Utils;

public class DistanceMarker {

    private CircleOptions mCircleOpt;
    private Circle mCircle;

    public DistanceMarker(LatLng latLng) {
        mCircleOpt = new CircleOptions();
        mCircleOpt.strokeWidth(5);
        mCircleOpt.strokeColor(Color.argb(200, 0, 255, 255));
        mCircleOpt.fillColor(Color.argb(50, 0, 255, 255));
        mCircleOpt.center(latLng);
    }

    public void show() {
        mCircle.setVisible(true);
    }

    public void hide() {
        mCircle.setVisible(false);
    }

    public void setCenter(LatLng latlng) {
        mCircleOpt.center(latlng);
    }

    public void setRadius(Double radius) {
        mCircleOpt.radius(radius);
    }

    public DistanceMarker addTo(GoogleMap map) {
        if (map != null) {
            mCircle = map.addCircle(mCircleOpt);
        }
        return this;
    }

    public void updateCenter(LatLng latlng) {
        mCircle.setCenter(latlng);
    }

    public void updateRadius(Double radius) {
        mCircle.setRadius(radius);
    }

    public LatLng getCenter() {
        if (mCircle == null) return Utils.getDefaultLatLng();
        return mCircle.getCenter();
    }

    public Double getRadius() {
        if (mCircle == null) return 100.0;
        return mCircle.getRadius();
    }

    public String getMarkerId() {
        if (mCircle == null) return "0";
        return mCircle.getId();
    }
}
