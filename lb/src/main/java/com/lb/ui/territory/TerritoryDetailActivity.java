package com.lb.ui.territory;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import static com.lb.Intents.EXTRA_TERRITORY;

import com.google.android.gms.maps.GoogleMap;
import com.lb.Intents;
import com.lb.R;
import com.lb.api.Territory;
import com.lb.ui.MapFragment;
import com.squareup.picasso.Picasso;

public class TerritoryDetailActivity extends ActionBarActivity implements MapFragment.OnGoogleMapFragmentListener {

    private Territory territory;

    public static Intent createIntent(Territory territory) {
        return new Intents.Builder("territory.detail.VIEW").territory(territory).toIntent();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_territory_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        territory = (Territory) getIntent().getExtras().getSerializable(EXTRA_TERRITORY);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, TerritoryDetailMapFragment.newInstance(territory))
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
