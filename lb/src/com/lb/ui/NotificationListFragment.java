package com.lb.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.lb.api.API;
import com.lb.model.Session;
import com.lb.model.Utils;
import com.lb.ui.TerritoryDetailFragment.OnTerritoryDetailFragmentListener;
import com.loopj.android.http.JsonHttpResponseHandler;

public class NotificationListFragment extends ListFragment {
	
	private OnNotificationListFragmentItemClickListener listener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (OnNotificationListFragmentItemClickListener) activity;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		API.getUserNotifications(Session.getUser(), true, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray jsonArray) {
				List<NotificationData> objects = new ArrayList<NotificationData>();
				
				// TODO
				for(int i = jsonArray.length()-1; i >= 0; i --) {
					try {
						JSONObject json = jsonArray.getJSONObject(i);
						NotificationData item = new NotificationData();
						item.setId(json.getInt("notification_id"));
						item.setRead(json.getBoolean("read"));
						String type = json.getString("notification_type");
						Date date = Utils.parseStringToDate(json.getString("created_at"));
						item.setMessage(Utils.getRelativeTimeSpanString(date));
						if(type.equals("entering")) {
							// みつかった
							item.setType(NotificationData.TYPE_DETECTED);
							item.setTitle(json.getJSONObject("territory_owner").getString("name") + " のテリトリーに入りました");
							item.setLatitude(json.getJSONObject("location").getDouble("latitude"));
							item.setLongitude(json.getJSONObject("location").getDouble("longitude"));
							item.setTerritoryId(0);
						}else{
							// みつけた
							item.setType(NotificationData.TYPE_DETECT);
							item.setTitle("テリトリー_"+json.getJSONObject("territory").getInt("territory_id")+" への侵入者発見");
							item.setLatitude(json.getJSONObject("territory").getDouble("latitude"));
							item.setLongitude(json.getJSONObject("territory").getDouble("longitude"));
							item.setTerritoryId(json.getJSONObject("territory").getInt("territory_id"));
						}
						
						objects.add(item);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				
				Context context = getActivity();
				if(context != null) {
					NotificationAdapter notificationAdapter = new NotificationAdapter(context, 0, objects);
					setListAdapter(notificationAdapter);
				}else{
					setListAdapter(null);
				}

			}

			@Override
			public void onFailure(Throwable throwable) {
				Log.i("game","getUserNotificationListOnFailure="+ throwable);
			}
		});
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		NotificationData item = (NotificationData) l.getItemAtPosition(position);
		listener.onClickNotificationListItem(item.getId(), item.getLatitude(), item.getLongitude(), item.getType(), item.getTitle(), item.getMessage(), item.getRead(), item.getTerritoryId());
	}
	
    public static interface OnNotificationListFragmentItemClickListener {
        void onClickNotificationListItem(Integer id, Double latitude, Double longitude, Integer type, String title, String message, boolean read, Integer territoryId);
    }
}