package com.lb.core.character;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lb.api.Character;
import com.lb.core.Pager;

import java.util.List;

/**
 * Created by ackyla on 1/28/14.
 */
public class CharacterPager extends Pager<Character> {
    @Override
    @JsonProperty("characters")
    public List<Character> getObjects() {
        return super.getObjects();
    }
}