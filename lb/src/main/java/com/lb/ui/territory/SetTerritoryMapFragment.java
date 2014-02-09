package com.lb.ui.territory;

import static android.app.AlertDialog.BUTTON_POSITIVE;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.lb.R;
import com.lb.api.Character;
import com.lb.api.Territory;
import com.lb.api.client.LbClient;
import com.lb.core.character.DistanceMarker;
import com.lb.core.territory.TerritoryMarker;
import com.lb.model.Session;
import com.lb.model.Utils;
import com.lb.ui.character.CharacterDialogFragment;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.lb.Intents.EXTRA_TERRITORY;

public class SetTerritoryMapFragment extends SupportMapFragment implements CharacterDialogFragment.OnItemClickListener, DialogInterface.OnClickListener {

    private LocationClient locationClient;
    private DistanceMarker distanceMarker;
    private TerritoryMarker territoryMarker;

    private Character character;
    private View characterView;
    private ImageView ivAvatar;
    private TextView tvName;

    public SetTerritoryMapFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        characterView = getActivity().findViewById(R.id.ll_character);
        ivAvatar = (ImageView) getActivity().findViewById(R.id.iv_avatar);
        tvName = (TextView) getActivity().findViewById(R.id.tv_name);

        characterView.setVisibility(View.GONE);
        characterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharacterDialogFragment.show(getActivity(), SetTerritoryMapFragment.this);
            }
        });

        CharacterDialogFragment.show(getActivity(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = super.onCreateView(inflater, container, savedInstanceState);

        final GoogleMap gMap = getMap();

        if (gMap != null) {
            v.setVisibility(View.INVISIBLE);
            gMap.setMyLocationEnabled(true);
            UiSettings settings = gMap.getUiSettings();
            settings.setMyLocationButtonEnabled(true);

            locationClient = new LocationClient(getActivity(), new GooglePlayServicesClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle bundle) {
                    Location location = locationClient.getLastLocation();
                    LatLng latlng = Utils.getDefaultLatLng();
                    if (location != null)
                        latlng = new LatLng(location.getLatitude(), location.getLongitude());
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 12));
                    distanceMarker = new DistanceMarker(latlng);
                    distanceMarker.addTo(gMap).hide();
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

            territoryMarker = new TerritoryMarker(Utils.getDefaultLatLng());
            territoryMarker.addTo(gMap);
            territoryMarker.hide();

            gMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    territoryMarker.updateCenter(cameraPosition.target);
                }
            });

            v.setVisibility(View.VISIBLE);
        }

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.territory_set, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_set:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.apply, this)
                        .setTitle("ここにテリトリーを設置しますか？")
                        .create().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AlertDialog dialog, Character character) {
        this.character = character;
        tvName.setText(character.getName());
        Picasso.with(getActivity()).load("http://placekitten.com/48/48").into(ivAvatar);
        characterView.setVisibility(View.VISIBLE);
        distanceMarker.updateRadius(character.getDistance());
        distanceMarker.show();
        territoryMarker.updateRadius(character.getRadius());
        territoryMarker.show();
        dialog.dismiss();
    }

    @Override
    public void onClick(final DialogInterface dialog, int which) {
        if (which == BUTTON_POSITIVE) {
            LbClient client = new LbClient();
            client.setToken(Session.getToken());
            client.createTerritory(territoryMarker.getCenter(), character.getId(), new Callback<Territory>() {
                @Override
                public void success(Territory territory, Response response) {
                    Toast.makeText(getActivity(), "テリトリーを設置しました", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

                @Override
                public void failure(RetrofitError retrofitError) {

                }
            });
        }
    }
}
