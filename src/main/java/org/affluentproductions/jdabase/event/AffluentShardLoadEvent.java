package org.affluentproductions.jdabase.event;

import net.dv8tion.jda.api.events.ReadyEvent;
import org.affluentproductions.jdabase.JDABase;

public class AffluentShardLoadEvent extends AffluentEvent {

    private final ReadyEvent readyEvent;

    public AffluentShardLoadEvent(JDABase jdaBase, ReadyEvent readyEvent) {
        super(jdaBase);
        this.readyEvent = readyEvent;
    }

    public ReadyEvent getReadyEvent() {
        return readyEvent;
    }
}