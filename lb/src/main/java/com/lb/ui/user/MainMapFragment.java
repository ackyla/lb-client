package com.lb.ui.user;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.lb.api.Coordinate;
import com.lb.api.Territory;
import com.lb.api.client.LbClient;
import com.lb.core.territory.TerritoryMarker;
import com.lb.core.territory.TerritoryPager;
import com.lb.model.Session;
import com.lb.ui.territory.TerritoryInfoWindowAdapter;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainMapFragment extends MapFragment {

    private TerritoryInfoWindowAdapter adapter;
    private GoogleMap gMap;

    public MainMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new TerritoryInfoWindowAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        gMap = getMap();
        gMap.setInfoWindowAdapter(adapter);
        refresh();
        return v;
    }

    private void refresh() {
        LbClient client = new LbClient();
        client.setToken(Session.getToken());
        client.getTerritoryList(1, 10000, new Callback<TerritoryPager>() {
            @Override
            public void success(TerritoryPager pager, Response response) {
                gMap.clear();
                adapter.clear();
                for (Territory t : pager.getObjects()) {
                    TerritoryMarker marker = t.getMarker();
                    if (gMap != null) marker.addTo(gMap);
                    adapter.setTerritory(marker.getMarkerId(), t);
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }
}
