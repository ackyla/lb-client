package com.lb.ui.character;

import java.util.List;

import com.lb.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lb.api.Character;
import com.squareup.picasso.Picasso;

public class CharacterListAdapter extends ArrayAdapter<Character> {

    private LayoutInflater mLayoutInflater;
    private Context context;

    public CharacterListAdapter(Context context, int resource, List<Character> objects) {
        super(context, resource, objects);
        this.context = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Character item = (Character) getItem(position);
        if (null == convertView)
            convertView = mLayoutInflater.inflate(R.layout.character_list_item, null);

        ImageView iv = (ImageView) convertView.findViewById(R.id.iv_avatar);

        Picasso.with(context).load("http://placekitten.com/120/120").into(iv); // TODO

		TextView tv1 = (TextView)convertView.findViewById(R.id.tv_name);
		tv1.setText(item.getName());
		
		TextView tv2 = (TextView)convertView.findViewById(R.id.tv_radius);
		tv2.setText("半径: "+item.getRadius()/1000+"km");
		
		TextView tv3 = (TextView)convertView.findViewById(R.id.tv_precision);
		tv3.setText("精度: "+item.getPrecision()*100+"%");
		
		TextView tv4 = (TextView)convertView.findViewById(R.id.tv_cost);
		tv4.setText("消費陣力: "+item.getCost());
		
		TextView tv5 = (TextView) convertView.findViewById(R.id.tv_distance);
		tv5.setText("遠隔: "+item.getDistance()/1000+"km");

        return convertView;
    }
}
