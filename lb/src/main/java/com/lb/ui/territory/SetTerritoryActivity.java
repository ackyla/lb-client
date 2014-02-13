package com.lb.ui.territory;

import com.google.android.gms.maps.GoogleMap;
import static com.lb.Intents.EXTRA_TERRITORY;

import com.lb.Intents;
import com.lb.R;
import com.lb.api.*;
import com.lb.ui.MapFragment;
import static com.lb.Intents.EXTRA_USER;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class SetTerritoryActivity extends ActionBarActivity implements MapFragment.OnGoogleMapFragmentListener, SetTerritoryMapFragment.OnSuccessListener {

    public static Intent createIntent() {
        return new Intents.Builder("set.territory.VIEW").toIntent();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_territory);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.title_activity_set_territory);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new SetTerritoryMapFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.territory_set, menu);
        return true;
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
    public void onMapReady(GoogleMap map, final View v) {
    }

    @Override
    public void onSuccessSetTerritory(Territory territory) {
        finish(territory);
    }

    public void finish(Territory territory) {
        Intent data = new Intent();
        data.putExtra(EXTRA_TERRITORY, territory);
        setResult(RESULT_OK, data);
        finish();
    }

}
