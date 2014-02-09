package com.lb.ui.territory;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import static com.lb.Intents.EXTRA_TERRITORY;

import com.lb.Intents;
import com.lb.R;
import com.lb.api.Territory;
import com.squareup.picasso.Picasso;

public class TerritoryDetailActivity extends ActionBarActivity {

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

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new TerritoryDetailMapFragment())
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
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
