package com.lb.core.territory;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.HashMap;

/**
 * Created by ackyla on 2/21/14.
 */
public class TerritoryClusterRenderer extends DefaultClusterRenderer<TerritoryClusterItem> {
    private final GoogleMap map;
    private HashMap<String, TerritoryMarker> hash;

    public TerritoryClusterRenderer(Context context, GoogleMap map, ClusterManager<TerritoryClusterItem> clusterManager) {
        super(context, map, clusterManager);
        this.map = map;
        hash = new HashMap<String, TerritoryMarker>();
    }

    @Override
    protected void onClusterItemRendered(TerritoryClusterItem item, Marker marker) {
        marker.setVisible(false);
        item.getMarker().show();
    }

    @Override
    protected void onClusterRendered(Cluster<TerritoryClusterItem> cluster, Marker marker) {
        for (TerritoryClusterItem item : cluster.getItems()) {
            item.getMarker().hide();
        }
    }
}