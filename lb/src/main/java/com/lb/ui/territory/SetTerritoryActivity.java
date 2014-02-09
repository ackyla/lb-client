package com.lb.ui.territory;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import static com.lb.Intents.EXTRA_TERRITORY;
import com.lb.Intents;
import com.lb.R;
import com.lb.api.Territory;
import com.lb.api.User;
import com.lb.model.Session;
import com.lb.model.Utils;
import com.lb.core.territory.TerritoryMarker;
import com.lb.ui.user.MapFragment;
import com.lb.ui.user.MapFragment.OnGoogleMapFragmentListener;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class SetTerritoryActivity extends ActionBarActivity {
    private GoogleMap gMap;
    private TerritoryMarker mTerritoryMarker;
    private Circle mDistance;
    private ProgressDialog mProgressDialog;
    private AlertDialog mSelectDialog;
    private Character mCharacter;
    private LocationClient mLocationClient;

    public static Intent createIntent() {
        return new Intents.Builder("set.territory.VIEW").toIntent();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_territory);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new SetTerritoryMapFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
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

    public void finish(Territory territory) {
        Intent data = new Intent();
        data.putExtra(EXTRA_TERRITORY, territory);
        setResult(RESULT_OK, data);
        finish();
    }

/*
    @Override
    public void onResume() {
        super.onResume();

        if (mSelectDialog == null) {
            LinearLayout v = (LinearLayout) getLayoutInflater().inflate(R.layout.character_list_dialog, null);
            Button bt = (Button) v.findViewById(R.id.bt_cancel);
            bt.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mSelectDialog != null) {
                        finish();
                        mSelectDialog.cancel();
                    }
                }

            });

            AlertDialog.Builder builder = new AlertDialog.Builder(SetTerritoryActivity.this);
            builder.setTitle("設置するテリトリーを選んで下さい");
            builder.setMessage("残り陣力: " + Session.getUser().getGpsPoint());
            builder.setView(v);
            builder.setCancelable(false);
            builder.setNegativeButton("キャンセル", new AlertDialog.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
					dialog.cancel();
				}
				
			});

            mSelectDialog = builder.create();
            mSelectDialog.show();
        }
    }

    @Override
    public void onMapReady(GoogleMap map, final View v) {
        if (gMap == null && map != null) {
            v.setVisibility(View.INVISIBLE);
            gMap = map;
            gMap.setMyLocationEnabled(true);
            UiSettings settings = gMap.getUiSettings();
            settings.setMyLocationButtonEnabled(true);

            // カメラを現在位置にフォーカスする
            mLocationClient = new LocationClient(getApplicationContext(), new ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle bundle) {
                    Location location = mLocationClient.getLastLocation();
                    LatLng latlng = Utils.getDefaultLatLng();
                    if (location != null)
                        latlng = new LatLng(location.getLatitude(), location.getLongitude());
                    mTerritoryMarker = addTerritory(latlng, 100.0, BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    mDistance = addDistance(latlng, 10000.0);
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 12));
                    v.setVisibility(View.VISIBLE);
                    mLocationClient.disconnect();
                }

                @Override
                public void onDisconnected() {
                }
            }, new OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(ConnectionResult result) {
                    v.setVisibility(View.VISIBLE);
                }
            }
            );
            mLocationClient.connect();

            // テリトリーを表示
			API.getUserTerritories(Session.getUser(), new JsonHttpResponseHandler() {
				@Override
				public void onStart() {
					if (mProgressDialog == null) mProgressDialog = Utils.createProgressDialog(SetTerritoryActivity.this);
					mProgressDialog.show();
				}
				
				@Override
				public void onSuccess(JSONArray jsonArray) {
					for(int i = 0; i < jsonArray.length(); i ++) {
						try {
							JSONObject json = jsonArray.getJSONObject(i);
							addTerritory(new LatLng(json.getDouble("latitude"), json.getDouble("longitude")), json.getDouble("radius"), BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
						} catch (JSONException e) {
							e.printStackTrace();
						}
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

            // 設置場所移動
            gMap.setOnCameraChangeListener(new OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition pos) {
                    if (mTerritoryMarker != null) mTerritoryMarker.updateCenter(pos.target);
                }
            });

            // テリトリー設置
            Button button = (Button) findViewById(R.id.territory_set_button);
            button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
					AlertDialog.Builder builder = new AlertDialog.Builder(SetTerritoryActivity.this);
					builder.setTitle("テリトリーを設置します");
					builder.setMessage("消費陣力: " + mCharacter.getCost());
					builder.setPositiveButton("する", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							API.createTerritory(Session.getUser(), mTerritoryMarker.getCenter().latitude, mTerritoryMarker.getCenter().longitude, mCharacter.getId(), new JsonHttpResponseHandler() {
								@Override
								public void onStart() {
									if (mProgressDialog == null) mProgressDialog = Utils.createProgressDialog(SetTerritoryActivity.this);
									mProgressDialog.show();
								}
								
								
								@Override
								public void onSuccess(JSONObject json) {
									try {
										Utils.updateSessionUserInfo(UserGen.get(json.getJSONObject("user").toString()));
										addTerritory(mTerritoryMarker.getCenter(), mTerritoryMarker.getRadius(), BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
									} catch (IOException e) {
										e.printStackTrace();
									} catch (JsonFormatException e) {
										e.printStackTrace();
									} catch (JSONException e) {
										e.printStackTrace();
									}
									Toast.makeText(SetTerritoryActivity.this, "テリトリーを設置しました", Toast.LENGTH_LONG).show();
									
									mSelectDialog.setMessage("残り陣力: " + Session.getUser().getGpsPoint());
									mSelectDialog.show();
								}

								@Override
								public void onFailure(Throwable throwable) {
									Toast.makeText(SetTerritoryActivity.this, "テリトリーを設置できませんでした", Toast.LENGTH_LONG).show();
									Log.v("game","postHitLocationOnFailure="+ throwable);
								}
								
								
								@Override
								public void onFinish() {
									mProgressDialog.dismiss();
								}
							});
						}
					});

					builder.setNegativeButton("しない", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					});
					builder.create().show();
                }

            });
        }
    }

    public TerritoryMarker addTerritory(LatLng latlng, Double radius, BitmapDescriptor icon) {
        TerritoryMarker tm = new TerritoryMarker();
        tm.setIcon(icon);
        tm.setCenter(latlng);
        tm.setRadius(radius);
        tm.setColor(0, 255, 0);
        tm.addTo(gMap);
        return tm;
		CircleOptions circleOptions = new CircleOptions();
		circleOptions.center(latlng);
		circleOptions.strokeWidth(5);
		circleOptions.radius(radius);
		circleOptions.strokeColor(Color.argb(200, 0, 255, 0));
		circleOptions.fillColor(Color.argb(50, 0, 255, 0));
		return gMap.addCircle(circleOptions);
    }

    public Circle addDistance(LatLng latlng, Double radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latlng);
        circleOptions.strokeWidth(2);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(200, 0, 255, 255));
        circleOptions.fillColor(Color.argb(50, 0, 255, 255));
        return gMap.addCircle(circleOptions);
    }

    @Override
    public void onClickCharacterListItem(Character character) {
		if(character.getCost() > Session.getUser().getGpsPoint()) {
			Toast.makeText(SetTerritoryActivity.this, "陣力が足りません", Toast.LENGTH_LONG).show();
		}else{
			mCharacter = character;
			mTerritoryMarker.updateRadius(character.getRadius());
			mDistance.setRadius(character.getDistance());
			mSelectDialog.cancel();
		}
    }
    */
}
