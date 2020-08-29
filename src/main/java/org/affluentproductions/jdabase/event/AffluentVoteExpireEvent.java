package org.affluentproductions.jdabase.event;

import org.affluentproductions.jdabase.JDABase;

public class AffluentVoteExpireEvent extends AffluentEvent {

    private final String userId;
    private final long end;

    public AffluentVoteExpireEvent(JDABase jdaBase, String userId, long end) {
        super(jdaBase);
        this.userId = userId;
        this.end = end;
    }

    public String getUserId() {
        return userId;
    }

    public long getEnd() {
        return end;
    }
}