package com.lb.logic;

import android.app.Activity;

import com.lb.api.Location;

public interface ILocationUpdateServiceClient {
    /**
     * A new location fix has been obtained.
     *
     * @param location
     */
    public void onLocationUpdate(Location location);

    /**
     * Asking the calling activity form to indicate that logging has stopped
     */
    public void onStopLogging();

    /**
     * Returns the activity
     *
     * @return
     */
    public Activity getActivity();
}
