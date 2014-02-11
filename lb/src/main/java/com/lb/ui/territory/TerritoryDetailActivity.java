package com.lb.ui.territory;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import static com.lb.Intents.EXTRA_TERRITORY;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.lb.Intents;
import com.lb.R;
import com.lb.api.Territory;
import com.lb.ui.user.MapFragment;
import com.squareup.picasso.Picasso;

public class TerritoryDetailActivity extends ActionBarActivity implements MapFragment.OnGoogleMapFragmentListener {

    private Territory territory;
    private GoogleMap gMap;

    public static Intent createIntent(Territory territory) {
        return new Intents.Builder("territory.detail.VIEW").territory(territory).toIntent();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_territory_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MapFragment())
                    .commit();
        }

        territory = (Territory) getIntent().getExtras().getSerializable(EXTRA_TERRITORY);

        ImageView ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
        TextView tvName = (TextView) findViewById(R.id.tv_name);

        Picasso.with(this).load("http://placekitten.com/48/48").into(ivAvatar);
        tvName.setText(territory.getCharacter().getName());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.territory_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_supply:
                return true;
            case R.id.action_move:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap map, View v) {
        gMap = map;
        if (gMap != null) {
            v.setVisibility(View.INVISIBLE);
            gMap.setMyLocationEnabled(true);
            UiSettings settings = gMap.getUiSettings();
            settings.setMyLocationButtonEnabled(true);
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(territory.getCoordinate().getLatitude(), territory.getCoordinate().getLongitude()), 15));

            gMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    Log.i("dump", "zoom=" + cameraPosition.zoom);
                }
            });

            v.setVisibility(View.VISIBLE);
            territory.getMarker().addTo(gMap);
        }
    }
}
