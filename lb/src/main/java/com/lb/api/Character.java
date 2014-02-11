package com.lb.api;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.lb.core.character.DistanceMarker;
import com.lb.core.territory.TerritoryMarker;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by ackyla on 1/29/14.
 */
public class Character implements Serializable {

    private static final long serialVersionUID = 110489440173466651L;

    private int id;
    private String name;
    private double radius;
    private double precision;
    private int cost;
    private double distance;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public TerritoryMarker getTerritoryMarker(LatLng latLng) {
        TerritoryMarker marker = new TerritoryMarker(latLng);
        marker.setRadius(radius);

        marker.setColor(0, 255, 0);
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        return marker;
    }

    public DistanceMarker getDistanceMarker(LatLng latLng) {
        DistanceMarker marker = new DistanceMarker(latLng);
        marker.setRadius(distance);
        return marker;
    }
}
