package com.lb.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {

    private static final long serialVersionUID = 997409178106060663L;

    private int id;
    private String name;
    private int gpsPoint;
    private int gpsPointLimit;
    private int level;
    private int exp;
    private String avatar;
    private Date createdAt;
    private Date updatedAt;

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

    @JsonProperty("gps_point")
    public int getGpsPoint() {
        return gpsPoint;
    }

    public void setGpsPoint(int gpsPoint) {
        this.gpsPoint = gpsPoint;
    }

    @JsonProperty("gps_point_limit")
    public int getGpsPointLimit() {
        return gpsPointLimit;
    }

    public void setGpsPointLimit(int gpsPointLimit) {
        this.gpsPointLimit = gpsPointLimit;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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
}
