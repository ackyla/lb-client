package com.lb.ui.location;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.lb.Intents;
import com.lb.R;
import com.lb.api.User;
import com.lb.api.client.LbClient;
import com.lb.model.Session;
import com.lb.model.Utils;
import com.lb.ui.MapFragment.OnGoogleMapFragmentListener;

import static com.lb.Intents.EXTRA_USER;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LocationHistoryActivity extends ActionBarActivity implements OnGoogleMapFragmentListener {

    private GoogleMap gMap;
    private Calendar day;
    private LocationClient mLocationClient;
    private User user;
    private TextView dayText;
    private DatePickerDialog dialog;

    public static Intent createIntent(User user) {
        return new Intents.Builder("history.location.VIEW").user(user).toIntent();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = (User) getIntent().getExtras().getSerializable(EXTRA_USER);
        day = Calendar.getInstance();

        dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                day.set(year, monthOfYear, dayOfMonth);
                updateDay();
                refresh();
            }

        }, day.get(Calendar.YEAR), day.get(Calendar.MONTH), day.get(Calendar.DAY_OF_MONTH));
        dialog.setTitle(getString(R.id.action_calendar));

        dayText = (TextView) findViewById(R.id.tv_date);
        dayText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        updateDay();
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.location_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh();
                break;
            case R.id.action_calendar:
                dialog.show();
                break;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap map, final View v) {
        if (gMap == null && map != null) gMap = map;
    }

    private void updateDay() {
        dayText.setText(getDateString(day));
    }

    private void refresh() {

        if (gMap != null) gMap.clear();

        final ProgressDialog dialog = Utils.createProgressDialog(this);

        LbClient client = new LbClient();
        client.setToken(Session.getToken());
        client.getUserLocations(day.getTime(), new Callback<List<com.lb.api.Location>>() {
            @Override
            public void success(List<com.lb.api.Location> locations, Response response) {
                dialog.dismiss();

                if (locations.size() < 1) {
                    Toast.makeText(LocationHistoryActivity.this, getDateString(day) + "のロケーション履歴はありません", Toast.LENGTH_SHORT).show();
                    return;
                }

                PolylineOptions lineOpts = Utils.createUserHistoryPolylineOptions();
                MarkerOptions markerOptions = Utils.createDefaultMarkerOptions();
                for(com.lb.api.Location location : locations) {
                    LatLng latLng = new LatLng(location.getCoordinate().getLatitude(), location.getCoordinate().getLongitude());
                    lineOpts.add(latLng);
                    markerOptions.position(latLng);
                    if (gMap != null) gMap.addMarker(markerOptions);
                }
                if (gMap != null) gMap.addPolyline(lineOpts);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                dialog.dismiss();
            }
        });
    }

    private String getDateString(Calendar cal) {
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        df.setTimeZone(cal.getTimeZone());
        return df.format(cal.getTime());
    }
}