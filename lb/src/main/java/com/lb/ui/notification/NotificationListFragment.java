package com.lb.ui.notification;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.lb.R;
import com.lb.api.Notification;
import com.lb.api.Territory;
import com.lb.api.client.LbClient;
import com.lb.core.notification.NotificationPager;
import com.lb.model.Session;
import com.lb.ui.territory.TerritoryDetailActivity;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NotificationListFragment extends ListFragment implements AbsListView.OnScrollListener {

    private static final int PER = 10;
    private NotificationListAdapter adapter;
    private boolean waitResponse = false;
    private int page = 1;
    private boolean hasMore = false;
    private View footerView;

    public NotificationListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        footerView = getLayoutInflater(savedInstanceState).inflate(R.layout.list_footer_view, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refresh();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.notification_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (position >= adapter.getCount()) return;
        Notification notification = adapter.getItem(position);

        if (notification.getNotificationType().equals(Notification.TYPE_ENTERING)) {

        } else if (notification.getNotificationType().equals(Notification.TYPE_DETECTION)) {
            Territory territory = notification.getTerritory();
            if (territory != null) startActivity(TerritoryDetailActivity.createIntent(territory));
        }

    }

    private void refresh() {
        if (waitResponse) return;
        waitResponse = true;
        page = 1;
        adapter = new NotificationListAdapter(getActivity(), 0, new ArrayList<Notification>());
        setListAdapter(adapter);
        getListView().setOnScrollListener(null);
        setListShown(false);
        setEmptyText(null);

        if (getListView().getFooterViewsCount() > 0) getListView().removeFooterView(footerView);

        LbClient client = new LbClient();
        client.setToken(Session.getToken());
        client.getUserNotifications(page, PER, new Callback<NotificationPager>() {
            @Override
            public void success(NotificationPager pager, Response response) {
                page = pager.getNextPage();
                hasMore = pager.hasMore();
                for (Notification n : pager.getObjects()) adapter.add(n);
                setEmptyText(getString(R.string.empty));
                setListShown(true);

                if(hasMore) {
                    getListView().setOnScrollListener(NotificationListFragment.this);
                    getListView().addFooterView(footerView);
                }

                waitResponse = false;
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                waitResponse = false;
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (totalItemCount == firstVisibleItem + visibleItemCount) {
            if (hasMore && !waitResponse) {
                waitResponse = true;
                LbClient client = new LbClient();
                client.setToken(Session.getToken());
                client.getUserNotifications(page, PER, new Callback<NotificationPager>() {
                    @Override
                    public void success(NotificationPager pager, Response response) {
                        page = pager.getNextPage();
                        hasMore = pager.hasMore();
                        for (Notification n : pager.getObjects()) adapter.add(n);

                        if (!hasMore) {
                            getListView().setOnScrollListener(null);
                            getListView().removeFooterView(footerView);
                        }

                        waitResponse = false;
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        waitResponse = false;
                    }
                });
            }
        }
    }
}
