package com.lb.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.lb.R;
import com.lb.api.Territory;
import com.lb.api.client.LbClient;
import com.lb.core.territory.TerritoryMarker;
import com.lb.core.territory.TerritoryPager;
import com.lb.model.Session;
import com.lb.ui.MapFragment;
import com.lb.ui.territory.TerritoryDetailActivity;
import com.lb.ui.territory.TerritoryInfoWindowAdapter;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainMapFragment extends MapFragment implements GoogleMap.OnInfoWindowClickListener {

    private TerritoryInfoWindowAdapter adapter;
    private GoogleMap gMap;

    public MainMapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        adapter = new TerritoryInfoWindowAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        initMap();
        gMap = getMap();
        gMap.setInfoWindowAdapter(adapter);
        gMap.setOnInfoWindowClickListener(this);
        refresh();
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_map, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

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

    @Override
    public void onInfoWindowClick(Marker marker) {
        Territory territory = adapter.getTerritory(marker.getId());
        startActivity(TerritoryDetailActivity.createIntent(territory));
    }
}
