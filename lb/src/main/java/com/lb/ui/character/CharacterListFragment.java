package com.lb.ui.character;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.ListFragment;

import android.view.View;
import android.widget.ListView;

import com.lb.api.Character;
import com.lb.api.client.LbClient;
import com.lb.core.character.CharacterPager;
import com.lb.model.Session;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class CharacterListFragment extends ListFragment {

    private CharacterListAdapter adapter;
    private boolean waitResponse = false;
    private int page = 1;
    private boolean hasMore = false;

    public CharacterListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new CharacterListAdapter(getActivity(), 0, new ArrayList<Character>());
        setListAdapter(adapter);

        LbClient client = new LbClient();
        client.setToken(Session.getToken());
        client.getCharacters(page, 0, new Callback<CharacterPager>() {
            @Override
            public void success(CharacterPager pager, Response response) {
                page = pager.getNextPage();
                hasMore = pager.hasMore();
                for (Character c : pager.getObjects()) adapter.add(c);
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

}
