package com.lb.ui.territory;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;

import static com.lb.Intents.EXTRA_TERRITORY;

import com.google.android.gms.maps.model.LatLng;
import com.lb.Intents;
import com.lb.R;
import com.lb.api.*;
import com.lb.api.Character;
import com.lb.api.client.LbClient;
import com.lb.core.character.DistanceMarker;
import com.lb.core.territory.TerritoryMarker;
import com.lb.model.Session;
import com.lb.model.Utils;
import com.lb.ui.character.CharacterDialogFragment;
import com.lb.ui.user.MapFragment;
import com.squareup.picasso.Picasso;

import static android.app.AlertDialog.BUTTON_POSITIVE;
import static com.lb.Intents.EXTRA_USER;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.*;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalPosition;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SetTerritoryActivity extends ActionBarActivity implements MapFragment.OnGoogleMapFragmentListener, CharacterDialogFragment.OnItemClickListener, DialogInterface.OnClickListener {

    private User user;
    private GoogleMap gMap;
    private LocationClient locationClient;
    private DistanceMarker distanceMarker;
    private TerritoryMarker territoryMarker;
    private LatLng currentLocation;

    private Character character;
    private View characterView;
    private ImageView ivAvatar;
    private TextView tvName;

    public static Intent createIntent(User user) {
        return new Intents.Builder("set.territory.VIEW").user(user).toIntent();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_territory);

        user = (User) getIntent().getExtras().getSerializable(EXTRA_USER);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.title_activity_set_territory);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MapFragment())
                    .commit();
        }

        characterView = findViewById(R.id.ll_character);
        ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
        tvName = (TextView) findViewById(R.id.tv_name);

        characterView.setVisibility(View.GONE);
        characterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharacterDialogFragment.show(SetTerritoryActivity.this);
            }
        });

        CharacterDialogFragment.show(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.territory_set, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_set:
                if (Utils.getDistance(territoryMarker.getCenter(), distanceMarker.getCenter()) > distanceMarker.getRadius()) {
                    Toast.makeText(this, R.string.message_out_of_distance, Toast.LENGTH_SHORT).show();
                    return true;
                }
                showSetDialog();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap map, final View v) {
        gMap = map;

        if (gMap != null) {
            v.setVisibility(View.INVISIBLE);
            gMap.setMyLocationEnabled(true);
            UiSettings settings = gMap.getUiSettings();
            settings.setMyLocationButtonEnabled(true);

            locationClient = new LocationClient(this, new GooglePlayServicesClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle bundle) {
                    android.location.Location location = locationClient.getLastLocation();
                    LatLng latlng = Utils.getDefaultLatLng();
                    if (location != null)
                        latlng = new LatLng(location.getLatitude(), location.getLongitude());
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 12));
                    currentLocation = latlng;
                    v.setVisibility(View.VISIBLE);
                    locationClient.disconnect();
                }

                @Override
                public void onDisconnected() {
                }
            }, new GooglePlayServicesClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(ConnectionResult result) {
                    v.setVisibility(View.VISIBLE);
                }
            });
            locationClient.connect();

            gMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    if (territoryMarker != null) territoryMarker.updateCenter(cameraPosition.target);
                 }
            });

            v.setVisibility(View.VISIBLE);
        }
    }

    public void finish(Territory territory) {
        Intent data = new Intent();
        data.putExtra(EXTRA_TERRITORY, territory);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onItemClick(AlertDialog dialog, Character character) {
        this.character = character;
        tvName.setText(character.getName());
        Picasso.with(this).load("http://placekitten.com/48/48").into(ivAvatar);
        characterView.setVisibility(View.VISIBLE);

        if (gMap != null) {
            gMap.clear();
            distanceMarker = character.getDistanceMarker(currentLocation);
            distanceMarker.addTo(gMap);
            territoryMarker = character.getTerritoryMarker(currentLocation);
            territoryMarker.addTo(gMap);
            dialog.dismiss();
        }
    }

    @Override
    public void onClick(final DialogInterface dialog, int which) {
        if (which == BUTTON_POSITIVE) {
            final ProgressDialog progress = Utils.createProgressDialog(this);

            LbClient client = new LbClient();
            client.setToken(Session.getToken());
            client.createTerritory(territoryMarker.getCenter(), character.getId(), new Callback<Territory>() {
                @Override
                public void success(Territory territory, Response response) {
                    Toast.makeText(SetTerritoryActivity.this, R.string.message_set_territory, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    progress.dismiss();
                    finish(territory);
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    Toast.makeText(SetTerritoryActivity.this, R.string.message_failure_set_territory, Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }
            });
        }
    }

    private void showSetDialog() {
        final ProgressDialog progress = Utils.createProgressDialog(this);
        LbClient client = new LbClient();
        client.setToken(Session.getToken());
        client.getUser(new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                progress.dismiss();
                View v = getLayoutInflater().inflate(R.layout.dialog_set_territory_view, null);
                TextView tvCost = (TextView) v.findViewById(R.id.tv_cost);
                TextView tvGpsPointFrom = (TextView) v.findViewById(R.id.tv_gps_point_from);
                TextView tvGpsPointTo = (TextView) v.findViewById(R.id.tv_gps_point_to);
                TextView tvMessageInsufficientGpsPoint = (TextView) v.findViewById(R.id.tv_message_insufficient_point);
                tvCost.setText(Integer.toString(character.getCost()));
                tvGpsPointFrom.setText(Integer.toString(user.getGpsPoint()));
                tvGpsPointTo.setText(Integer.toString(user.getGpsPoint()-character.getCost()));
                AlertDialog.Builder builder = new AlertDialog.Builder(SetTerritoryActivity.this);

                if (user.getGpsPoint() - character.getCost() < 0) {
                    tvGpsPointTo.setTextColor(getResources().getColor(R.color.dangerColor));
                    tvMessageInsufficientGpsPoint.setVisibility(View.VISIBLE);
                    builder.setNegativeButton(R.string.cancel, null)
                            .setTitle(R.string.dialog_title_set_territory)
                            .setView(v)
                            .create().show();
                } else {
                    builder.setNegativeButton(R.string.cancel, null)
                            .setPositiveButton(R.string.apply, SetTerritoryActivity.this)
                            .setTitle(R.string.dialog_title_set_territory)
                            .setView(v)
                            .create().show();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                progress.dismiss();
                Toast.makeText(SetTerritoryActivity.this, R.string.message_network_error, Toast.LENGTH_SHORT);
            }
        });
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
