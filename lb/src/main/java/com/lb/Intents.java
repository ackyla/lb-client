package com.lb;

import android.content.Intent;

import com.lb.api.Territory;
import com.lb.api.User;

import java.io.Serializable;

/**
 * Created by ackyla on 1/30/14.
 */
public class Intents {

    public static final String INTENT_PREFIX = "com.lb.";

    public static final String INTENT_EXTRA_PREFIX = INTENT_PREFIX + "extra.";

    public static final String EXTRA_GAME = INTENT_EXTRA_PREFIX + "GAME";

    public static final String EXTRA_USER = INTENT_EXTRA_PREFIX + "USER";

    public static final String EXTRA_TERRITORY = INTENT_EXTRA_PREFIX + "TERRITORY";

    public static class Builder {

        private final Intent intent;

        public Builder(String actionSuffix) {
            intent = new Intent(INTENT_PREFIX + actionSuffix);
        }

        public Builder user(User user) {
            return add(EXTRA_USER, user);
        }

        public Builder territory(Territory territory) {
            return add(EXTRA_TERRITORY, territory);
        }

        public Builder add(String fieldName, Serializable value) {
            intent.putExtra(fieldName, value);
            return this;
        }

        public Intent toIntent() {
            return intent;
        }
    }

}
