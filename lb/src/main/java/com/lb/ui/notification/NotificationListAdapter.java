package com.lb.ui.notification;

import java.util.List;

import com.lb.R;
import com.lb.api.Notification;
import com.lb.model.Utils;

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

    public NotificationListAdapter(Context context, int resourceId, List<Notification> objects) {
        super(context, resourceId, objects);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Notification item = (Notification) getItem(position);
        if (null == convertView)
            convertView = mLayoutInflater.inflate(R.layout.notification_list_item, null);

        LinearLayout ll = (LinearLayout) convertView.findViewById(R.id.ll_notification_list_item);
        if (item.isRead()) ll.setBackgroundColor(Color.argb(30, 0, 0, 0));

        ImageView ivNotification = (ImageView) convertView.findViewById(R.id.iv_notification);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
        if (item.getNotificationType().equals(Notification.TYPE_ENTERING)) {
            ivNotification.setBackgroundResource(android.R.drawable.ic_menu_info_details);
            tvTitle.setText(item.getTerritoryOwner().getName() + "に見つかりました");
        }

        if (item.getNotificationType().equals(Notification.TYPE_DETECTION)) {
            ivNotification.setBackgroundResource(android.R.drawable.ic_menu_mylocation);
            tvTitle.setText(item.getTerritory().getCharacter().getName() + "で見つけました");
        }

        TextView tvMessage = (TextView) convertView.findViewById(R.id.tv_message);
        tvMessage.setText(Utils.getRelativeTimeSpanString(item.getCreatedAt()));

        return convertView;
    }
}