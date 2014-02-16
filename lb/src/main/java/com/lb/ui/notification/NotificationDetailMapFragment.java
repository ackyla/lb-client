package com.lb.ui.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.lb.R;
import com.lb.api.Notification;
import com.lb.api.User;
import com.lb.core.notification.EnteringMarker;
import com.lb.ui.MapFragment;
import com.squareup.picasso.Picasso;

public class NotificationDetailMapFragment extends MapFragment {
    private static final String ARG_NOTIFICATION = "notification";

    private Notification notification;
    private GoogleMap gMap;
    private EnteringMarker marker;
    private NotificationInfoWindowAdapter adapter;

    public static NotificationDetailMapFragment newInstance(Notification notification) {
        NotificationDetailMapFragment fragment = new NotificationDetailMapFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_NOTIFICATION, notification);
        fragment.setArguments(args);
        return fragment;
    }

    public NotificationDetailMapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notification = (Notification) getArguments().getSerializable(ARG_NOTIFICATION);
        adapter = new NotificationInfoWindowAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        initMap(notification.getLocation().getCoordinate());
        gMap = getMap();
        gMap.setInfoWindowAdapter(adapter);
        refresh();
        return v;
    }

    private void refresh() {
        if (gMap != null && notification != null) {
            gMap.clear();
            adapter.clear();
            marker = notification.getEnteringMarker();
            marker.addTo(gMap);
            adapter.setNotification(marker.getMarkerId(), notification);
            marker.showInfoWindow();
        }
    }
}
