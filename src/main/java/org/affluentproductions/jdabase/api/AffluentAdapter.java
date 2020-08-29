package org.affluentproductions.jdabase.api;

import org.affluentproductions.jdabase.event.*;

public abstract class AffluentAdapter implements AffluentListener {

    public void onAffluentEvent(AffluentEvent event) {
    }

    public void onAffluentVoteEvent(AffluentVoteEvent event) {
    }

    public void onAffluentVoteExpireEvent(AffluentVoteExpireEvent event) {
    }

    public void onAffluentShardLoadEvent(AffluentShardLoadEvent event) {
    }

    public void onAffluentPostLoadEvent(AffluentPostLoadEvent event) {
    }

    public void onAffluentPreLoadEvent(AffluentPreLoadEvent event) {
    }

    public void onAffluentCommandCancelEvent(AffluentCommandCancelEvent event) {
    }

    @Override
    public final void onEvent(AffluentEvent event) {
        onAffluentEvent(event);

        // Vote Events
        if (event instanceof AffluentVoteEvent) onAffluentVoteEvent((AffluentVoteEvent) event);
        else if (event instanceof AffluentVoteExpireEvent) onAffluentVoteExpireEvent((AffluentVoteExpireEvent) event);

            // Command Events
        else if (event instanceof AffluentCommandCancelEvent)
            onAffluentCommandCancelEvent((AffluentCommandCancelEvent) event);

            // Main Events (Shards, Loadings)
        else if (event instanceof AffluentShardLoadEvent) onAffluentShardLoadEvent((AffluentShardLoadEvent) event);
        else if (event instanceof AffluentPostLoadEvent) onAffluentPostLoadEvent((AffluentPostLoadEvent) event);
        else if (event instanceof AffluentPreLoadEvent) onAffluentPreLoadEvent((AffluentPreLoadEvent) event);
    }
}