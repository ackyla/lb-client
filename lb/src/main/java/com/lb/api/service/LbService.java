package com.lb.api.service;

import com.lb.api.Authorization;
import com.lb.api.Coordinate;
import com.lb.api.Location;
import com.lb.api.Notification;
import com.lb.api.Territory;
import com.lb.api.User;
import com.lb.core.character.CharacterPager;
import com.lb.core.notification.NotificationPager;
import com.lb.core.territory.TerritoryPager;

import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

import static com.lb.api.client.ILbConstants.SEGMENT_AVATAR;
import static com.lb.api.client.ILbConstants.SEGMENT_CHARACTERS;
import static com.lb.api.client.ILbConstants.SEGMENT_DESTROY;
import static com.lb.api.client.ILbConstants.SEGMENT_LIST;
import static com.lb.api.client.ILbConstants.SEGMENT_NOTIFICATIONS;
import static com.lb.api.client.ILbConstants.SEGMENT_READ;
import static com.lb.api.client.ILbConstants.SEGMENT_SUPPLY;
import static com.lb.api.client.ILbConstants.SEGMENT_USERS;
import static com.lb.api.client.ILbConstants.SEGMENT_USER;
import static com.lb.api.client.ILbConstants.SEGMENT_LOCATIONS;
import static com.lb.api.client.ILbConstants.SEGMENT_TERRITORIES;
import static com.lb.api.client.ILbConstants.SEGMENT_CREATE;
import static com.lb.api.client.ILbConstants.SEGMENT_SHOW;
import static com.lb.api.client.ILbConstants.PARAM_PAGE;
import static com.lb.api.client.ILbConstants.PARAM_PER;
import static com.lb.api.client.ILbConstants.SEGMENT_MOVE;

/**
 * Created by ackyla on 1/29/14.
 */
public interface LbService {

    static final String HEADER_AUTHORIZATION = "Authorization";

    @POST(SEGMENT_USERS + SEGMENT_CREATE)
    void createUser(@Query("name") String name, Callback<Authorization> callback);

    @GET(SEGMENT_USER + SEGMENT_SHOW)
    void getUser(@Header(HEADER_AUTHORIZATION) String token, Callback<User> callback);

    @FormUrlEncoded
    @POST(SEGMENT_LOCATIONS + SEGMENT_CREATE)
    void createLocation(@Header(HEADER_AUTHORIZATION) String token, @Field("latitude") double latitude, @Field("longitude") double longitude, Callback<Location> callback);

    @GET(SEGMENT_USER + SEGMENT_TERRITORIES)
    void getUserTerritories(@Header(HEADER_AUTHORIZATION) String token, @Query(PARAM_PAGE) int page, @Query(PARAM_PER) int per, Callback<TerritoryPager> callback);

    @GET(SEGMENT_USER + SEGMENT_NOTIFICATIONS)
    void getUserNotifications(@Header(HEADER_AUTHORIZATION) String token, @Query(PARAM_PAGE) int page, @Query(PARAM_PER) int per, Callback<NotificationPager> callback);

    @GET(SEGMENT_CHARACTERS + SEGMENT_LIST)
    void getCharacters(@Header(HEADER_AUTHORIZATION) String token, @Query(PARAM_PAGE) int page, @Query(PARAM_PER) int per, Callback<CharacterPager> callback);

    @Multipart
    @POST(SEGMENT_USER + SEGMENT_AVATAR)
    void updateAvatar(@Header(HEADER_AUTHORIZATION) String token, @Part("avatar") TypedFile avatar, Callback<User> callback);

    @FormUrlEncoded
    @POST(SEGMENT_TERRITORIES + SEGMENT_CREATE)
    void createTerritory(@Header(HEADER_AUTHORIZATION) String token, @Field("latitude") double latitude, @Field("longitude") double longitude, @Field("character_id") int characterId, Callback<Territory> callback);

    @FormUrlEncoded
    @POST(SEGMENT_TERRITORIES + SEGMENT_DESTROY)
    void destroyTerritory(@Header(HEADER_AUTHORIZATION) String token, @Field("territory_id") int territoryId, Callback<Territory> callback);

    @FormUrlEncoded
    @POST(SEGMENT_NOTIFICATIONS + SEGMENT_READ)
    void readNotification(@Header(HEADER_AUTHORIZATION) String token, @Field("id") int notificationId, Callback<Notification> callback);

    @GET(SEGMENT_USER + SEGMENT_LOCATIONS)
    void getUserLocations(@Header(HEADER_AUTHORIZATION) String token, @Query("date") Date date, Callback<List<Location>> callback);

    @FormUrlEncoded
    @POST(SEGMENT_TERRITORIES + SEGMENT_SUPPLY)
    void supplyGpsPoint(@Header(HEADER_AUTHORIZATION) String token, @Field("id") int territoryId, @Field("gps_point") int gpsPoint, Callback<Territory> callback);

    @FormUrlEncoded
    @POST(SEGMENT_TERRITORIES + SEGMENT_MOVE)
    void moveTerritory(@Header(HEADER_AUTHORIZATION) String token, @Field("id") int territoryId, @Field("latitude") double latitude, @Field("longitude") double longitude, Callback<Territory> callback);
}
