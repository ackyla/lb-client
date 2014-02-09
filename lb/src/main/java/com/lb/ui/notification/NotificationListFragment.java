package com.lb.ui.notification;

import android.os.Bundle;
import android.support.v4.app.ListFragment;

import com.lb.api.Notification;
import com.lb.api.client.LbClient;
import com.lb.core.notification.NotificationPager;
import com.lb.model.Session;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NotificationListFragment extends ListFragment {

    private NotificationListAdapter adapter;
    private boolean waitResponse = false;
    private int page = 1;
    private boolean hasMore = false;

    public NotificationListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new NotificationListAdapter(getActivity(), 0, new ArrayList<Notification>());
        setListAdapter(adapter);

        LbClient client = new LbClient();
        client.setToken(Session.getToken());
        client.getUserNotifications(page, 30, new Callback<NotificationPager>() {
            @Override
            public void success(NotificationPager pager, Response response) {
                page = pager.getNextPage();
                hasMore = pager.hasMore();
                for (Notification n : pager.getObjects()) adapter.add(n);
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }
}
