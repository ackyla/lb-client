package com.lb.ui.territory;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.lb.R;
import com.lb.api.Territory;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * Created by ackyla on 2/11/14.
 */
public class TerritoryInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private LayoutInflater inflater;
    private Context context;
    private HashMap<String, Territory> territories;

    public TerritoryInfoWindowAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        territories = new HashMap<String, Territory>();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View v = inflater.inflate(R.layout.territory_info_contents, null);

        ImageView ivAvatar = (ImageView) v.findViewById(R.id.iv_avatar);
        TextView tvName = (TextView) v.findViewById(R.id.tv_name);
        TextView tvDetectionCount = (TextView) v.findViewById(R.id.tv_detection_count);
        TextView tvExpirationDate = (TextView) v.findViewById(R.id.tv_expiration_date);

        Territory territory = territories.get(marker.getId());
        String expirateionDate = territory.isExpired() ? context.getString(R.string.expired) : territory.getRelativeTimeSpanString();

        Picasso.with(context).load("http://placekitten.com/48/48").into(ivAvatar);
        tvName.setText(territory.getCharacter().getName());
        tvDetectionCount.setText(Integer.toString(territory.getDetectionCount()));
        tvExpirationDate.setText(expirateionDate);

        return v;
    }

    public void setTerritory(String markerId, Territory territory) {
        territories.put(markerId, territory);
    }

    public void clear() {
        territories = new HashMap<String, Territory>();
    }
}
