package com.lb.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by ackyla on 2/1/14.
 */
public class Coordinate implements Serializable {

    private static final long serialVersionUID = 205139381829468639L;

    private double latitude;
    private double longitude;

    @JsonProperty("lat")
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @JsonProperty("long")
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
