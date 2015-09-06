package com.heavyconnect.heavyconnect.geolocation;

import android.location.Location;

/**
 * OnLocationChanged listener interface.
 */
public interface OnLocationChangedListener {

    /**
     * This method is called when location is changed.
     * @param location - New location
     */
    void onLocationChanged(Location location);
}
