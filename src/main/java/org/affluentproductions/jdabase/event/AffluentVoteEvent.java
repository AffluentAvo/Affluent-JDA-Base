package org.affluentproductions.jdabase.event;

import org.affluentproductions.jdabase.JDABase;

public class AffluentVoteEvent extends AffluentEvent {

    private final String userId;
    private final boolean weekend;

    public AffluentVoteEvent(JDABase jdaBase, String userId, boolean weekend) {
        super(jdaBase);
        this.userId = userId;
        this.weekend = weekend;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isWeekend() {
        return weekend;
    }
}
