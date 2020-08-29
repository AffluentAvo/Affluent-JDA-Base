package org.affluentproductions.jdabase.api;

import org.affluentproductions.jdabase.event.*;

public interface AffluentListener {

    void onAffluentPreLoadEvent(AffluentPreLoadEvent event);

    void onAffluentPostLoadEvent(AffluentPostLoadEvent event);

    void onAffluentShardLoadEvent(AffluentShardLoadEvent event);

    void onAffluentVoteExpireEvent(AffluentVoteExpireEvent event);

    void onAffluentEvent(AffluentEvent event);

    void onAffluentVoteEvent(AffluentVoteEvent event);

    void onAffluentCommandCancelEvent(AffluentCommandCancelEvent event);

    void onEvent(AffluentEvent event);

}