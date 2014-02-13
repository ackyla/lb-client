package com.lb.ui.territory;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.lb.R;
import com.lb.api.Character;
import com.lb.api.Territory;
import com.lb.api.User;
import com.lb.api.client.LbClient;
import com.lb.core.character.DistanceMarker;
import com.lb.core.territory.TerritoryMarker;
import com.lb.model.Session;
import com.lb.model.Utils;
import com.lb.ui.MapFragment;
import com.lb.ui.character.CharacterDialogFragment;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class SetTerritoryMapFragment extends MapFragment implements DialogInterface.OnClickListener, CharacterDialogFragment.OnItemClickListener {

    private GoogleMap gMap;
    private Character character;
    private TerritoryMarker territoryMarker;
    private DistanceMarker distanceMarker;
    private View characterView;
    private ImageView ivAvatar;
    private TextView tvName;

    public interface OnSuccessListener {
        void onSuccessSetTerritory(Territory territory);
    }

    public SetTerritoryMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.character_view, null);
        characterView = ll.findViewById(R.id.ll_character);
        ivAvatar = (ImageView) ll.findViewById(R.id.iv_avatar);
        tvName = (TextView) ll.findViewById(R.id.tv_name);

        characterView.setVisibility(View.GONE);
        characterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharacterDialogFragment.show(SetTerritoryMapFragment.this);
            }
        });

        initMap();
        gMap = getMap();
        if (gMap != null) {
            gMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    if (territoryMarker != null) territoryMarker.updateCenter(cameraPosition.target);
                }
            });
        }

        CharacterDialogFragment.show(this);

        ll.addView(v);
        return ll;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_set:
                if (Utils.getDistance(territoryMarker.getCenter(), distanceMarker.getCenter()) > distanceMarker.getRadius()) {
                    Toast.makeText(getActivity(), R.string.message_out_of_distance, Toast.LENGTH_SHORT).show();
                    return true;
                }
                showSetDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setMarker(Character character) {
        LatLng latLng = getMyLocation();

        if (gMap != null && latLng != null) {
            gMap.clear();
            distanceMarker = character.getDistanceMarker(latLng);
            distanceMarker.addTo(gMap);
            territoryMarker = character.getTerritoryMarker(gMap.getCameraPosition().target);
            territoryMarker.addTo(gMap);

            characterView.setVisibility(View.VISIBLE);
            tvName.setText(character.getName());
            Picasso.with(getActivity()).load("http://placekitten.com/48/48").into(ivAvatar);

            this.character = character;
        }
    }

    private void showSetDialog() {
        final ProgressDialog progress = Utils.createProgressDialog(getActivity());
        LbClient client = new LbClient();
        client.setToken(Session.getToken());
        client.getUser(new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                progress.dismiss();
                View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_set_territory_view, null);
                TextView tvCost = (TextView) v.findViewById(R.id.tv_cost);
                TextView tvGpsPointFrom = (TextView) v.findViewById(R.id.tv_gps_point_from);
                TextView tvGpsPointTo = (TextView) v.findViewById(R.id.tv_gps_point_to);
                TextView tvMessageInsufficientGpsPoint = (TextView) v.findViewById(R.id.tv_message_insufficient_point);
                tvCost.setText(Integer.toString(character.getCost()));
                tvGpsPointFrom.setText(Integer.toString(user.getGpsPoint()));
                tvGpsPointTo.setText(Integer.toString(user.getGpsPoint()-character.getCost()));
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                if (user.getGpsPoint() - character.getCost() < 0) {
                    tvGpsPointTo.setTextColor(getResources().getColor(R.color.dangerColor));
                    tvMessageInsufficientGpsPoint.setVisibility(View.VISIBLE);
                    builder.setNegativeButton(R.string.cancel, null)
                            .setTitle(R.string.dialog_title_set_territory)
                            .setView(v)
                            .create().show();
                } else {
                    builder.setNegativeButton(R.string.cancel, null)
                            .setPositiveButton(R.string.apply, SetTerritoryMapFragment.this)
                            .setTitle(R.string.dialog_title_set_territory)
                            .setView(v)
                            .create().show();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                progress.dismiss();
                Toast.makeText(getActivity(), R.string.message_network_error, Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public void onItemClick(AlertDialog dialog, Character character) {
        setMarker(character);
        dialog.dismiss();
    }

    @Override
    public void onClick(final DialogInterface dialog, int which) {
        if (which == BUTTON_POSITIVE) {
            final ProgressDialog progress = Utils.createProgressDialog(getActivity());

            LbClient client = new LbClient();
            client.setToken(Session.getToken());
            client.createTerritory(territoryMarker.getCenter(), character.getId(), new Callback<Territory>() {
                @Override
                public void success(Territory territory, Response response) {
                    Toast.makeText(getActivity(), R.string.message_set_territory, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    progress.dismiss();
                    OnSuccessListener listener = (OnSuccessListener) getActivity();
                    listener.onSuccessSetTerritory(territory);
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    Toast.makeText(getActivity(), R.string.message_failure_set_territory, Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }
            });
        }
    }
}
