package com.lb.api.client;

import android.graphics.Bitmap;
import android.net.Uri;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.model.LatLng;
import com.lb.api.Authorization;
import com.lb.api.Coordinate;
import com.lb.api.Location;
import com.lb.api.Territory;
import com.lb.api.User;
import com.lb.api.service.LbService;
import com.lb.core.character.CharacterPager;
import com.lb.core.notification.NotificationPager;
import com.lb.core.territory.TerritoryPager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.mime.TypedFile;

import static com.lb.api.client.ILbConstants.DATE_FORMAT;
import static com.lb.api.client.ILbConstants.HOST_API;
import static com.lb.api.client.ILbConstants.PROTOCOL_HTTPS;
import static com.lb.api.client.ILbConstants.AUTH_TOKEN;

/**
 * Created by ackyla on 1/29/14.
 */
public class LbClient {


    protected final String baseUri;

    private String credentials;

    private LbService lb;

    public LbClient() {
        this(HOST_API);
    }

    public LbClient(String hostname) {
        this(hostname, -1, PROTOCOL_HTTPS);
    }

    public LbClient(final String hostname, final int port, final String scheme) {
        final StringBuilder uri = new StringBuilder(scheme);
        uri.append("://");
        uri.append(hostname);
        if (port > 0) uri.append(':').append(port);
        baseUri = uri.toString();
        JacksonConverter converter = new JacksonConverter(new ObjectMapper());
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setConverter(converter)
                .setServer(baseUri)
                .build();
        lb = restAdapter.create(LbService.class);
    }

    public LbClient setToken(final String token) {
        if (token != null && token.length() > 0)
            credentials = AUTH_TOKEN + ' ' + token;
        else
            credentials = null;
        return this;
    }

    public void createUser(String name, Callback<Authorization> callback) {
        lb.createUser(name, callback);
    }

    public void getUser(Callback<User> callback) {
        lb.getUser(credentials, callback);
    }

    public void createLocation(double latitude, double longitude, Callback<Location> callback) {
        lb.createLocation(credentials, latitude, longitude, callback);
    }

    public void getTerritoryList(int page, int per, Callback<TerritoryPager> callback) {
        if (page < 1) page = 1;
        if (per < 1) per = 30;
        lb.getUserTerritories(credentials, page, per, callback);
    }

    public void getUserNotifications(int page, int per, Callback<NotificationPager> callback) {
        if (page < 1) page = 1;
        if (per < 1) per = 30;
        lb.getUserNotifications(credentials, page, per, callback);
    }

    public void getCharacters(int page, int per, Callback<CharacterPager> callback) {
        lb.getCharacters(credentials, page, per, callback);
    }

    public void updateAvatar(Bitmap bitmap, File tempFile, Callback<User> callback) {
        try {
            FileOutputStream out = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
            out.close();
            lb.updateAvatar(credentials, new TypedFile("image/jpeg", tempFile), callback);
        } catch (IOException e) {
            e.printStackTrace();
            if (tempFile.exists()) tempFile.delete();
        }
    }

    public void getUserLocations(Date date, Callback<List<Location>> callback) {
        lb.getUserLocations(credentials, date, callback);
    }

    public void createTerritory(LatLng latLng, int characterId, Callback<Territory> callback) {
        lb.createTerritory(credentials, latLng.latitude, latLng.longitude, characterId, callback);
    }

    public void supplyGpsPoint(int territoryId, int gpsPoint, Callback<Territory> callback) {
        lb.supplyGpsPoint(credentials, territoryId, gpsPoint, callback);
    }
}
