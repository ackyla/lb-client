package com.lb.ui.territory;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lb.R;
import com.lb.api.Territory;
import com.lb.model.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ackyla on 1/27/14.
 */
public class TerritoryListAdapter extends ArrayAdapter<Territory> {
    private LayoutInflater layoutInflater;
    private Context context;

    public TerritoryListAdapter(Context context, int resource, List<Territory> objects) {
        super(context, resource, objects);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.territory_list_item, null);

        Territory item = (Territory) getItem(position);
        String expirationDate = item.isExpired() ? context.getString(R.string.expired) : item.getRelativeTimeSpanString();

        ImageView iv = (ImageView) convertView.findViewById(R.id.iv_territory);
        TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
        TextView tvRadius = (TextView) convertView.findViewById(R.id.tv_radius);
        TextView tvPrecision = (TextView) convertView.findViewById(R.id.tv_precision);
        TextView tvDetectionCount = (TextView) convertView.findViewById(R.id.tv_detection_count);
        TextView tvExpirationDate = (TextView) convertView.findViewById(R.id.tv_expiration_date);

        Picasso.with(context).load("http://placekitten.com/120/120").into(iv); // TODO
        tvName.setText(item.getCharacter().getName());
        tvRadius.setText(item.getRadius() / 1000 + "km");
        tvPrecision.setText(item.getPrecision() * 100 + "%");
        tvDetectionCount.setText(Integer.toString(item.getDetectionCount()));
        tvExpirationDate.setText(expirationDate);
        Log.i("dump", "pos="+position);
        if (item.isExpired()) tvExpirationDate.setTextColor(context.getResources().getColor(R.color.dangerColor));
        else tvExpirationDate.setTextColor(context.getResources().getColor(R.color.infoBlueColor));


        return convertView;
    }
}
