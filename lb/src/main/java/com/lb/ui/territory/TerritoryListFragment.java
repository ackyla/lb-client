package com.lb.ui.territory;

import java.util.ArrayList;

import static com.lb.Intents.EXTRA_USER;
import static com.lb.RequestCodes.SET_TERRITORY;
import com.lb.R;
import com.lb.api.User;
import com.lb.api.client.LbClient;
import com.lb.core.territory.TerritoryPager;
import com.lb.api.Territory;
import com.lb.model.Session;
import com.lb.model.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TerritoryListFragment extends ListFragment implements AbsListView.OnScrollListener{

    private static final int PER = 10;
    private TerritoryListAdapter adapter;
    private boolean waitResponse = false;
    private int page = 1;
    private boolean hasMore = false;
    private View footerView;

    public TerritoryListFragment() {
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
        inflater.inflate(R.menu.territory_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh();
                return true;
            case R.id.action_add:
                startActivityForResult(SetTerritoryActivity.createIntent(), SET_TERRITORY);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Territory territory = adapter.getItem(position);
        startActivity(TerritoryDetailActivity.createIntent(territory));
    }

    public void refresh() {
        if (waitResponse) return;
        waitResponse = true;
        page = 1;
        adapter = new TerritoryListAdapter(getActivity(), 0, new ArrayList<Territory>());
        setListAdapter(adapter);
        getListView().setOnScrollListener(null);
        setListShown(false);
        setEmptyText(null);

        if (getListView().getFooterViewsCount() > 0) getListView().removeFooterView(footerView);

        LbClient client = new LbClient();
        client.setToken(Session.getToken());
        client.getTerritoryList(page, PER, new Callback<TerritoryPager>() {
            @Override
            public void success(TerritoryPager pager, Response response) {
                page = pager.getNextPage();
                hasMore = pager.hasMore();
                for (Territory t : pager.getObjects()) adapter.add(t);
                setListAdapter(adapter);
                setEmptyText(getString(R.string.empty));
                setListShown(true);

                if(hasMore) {
                    getListView().setOnScrollListener(TerritoryListFragment.this);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK != resultCode || data == null) return;

        switch (requestCode) {
            case SET_TERRITORY:
                refresh();
                return;
        }
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
                client.getTerritoryList(page, PER, new Callback<TerritoryPager>() {
                    @Override
                    public void success(TerritoryPager pager, Response response) {
                        page = pager.getNextPage();
                        hasMore = pager.hasMore();
                        for (Territory t : pager.getObjects()) adapter.add(t);

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

