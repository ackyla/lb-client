package com.lb.ui.user;

import static com.lb.Intents.EXTRA_USER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.lb.Intents;
import com.lb.R;
import com.lb.api.User;
import com.lb.api.client.LbClient;
import com.lb.logic.ILocationUpdateServiceClient;
import com.lb.logic.LocationUpdateService;
import com.lb.model.Session;
import com.lb.ui.MapFragment;
import com.lb.ui.location.LocationHistoryActivity;
import com.lb.ui.notification.NotificationListFragment;
import com.lb.ui.territory.TerritoryListFragment;
import com.lb.ui.MapFragment.OnGoogleMapFragmentListener;
import com.lb.ui.PreferenceScreenActivity;
import com.lb.ui.SetAvatarActivity;
import com.lb.ui.TabRootFragment;
import com.lb.ui.TerritoryDetailBackFragment.OnTerritoryDetailFragmentListener;
import com.lb.core.territory.TerritoryMarker;
import com.squareup.picasso.Picasso;

import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GameActivity extends ActionBarActivity implements ILocationUpdateServiceClient, OnGoogleMapFragmentListener, OnTerritoryDetailFragmentListener {

    private User user;

    private static Intent serviceIntent;
    private LocationUpdateService updateService;
    private GoogleMap gMap;
    private FragmentTabHost mTabHost;
    private ProgressDialog mProgressDialog;
    private GameDropdownAdapter mGameDropdownAdapter;
    private Integer mTerritoryId = 0;
    private LocationClient mLocationClient;
    private List<TerritoryMarker> mTerritoryMarkerList;
    private HashMap<String, String> mMarkerIdUrlMap;
    private int mTerritoryMarkerListIndex = 0;
    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.v("game", "on service connected");
            updateService = ((LocationUpdateService.LocationUpdateBinder) service).getService();
            LocationUpdateService.setServiceClient(GameActivity.this);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(GameActivity.this);
            if (prefs.getBoolean(PreferenceScreenActivity.PREF_KEY_BACKGROUND, true)) {
                updateService.startUpdate();
            } else {
                updateService.stopUpdate();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v("game", "on service disconnected");
            updateService = null;
        }

    };

    public static Intent createIntent(User user) {
        return new Intents.Builder("game.VIEW").user(user).toIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent();
                intent.setClass(GameActivity.this,
                        PreferenceScreenActivity.class);
                startActivity(intent);
                break;
            case R.id.action_reload:
                refreshMap();
                break;
            case R.id.action_back:
                backTerritory();
                break;
            case R.id.action_forward:
                forwardTerritory();
                break;
            default:
                break;
        }
        return false;
    }

    private void configureActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        final GameDropdownData item = new GameDropdownData();

        final GameDropdownData item2 = new GameDropdownData();
        item2.setTitle("ロケーション履歴");
        item2.setImage(android.R.drawable.ic_menu_recent_history);

        final GameDropdownData item3 = new GameDropdownData();
        item3.setTitle("アバター設定");
        item3.setImage(android.R.drawable.ic_menu_crop);

        mGameDropdownAdapter = new GameDropdownAdapter(this);
        actionBar.setListNavigationCallbacks(mGameDropdownAdapter, new ActionBar.OnNavigationListener() {

            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                switch (itemPosition) {
                    case 1:
                        startActivity(LocationHistoryActivity.createIntent(user));
                        getSupportActionBar().setSelectedNavigationItem(0);
                        break;
                    case 2:
                        startActivity(SetAvatarActivity.createIntent(user));
                        getSupportActionBar().setSelectedNavigationItem(0);
                        break;
                }
                return true;
            }

        });

        LbClient client = new LbClient();
        client.setToken(Session.getToken());
        client.getUser(new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                item.setTitle(user.getName());
                item.setMessage("Lv" + user.getLevel() + " (" + user.getExp() + "/100)");
                item.setGpsPoint(user.getGpsPoint());
                item.setGpsPointMax(user.getGpsPointLimit());
                item.setImage(R.drawable.ic_launcher);
                item.setAvatar(user.getAvatar());

                mGameDropdownAdapter.add(item);
                mGameDropdownAdapter.add(item2);
                mGameDropdownAdapter.add(item3);
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("game", "life create");
        setContentView(R.layout.activity_game);

        user = (User) getIntent().getExtras().getSerializable(EXTRA_USER);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.content);

        addTab("tab1", "マップ", MapFragment.class);
        addTab("tab2", "テリトリー", TerritoryListFragment.class);
        addTab("tab3", "通知", NotificationListFragment.class);

        mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                getCurrentFragment().clearBackStack();
            }
        });
        startAndBindService();
    }

    @Override
    public void onStart() {
        super.onStart();
        startAndBindService();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("game", "life resume");
        configureActionBar();
        startAndBindService();
    }

    @Override
    public void onPause() {
        Log.v("game", "life pause");
        stopAndUnBindService();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.v("game", "life destroy");
        stopAndUnBindService();
        super.onDestroy();
    }

    private void startAndBindService() {
        serviceIntent = new Intent(getActivity(), LocationUpdateService.class);
        getActivity().startService(serviceIntent);
        getActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        Session.setIsBound(true);
    }

    private void stopAndUnBindService() {
        if (Session.getIsBound()) {
            Log.v("game", "unbind");
            getActivity().unbindService(serviceConnection);
            Session.setIsBound(false);
        }

        if (!Session.getIsStarted()) {
            Log.v("game", "stop");
            getActivity().stopService(serviceIntent);
        }
    }

    @Override
    public void onLocationUpdate(com.lb.api.Location location) {
        /*
        try {
			user = Utils.updateSessionUserInfo(UserGen.get(json.getJSONObject("user").toString()));
			refreshGameDropdownAdapter();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JsonFormatException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		*/
    }

    @Override
    public void onStopLogging() {

    }

    @Override
    public Activity getActivity() {
        return this;
    }

    public void refreshMap() {
        if (gMap != null) {
            gMap.clear();
            if (mTerritoryMarkerList == null)
                mTerritoryMarkerList = new ArrayList<TerritoryMarker>();
            mTerritoryMarkerList.clear();
            if (mMarkerIdUrlMap == null) mMarkerIdUrlMap = new HashMap<String, String>();
            mMarkerIdUrlMap.clear();

            // テリトリーを表示
            /*
			API.getUserTerritories(Session.getUser(), new JsonHttpResponseHandler() {
				
				@Override
				public void onStart() {
					if (mProgressDialog == null) mProgressDialog = Utils.createProgressDialog(GameActivity.this);
					mProgressDialog.show();
				}
				
				@Override
				public void onSuccess(JSONArray jsonArray) {
					List<Territory> territories = new ArrayList<Territory>();

					try {
						territories = TerritoryGen.getList(jsonArray.toString());
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (JsonFormatException e1) {
						e1.printStackTrace();
					}
					
					for(int i = 0; i < territories.size(); i ++) {
						Territory territory = territories.get(i);
						TerritoryMarker territoryMarker = new TerritoryMarker();
						territoryMarker.setCenter(new LatLng(territory.getLatitude(), territory.getLongitude()));
						territoryMarker.setRadius(territory.getRadius());
						if (Utils.parseStringToDate(territory.getExpirationDate()).compareTo(Calendar.getInstance().getTime()) <= 0) {
							territoryMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
							territoryMarker.setColor(50, 50, 50);
						}else{
							territoryMarker.setColor(0, 255, 0);
						}
						territoryMarker.setTitle("territory_" + territory.getId());
						territoryMarker.setSnippet(territory.getDetectionCount() + "人発見しました");
						territoryMarker.addTo(gMap);
						if(territory.getId() == mTerritoryId) {
							territoryMarker.showInfoWindow();
							mTerritoryId = 0;
						}
						
						mTerritoryMarkerList.add(territoryMarker);
                        mMarkerIdUrlMap.put(territoryMarker.getMarkerId(), "http://placekitten.com/48/48"); // TODO
					}
				}

				@Override
				public void onFailure(Throwable throwable) {
					Log.i("game","getUserTerritoryListOnFailure="+ throwable);
				}
				
				@Override
				public void onFinish() {
					mProgressDialog.dismiss();
				}
			});
		*/
        }
    }

    private class TerritoryInfoWindowAdapter implements InfoWindowAdapter {
        private final View mWindow;

        public TerritoryInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.territory_info_window, null);
        }

        @Override
        public View getInfoContents(Marker marker) {
            showInfoWindow(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        private void showInfoWindow(Marker marker, View v) {
            ImageView avatar = (ImageView) v.findViewById(R.id.iv_avatar);

            if (mMarkerIdUrlMap != null)
                Picasso.with(GameActivity.this).load(mMarkerIdUrlMap.get(marker.getId())).into(avatar);

            TextView title = (TextView) v.findViewById(R.id.title);
            title.setText(marker.getTitle());

            //TextView snippet = (TextView) v.findViewById(R.id.snippet);
            //snippet.setText(marker.getSnippet());
        }
    }

    @Override
    public void onMapReady(GoogleMap map, final View v) {
        if (gMap == null && map != null) {
            gMap = map;


            // マーカーに吹き出しつける
            gMap.setInfoWindowAdapter(new TerritoryInfoWindowAdapter());

            // 吹き出しクリックした時
            gMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

                @Override
                public void onInfoWindowClick(Marker marker) {

                }

            });

            gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    Log.i("dump", "center="+gMap.getProjection().getVisibleRegion().latLngBounds.getCenter());
                    Log.i("dump", "ne="+gMap.getProjection().getVisibleRegion().latLngBounds.northeast);
                    Log.i("dump", "sw="+gMap.getProjection().getVisibleRegion().latLngBounds.southwest);
                }
            });

            refreshMap();

            /*// ノーティフィケーションから来た時
            Intent intent = getIntent();
            Integer notificationId = intent.getIntExtra("notification_id", 0);
            Double latitude = intent.getDoubleExtra("latitude", 35.0);
            Double longitude = intent.getDoubleExtra("longitude", 135.8);
            String title = intent.getStringExtra("title");
            String message = intent.getStringExtra("message");
            Integer type = intent.getIntExtra("notification_type", NotificationData.TYPE_DETECTED);
            boolean read = intent.getBooleanExtra("read", true);
            Integer territoryId = intent.getIntExtra("territory_id", 0);
            if (notificationId > 0)
                onClickNotificationListItem(notificationId, latitude, longitude, type, title, message, read, territoryId);
                */
        }
    }

    @Override
    public void onBackPressed() {
        if (!getCurrentFragment().popBackStack()) {
            // タブ内FragmentのBackStackがない場合は何もしない
        }
    }

    /**
     * タブ追加.
     *
     * @param tag      タグ
     * @param indector タブウィジェット表示ラベル
     * @param clazz    Fragmentクラス
     */
    private void addTab(String tag, String indector, Class<? extends Fragment> clazz) {

        TabSpec tabSpec = mTabHost.newTabSpec(tag).setIndicator(indector);

        // TabRootFragmentに渡すことでクラス名から初期Fragmentを決定
        Bundle args = new Bundle();
        args.putString("root", clazz.getName());

        mTabHost.addTab(tabSpec, TabRootFragment.class, args);

    }

    /**
     * カレントFragment取得.
     *
     * @return TabRootFragment
     */
    private TabRootFragment getCurrentFragment() {
        return (TabRootFragment) getSupportFragmentManager().findFragmentById(R.id.content);
    }

    @Override
    public void onClickShowTerritoryButton(Double latitude, Double longitude) {
        mTabHost.setCurrentTabByTag("tab1");
        refreshMap();
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12));
    }
/*
    @Override
    public void onClickNotificationListItem(Integer id, Double latitude, Double longitude, Integer type, String title, String message, boolean read, Integer territoryId) {
        mTabHost.setCurrentTabByTag("tab1");

        mTerritoryId = territoryId;
        refreshMap();

        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12));

        if (type == NotificationData.TYPE_DETECTED) {
            MarkerOptions markerOpt = new MarkerOptions();
            markerOpt.title(title);
            markerOpt.snippet(message);
            markerOpt.position(new LatLng(latitude, longitude));
            //BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(android.R.drawable.ic_dialog_info);
            BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
            markerOpt.icon(icon);
            Marker marker = gMap.addMarker(markerOpt);
            marker.showInfoWindow();
        }

        // 既読にする
//		if(!read) API.readNotification(Session.getUser(), id, null);
    }
*/
    @Override
    public void onSupply() {
        refreshGameDropdownAdapter();
    }

    private void refreshGameDropdownAdapter() {
        /*
		if(mGameDropdownAdapter != null) {
			mGameDropdownAdapter.refreshGpsPoint(0, user.getGpsPoint(), user.getGpsPointLimit());
			mGameDropdownAdapter.notifyDataSetChanged();
		}
		*/
    }

    private void forwardTerritory() {
        if (gMap != null && mTerritoryMarkerList != null && mTerritoryMarkerList.size() > 0) {
            TerritoryMarker tm = mTerritoryMarkerList.get(mTerritoryMarkerListIndex);
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(tm.getCenter(), 12));
            mTerritoryMarkerListIndex++;
            if (mTerritoryMarkerListIndex >= mTerritoryMarkerList.size())
                mTerritoryMarkerListIndex = 0;
        }
    }

    private void backTerritory() {
        if (gMap != null && mTerritoryMarkerList != null && mTerritoryMarkerList.size() > 0) {
            TerritoryMarker tm = mTerritoryMarkerList.get(mTerritoryMarkerListIndex);
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(tm.getCenter(), 12));
            mTerritoryMarkerListIndex--;
            if (mTerritoryMarkerListIndex < 0)
                mTerritoryMarkerListIndex = mTerritoryMarkerList.size() - 1;
        }

    }
}
