package com.lb.ui.notification;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.lb.Intents;
import com.lb.R;
import com.lb.api.Notification;
import com.lb.api.User;
import com.lb.ui.MapFragment;

import static com.lb.Intents.EXTRA_NOTIFICATION;
import static com.lb.Intents.EXTRA_USER;

public class NotificationDetailActivity extends ActionBarActivity implements MapFragment.OnGoogleMapFragmentListener {

    private Notification notification;

    public static Intent createIntent(Notification notification) {
        return new Intents.Builder("notification.detail.VIEW").notification(notification).toIntent();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);

        notification = (Notification) getIntent().getExtras().getSerializable(EXTRA_NOTIFICATION);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, NotificationDetailMapFragment.newInstance(notification))
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap map, View v) {

    }
}
