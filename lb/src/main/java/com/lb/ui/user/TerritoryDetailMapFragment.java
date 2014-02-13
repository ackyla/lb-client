package com.lb.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.lb.api.Territory;
import com.lb.ui.MapFragment;

public class TerritoryDetailMapFragment extends MapFragment {
    private static final String ARG_TERRITORY = "territory";

    private Territory territory;
    private GoogleMap gMap;

    public static TerritoryDetailMapFragment newInstance(Territory territory) {
        TerritoryDetailMapFragment fragment = new TerritoryDetailMapFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TERRITORY, territory);
        fragment.setArguments(args);
        return fragment;
    }

    public TerritoryDetailMapFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        territory = (Territory) getArguments().getSerializable(ARG_TERRITORY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        initMap(territory.getCoordinate());
        gMap = getMap();
        refresh();
        return v;
    }

    private void refresh() {
        gMap.clear();
        territory.getMarker().addTo(gMap);
    }
}
