package com.lb.api;

import java.io.Serializable;

/**
 * Created by ackyla on 1/29/14.
 */
public class Authorization implements Serializable {

    private static final long serialVersionUID = 861983283732402401L;

    private int id;
    private String token;
    private String name;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
