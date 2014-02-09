package com.lb.core.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lb.api.Notification;
import com.lb.api.Territory;
import com.lb.core.Pager;

import java.util.List;

/**
 * Created by ackyla on 1/28/14.
 */
public class NotificationPager extends Pager<Notification> {
    @Override
    @JsonProperty("notifications")
    public List<Notification> getObjects() {
        return super.getObjects();
    }
}