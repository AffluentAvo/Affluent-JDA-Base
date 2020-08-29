package org.affluentproductions.jdabase.event;

import org.affluentproductions.jdabase.JDABase;

public class AffluentPostLoadEvent extends AffluentEvent {

    public AffluentPostLoadEvent(JDABase jdaBase) {
        super(jdaBase);
    }
}