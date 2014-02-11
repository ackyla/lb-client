package com.lb.ui.character;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lb.R;
import com.lb.api.Character;
import com.lb.api.client.LbClient;
import com.lb.core.character.CharacterPager;
import com.lb.model.Session;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ackyla on 2/7/14.
 */
public class CharacterDialogFragment extends DialogFragment {

    private static final String TAG = "characterDialog";

    static public interface OnItemClickListener {
        void onItemClick(AlertDialog dialog, Character character);
    }

    private static class CharacterListAdapter extends ArrayAdapter<Character> {

        private LayoutInflater inflater;
        private Context context;

        public CharacterListAdapter(Context context, int resource, List<Character> objects) {
            super(context, resource, objects);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Character item = (Character) getItem(position);
            if (null == convertView) convertView = inflater.inflate(R.layout.character_list_item, null);

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

    public static void show(FragmentActivity activity) {
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment current = manager.findFragmentByTag(TAG);
        if (current != null)
            transaction.remove(current);
        transaction.addToBackStack(null);

        CharacterDialogFragment fragment = new CharacterDialogFragment();
        fragment.show(manager, TAG);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.select_character)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                });

        final AlertDialog dialog = builder.create();

        ListView view = (ListView) getActivity().getLayoutInflater().inflate(R.layout.list_dialog_view, null);

        final CharacterListAdapter adapter = new CharacterListAdapter(getActivity(), 0, new ArrayList<Character>());

        LbClient client = new LbClient();
        client.setToken(Session.getToken());
        client.getCharacters(1, 10, new Callback<CharacterPager>() {
            @Override
            public void success(CharacterPager pager, Response response) {
                for (Character c : pager.getObjects()) adapter.add(c);
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });

        view.setAdapter(adapter);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OnItemClickListener listener = (OnItemClickListener) getActivity();
                listener.onItemClick(dialog, adapter.getItem(position));
            }
        });
        dialog.setView(view);
        return dialog;
    }
}