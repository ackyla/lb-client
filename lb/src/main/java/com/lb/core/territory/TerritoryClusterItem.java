package com.lb.core.territory;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.lb.api.Territory;
import com.lb.core.territory.TerritoryMarker;

/**
 * Created by ackyla on 2/17/14.
 */
public class TerritoryClusterItem implements ClusterItem {
    private final Territory territory;
    private final TerritoryMarker marker;

    public TerritoryClusterItem(Territory territory, TerritoryMarker marker) {
        this.territory = territory;
        this.marker = marker;
    }

    @Override
    public LatLng getPosition() {
        return territory.getCoordinate().getLatLng();
    }

    public Territory getTerritory() {
        return territory;
    }

    public TerritoryMarker getMarker() {
        return marker;
    }
}
