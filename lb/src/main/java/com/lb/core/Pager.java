package com.lb.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by ackyla on 1/28/14.
 */
public abstract class Pager<E> {

    private int nextPage;
    private int previousPage;
    private boolean hasMore;
    private List<E> objects;

    public List<E> getObjects() {
        return objects;
    }

    @JsonProperty("next_page")
    public int getNextPage() {
        return nextPage;
    }

    @JsonProperty("previous_page")
    public int getPreviousPage() {
        return previousPage;
    }

    @JsonProperty("has_more")
    public boolean hasMore() {
        return hasMore;
    }
}
