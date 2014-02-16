package com.lb.ui.notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.lb.R;
import com.lb.api.Notification;
import com.lb.api.User;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * Created by ackyla on 2/11/14.
 */
public class NotificationInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private LayoutInflater inflater;
    private Context context;
    private HashMap<String, Notification> notifications;

    public NotificationInfoWindowAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        notifications = new HashMap<String, Notification>();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View v = inflater.inflate(R.layout.notification_info_contents, null);

        ImageView ivAvatar = (ImageView) v.findViewById(R.id.iv_avatar);
        TextView tvName = (TextView) v.findViewById(R.id.tv_name);
        TextView tvLevel = (TextView) v.findViewById(R.id.tv_level);
        TextView tvDate = (TextView) v.findViewById(R.id.tv_date);

        Notification notification = notifications.get(marker.getId());
        User user = notification.getTerritoryOwner();

        Picasso.with(context).load(user.getAvatar()).into(ivAvatar);
        tvName.setText(user.getName());
        tvLevel.setText(Integer.toString(user.getLevel()));
        tvDate.setText(notification.getRelativeTimeSpanString());

        return v;
    }

    public void setNotification(String markerId, Notification notification) {
        notifications.put(markerId, notification);
    }

    public Notification getNotification(String markerId) {
        return notifications.get(markerId);
    }

    public void clear() {
        notifications = new HashMap<String, Notification>();
    }
}
