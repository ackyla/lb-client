package com.lb.ui.user;

import com.lb.R;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GameDropdownAdapter extends ArrayAdapter<GameDropdownData> {

    private LayoutInflater mLayoutInflater;
    private Context context;

    public GameDropdownAdapter(Context context) {
        super(context, android.R.layout.simple_spinner_item);
        this.context = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void refreshGpsPoint(int position, int current, int limit) {
        GameDropdownData item = getItem(position);
        item.setGpsPoint(current);
        item.setGpsPointMax(limit);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = mLayoutInflater.inflate(R.layout.game_dropdown_list_title, parent, false);
        } else {
            view = convertView;
        }

        GameDropdownData item = getItem(position);
        TextView tv1 = (TextView) view.findViewById(R.id.tv_title);
        tv1.setText(item.getTitle());
        TextView tv2 = (TextView) view.findViewById(R.id.tv_message);
        tv2.setText(item.getMessage());
        ProgressBar pb = (ProgressBar) view.findViewById(R.id.pb_gps_point);
        pb.setMax(item.getGpsPointMax());
        pb.setProgress(item.getGpsPoint());
        TextView tv3 = (TextView) view.findViewById(R.id.tv_gps_point);
        tv3.setText("陣力 " + item.getGpsPoint() + "/" + item.getGpsPointMax());
        ImageView iv = (ImageView) view.findViewById(R.id.iv_avatar);
        Picasso.with(context).load(item.getAvatar()).into(iv);
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = mLayoutInflater.inflate(R.layout.game_dropdown_list_item, parent, false);
        } else {
            view = convertView;
        }
        GameDropdownData item = getItem(position);
        ImageView icon = (ImageView) view.findViewById(R.id.iv_avatar);
        if (position == 0)
            Picasso.with(context).load(item.getAvatar()).into(icon);
        else
            icon.setImageResource(item.getImage());
        //icon.setColorFilter(getContext().getColor(item.getColorId())));
        TextView tv1 = (TextView) view.findViewById(R.id.tv_title);
        tv1.setText(item.getTitle());

        return view;
    }

}
