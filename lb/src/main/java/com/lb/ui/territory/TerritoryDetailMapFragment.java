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

import static com.lb.api.client.ILbConstants.MINUTES_PER_POINT;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.lb.R;
import com.lb.api.*;
import com.lb.api.client.LbClient;
import com.lb.core.character.DistanceMarker;
import com.lb.core.territory.TerritoryMarker;
import com.lb.model.Session;
import com.lb.model.Utils;
import com.lb.ui.MapFragment;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TerritoryDetailMapFragment extends MapFragment {
    private static final int  SUPPLY_POINT = 5;
    private static final String ARG_TERRITORY = "territory";

    private Territory territory;
    private GoogleMap gMap;
    private Menu menu;
    private TerritoryMarker territoryMarker;
    private DistanceMarker distanceMarker;
    private View territoryView;
    private ImageView ivAvatar;
    private TextView tvName;

    public static TerritoryDetailMapFragment newInstance(Territory territory) {
        TerritoryDetailMapFragment fragment = new TerritoryDetailMapFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TERRITORY, territory);
        fragment.setArguments(args);
        return fragment;
    }

    public TerritoryDetailMapFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        territory = (Territory) getArguments().getSerializable(ARG_TERRITORY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.territory_view, null);
        territoryView = ll.findViewById(R.id.ll_territory);
        ivAvatar = (ImageView) ll.findViewById(R.id.iv_avatar);
        tvName = (TextView) ll.findViewById(R.id.tv_name);

        territoryView.setOnClickListener(null);

        initMap(territory.getCoordinate());
        gMap = getMap();

        refresh();

        ll.addView(v);
        return ll;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.territory_detail, menu);
        this.menu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_supply:
                showSupplyDialog();
                return true;
            case R.id.action_move:
                move();
                return true;
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

    private void showSetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setNegativeButton(R.string.cancel, null)
                .setNeutralButton(R.string.clear, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        toggleSetMenu();
                        initMap(territory.getCoordinate());
                    }
                })
                .setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        set();
                        dialog.dismiss();
                        toggleSetMenu();
                    }
                })
                .setTitle(R.string.dialog_title_move)
                .create().show();

    }

    private void toggleSetMenu() {
        if (menu.findItem(R.id.action_set).isVisible()) {
            menu.findItem(R.id.action_supply).setVisible(true);
            menu.findItem(R.id.action_move).setVisible(true);
            menu.findItem(R.id.action_set).setVisible(false);
        } else {
            menu.findItem(R.id.action_supply).setVisible(false);
            menu.findItem(R.id.action_move).setVisible(false);
            menu.findItem(R.id.action_set).setVisible(true);
        }
    }

    private void set() {
        final ProgressDialog progress = Utils.createProgressDialog(getActivity());
        LatLng latLng = territoryMarker.getCenter();

        LbClient client = new LbClient();
        client.setToken(Session.getToken());
        client.moveTerritory(territory.getId(), latLng.latitude, latLng.longitude, new Callback<Territory>() {
            @Override
            public void success(Territory movedTerritory, Response response) {
                territory = movedTerritory;
                Toast.makeText(getActivity(), R.string.message_move, Toast.LENGTH_SHORT).show();
                progress.dismiss();
                initMap(territory.getCoordinate());
                refresh();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(getActivity(), R.string.message_network_error, Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        });
    }

    private void move() {
        if (territory.isExpired()) {
            Toast.makeText(getActivity(), getString(R.string.message_cant_move), Toast.LENGTH_SHORT).show();
            return;
        }

        if (gMap != null) {
            Toast.makeText(getActivity(), getString(R.string.message_begin_move), Toast.LENGTH_SHORT).show();
            LatLng latLng = territory.getCoordinate().getLatLng();
            gMap.clear();
            distanceMarker = territory.getCharacter().getDistanceMarker(latLng);
            distanceMarker.addTo(gMap);
            territoryMarker = territory.getMarker();
            territoryMarker.addTo(gMap);
            gMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    if (territoryMarker != null) territoryMarker.updateCenter(cameraPosition.target);
                }
            });

            toggleSetMenu();
        }
    }

    private void showSupplyDialog() {
        final ProgressDialog progress = Utils.createProgressDialog(getActivity());
        LbClient client = new LbClient();
        client.setToken(Session.getToken());
        client.getUser(new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                progress.dismiss();

                View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_supply_view, null);
                TextView tvCost = (TextView) v.findViewById(R.id.tv_cost);
                TextView tvGpsPointFrom = (TextView) v.findViewById(R.id.tv_gps_point_from);
                TextView tvGpsPointTo = (TextView) v.findViewById(R.id.tv_gps_point_to);
                TextView tvExpirationDateFrom = (TextView) v.findViewById(R.id.tv_expiration_date_from);
                TextView tvExpirationDateTo = (TextView) v.findViewById(R.id.tv_expiration_date_to);
                TextView tvMessageInsufficientGpsPoint = (TextView) v.findViewById(R.id.tv_message_insufficient_point);

                tvCost.setText(Integer.toString(SUPPLY_POINT));
                tvGpsPointFrom.setText(Integer.toString(user.getGpsPoint()));
                tvGpsPointTo.setText(Integer.toString(user.getGpsPoint() - SUPPLY_POINT));

                String expirationDate = territory.isExpired() ? getString(R.string.expired) : territory.getRelativeTimeSpanString();
                tvExpirationDateFrom.setText(expirationDate);

                Calendar cal = Calendar.getInstance();
                if (!territory.isExpired()) cal.setTime(territory.getExpirationDate());
                cal.add(Calendar.MINUTE, MINUTES_PER_POINT*SUPPLY_POINT);
                tvExpirationDateTo.setText(Utils.getRelativeTimeSpanString(cal.getTime()));

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                if (user.getGpsPoint() - SUPPLY_POINT < 0) {
                    tvGpsPointTo.setTextColor(getResources().getColor(R.color.dangerColor));
                    tvMessageInsufficientGpsPoint.setVisibility(View.VISIBLE);
                    builder.setNegativeButton(R.string.cancel, null)
                            .setTitle(R.string.dialog_title_supply)
                            .setView(v)
                            .create().show();
                } else {
                    builder.setNegativeButton(R.string.cancel, null)
                            .setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    supply();
                                    dialog.dismiss();
                                }
                            })
                            .setTitle(R.string.dialog_title_supply)
                            .setView(v)
                            .create().show();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                progress.dismiss();
                Toast.makeText(getActivity(), R.string.message_network_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void supply() {
        final ProgressDialog progress = Utils.createProgressDialog(getActivity());
        LbClient client = new LbClient();
        client.setToken(Session.getToken());
        client.supplyGpsPoint(territory.getId(), SUPPLY_POINT, new Callback<Territory>() {
            @Override
            public void success(Territory suppliedTerritory, Response response) {
                territory = suppliedTerritory;
                Toast.makeText(getActivity(), R.string.message_supply, Toast.LENGTH_SHORT).show();
                progress.dismiss();
                refresh();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                progress.dismiss();
                Toast.makeText(getActivity(), R.string.message_network_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refresh() {
        if (gMap != null) {
            gMap.clear();
            territory.getMarker().addTo(gMap);
        }

        Picasso.with(getActivity()).load("http://placekitten.com/48/48").into(ivAvatar);
        tvName.setText(territory.getCharacter().getName());
    }
}
