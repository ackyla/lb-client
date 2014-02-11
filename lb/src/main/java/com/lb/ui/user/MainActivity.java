package com.lb.ui.user;

import java.util.Locale;

import android.app.Activity;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import static com.lb.Intents.EXTRA_USER;
import com.lb.Intents;
import com.lb.R;
import com.lb.api.Location;
import com.lb.api.User;
import com.lb.api.client.LbClient;
import com.lb.logic.ILocationUpdateServiceClient;
import com.lb.logic.LocationUpdateService;
import com.lb.model.Session;
import com.lb.ui.PreferenceScreenActivity;
import com.lb.ui.notification.NotificationListFragment;
import com.lb.ui.territory.TerritoryListFragment;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends ActionBarActivity implements ILocationUpdateServiceClient, ActionBar.TabListener, MapFragment.OnGoogleMapFragmentListener {

    private User user;
    private static Intent serviceIntent;
    private LocationUpdateService updateService;

    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            updateService = ((LocationUpdateService.LocationUpdateBinder) service).getService();
            LocationUpdateService.setServiceClient(MainActivity.this);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            if (prefs.getBoolean(PreferenceScreenActivity.PREF_KEY_BACKGROUND, true)) {
                updateService.startUpdate();
            } else {
                updateService.stopUpdate();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            updateService = null;
        }

    };

    public static Intent createIntent(User user) {
        return new Intents.Builder("main.VIEW").user(user).toIntent();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = (User) getIntent().getExtras().getSerializable(EXTRA_USER);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.custom_view_gps_point);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        showUserInfo();
        startAndBindService();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startAndBindService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAndBindService();
        refreshUserInfo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAndUnBindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAndUnBindService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onMapReady(GoogleMap map, View v) {

    }

    @Override
    public void onLocationUpdate(Location location) {
        refreshUserInfo(location.getUser());
    }

    @Override
    public void onStopLogging() {

    }

    @Override
    public Activity getActivity() {
        return this;
    }


    private void startAndBindService() {
        serviceIntent = new Intent(getActivity(), LocationUpdateService.class);
        getActivity().startService(serviceIntent);
        getActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        Session.setIsBound(true);
    }

    private void stopAndUnBindService() {
        if (Session.getIsBound()) {
            getActivity().unbindService(serviceConnection);
            Session.setIsBound(false);
        }

        if (!Session.getIsStarted()) {
            getActivity().stopService(serviceIntent);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                default:
                    return new MainMapFragment();
                case 1:
                    return new TerritoryListFragment();
                case 2:
                    return new NotificationListFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section_map).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section_territory_list).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section_notification_list).toUpperCase(l);
            }
            return null;
        }
    }

    private void showUserInfo() {
        ActionBar bar = getSupportActionBar();
        ImageView ivAvatar = (ImageView) findViewById(android.R.id.home);
        View customView = bar.getCustomView();
        ProgressBar pBar = (ProgressBar) customView.findViewById(R.id.pb_gps_point);
        TextView tvGpsPoint = (TextView) customView.findViewById(R.id.tv_gps_point);

        Picasso.with(this).load(user.getAvatar()).into(ivAvatar);
        bar.setTitle(user.getName());
        bar.setSubtitle(getString(R.string.status_user_level)+" "+user.getLevel());
        pBar.setMax(user.getGpsPointLimit());
        pBar.setProgress(user.getGpsPoint());
        tvGpsPoint.setText(user.getGpsPoint()+"/"+user.getGpsPointLimit());
    }

    private void refreshUserInfo(User user) {
        this.user = user;
        showUserInfo();
    }

    private void refreshUserInfo() {
        LbClient client = new LbClient();
        client.setToken(Session.getToken());
        client.getUser(new Callback<User>() {
            @Override
            public void success(User u, Response response) {
                user = u;
                showUserInfo();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                stopAndUnBindService();
                finish();
            }
        });
    }
}
