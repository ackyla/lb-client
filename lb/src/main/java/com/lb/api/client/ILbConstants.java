package com.lb.api.client;

/**
 * Created by ackyla on 1/29/14.
 */
public interface ILbConstants {
    String AUTH_TOKEN = "token";

    String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    int MINUTES_PER_POINT = 10;

    String HOST_API = "192.168.11.6:3000";
    //String HOST_API = "ackyla.com:3000";

    String CONTENT_TYPE_JSON = "application/json";
    String CHARSET_UTF8 = "UTF-8";

    String PROTOCOL_HTTPS = "http";
    //String PROTOCOL_HTTPS = "https";

    String SEGMENT_USER = "/user";
    String SEGMENT_USERS = "/users";
    String SEGMENT_LOCATIONS = "/locations";
    String SEGMENT_TERRITORIES = "/territories";
    String SEGMENT_NOTIFICATIONS = "/notifications";
    String SEGMENT_CHARACTERS = "/characters";
    String SEGMENT_CREATE = "/create";
    String SEGMENT_SHOW = "/show";
    String SEGMENT_LIST = "/list";
    String SEGMENT_DESTROY = "/destroy";
    String SEGMENT_READ = "/read";
    String SEGMENT_SUPPLY = "/supply";
    String SEGMENT_AVATAR = "/avatar";
    String SEGMENT_MOVE = "/move";

    String PARAM_PAGE = "page";
    String PARAM_PER = "per";
}
