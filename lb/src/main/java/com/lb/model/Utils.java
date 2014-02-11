package com.lb.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.lb.R;
import com.lb.api.User;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.view.WindowManager.BadTokenException;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalPosition;

public class Utils {

    public static PolylineOptions createUserHistoryPolylineOptions() {
        PolylineOptions lineOpts = new PolylineOptions();
        lineOpts.color(Color.argb(50, 0, 0, 255));
        lineOpts.width(5);
        lineOpts.geodesic(true);
        return lineOpts;
    }

    public static MarkerOptions createDefaultMarkerOptions() {
        MarkerOptions markerOptions = new MarkerOptions();
        return markerOptions;
    }

    public static ProgressDialog createProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        try {
            dialog.show();
        } catch (BadTokenException e) {

        }
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.progress_dialog);
        // dialog.setMessage(Message);
        return dialog;
    }

    public static Date parseStringToDate(String dateString) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss");

        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getDateTimeString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy'/'MM'/'dd' 'HH':'mm':'ss");
        return sdf.format(date);
    }

    /**
     * 現在時刻との相対時間を返す
     *
     * @param date
     * @return
     */
    public static String getRelativeTimeSpanString(Date date) {
        Date now = Calendar.getInstance().getTime();
        return DateUtils.getRelativeTimeSpanString(date.getTime(), now.getTime(), DateUtils.FORMAT_ABBREV_RELATIVE).toString();
    }

    public static User updateSessionUserInfo(User user) {
        User sessionUser = Session.getUser();
        user.setId(sessionUser.getId());
        //user.setToken(sessionUser.getToken());
        Session.setUser(user);
        return user;
    }

    /**
     * 富士山のLatLngを返す
     *
     * @return
     */
    public static LatLng getDefaultLatLng() {
        return new LatLng(35.360549, 138.727786);
    }

    public static double getDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
        GeodeticCalculator calc = new GeodeticCalculator();
        Ellipsoid ellipsoid = Ellipsoid.WGS84;
        GlobalPosition pos1 = new GlobalPosition(latitude1, longitude1, 0.0);
        GlobalPosition pos2 = new GlobalPosition(latitude2, longitude2, 0.0);
        return calc.calculateGeodeticCurve(ellipsoid, pos1, pos2).getEllipsoidalDistance();
    }

    public static double getDistance(LatLng latLng1, LatLng latLng2) {
        return getDistance(latLng1.latitude, latLng1.longitude, latLng2.latitude, latLng2.longitude);
    }
}
