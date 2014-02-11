package com.lb.ui.territory;

import java.util.ArrayList;

import static com.lb.RequestCodes.SET_TERRITORY;
import com.lb.R;
import com.lb.api.client.LbClient;
import com.lb.core.territory.TerritoryPager;
import com.lb.api.Territory;
import com.lb.model.Session;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TerritoryListFragment extends ListFragment {

    private TerritoryListAdapter adapter;
    private boolean waitResponse = false;
    private int page = 1;
    private boolean hasMore = false;

    public TerritoryListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

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

        LbClient client = new LbClient();
        client.setToken(Session.getToken());
        client.getTerritoryList(page, 30, new Callback<TerritoryPager>() {
            @Override
            public void success(TerritoryPager pager, Response response) {
                page = pager.getNextPage();
                hasMore = pager.hasMore();
                for (Territory t : pager.getObjects()) adapter.add(t);
                setListAdapter(adapter);
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
}

