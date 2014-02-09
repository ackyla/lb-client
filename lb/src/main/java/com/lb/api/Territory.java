package com.lb.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.lb.core.territory.TerritoryMarker;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ackyla on 1/29/14.
 */
public class Territory implements Serializable {

    private static final long serialVersionUID = 812465072585789495L;

    private int id;
    private double radius;
    private double precision;
    private int detectionCount;
    private Date expirationDate;
    private Date createdAt;
    private Date updatedAt;
    private Character character;
    private Coordinate coordinate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @JsonProperty("detection_count")
    public int getDetectionCount() {
        return detectionCount;
    }

    public void setDetectionCount(int detectionCount) {
        this.detectionCount = detectionCount;
    }

    @JsonProperty("expiration_date")
    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    @JsonProperty("created_at")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("updated_at")
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public TerritoryMarker getMarker() {
        TerritoryMarker marker = new TerritoryMarker(new LatLng(coordinate.getLatitude(), coordinate.getLongitude()));
        marker.setRadius(radius);

        if (Calendar.getInstance().getTime().compareTo(expirationDate) < 0) {
            marker.setColor(0, 255, 0);
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        } else {
            marker.setColor(0, 0, 0);
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        }

        return marker;
    }
}
