package com.lb.core.territory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lb.api.Territory;
import com.lb.core.Pager;

import java.util.List;

/**
 * Created by ackyla on 1/28/14.
 */
public class TerritoryPager extends Pager<Territory> {
    @Override
    @JsonProperty("territories")
    public List<Territory> getObjects() {
        return super.getObjects();
    }
}