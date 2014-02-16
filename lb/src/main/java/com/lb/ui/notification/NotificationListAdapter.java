package com.lb.ui.notification;

import java.util.List;

import com.lb.R;
import com.lb.api.Notification;
import com.lb.model.Utils;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NotificationListAdapter extends ArrayAdapter<Notification> {

    private LayoutInflater mLayoutInflater;
    private Context context;

    public NotificationListAdapter(Context context, int resourceId, List<Notification> objects) {
        super(context, resourceId, objects);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Notification item = (Notification) getItem(position);
        if (null == convertView)
            convertView = mLayoutInflater.inflate(R.layout.notification_list_item, null);

        LinearLayout ll = (LinearLayout) convertView.findViewById(R.id.ll_notification_list_item);
        ImageView ivAvatar = (ImageView) convertView.findViewById(R.id.iv_notification);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
        TextView tvDetectionDate = (TextView) convertView.findViewById(R.id.tv_detection_date);

        if (item.isRead()) ll.setBackgroundColor(Color.argb(30, 0, 0, 0));
        else ll.setBackgroundColor(Color.argb(0, 255, 255, 255));

        if (item.getNotificationType().equals(Notification.TYPE_ENTERING)) {
            Picasso.with(context).load(item.getTerritoryOwner().getAvatar()).into(ivAvatar);
            tvTitle.setText(item.getTerritoryOwner().getName()+context.getString(R.string.entering_to));
        }

        if (item.getNotificationType().equals(Notification.TYPE_DETECTION)) {
            Picasso.with(context).load(Utils.getDummyImage(48, 48)).into(ivAvatar);
            tvTitle.setText(item.getTerritory().getCharacter().getName()+context.getString(R.string.detect_on));
        }

        tvDetectionDate.setText(Utils.getRelativeTimeSpanString(item.getCreatedAt()));

        return convertView;
    }
}