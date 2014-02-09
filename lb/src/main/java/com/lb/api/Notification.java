package com.lb.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ackyla on 1/29/14.
 */
public class Notification implements Serializable {

    private static final long serialVersionUID = 407687284825949936L;

    public static final String TYPE_ENTERING = "entering";
    public static final String TYPE_DETECTION = "detection";

    private int id;
    private String notificationType;
    private boolean read;
    private Date createdAt;
    private Date updatedAt;
    private Location location;
    private User territoryOwner;
    private Territory territory;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("notification_type")
    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @JsonProperty("territory_owner")
    public User getTerritoryOwner() {
        return territoryOwner;
    }

    public void setTerritoryOwner(User territoryOwner) {
        this.territoryOwner = territoryOwner;
    }

    public Territory getTerritory() {
        return territory;
    }

    public void setTerritory(Territory territory) {
        this.territory = territory;
    }
}
